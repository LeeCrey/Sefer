package eth.social.sefer.ui.vm.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eth.social.sefer.ui.vm.ReplyVM

class ReplyVMF(
  private val commentId: Long
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return if (modelClass.isAssignableFrom(ReplyVM::class.java)) {
      ReplyVM(commentId) as (T)
    } else {
      throw IllegalArgumentException("Can not instantiate from ${modelClass.name}")
    }
  }
}