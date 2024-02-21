package eth.social.sefer.data.repos.remote

import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.like_button.LikeButton
import eth.social.sefer.data.apis.PostApi
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat

open class PostRepo(
  protected val viewModelScope: CoroutineScope, protected val url: String
) : ApplicationRepo() {
  private val _mPosts: MutableLiveData<List<Post>> = MutableLiveData()
  private val api: PostApi = retrofitInstance.create(PostApi::class.java)

  val posts: LiveData<List<Post>> get() = _mPosts

  // meta data(counter)
  private var totalPosts: Int = 0

  fun listOfPosts(page: Int?, append: Boolean) {
    isLastRequestAppend = append
    val handler = CoroutineExceptionHandler { _, t ->
      // when load more swipe refresh layout value is false, so no need to call
      // What indicate loading? there is loading layout at the end of list
      if (!append) {
        viewModelScope.launch { setApiResponse(t, null) }
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      if (!isInternetAvailable()) {
        delay(1000)
      }

      val response = api.listOfPost(url, authorization, page)

      if (response.isSuccessful) {
        withContext(Dispatchers.Main) {
          meta = response.body()?.meta

          val list: MutableList<Post> = response.body()?.posts!!

          if (meta?.isNextPageAvailable == true) {
            list.add(placeHolder())
          }

          if (append) {
            // on append replace placeholder
            val currentList = ArrayList(_mPosts.value!!)
            // remove from last index which is loading placeholder
            currentList.removeAt(totalPosts - 1)
            list.addAll(0, currentList)
          }

          meta?.let {
            if (append) {
              totalPosts += it.count
            } else {
              totalPosts == it.count
            }
          }

          _mPosts.postValue(list)
        }
        mResponse.emit(ApiResponse().apply { okay = true })
      } else {
        emitRequestError(response.errorBody(), response.code(), true)
      }
    }
  }

  fun toggleVote(
    post: Post, voteButton: LikeButton, numberOfLikes: TextView, numberFormatter: NumberFormat
  ) {
    val isVoted = post.isVoted ?: false // original value
    setLikeButtonStatus(voteButton, !isVoted, false)

    val handler = CoroutineExceptionHandler { _, _ ->
      viewModelScope.launch(Dispatchers.Main) {
        // UNDO
        setLikeButtonStatus(voteButton, isVoted)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.votePost(authorization, post.id)
      if (response.isSuccessful) {
        val serverPost: Post = response.body()?.post!!
        withContext(Dispatchers.Main) {
          post.isVoted = serverPost.isVoted
          post.votesCount = serverPost.votesCount

          if (serverPost.votesCount > 0) {
            numberOfLikes.text = numberFormatter.format(serverPost.votesCount)
          } else {
            numberOfLikes.isVisible = false
          }
          voteButton.isEnabled = true
        }
      } else {
        // UNDO
        withContext(Dispatchers.Main) {
          setLikeButtonStatus(voteButton, isVoted, true)
        }
        emitRequestError(response.errorBody(), response.code())
      }
    }
  }

  fun deletePost(post: Post) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)

        restorePost(post)
      }
    }

    removePostFromList(post)

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.deletePost(authorization, post.id)

      if (response.isSuccessful) {
        withContext(Dispatchers.Main) {
          totalPosts -= 1
        }
      } else {
        emitRequestError(response.errorBody(), response.code())

        if (response.code() == unauthorizedCode) {
          restorePost(post)
        }
      }
    }
  }

  fun reportPost(post: Post) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.reportPost(authorization, post.id)
      if (response.isSuccessful) {
        removePostFromList(post)
      } else {
        val resp = parseRequestResponse(response.errorBody()!!).also {
          it.okay = false
        }
        mResponse.emit(resp)
      }
    }
  }

  fun loadMorePosts() {
    listOfPosts(meta!!.next, true)
  }

  private fun restorePost(post: Post) {
    val list: MutableList<Post> = ArrayList(_mPosts.value!!)

    // restore
    val index = list.indexOfFirst {
      if (it.id < 0) {
        false
      } else {
        // since ordered by id, get the first comment which its id is greater
        // comment id: 4 , list -> 2,3,5,6 here target is 5
        it.id > post.id
      }
    }

    // in index 0 is for post show
    val lastIndex: Int = if (index == -1) {
      totalPosts // last index
    } else {
      index
    }
    list.add(lastIndex, post)
    _mPosts.postValue(list)
  }

  private fun removePostFromList(post: Post) {
    val updatedList: MutableList<Post> = ArrayList(_mPosts.value!!)
    updatedList.remove(post)

    _mPosts.postValue(updatedList)
  }

  private fun placeHolder(): Post = Post().apply { id = -1L }

  fun retryCurrentRequest() {
    listOfPosts(meta?.page, isLastRequestAppend)
  }
}