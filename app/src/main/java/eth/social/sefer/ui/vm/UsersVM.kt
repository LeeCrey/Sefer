package eth.social.sefer.ui.vm

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.UserRepo
import kotlinx.coroutines.flow.SharedFlow

class UsersVM(url: String) : ViewModel() {
  private val repo: UserRepo = UserRepo(viewModelScope, url)
  val users: LiveData<List<User>> = repo.users
  val response: SharedFlow<ApiResponse> = repo.response
  var isLoaded: Boolean = false

  init {
    listOfUsers(1, false)
  }

  fun listOfUsers(page: Int, append: Boolean) = repo.listOfUsers(page, append)

  fun followOrUnfollow(user: User, btn: TextView, pos: Int) = repo.followOrUnfollow(user, btn)

  fun searchUser(query: String, page: Int?, append: Boolean) =
    repo.searchUser(query, page)

  fun unblockUser(user: User) = repo.unblockUser(user)

  fun loadMoreUsers() = repo.loadMore()

  fun isNextAvailable(): Boolean = repo.isNextAvailable()

}