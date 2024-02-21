package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Post

class PostResponse : ApiResponse() {
  @JsonProperty("post")
  val post: Post? = null

  @JsonProperty("posts")
  val posts: MutableList<Post>? = null

  // for post show when though deep link
  @JsonProperty("comments")
  var comments: MutableList<Comment>? = null
}