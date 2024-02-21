package eth.social.sefer.ui.view.auth_flow

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CreatePasswordResponse
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import eth.social.sefer.R
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.FSignUpBinding
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ApplicationHelper.setUpToolBar
import eth.social.sefer.helpers.KeyboardUtils.closeKeyboard
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.RegistrationVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
  private lateinit var navController: NavController
  private val vm: RegistrationVM by viewModels()
  private lateinit var credentialManager: CredentialManager

  private var _inputWatcher: TextWatcher? = null
  private var _binding: FSignUpBinding? = null

  private var isRegistrationFine = false
  private val binding get() = _binding!!
  private val inputWatcher get() = _inputWatcher!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FSignUpBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setUpToolBar(view, R.id.tool_bar, this)

    credentialManager = CredentialManager.create(requireContext())
    navController = findNavController()
    vm.initFormError()

    val countryName: TextView = binding.countryPicker.tvCountryInfo
    var genderValue: RadioButton? = null
    var user: User? = null

    // observer
    lifecycleScope.launch {
      vm.response.collectLatest {
        navController.navigateUp() // close loading dialog first

        if (it.okay) {
          createPassword(user!!, it.message!!)
        } else {
          ToastUtility.showToast(it.message!!, requireContext())
        }
      }
    }
    lifecycleScope.launch {
      vm.errors.collectLatest { form ->
        binding.fullName.error = form.fullNameError
        binding.password.error = form.passwordError
        binding.passwordConfirmation.error = form.passwordConfirmationError
        binding.email.error = form.emailError
        binding.passwordLayout.endIconMode = if (form.passwordError == null) {
          TextInputLayout.END_ICON_PASSWORD_TOGGLE
        } else {
          TextInputLayout.END_ICON_NONE
        }

        isRegistrationFine = form.isRegistrationValid
        binding.signUp.isEnabled = form.isRegistrationValid && (genderValue != null)
      }
    }

    // event
    binding.gender.setOnCheckedChangeListener { _, checkedId ->
      genderValue = view.findViewById(checkedId)
      binding.signUp.isEnabled = isRegistrationFine
    }
    afterInputChanged()
    binding.signUp.setOnClickListener {
      closeKeyboard(requireActivity())

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }

      val countryVal: String = countryName.text.toString()
      if (countryVal == "Country") {
        ToastUtility.showToast(getString(R.string.please_select_country), requireContext())
        return@setOnClickListener
      }

      user = User().apply {
        fullName = binding.fullName.text.toString()
        password = binding.password.text.toString()
        passwordConfirmation = binding.passwordConfirmation.text.toString()
        country = countryVal
        email = binding.email.text.toString()
        gender = genderValue?.text.toString()
      }
      vm.signUp(user!!)

      navController.navigate(R.id.navigation_loading)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    // prevent memory leak
    binding.email.removeTextChangedListener(inputWatcher)
    binding.fullName.removeTextChangedListener(inputWatcher)
    binding.password.removeTextChangedListener(inputWatcher)
    binding.passwordConfirmation.removeTextChangedListener(inputWatcher)

    _binding = null
    _inputWatcher = null
  }

  private fun afterInputChanged() {
    _inputWatcher = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(s: Editable) {
        binding.apply {
          vm.formChanged(
            requireContext(),
            email.text.toString(),
            fullName.text.toString(),
            password.text.toString(),
            passwordConfirmation.text.toString()
          )
        }
      }
    }

    binding.email.addTextChangedListener(inputWatcher)
    binding.fullName.addTextChangedListener(inputWatcher)
    binding.password.addTextChangedListener(inputWatcher)
    binding.passwordConfirmation.addTextChangedListener(inputWatcher)
  }

  private suspend fun createPassword(user: User, message: String) {
    val request = CreatePasswordRequest(user.email!!, user.password!!)
    try {
      credentialManager.createCredential(
        requireContext(),
        request
      ) as CreatePasswordResponse
      navController.navigate(SignUpFragmentDirections.signUpDone(message))
    } catch (e: Exception) {
      e.message?.let {
        ToastUtility.showToast(it, requireContext())
      }
    }
  }
}