package eth.social.sefer.ui.view.auth_flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.databinding.FInstructionsBinding
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ApplicationHelper.setUpToolBar
import eth.social.sefer.helpers.KeyboardUtils
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.OperationVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


// instruction can be
//   1:) password forgot link request    API(users/passwords)
//   2:) account unlock link request     API(users/unlocks)
//   3:) account confirm link request    API(users/confirmation)
// The only difference for these requests is API
// THy are almost the same form, ui except end point(API)
class InstructionFragment : Fragment() {
  private var _binding: FInstructionsBinding? = null

  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FInstructionsBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setUpToolBar(view, R.id.tool_bar, this)
    val navController = findNavController()

    val email: TextInputEditText = _binding!!.email
    val arg: InstructionFragmentArgs by navArgs()

    val msg: String
    var header: String = getString(R.string.lbl_locked_header)
    val url: String = when (arg.instructionCode) {
      FORGOT_PASSWORD -> {
        msg = getString(R.string.instruction_msg, "account reset")
        header = getString(R.string.lbl_forgot_password)
        Constants.PASSWORD
      }

      CONFIRM_ACCOUNT -> {
        msg = getString(R.string.instruction_msg, "account confirmation")
        header = getString(R.string.lbl_confirm_header)
        Constants.CONFIRM
      }

      else -> {
        msg = getString(R.string.instruction_msg, "account unlock")
        Constants.UNLOCK
      }
    }

    binding.instructionMsg.text = msg
    binding.instructionHeader.text = header
    val vm: OperationVM by viewModels()

    // observer
    lifecycleScope.launch {
      vm.response.collectLatest {
        // close loading dialog
        navController.navigateUp()

        if (it.okay) {
          navController.navigate(InstructionFragmentDirections.openOperationDone(it.message!!))
        } else {
          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }
        }
      }
    }

    // event
    email.doAfterTextChanged {
      binding.sendInstructions.isEnabled = it.toString().isNotEmpty()
    }
    binding.sendInstructions.setOnClickListener {
      KeyboardUtils.hide(view)

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }

      vm.requestInstruction(email.text.toString(), url)
      navController.navigate(R.id.navigation_loading)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }

  companion object {
    // 1 -> unlock account
    const val FORGOT_PASSWORD = 2

    const val CONFIRM_ACCOUNT = 3
  }
}