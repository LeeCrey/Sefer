package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonSetter

@JsonRootName("reply")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Reply {
  @JsonProperty("id")
  var id: Long? = null

  @JsonProperty("user_id")
  var userId: Long? = null

  @JsonProperty("content")
  var content: String? = null

  @JsonProperty("votes_count")
  var likesCount: Int? = null

  @JsonProperty("user")
  var user: User? = null

  var createdAt: Long? = null

  @JsonSetter("created_at")
  fun timeAgo(created: Long) {
    createdAt = created
  }
}