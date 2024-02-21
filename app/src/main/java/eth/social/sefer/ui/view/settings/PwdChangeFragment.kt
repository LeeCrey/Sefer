package eth.social.sefer.ui.view.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import eth.social.sefer.R
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.FPasswordBinding
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.KeyboardUtils
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.OperationVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PwdChangeFragment : Fragment() {
  private var _binding: FPasswordBinding? = null
  private var _watcher: TextWatcher? = null

  private val binding get() = _binding!!
  private val watcher get() = _watcher!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FPasswordBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    ApplicationHelper.setUpToolBar(view, R.id.tool_bar, this)
    val vm: OperationVM = ViewModelProvider(this)[OperationVM::class.java]

    val navController = findNavController()

    // observers
    lifecycleScope.launch {
      vm.response.collectLatest {
        it.message?.let { msg ->
          ToastUtility.showToast(msg, requireContext())
        }

        navController.navigateUp()

        if (it.okay) {
          ApplicationHelper.backToLogin(requireActivity() as AppCompatActivity)
        }
      }
    }

    val pwd = binding.password
    val currentPwd = binding.currentPassword
    val pwdConfirmation = binding.passwordConfirmation

    // event
    initTextWatcher()
    binding.saveChanges.setOnClickListener {
      KeyboardUtils.closeKeyboard(requireActivity())

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }

      val student = User().apply {
        password = pwd.text.toString()
        currentPassword = currentPwd.text.toString()
        passwordConfirmation = pwdConfirmation.text.toString()
      }

      vm.changePassword(student)

      navController.navigate(R.id.navigation_loading)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    binding.password.removeTextChangedListener(watcher)
    binding.currentPassword.removeTextChangedListener(watcher)
    binding.passwordConfirmation.removeTextChangedListener(watcher)

    _binding = null
    _watcher = null
  }

  private fun initTextWatcher() {
    val passWd = binding.password
    val currentPwd = binding.currentPassword
    val pwdConfirmation = binding.passwordConfirmation
    val change = binding.saveChanges

    _watcher = object : TextWatcher {
      override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
      override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
      override fun afterTextChanged(editable: Editable) {
        val cPassword = currentPwd.text.toString().trim()
        // current password
        var cValid = false
        currentPwd.error = if (cPassword.isEmpty()) {
          getString(R.string.required)
        } else {
          cValid = true
          null
        }

        // password
        var pwdValid = false
        val pwd = passWd.text.toString().trim()
        passWd.error = if (pwd.isEmpty()) {
          getString(R.string.required)
        } else {
          if (pwd.length > 5) {
            pwdValid = true
            null
          } else {
            getString(R.string.pwd_length)
          }
        }

        // password confirmation
        var cPwdValid = false
        val cPwd = pwdConfirmation.text.toString().trim()
        pwdConfirmation.error = if (cPwd == pwd) {
          cPwdValid = true
          null
        } else {
          getString(R.string.don_t_match)
        }
        change.isEnabled = cPwdValid && pwdValid && cValid
        if (change.isEnabled) {
          KeyboardUtils.closeKeyboard(requireActivity())
        }
      }
    }
    passWd.addTextChangedListener(watcher)
    currentPwd.addTextChangedListener(watcher)
    pwdConfirmation.addTextChangedListener(watcher)
  }
}
