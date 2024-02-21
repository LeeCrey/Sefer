package eth.social.sefer.data.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonSetter

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName("post")
open class Post() : Parcelable, Cloneable {
  var likesCount: String? = null

  @JsonIgnore
  var id: Long = 0

  // end of tmp
  @JsonProperty("thumbnail")
  var thumb: String? = null

  @JsonIgnore
  var isVoted: Boolean? = null

  @JsonProperty("content")
  var content: String? = null

  @JsonIgnore
  var votesCount: Long = 0

  @JsonAlias("user")
  var user: User? = null

  @JsonIgnore
  var commentsCount: Long = 0

  @JsonProperty("image")
  var photoUrl: String? = null

  @JsonIgnore
  var createdAt: Long? = null

  @JsonProperty("user_id")
  var userId: Long? = null

  @JsonProperty("updated_at")
  var updatedAt: Long? = null

  @JsonProperty("community_id")
  var communityId: Long? = null

  @JsonProperty("community")
  var community: Community? = null

  @JsonProperty("tag_list")
  var hashTags: String? = null

  @JsonSetter("comments_count")
  fun commentsCountJsonSetter(count: Long) {
    this.commentsCount = count
  }

  @JsonSetter("votes_count")
  fun votesCountJsonSetter(count: Long) {
    this.votesCount = count
  }

  @JsonSetter("id")
  fun idJsonSetter(identifier: Long) {
    id = identifier
  }

  @JsonSetter("is_voted")
  fun voteStatus(status: Boolean) {
    isVoted = status
  }

  @JsonSetter("created_at")
  fun timeAgo(ago: Long) {
    createdAt = ago * 1_000
  }

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    thumb = parcel.readString()
    isVoted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    content = parcel.readString()
    votesCount = parcel.readLong()
    user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      parcel.readParcelable(User::class.java.classLoader, User::class.java)
    } else {
      parcel.readParcelable(User::class.java.classLoader)
    }
    commentsCount = parcel.readLong()
    photoUrl = parcel.readString()
    createdAt = parcel.readValue(Long::class.java.classLoader) as? Long
    userId = parcel.readValue(Long::class.java.classLoader) as? Long
    updatedAt = parcel.readValue(Long::class.java.classLoader) as? Long
    communityId = parcel.readValue(Long::class.java.classLoader) as? Long
    hashTags = parcel.readString()
  }

  val isCommentAndPostPositive: Boolean
    get() {
      return commentsCount > 0 && votesCount > 0
    }

  // no need to compare content.  only updated at is enough for that
  fun areContentTheSame(newItem: Post): Boolean {
    val contSame = updatedAt == newItem.updatedAt
    val vSame = votesCount == newItem.votesCount
    val vtd = isVoted == newItem.isVoted
    val commentsCountSame = commentsCount == newItem.commentsCount

    return contSame && vSame && vtd && commentsCountSame
  }

  public override fun clone(): Post {
    return try {
      super.clone() as Post
    } catch (e: CloneNotSupportedException) {
      throw AssertionError()
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeString(thumb)
    parcel.writeValue(isVoted)
    parcel.writeString(content)
    parcel.writeLong(votesCount)
    parcel.writeParcelable(user, flags)
    parcel.writeLong(commentsCount)
    parcel.writeString(photoUrl)
    parcel.writeValue(createdAt)
    parcel.writeValue(userId)
    parcel.writeValue(updatedAt)
    parcel.writeValue(communityId)
    parcel.writeString(hashTags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Post> {
    override fun createFromParcel(parcel: Parcel): Post {
      return Post(parcel)
    }

    override fun newArray(size: Int): Array<Post?> {
      return arrayOfNulls(size)
    }
  }

}