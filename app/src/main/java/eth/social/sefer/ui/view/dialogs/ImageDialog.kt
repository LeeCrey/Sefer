package eth.social.sefer.ui.view.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eth.social.sefer.R
import eth.social.sefer.databinding.DImagesBinding

class ImageDialog : BottomSheetDialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
    val binding = DImagesBinding.inflate(layoutInflater)
    builder.setView(binding.root)

    val arg: ImageDialogArgs by navArgs()

    Glide.with(requireContext()).load(arg.url).placeholder(R.drawable.photo_load_error)
      .into(binding.image)

    return builder.create()
  }
}