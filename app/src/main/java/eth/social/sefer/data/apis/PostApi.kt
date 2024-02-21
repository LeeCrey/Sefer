package eth.social.sefer.data.apis

import eth.social.sefer.data.models.api_response.PostResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PostApi {
  @GET
  suspend fun listOfPost(
    @Url url: String,
    @Header("Authorization") authorization: String?,
    @Query("page") page: Int?
  ): Response<PostResponse>

  @DELETE("api/v1/posts/{id}")
  suspend fun deletePost(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<PostResponse>

  @POST("api/v1/posts/{id}")
  suspend fun reportPost(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<PostResponse>

  @POST("api/v1/posts/{id}/vote")
  suspend fun votePost(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<PostResponse>
}