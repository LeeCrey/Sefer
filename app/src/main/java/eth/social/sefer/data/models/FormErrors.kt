package eth.social.sefer.data.models

import com.fasterxml.jackson.annotation.JsonProperty

// for both client side and response from server
class FormErrors {
  @JsonProperty("full_name")
  var fullNameError: String? = null

  @JsonProperty("password")
  var passwordError: String? = null

  @JsonProperty("password_confirmation")
  var passwordConfirmationError: String? = null

  @JsonProperty("current_password")
  var currentPasswordError: String? = null

  @JsonProperty("email_error")
  var emailError: String? = null

  var tokenError: String? = null

  val isRegistrationValid: Boolean
    // BEGIN
    get() = fullNameError == null && passwordError == null
            && emailError == null && passwordConfirmationError == null

  val isLoginValid: Boolean
    get() = passwordError == null && emailError == null

  val isChangePasswordFormValid: Boolean
    get() = passwordError == null && passwordConfirmationError == null &&
            tokenError == null && currentPasswordError == null
}