package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("tag")
class HashTag {
  @JsonIgnore
  var id: Int? = null

  @JsonProperty("name")
  var name: String? = null

  override fun toString(): String {
    return name!!
  }

  override fun equals(other: Any?): Boolean {
    if (other is HashTag) {
      return name == other.name
    }

    return false
  }

  override fun hashCode(): Int {
    return name!!.hashCode()
  }
}