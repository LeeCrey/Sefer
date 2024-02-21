package eth.social.sefer.helpers

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object ToastUtility {
  fun showToast(msg: String, context: Context) {
    toastInstance(msg, context).show()
  }

  fun showToast(msg: Int, context: Context) {
    toastInstance(context.getString(msg), context).show()
  }

  fun toastInstance(msg: String, myContext: Context): Toast {
    return Toast.makeText(myContext, msg, Toast.LENGTH_SHORT)
  }

  fun snackBarInstance(msg: String, root: View): Snackbar {
    return Snackbar.make(root, msg, Snackbar.LENGTH_SHORT)
  }
}