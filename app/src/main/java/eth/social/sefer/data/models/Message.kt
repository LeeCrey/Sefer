package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import java.text.SimpleDateFormat
import java.util.Date

@JsonRootName("message")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Message {
  @JsonProperty("id")
  var id: Long = 0

  @JsonProperty("body")
  var body: String? = null

  @JsonProperty("chat_id")
  var chatId = 0

  @JsonProperty("student_id")
  var studentId: Long = 0

  @JsonProperty("sender_full_name")
  var senderFullName: String? = null

  @JsonProperty("sender_profile_url")
  var senderProfileUrl: String? = null

  @JsonProperty("created_at")
  var createdAt: Long? = null
    private set

  @JsonIgnore
  var isSentDate = false

  @JsonProperty("created_at")
  fun setCreatedAt(createdAt: Long) {
    this.createdAt = 1000 * createdAt
  }

  fun getFormattedDate(format: SimpleDateFormat): String {
    val date = Date(createdAt!!)
    return format.format(date)
  }
}