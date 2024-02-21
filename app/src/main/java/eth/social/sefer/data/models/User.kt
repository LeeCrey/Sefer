package eth.social.sefer.data.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.annotation.JsonSetter
import eth.social.sefer.MyApp
import eth.social.sefer.R

@JsonRootName("user")
@JsonInclude(JsonInclude.Include.NON_NULL)
class User() : Parcelable {
  @JsonProperty("id")
  var id: Long = 0

  @JsonProperty("full_name")
  var fullName: String? = null

  @JsonProperty("profile_picture")
  var profileUrl: String? = null

  @JsonProperty("cover_picture")
  val coverUrl: String? = null

  var username: String? = null

  @JsonAlias("is_follow")
  var following: Boolean = false

  @JsonProperty("verified")
  var verified: Boolean = false

  @JsonProperty("email")
  var email: String? = null

  @JsonProperty("password")
  var password: String? = null

  @JsonProperty("password_confirmation")
  var passwordConfirmation: String? = null

  @JsonProperty("current_password")
  var currentPassword: String? = null

  @JsonProperty("country")
  var country: String? = null

  @JsonProperty("gender")
  var gender: String? = null

  @JsonProperty("biography")
  var bio: String? = null

  @JsonProperty("followers_count")
  val followersCount: Int? = null

  @JsonProperty("followings_count")
  val followingCount: Int? = null

  @JsonProperty("posts_count")
  val postsCount: Int? = null

  constructor(parcel: Parcel) : this() {
    id = parcel.readLong()
    fullName = parcel.readString()
    profileUrl = parcel.readString()
    username = parcel.readString()
    following = parcel.readByte() != 0.toByte()
    verified = parcel.readByte() != 0.toByte()
    bio = parcel.readString()
  }

  @JsonSetter("username")
  fun userName(uName: String?) {
    uName?.let {
      this.username = MyApp.instance.getString(R.string.username, it)
    }
  }

  @JsonGetter("username")
  fun userName(): String? {
    return username
  }

  @JsonSetter("following")
  fun followStatus(isFollowings: Boolean = false) {
    following = isFollowings
  }

  fun areContentSame(user: User): Boolean {
    return username == user.username &&
        following == user.following &&
        fullName == user.fullName &&
        verified == user.verified &&
        profileUrl == user.profileUrl
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeLong(id)
    parcel.writeString(fullName)
    parcel.writeString(profileUrl)
    parcel.writeString(username)
    parcel.writeByte(if (following) 1 else 0)
    parcel.writeByte(if (verified) 1 else 0)
    parcel.writeString(bio)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<User> {
    override fun createFromParcel(parcel: Parcel): User {
      return User(parcel)
    }

    override fun newArray(size: Int): Array<User?> {
      return arrayOfNulls(size)
    }
  }
}