package eth.social.sefer.data.apis

import eth.social.sefer.data.models.api_response.ShortVideoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ShortVideoApi {
  @Multipart
  @POST("api/v1/short_videos")
  suspend fun uploadVideo(
    @Header("Authorization") authorization: String?,
    @Part("short_video") video: MultipartBody,
  ): Response<ShortVideoResponse>

  @GET
  suspend fun listOf(
    @Url url: String,
    @Header("Authorization") authorization: String?,
    @Query("page") page: Int?
  ): Response<ShortVideoResponse>

  @POST("api/v1/short_videos/{id}/vote")
  suspend fun toggleLike(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<ShortVideoResponse>

  @DELETE("api/v1/short_videos/{id}")
  suspend fun destroy(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<ShortVideoResponse>
}