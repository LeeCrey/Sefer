package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.Community


class CommunityResponse : ApiResponse() {
  @JsonProperty("community")
  val community: Community? = null

  @JsonProperty("communities")
  val communities: MutableList<Community>? = null
}