package eth.social.sefer.data.apis

import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.CommunityResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface CommunityApi {
  @GET
  suspend fun communityList(
    @Url url: String,
    @Header("Authorization") authorization: String?,
    @Query("page") page: Int?
  ): Response<CommunityResponse>

  @DELETE("api/v1/communities/{id}")
  suspend fun deleteCommunity(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<CommunityResponse>

  @POST("api/v1/communities/{id}/join")
  suspend fun joinCommunity(
    @Header("Authorization") authorization: String?,
    @Path("id") id: Long
  ): Response<ApiResponse>
}