package eth.social.sefer.data.repos.remote

import eth.social.sefer.data.apis.RegistrationApi
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.UserResponse
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RegistrationRepo(
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val api: RegistrationApi = retrofitInstance.create(RegistrationApi::class.java)

  fun register(user: User) = viewModelScope.launch(Dispatchers.IO + handler) {
    mResponse.emit(finishRequest(api.signUp(user)))
  }

  fun updateBio(newBio: String) = viewModelScope.launch(Dispatchers.IO + handler) {
    val user = User().apply { bio = newBio }
    mResponse.emit(finishRequest(api.updateInfo(authorization, user)))
  }

  private fun finishRequest(response: Response<UserResponse>): ApiResponse {
    return if (response.isSuccessful) {
      response.body()!!
    } else {
      parseRequestResponse(response.errorBody()!!).also { it.okay = false }
    }
  }

  private val handler = CoroutineExceptionHandler { _, t ->
    viewModelScope.launch { setApiResponse(t, null) }
  }
}