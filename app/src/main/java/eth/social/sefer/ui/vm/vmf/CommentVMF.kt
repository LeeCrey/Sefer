package eth.social.sefer.ui.vm.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eth.social.sefer.ui.vm.CommentVM

open class CommentVMF(
  val url: String,
  private val isPostShow: Boolean
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return if (modelClass.isAssignableFrom(CommentVM::class.java)) {
      CommentVM(url, isPostShow) as T
    } else {
      throw IllegalArgumentException("Can not instantiate from ${modelClass.name}")
    }
  }
}