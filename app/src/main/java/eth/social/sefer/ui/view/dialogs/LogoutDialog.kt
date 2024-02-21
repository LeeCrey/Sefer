package eth.social.sefer.ui.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import eth.social.sefer.R
import eth.social.sefer.databinding.DLogoutBinding
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility.showToast
import eth.social.sefer.ui.vm.OperationVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogoutDialog : DialogFragment() {
  private var _binding: DLogoutBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = DLogoutBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    isCancelable = false

    val vm: OperationVM by viewModels()
    val navController = findNavController()
    binding.btnYes.setOnClickListener {
      if (!isInternetAvailable()) {
        showToast(R.string.msg_connection_error, requireContext())
        return@setOnClickListener
      }

      vm.logout()
      it.isEnabled = false
      binding.loading.visibility = View.VISIBLE
      binding.btnCancel.isEnabled = false
    }
    binding.btnCancel.setOnClickListener { navController.navigateUp() }

    //
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          if (it.okay) {
            navController.navigateUp()
            ApplicationHelper.backToLogin(requireActivity())
          } else {
            binding.loading.visibility = View.GONE
            binding.btnYes.isEnabled = true
            binding.btnCancel.isEnabled = true

            it.message?.let { msg ->
              showToast(msg, requireContext())
            }
          }
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}