package eth.social.sefer.data.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("short_video")
class ShortVideo() : Parcelable {
  @JsonProperty("id")
  var id: Long = 0

  @JsonProperty("video_url")
  var url: String? = null

  @JsonProperty("user")
  lateinit var user: User

  @JsonProperty("thumbnail")
  var thumbnail: String? = null

  @JsonProperty("is_voted")
  var isLiked: Boolean = false

  @JsonProperty("votes_count")
  var likesCount: Int = 0

  @JsonProperty("comments_count")
  var numberOfComments: Int = 0

  @JsonProperty("created_at")
  var createdAt: Long = 0

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    url = parcel.readString()
    thumbnail = parcel.readString()
    isLiked = parcel.readByte() != 0.toByte()
    likesCount = parcel.readInt()
    numberOfComments = parcel.readInt()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeString(url)
    parcel.writeString(thumbnail)
    parcel.writeByte(if (isLiked) 1 else 0)
    parcel.writeInt(likesCount)
    parcel.writeInt(numberOfComments)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ShortVideo> {
    override fun createFromParcel(parcel: Parcel): ShortVideo {
      return ShortVideo(parcel)
    }

    override fun newArray(size: Int): Array<ShortVideo?> {
      return arrayOfNulls(size)
    }
  }
}