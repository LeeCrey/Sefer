package eth.social.sefer.data.apis

import eth.social.sefer.data.models.api_response.ReplyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ReplyApi {
  @GET("api/v1/comments/{id}/replies")
  suspend fun loadReplies(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long,
    @Query("page") page: Int?
  ): Response<ReplyResponse>
}