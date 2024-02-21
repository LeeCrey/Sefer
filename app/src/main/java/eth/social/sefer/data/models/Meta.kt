package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

// for pagination
@JsonInclude(JsonInclude.Include.NON_NULL)
class Meta {
  @JsonProperty("count")
  val count: Int = 0

  @JsonProperty("page")
  val page: Int? = null

  @JsonProperty("next")
  val next: Int? = null

  @JsonProperty("prev")
  val prev: Int? = null

  val isNextPageAvailable: Boolean
    get() = next != null
}