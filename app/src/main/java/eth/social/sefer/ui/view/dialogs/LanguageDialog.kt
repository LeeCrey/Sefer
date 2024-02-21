package eth.social.sefer.ui.view.dialogs

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.helpers.ToastUtility

class LanguageDialog : BottomSheetDialogFragment(R.layout.d_language) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

    val currentLang: String = pref.getString(Constants.LANG, "en").toString()

    val languages: RadioGroup = view.findViewById(R.id.language_group)
    val current: RadioButton = view.findViewWithTag(currentLang)
    current.isChecked = true

    languages.setOnCheckedChangeListener { group, checkedId ->
      val language: RadioButton = group.findViewById(checkedId)
      val editor = pref.edit()
      val lang = language.tag as String
      ToastUtility.showToast(R.string.re_open_the_app, requireContext())
      editor.putString(Constants.LANG, lang)
      editor.apply()
      dismiss()
    }
  }
}