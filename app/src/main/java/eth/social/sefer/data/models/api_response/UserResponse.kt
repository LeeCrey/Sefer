package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.User

class UserResponse : ApiResponse() {
  @JsonProperty("users")
  val usersList: MutableList<User>? = null

  @JsonProperty("user")
  val user: User? = null
}