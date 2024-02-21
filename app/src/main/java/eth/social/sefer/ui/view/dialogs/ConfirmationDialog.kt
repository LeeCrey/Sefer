package eth.social.sefer.ui.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eth.social.sefer.databinding.DConfirmationBinding

class ConfirmationDialog : BottomSheetDialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(requireContext())
    val binding = DConfirmationBinding.inflate(layoutInflater)
    builder.setView(binding.root)

    // event
    binding.btnYes.setOnClickListener {
      dismiss()
      requireActivity().finish()
    }
    binding.btnCancel.setOnClickListener {
      dismiss()
    }

    return builder.create()
  }
}
