package eth.social.sefer.data.repos.remote

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.like_button.LikeButton
import eth.social.sefer.data.apis.CommentApi
import eth.social.sefer.data.models.Comment
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

class CommentRepo(
  private val url: String,
  private val isPostShow: Boolean,
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val _mComments: MutableLiveData<List<Comment>> = MutableLiveData()
  private val api: CommentApi = retrofitInstance.create(CommentApi::class.java)
  private var totalComments = 0
  val comments: LiveData<List<Comment>> get() = _mComments

  init {
    if (isPostShow) {
      totalComments = 1 // indicates placeholder for post
      _mComments.postValue(listOf(placeHolder()))
    }
  }

  fun commentsList(page: Int?, append: Boolean) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null, false)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      // since it loads from cache, i wanna suspend
      if (!isInternetAvailable()) {
        delay(500L)
      }

      val response = api.listOfComment(url, authorization, page)
      if (response.isSuccessful) {
        meta = response.body()?.meta

        val list: MutableList<Comment>? = response.body()?.comments

        if (meta?.isNextPageAvailable == true) {
          list?.add(placeHolder())
        }

        if (append) {
          // on append replace placeholder
          val currentList = ArrayList(_mComments.value!!)
          // remove from last index which is loading placeholder
          currentList.removeAt(totalComments - 1)
          list?.addAll(0, currentList)
        } else {
          list?.add(0, placeHolder())
          // on post show , we have placeholder for post show which is at index 0
          totalComments = if (isPostShow) 1 else 0
        }

        meta?.let {
          totalComments += it.count

          if (it.isNextPageAvailable) {
            list?.add(loadingPlaceHolder())
          }
        }

        _mComments.postValue(list!!)
        mResponse.emit(ApiResponse().also { it.okay = true })
      } else {
        val error = parseRequestResponse(response.errorBody()!!).also {
          it.okay = false
          it.unauthorized = response.code() == unauthorizedCode
        }

        mResponse.emit(error)
      }
    }
  }

  fun deleteComment(comment: Comment, position: Int) {
    val handler = CoroutineExceptionHandler { _, _ ->
      val list: MutableList<Comment> = ArrayList(_mComments.value!!)

      val index = list.indexOfFirst {
        if (it.id < 0) {
          false
        } else {
          // since ordered by id, get the first comment which its id is greater
          // comment id: 4 , list -> 2,3,5,6 here target is 5
          it.id > comment.id
        }
      }

      // in index 0 is for post show
      val lastIndex: Int = if (index == -1) {
        totalComments // last index
      } else {
        index
      }
      list.add(lastIndex, comment)
      _mComments.postValue(list)

      viewModelScope.launch {
        withContext(Dispatchers.Main) {
          totalComments += 1
        }
      }
    }

    val list: MutableList<Comment> = ArrayList(_mComments.value!!)
    list.removeAt(position)
    _mComments.postValue(list)

    totalComments -= 1

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.deleteComment(authorization, comment.id)
      if (!response.isSuccessful) {
        // not found
        if (response.code() == 404) {
          return@launch
        }

        withContext(Dispatchers.Main) {
          emitRequestError(response.errorBody(), response.code(), false)
          totalComments += 1
        }
      }
    }
  }

  fun toggleVote(comment: Comment, likeButton: LikeButton, numberOfLikes: TextView) {
    val liked: Boolean = comment.isLiked
    setLikeButtonStatus(likeButton, !liked, false)

    val handler = CoroutineExceptionHandler { _, _ ->
      viewModelScope.launch(Dispatchers.Main) {
        setLikeButtonStatus(likeButton, liked)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.voteComment(authorization, comment.id)
      if (response.isSuccessful) {
        withContext(Dispatchers.Main) {
          val updateComment = response.body()?.comment
          updateComment?.let {
            comment.isLiked = it.isLiked
            comment.likesCount = it.likesCount
            numberOfLikes.text = it.likesCount
          }
          likeButton.isEnabled = true
          numberOfLikes.visibility = if (updateComment?.likesCount == null) {
            View.GONE
          } else {
            View.VISIBLE
          }
        }
      } else {
        withContext(Dispatchers.Main) {
          setLikeButtonStatus(likeButton, liked, true)
        }
      }
    }
  }

  fun loadMoreComments() {
    val page: Int = meta?.page ?: 1
    commentsList(page + 1, true)
  }

  private fun placeHolder(): Comment = Comment().apply { id = -1L }

  // Loading
  private fun loadingPlaceHolder(): Comment = Comment().apply { id = -2L }

  fun shouldShowError(): Boolean {
    return if (isPostShow) {
      totalComments <= 1
    } else {
      totalComments == 0
    }
  }

  fun togglePostVote(post: Post, upvote: LikeButton) {

  }

//
//    val audioFile = File(audioRecorder.outputFile)
//    val requestFile = RequestBody.create(MediaType.parse("audio/*"), audioFile)
//    val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)
}