package eth.social.sefer.data.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonSetter
import java.util.UUID

// Polymorphic is in server side.
// possible commentable_type -> Post, ShortVideo
@JsonRootName("comment")
@JsonInclude(JsonInclude.Include.NON_NULL)
open class Comment() : Cloneable, Parcelable {
  @JsonIgnore
  var id: Long = 0

  @JsonProperty("commentable_id")
  var commentableId: Long? = null

  @JsonProperty("user_id")
  var userId: Long? = null

  @JsonProperty("content")
  var content: String? = null

  @JsonIgnore
  var createdAt: Long? = null

  @JsonProperty("votes_count")
  var likesCount: String? = null

  @JsonProperty("replies_count")
  var repliesCount: String? = null

  @JsonProperty("image_url")
  val imageUrl: String? = null

  @JsonProperty("user")
  var user: User? = null

  @JsonIgnore
  var isLiked: Boolean = false

  @JsonIgnore
  var uid: UUID? = null

  @JsonProperty("commentable_type")
  var type: String? = null

  @JsonSetter("id")
  fun idJsonSetter(identifier: Long) {
    this.id = identifier
  }

  @JsonSetter("created_at")
  fun createdAtJsonSetter(created: Long) {
    createdAt = created * 1_000L
  }

  @JsonSetter("voted")
  fun voteStatusJsonSetter(liked: Boolean) {
    this.isLiked = liked
  }

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    commentableId = parcel.readValue(Long::class.java.classLoader) as? Long
    userId = parcel.readValue(Long::class.java.classLoader) as? Long
    content = parcel.readString()
    createdAt = parcel.readValue(Long::class.java.classLoader) as? Long
    likesCount = parcel.readString()
    repliesCount = parcel.readString()
    isLiked = parcel.readByte() != 0.toByte()
    type = parcel.readString()
  }

  fun areContentTheSame(newItem: Comment): Boolean {
    return likesCount == newItem.likesCount && repliesCount == newItem.repliesCount
  }

  val shouldSeparatorBeVisible: Boolean
    get() {
      return likesCount != null && repliesCount != null
    }

  override fun equals(other: Any?): Boolean {
    val otherComment = other as Comment

    return id == otherComment.id && uid == other.uid
  }

  public override fun clone(): Comment {
    return try {
      super.clone() as Comment
    } catch (e: CloneNotSupportedException) {
      throw AssertionError()
    }
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + (commentableId?.hashCode() ?: 0)
    return result
  }


  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeValue(commentableId)
    parcel.writeValue(userId)
    parcel.writeString(content)
    parcel.writeValue(createdAt)
    parcel.writeString(likesCount)
    parcel.writeString(repliesCount)
    parcel.writeByte(if (isLiked) 1 else 0)
    parcel.writeString(type)
  }

  companion object CREATOR : Parcelable.Creator<Comment> {
    override fun createFromParcel(parcel: Parcel): Comment {
      return Comment(parcel)
    }

    override fun newArray(size: Int): Array<Comment?> {
      return arrayOfNulls(size)
    }
  }
}