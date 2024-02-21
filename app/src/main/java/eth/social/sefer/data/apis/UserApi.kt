package eth.social.sefer.data.apis

import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.UserResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface UserApi {
  @GET
  suspend fun users(
    @Url url: String,
    @Header("Authorization") authorization: String?,
    @Query("page") page: Int?
  ): Response<UserResponse>

  @POST("api/v1/users/{id}/follow")
  suspend fun follow(
    @Header("Authorization") authorization: String?, @Path("id") id: Long
  ): Response<UserResponse>

  @DELETE("api/v1/users/{id}/unfollow")
  suspend fun unfollow(
    @Header("Authorization") authorization: String?, @Path("id") id: Long
  ): Response<UserResponse>

  @GET("api/v1/users/search")
  suspend fun searchUser(
    @Header("Authorization") authorization: String?,
    @Query("q") query: String,
    @Query("page") page: Int?
  ): Response<UserResponse>

  @GET("api/v1/users/{id}")
  suspend fun showUser(
    @Header("Authorization") authorization: String?,
    @Path("id") userId: Long
  ): Response<UserResponse>

  @DELETE("api/v1/users/{id}/unblock")
  suspend fun unblock(
    @Header("Authorization") authorization: String?,
    @Path("id") userId: Long
  ): Response<ApiResponse>
}