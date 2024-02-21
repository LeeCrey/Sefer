package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.Meta


@JsonInclude(JsonInclude.Include.NON_NULL)
open class ApiResponse {
  @JsonProperty("okay")
  var okay = false

  @JsonProperty("_message")
  @JsonAlias("reason", "error")
  var message: String? = null

  @JsonProperty("token")
  var token: String? = null

  @JsonProperty("metadata")
  val meta: Meta? = null

  var unauthorized: Boolean = false
}