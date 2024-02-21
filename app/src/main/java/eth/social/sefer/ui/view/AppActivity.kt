package eth.social.sefer.ui.view

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.helpers.LocaleHelper

// shared between activity
open class AppActivity : AppCompatActivity() {

  override fun attachBaseContext(newBase: Context) {
    val pref = PreferenceManager.getDefaultSharedPreferences(newBase)
    val lang = pref.getString(Constants.LANG, "en").toString()

    super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
  }

  private fun setTheme(theme: String) {
    val themeValue: Int = if ("tg" == theme) R.style.Theme_Sefer else R.style.Theme_Telegram

    getTheme().applyStyle(themeValue, true)
  }

  protected fun isDarkMode(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      resources.configuration.isNightModeActive
    } else {
      resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
          Configuration.UI_MODE_NIGHT_YES
    }
  }
}