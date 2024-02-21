package eth.social.sefer.ui.vm.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eth.social.sefer.ui.vm.CommunityVM

class CommunityVMF(private val url: String) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return if (modelClass.isAssignableFrom(CommunityVM::class.java)) {
      CommunityVM(url) as T
    } else {
      throw IllegalArgumentException("Can not instantiate from ${modelClass.name}")
    }
  }
}