package eth.social.sefer.data.apis

import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SessionApi {
  @POST("users/login")
  suspend fun login(@Body user: User): Response<LoginResponse>

  @POST("users/login")
  suspend fun login(@Query("Token") token: String?): Response<LoginResponse>

  @DELETE("users/logout")
  suspend fun logout(@Header("Authorization") authorization: String): Response<ApiResponse>
}