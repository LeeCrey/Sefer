package eth.social.sefer.data.apis

import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface RegistrationApi {
  @PATCH("users")
  suspend fun updateInfo(
    @Header("Authorization") authorization: String?,
    @Body user: User
  ): Response<UserResponse>

  @POST("users")
  suspend fun signUp(@Body user: User): Response<UserResponse>
}