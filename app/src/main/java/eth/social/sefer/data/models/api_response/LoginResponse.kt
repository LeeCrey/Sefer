package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.User

class LoginResponse : ApiResponse() {
  @JsonProperty("user")
  val user: User? = null
}