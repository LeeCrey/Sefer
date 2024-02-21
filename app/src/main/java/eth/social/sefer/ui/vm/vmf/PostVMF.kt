package eth.social.sefer.ui.vm.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eth.social.sefer.ui.vm.PostVM

class PostVMF(
  private val url: String
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(PostVM::class.java)) {
      return PostVM(url) as (T)
    } else {
      throw IllegalArgumentException("Can not create from $modelClass")
    }
  }
}