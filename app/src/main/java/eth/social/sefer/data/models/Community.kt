package eth.social.sefer.data.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonSetter
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.lib.txtdrawable.TextDrawable
import eth.social.sefer.lib.txtdrawable.ColorGenerator

@JsonRootName("community")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Community() : Parcelable {
  @JsonProperty("id")
  var id: Long = 0

  @JsonProperty("name")
  var name: String? = null

  @JsonProperty("description")
  var description: String? = null

  var membersCount: String? = null

  @JsonProperty("profile")
  var profile: String? = null

  @JsonIgnore
  var coverUri: Uri? = null

  @JsonProperty("community_type")
  var type: String? = null

  @JsonProperty("is_owner")
  var isOwner: Boolean = false

  @JsonIgnore
  lateinit var placeholder: TextDrawable

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    name = parcel.readString()
    description = parcel.readString()
    profile = parcel.readString()
    type = parcel.readString()
    isOwner = parcel.readByte() != 0.toByte()
  }

  @JsonSetter("name")
  fun nameSetter(name: String) {
    this.name = name

    val color = ColorGenerator.MATERIAL.randomColor
    placeholder = TextDrawable.builder()
      .beginConfig()
      .width(40)
      .height(40)
      .endConfig()
      .buildRect(name.substring(0, 2), color)
  }

  @JsonSetter("members_count")
  fun membersSetter(count: Long) {
    if (count == 1L) {
      membersCount = MyApp.instance.getString(R.string.member, count)
    } else {
      membersCount = MyApp.instance.getString(R.string.members, count)
    }
  }

  override fun equals(other: Any?): Boolean {
    val newC = other as Community

    return id == newC.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeString(name)
    parcel.writeString(description)
    parcel.writeString(profile)
    parcel.writeString(type)
    parcel.writeByte(if (isOwner) 1 else 0)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Community> {
    override fun createFromParcel(parcel: Parcel): Community {
      return Community(parcel)
    }

    override fun newArray(size: Int): Array<Community?> {
      return arrayOfNulls(size)
    }
  }
}