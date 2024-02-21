package eth.social.sefer.data.repos.remote

import eth.social.sefer.data.apis.SessionApi
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.LoginResponse
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginRepo(
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val api: SessionApi = retrofitInstance.create(SessionApi::class.java)

  // vars
  private val handler = CoroutineExceptionHandler { _, t ->
    viewModelScope.launch {
      setApiResponse(t, null)
    }
  }

  fun login(token: String?) =
    viewModelScope.launch(Dispatchers.IO + handler) { loginSendRequest(api.login(token)) }

  fun login(user: User) =
    viewModelScope.launch(Dispatchers.IO + handler) { loginSendRequest(api.login(user)) }

  // should be called from coroutine
  private suspend fun loginSendRequest(response: Response<LoginResponse>) {
    if (response.isSuccessful) {
      val loginResponse = response.body()

      // do in ui thread
      // TODO: INSERT INTO DATABASE
      withContext(Dispatchers.Main) {
        loginResponse?.let {
          PrefHelper.putUser(it.user!!)
          PrefHelper.putAuthToken(response.headers()["Authorization"])
        }
      }

      val apiResp = ApiResponse().apply {
        okay = true
        message = loginResponse!!.message
      }

      mResponse.emit(apiResp)
    } else {
      val loginError: ApiResponse = parseRequestResponse(response.errorBody()!!).also {
        it.okay = false
        it.unauthorized = response.code() == unauthorizedCode
      }

      mResponse.emit(loginError)
    }
  }
}