package eth.social.sefer.data.apis

import eth.social.sefer.data.models.api_response.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ProductApi {
  @GET("api/v1/products")
  suspend fun loadProducts(
    @Header("Authorization") authorization: String?,
    @Query("page") page: Int?
  ): Response<ProductResponse>
}