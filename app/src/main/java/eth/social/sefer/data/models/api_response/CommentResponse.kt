package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.Comment

class CommentResponse : ApiResponse() {
  @JsonProperty("comment")
  val comment: Comment? = null

  @JsonProperty("comments")
  val comments: MutableList<Comment>? = null
}