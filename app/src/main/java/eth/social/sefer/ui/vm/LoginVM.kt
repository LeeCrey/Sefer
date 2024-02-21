package eth.social.sefer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.FormErrors
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.LoginRepo
import eth.social.sefer.helpers.InputHelper.checkEmail
import eth.social.sefer.helpers.InputHelper.checkInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class LoginVM : ViewModel() {
  private val repo: LoginRepo = LoginRepo(viewModelScope)
  private val _errors: MutableSharedFlow<FormErrors> = MutableSharedFlow()

  val errors: SharedFlow<FormErrors> get() = _errors
  val response: SharedFlow<ApiResponse> = repo.response

  var enableLoginButton = false

  fun login(user: User) = repo.login(user)
  fun login(token: String?) = repo.login(token)

  fun loginInputChanged(context: Context, email: String, password: String) {
    viewModelScope.launch {
      val errors = FormErrors()
      if (email.trim().isEmpty() && password.trim().isEmpty()) {
        _errors.emit(errors)
        enableLoginButton = false

        return@launch
      }

      enableLoginButton = true

      errors.emailError = checkEmail(email, context)
      errors.passwordError = checkInput(password, context)
      _errors.emit(errors)
    }
  }
}