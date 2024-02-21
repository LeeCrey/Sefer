package eth.social.sefer.data.repos.remote

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.sefer.data.apis.UserApi
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileRepo(
  private val viewModelScope: CoroutineScope,
  private val userToShow: User
) : ApplicationRepo() {
  private val api: UserApi = retrofitInstance.create(UserApi::class.java)
  private val mUser: MutableLiveData<User> = MutableLiveData(userToShow)

  val user: LiveData<User> = mUser

  fun loadUser() {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.showUser(authorization, userToShow.id)
      if (response.isSuccessful) {
        mUser.postValue(response.body()?.user)
      } else {
        emitRequestError(response.errorBody(), response.code(), true)
      }
    }
  }

  fun changeCoverPicture(uri: Uri) {

  }
}
