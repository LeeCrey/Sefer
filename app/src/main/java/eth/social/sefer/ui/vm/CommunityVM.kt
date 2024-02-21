package eth.social.sefer.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.Community
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.CommunityRepo
import kotlinx.coroutines.flow.SharedFlow

class CommunityVM(url: String) : ViewModel() {
  private val repo: CommunityRepo = CommunityRepo(viewModelScope, url)
  val communities: LiveData<List<Community>> = repo.communities
  val response: SharedFlow<ApiResponse> = repo.response
  var isLoaded: Boolean = false

  init {
    communityList(null, false)
  }

  fun communityList(page: Int?, append: Boolean) = repo.list(page, append)

  fun joinCommunity(community: Community) = repo.joinCommunity(community)

  fun loadMore() = repo.loadMore()

  fun isNextAvailable(): Boolean = repo.isNextAvailable()
}