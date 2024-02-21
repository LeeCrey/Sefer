package eth.social.sefer.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import eth.social.sefer.MyApp
import eth.social.sefer.R

object ClipboardUtils {
  fun copyText(text: CharSequence?, context: Context) {
    val cm =
      context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText(MyApp.instance.packageName, text))

    ToastUtility.showToast(R.string.copied, context)
  }
}