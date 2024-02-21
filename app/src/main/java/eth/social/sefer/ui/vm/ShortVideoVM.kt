package eth.social.sefer.ui.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.like_button.LikeButton
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.ShortVideoRepo
import kotlinx.coroutines.flow.SharedFlow

class ShortVideoVM(url: String) : ViewModel() {
  private var repo: ShortVideoRepo = ShortVideoRepo(url, viewModelScope)
  var shortVideos: LiveData<List<ShortVideo>> = repo.shortVideos
  val response: SharedFlow<ApiResponse> = repo.response

  init {
    listOfVideos(null, false)
  }

  fun listOfVideos(page: Int?, append: Boolean) = repo.listOf(page, append)

  fun voteVideo(likeButton: LikeButton, video: ShortVideo) = repo.toggleVote(likeButton, video)

  fun uploadVideo(uri: Uri) = repo.uploadVideo(uri)

  fun isNextAvailable(): Boolean = repo.isNextAvailable()

  fun loadMore() = repo.loadMore()

  fun page(): Int = repo.page()

}