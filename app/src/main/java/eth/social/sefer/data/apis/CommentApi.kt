package eth.social.sefer.data.apis

import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.api_response.CommentResponse
import eth.social.sefer.data.models.api_response.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

// possible urls for GET and POSt
// api/v1/posts/:id/comments
// api/v1/videos/:id/comments
interface CommentApi {
  @GET
  suspend fun listOfComment(
    @Url string: String,
    @Header("Authorization") authorization: String?,
    @Query("page") page: Int?
  ): Response<CommentResponse>

  @POST
  suspend fun createComment(
    @Url string: String,
    @Header("Authorization") authorization: String?,
    @Body comment: Comment
  ): Response<CommentResponse>

  @DELETE("api/v1/comments/{id}")
  suspend fun deleteComment(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<CommentResponse>

  @POST("api/v1/comments/{id}/vote")
  suspend fun voteComment(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<CommentResponse>

  @PATCH("api/v1/comments/{id}")
  suspend fun updateComment(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long,
    @Body comment: Comment
  ): Response<CommentResponse>

  // POST API
  @GET("api/v1/posts/{id}")
  suspend fun showPost(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<PostResponse>
}