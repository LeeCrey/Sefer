package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

// non null
@JsonRootName("category")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Category {
  @JsonProperty("id")
  var categoryId: Long? = null

  @JsonProperty("name")
  var name: String? = null

  @get:JsonIgnore
  @JsonProperty("amharic")
  var amharic: String? = null

  var isSelected = false
}