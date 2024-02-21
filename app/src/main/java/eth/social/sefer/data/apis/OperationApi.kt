package eth.social.sefer.data.apis

import eth.social.sefer.data.models.Community
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.PostResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface OperationApi {
  @POST("api/v1/posts")
  suspend fun createPost(
    @Header("Authorization") authorization: String?,
    @Body post: Post
  ): Response<PostResponse>

  @PATCH("api/v1/posts/{id}")
  suspend fun updatePost(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long,
    @Body post: Post
  ): Response<ApiResponse>

  @POST
  suspend fun requestLink(@Url link: String, @Body user: User): Response<ApiResponse>

  @PATCH("students")
  suspend fun changePassword(
    @Header("Authorization") authorization: String?,
    @Body user: User
  ): Response<ApiResponse>

  @DELETE("users/logout")
  suspend fun logout(@Header("Authorization") authorization: String?): Response<ApiResponse>

  @POST("api/v1/communities")
  suspend fun createCommunity(
    @Header("Authorization") authorization: String?,
    @Body community: Community
  ): Response<ApiResponse>

  @POST("api/v1/fcm/token")
  fun sendPostNotificationToken(
    @Header("Authorization") authorization: String?,
    @Query("token") token: String
  ): Call<ApiResponse>

  @PATCH("api/v1/communities/{id}")
  suspend fun updateCommunity(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long,
    @Body community: Community
  ): Response<ApiResponse>

  @POST("api/v1/communities/{id}/posts")
  suspend fun createCommunityPost(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long,
    @Body post: Post
  ): Response<PostResponse>
}