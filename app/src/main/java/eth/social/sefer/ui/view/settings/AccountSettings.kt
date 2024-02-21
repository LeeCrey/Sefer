package eth.social.sefer.ui.view.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import eth.social.sefer.R
import eth.social.sefer.databinding.SettingsAccountBinding
import eth.social.sefer.helpers.ApplicationHelper

class AccountSettings : Fragment(R.layout.settings_account) {
  private var _binding: SettingsAccountBinding? = null

  private val binding get() = _binding!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    _binding = SettingsAccountBinding.bind(view)

    ApplicationHelper.setUpToolBar(view, R.id.tool_bar, this)
    val navController = findNavController()

    binding.changePassword.setOnClickListener {
      navController.navigate(AccountSettingsDirections.openChangePassword())
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}