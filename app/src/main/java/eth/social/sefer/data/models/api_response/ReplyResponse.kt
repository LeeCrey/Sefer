package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.Reply

class ReplyResponse : ApiResponse() {
  @JsonProperty("reply")
  val reply: Reply? = null

  @JsonProperty("replies")
  val replies: MutableList<Reply>? = null
}