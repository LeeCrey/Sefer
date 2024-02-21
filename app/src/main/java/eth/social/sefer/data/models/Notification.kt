package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("notification")
class Notification : Cloneable {
  @JsonProperty("id")
  val id: Long? = null

  @JsonProperty("action_type")
  val type: String? = null

  @JsonProperty("message")
  val message: String? = null

  @JsonProperty("user")
  private val user: User? = null

  @JsonProperty("read")
  var isRead = false

  public override fun clone(): Notification {
    return try {
      super.clone() as Notification
    } catch (e: CloneNotSupportedException) {
      throw AssertionError()
    }
  }
}