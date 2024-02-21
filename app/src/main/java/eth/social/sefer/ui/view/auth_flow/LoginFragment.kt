package eth.social.sefer.ui.view.auth_flow


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.textfield.TextInputLayout
import eth.social.sefer.BuildConfig
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.FLoginBinding
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.DataProvider
import eth.social.sefer.helpers.KeyboardUtils.closeKeyboard
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.helpers.readFromAsset
import eth.social.sefer.ui.view.MainActivity
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.LoginVM
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID


/*
 Solomon Boloshe(instagram: @lee_crey)
 Dec 07, 2023
 */
class LoginFragment : Fragment() {
  private val vm: LoginVM by viewModels()
  private var _navController: NavController? = null
  private var _binding: FLoginBinding? = null
  private var _inputWatcher: TextWatcher? = null
  private var _credentialManager: CredentialManager? = null
  private var getPasswordOption: GetPasswordOption? = null
  private var publicKey: GetPublicKeyCredentialOption? = null

  private val credentialManager get() = _credentialManager!!
  private val binding get() = _binding!!
  private val navController get() = _navController!!
  private val inputWatcher get() = _inputWatcher!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FLoginBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    _navController = findNavController()
    _credentialManager = CredentialManager.create(MyApp.instance)
    getPasswordOption = GetPasswordOption()
    publicKey = GetPublicKeyCredentialOption(fetchAuthJsonFromServer(), null)
    DataProvider.initSharedPref()

    // observer
    lifecycleScope.launch {
      // network request response
      vm.response.collectLatest {
        // close loading dialog
        navController.navigateUp()

        if (it.okay) {
          DataProvider.configureSignedInPref(true)
          startActivity(Intent(requireActivity(), MainActivity::class.java))
          requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }
        }
      }
    }
    lifecycleScope.launch {
      vm.errors.collectLatest {
        binding.emailValue.error = it.emailError
        binding.passwordLayout.endIconMode = if (it.passwordError == null) {
          TextInputLayout.END_ICON_PASSWORD_TOGGLE
        } else {
          TextInputLayout.END_ICON_NONE
        }
        binding.passwordValue.error = it.passwordError
        binding.loginButton.isEnabled = it.isLoginValid && vm.enableLoginButton
      }
    }

    // event
    afterInputChanged()
    binding.termsAndCondition.movementMethod = LinkMovementMethod.getInstance()
    binding.signUp.setOnClickListener { navController.navigate(it.id) }
    binding.openInstruction.setOnClickListener {
      navController.navigate(LoginFragmentDirections.openInstruction())
    }
    binding.forgotPassword.setOnClickListener {
      navController.navigate(
        LoginFragmentDirections.openInstruction(
          getString(R.string.recover_account), InstructionFragment.FORGOT_PASSWORD
        )
      )
    }
    binding.loginButton.setOnClickListener {
      closeKeyboard(requireActivity())

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }

      val user = User().apply {
        email = binding.emailValue.text.toString()
        password = binding.passwordValue.text.toString()
      }
      vm.login(user)
      navController.navigate(R.id.navigation_loading)
    }
    binding.signInWithSavedCredentials.setOnClickListener {
      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }
      navController.navigate(R.id.navigation_loading)

      lifecycleScope.launch { getSavedCredentials() }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    binding.emailValue.removeTextChangedListener(inputWatcher)
    binding.passwordValue.removeTextChangedListener(inputWatcher)

    _navController = null
    _inputWatcher = null
    _credentialManager = null
    getPasswordOption = null
    publicKey = null
    _binding = null

    lifecycleScope.coroutineContext.cancelChildren()
  }

  private suspend fun getSavedCredentials() {
    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(true)
      .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
      .setNonce(UUID.randomUUID().toString())
      .build()
    val request = GetCredentialRequest(listOf(publicKey!!, getPasswordOption!!, googleIdOption))

    try {
      val response = credentialManager.getCredential(requireContext(), request)
      handleSignIn(response)
    } catch (e: Exception) {
      navController.navigateUp()
      e.message?.let { msg ->
        ToastUtility.showToast(msg, requireContext())
      }
    }
  }

  private fun handleSignIn(result: GetCredentialResponse) {
    when (val credentials = result.credential) {
      is PublicKeyCredential -> {
        // Share responseJson such as a GetCredentialResponse on your server to
        // validate and authenticate
        DataProvider.loginType = DataProvider.LOGIN.PASSKEY
        // TODO what to do?
      }

      is PasswordCredential -> {
        // Send ID and password to your server to validate and authenticate.
        DataProvider.loginType = DataProvider.LOGIN.CREDENTIAL
        val user = User().apply {
          email = credentials.id
          password = credentials.password
        }
        vm.login(user)
      }

      is CustomCredential -> {
        if (credentials.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          ToastUtility.showToast(R.string.unknown_credentials, requireContext())
          navController.navigateUp()
        }

        try {
          DataProvider.loginType = DataProvider.LOGIN.GOOGLE
          // Use googleIdTokenCredential and extract id to validate and
          // authenticate on your server.
          val googleIdTokenCredential =
            GoogleIdTokenCredential.createFrom(credentials.data)
          vm.login(googleIdTokenCredential.idToken)
        } catch (e: GoogleIdTokenParsingException) {
          navController.navigateUp()
          e.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }
        }
      }

      else -> {
        navController.navigateUp()
        ToastUtility.showToast(R.string.unknown_credentials, requireContext())
      }
    }
  }

  private fun fetchAuthJsonFromServer(): String {
    return requireContext().readFromAsset("AuthFromServer")
  }

  private fun afterInputChanged() {
    _inputWatcher = object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(s: Editable) {
        vm.loginInputChanged(
          requireContext(),
          binding.emailValue.text.toString(),
          binding.passwordValue.text.toString()
        )
      }
    }
    binding.emailValue.addTextChangedListener(inputWatcher)
    binding.passwordValue.addTextChangedListener(inputWatcher)
  }
}