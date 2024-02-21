package eth.social.sefer.data.models

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

// TODO: change vars to val
@JsonRootName("product")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class Product() : Parcelable {
  @JsonProperty("id")
  var id: Long? = null

  @JsonProperty("quantity")
  val quantity: Int? = null

  // name
  @JsonProperty("name")
  var name: String? = null

  // rate
  @JsonProperty("rates")
  val rate: Float? = null

  @JsonProperty("price")
  var price: Double? = null

  @JsonProperty("discount")
  val discount: Double = 0.0

  @get:JsonIgnore
  @JsonProperty("photos")
  val images: List<String>? = null

  @JsonProperty("origin")
  val origin: String? = null

  @JsonProperty("description")
  val description: String? = null

  @JsonProperty("shop_id")
  val shop: Long? = null

  @JsonProperty("numberOfRates")
  val numberOfRates: Int? = null

  @JsonProperty("total_votes")
  val votes: Long? = null

  @JsonProperty("thumb_nail")
  var thumbNail: String? = null

  constructor(parcel: Parcel) : this() {
    id = parcel.readValue(Long::class.java.classLoader) as? Long
    name = parcel.readString()
    price = parcel.readValue(Double::class.java.classLoader) as? Double
    thumbNail = parcel.readString()
  }

  override fun toString(): String {
    return name!!
  }

  fun isContentTheSame(newProduct: Product): Boolean {
    val nameTheSame = name == newProduct.name
    val priceSame = price == newProduct.price
    val idSame = id == newProduct.id
    return nameTheSame && priceSame && idSame
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeValue(id)
    parcel.writeString(name)
    parcel.writeValue(price)
    parcel.writeString(thumbNail)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Product> {
    override fun createFromParcel(parcel: Parcel): Product {
      return Product(parcel)
    }

    override fun newArray(size: Int): Array<Product?> {
      return arrayOfNulls(size)
    }
  }
}