package eth.social.sefer.ui.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.User
import eth.social.sefer.data.repos.remote.ProfileRepo

class ProfileVM(
  user: User
) : ViewModel() {
  val repo = ProfileRepo(viewModelScope, user)
  val response = repo.response
  val user: LiveData<User> = repo.user

  init {
    loadUser()
  }

  fun loadUser() = repo.loadUser()

  fun changeCoverPicture(uri: Uri) = repo.changeCoverPicture(uri)
}