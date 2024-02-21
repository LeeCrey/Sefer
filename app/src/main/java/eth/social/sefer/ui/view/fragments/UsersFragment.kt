package eth.social.sefer.ui.view.fragments

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.navArgs

class UsersFragment : SearchFragment() {
  private val args: UsersFragmentArgs by navArgs()

  override fun endPoint(): String {
    return args.url
  }

  override fun vmOwner(): ViewModelStoreOwner {
    return this
  }
}