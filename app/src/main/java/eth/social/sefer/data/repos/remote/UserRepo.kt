package eth.social.sefer.data.repos.remote

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.data.apis.UserApi
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UserRepo(
  private val viewModelScope: CoroutineScope,
  private val url: String
) : ApplicationRepo() {
  private var _mUsers: MutableLiveData<List<User>> = MutableLiveData()
  private var api: UserApi = retrofitInstance.create(UserApi::class.java)

  val users: LiveData<List<User>> get() = _mUsers

  private var totalUsers = 0

  // possible requests. 1 indicates sample id. not v1
  //   1) api/v1/users
  //   2) api/v1/users/1/followers => return people who follows the given users
  //   3) api/v1/users/1/followings => return people who are followed by the given users
  //   4) api/v1/posts/1/users => return people who liked the post
  //   5) api/v1/comments/1/users => returns users who liked the comment
  //   6) api/v1/replies/1/users => returns people who liked the replies
  //   7) api/v1/blocked_users => return blocked users by current user
  fun listOfUsers(page: Int?, append: Boolean) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      if (!isInternetAvailable()) {
        delay(1_000)
      }

      val response = api.users(url, authorization, page)
      if (response.isSuccessful) {
        meta = response.body()?.meta

        val list: MutableList<User> = response.body()?.usersList!!

        if (append) {
          val currentList: MutableList<User> = ArrayList(users.value!!)
          currentList.removeAt(totalUsers)
          list.addAll(0, currentList)
        }

        val data = meta!!
        if (append) {
          totalUsers += data.count
        } else {
          totalUsers = data.count
        }

        if (data.isNextPageAvailable) {
          list.add(placeholder)
        }

        _mUsers.postValue(list)
      } else {
        emitRequestError(response.errorBody()!!, response.code(), true)
      }
    }
  }

  fun followOrUnfollow(user: User, btn: TextView) {
    btn.isEnabled = false

    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch(Dispatchers.Main) {
        btn.isEnabled = true

        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = if (user.following) {
        api.unfollow(authorization, user.id)
      } else {
        api.follow(authorization, user.id)
      }

      // run in ui thread
      withContext(Dispatchers.Main) {
        if (response.isSuccessful) {
          user.following = !user.following
          btn.text = MyApp.instance.getString(
            if (user.following) {
              R.string.unfollow
            } else {
              R.string.follow
            }
          )
        } else {
          emitRequestError(response.errorBody(), response.code())
        }
        btn.isEnabled = true
      }
    }
  }

  fun unblockUser(user: User) {
    val handler = CoroutineExceptionHandler { _, _ ->
//            val list: MutableList<User> = ArrayList(users.value!!)
      // add in the place just like comment
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val list: MutableList<User> = ArrayList(users.value!!)
      list.remove(user)
      _mUsers.postValue(list)

      val response = api.unblock(authorization, user.id)
      if (!response.isSuccessful) {
        emitRequestError(response.errorBody(), response.code())
      }
    }
  }

  // Only for search fragment since it uses this repo
  fun searchUser(query: String, page: Int?) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch(Dispatchers.Main) {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.searchUser(authorization, query, page)

      if (response.isSuccessful) {
        _mUsers.postValue(response.body()?.usersList)
      }
    }
  }

  fun loadMore() {
    listOfUsers(meta!!.next, true)
  }

  private val placeholder: User = User().apply { id = -1L }
}