package eth.social.sefer.ui.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eth.social.sefer.R

class LoadingDialog : BottomSheetDialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
    val view = layoutInflater.inflate(R.layout.d_loading, null)
    builder.setView(view)

    isCancelable = false

    return builder.create()
  }
}