package eth.social.sefer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.R
import eth.social.sefer.data.models.FormErrors
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.RegistrationRepo
import eth.social.sefer.helpers.InputHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RegistrationVM : ViewModel() {
  private val repo: RegistrationRepo = RegistrationRepo(viewModelScope)
  val response: SharedFlow<ApiResponse> = repo.response
  private var _errors: MutableSharedFlow<FormErrors>? = null

  val errors: SharedFlow<FormErrors> get() = _errors!!.asSharedFlow()

  fun signUp(user: User) = repo.register(user)

  fun updateBioMessage(bio: String) = repo.updateBio(bio)

  fun initFormError() {
    _errors = MutableSharedFlow()
  }

  fun formChanged(
    context: Context,
    email: String,
    fullName: String,
    pwd: String,
    pwdConfirm: String
  ) {
    val error = FormErrors()

    error.fullNameError = InputHelper.checkInput(fullName, context)
    error.emailError = InputHelper.checkInput(email, context)
    val pwdError = InputHelper.checkInput(pwd, context)
    error.passwordError = pwdError
    // check it's length
    if (pwdError == null) {
      if (pwd.length < 6) {
        error.passwordError = context.getString(R.string.pwd_length_error)
      }
    }

    if (pwd == pwdConfirm) {
      error.passwordConfirmationError = null
    } else {
      error.passwordConfirmationError = context.getString(R.string.don_not_match)
    }

    viewModelScope.launch {
      _errors?.emit(error)
    }
  }
}