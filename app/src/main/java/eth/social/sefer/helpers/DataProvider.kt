package eth.social.sefer.helpers

import android.content.Context
import android.content.SharedPreferences
import eth.social.sefer.Constants
import eth.social.sefer.MyApp


object DataProvider {
  enum class LOGIN {
    CREDENTIAL,
    PASSKEY,
    GOOGLE
  }

  private lateinit var sharedPreference: SharedPreferences
  private lateinit var editor: SharedPreferences.Editor

  private const val IS_SIGNED_IN = "eth.social.sefer.isSignedIn"

  var loginType: LOGIN = LOGIN.CREDENTIAL

  fun initSharedPref() {
    sharedPreference =
      MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, Context.MODE_PRIVATE)
    editor = sharedPreference.edit()
  }

  fun configureSignedInPref(flag: Boolean) {
    editor.putBoolean(IS_SIGNED_IN, flag)
    editor.apply()
  }

  val isSignedIn: Boolean get() = sharedPreference.getBoolean(IS_SIGNED_IN, false)
}
