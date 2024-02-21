package eth.social.sefer.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.Community
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.OperationRepo
import kotlinx.coroutines.flow.SharedFlow

open class OperationVM : ViewModel() {
  protected val repo: OperationRepo = OperationRepo(viewModelScope)
  val response: SharedFlow<ApiResponse> = repo.response

  fun requestInstruction(email: String, url: String) = repo.requestInstructionLink(email, url)

  fun logout() = repo.logout()

  fun createCommunity(community: Community) = repo.createCommunity(community)


  // post
  fun changePassword(student: User) = repo.changePassword(student)

  fun updateCommunity(id: Long, community: Community) = repo.updateCommunity(id, community)
}