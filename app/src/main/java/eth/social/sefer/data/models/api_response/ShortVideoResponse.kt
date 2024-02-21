package eth.social.sefer.data.models.api_response

import com.fasterxml.jackson.annotation.JsonProperty
import eth.social.sefer.data.models.ShortVideo

class ShortVideoResponse : ApiResponse() {
  @JsonProperty("short_video")
  val video: ShortVideo? = null

  @JsonProperty("short_videos")
  val videos: MutableList<ShortVideo>? = null
}