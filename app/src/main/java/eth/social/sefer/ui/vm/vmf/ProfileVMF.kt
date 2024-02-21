package eth.social.sefer.ui.vm.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eth.social.sefer.data.models.User
import eth.social.sefer.ui.vm.ProfileVM

class ProfileVMF(
  private val user: User
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return if (modelClass.isAssignableFrom(ProfileVM::class.java)) {
      ProfileVM(user) as (T)
    } else {
      throw IllegalArgumentException("Can not instantiate from ${modelClass.name}")
    }
  }
}