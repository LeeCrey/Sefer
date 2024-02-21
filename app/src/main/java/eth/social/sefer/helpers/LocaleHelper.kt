package eth.social.sefer.helpers

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import eth.social.sefer.Constants
import java.util.Locale

object LocaleHelper {
  fun getConfiguredLocale(context: Context): Locale {
    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val value: String = pref.getString(Constants.LANG, "en") ?: "en"

    return Locale(value)
  }

  fun setLocale(context: Context, languageCode: String): Context {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = context.resources
    val config = Configuration(resources.configuration)
    config.setLocale(locale)

    return context.createConfigurationContext(config)
  }
}