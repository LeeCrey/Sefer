package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.Product

class ProductResponse : ApiResponse() {
  @JsonProperty("product")
  val product: Product? = null

  @JsonProperty("products")
  val products: MutableList<Product>? = null
}
