package eth.social.sefer.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.Reply
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.ReplyRepo
import kotlinx.coroutines.flow.SharedFlow

class ReplyVM(
  commentId: Long
) : ViewModel() {
  var isLoaded: Boolean = false
  private val repo = ReplyRepo(commentId, viewModelScope)
  val replies: MutableLiveData<List<Reply>> = repo.replies
  val response: SharedFlow<ApiResponse> = repo.response

  init {
    loadReplies(null)
  }

  fun loadReplies(page: Int?, append: Boolean = false) = repo.loadReplies(page, append)

  fun deleteReply(reply: Reply) = repo.deleteReply(reply)

  fun updateReply(reply: Reply) = repo.updateReply(reply)

  fun voteReply(reply: Reply) = repo.voteReply(reply)

  fun loadMore() = repo.loadMore()

  val isNextAvailable: Boolean = repo.isNextAvailable()
}