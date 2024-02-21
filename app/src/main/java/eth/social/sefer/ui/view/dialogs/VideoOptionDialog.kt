package eth.social.sefer.ui.view.dialogs

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eth.social.sefer.R


class VideoOptionDialog : BottomSheetDialogFragment(R.layout.d_video_option) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    // Set a specific height for the dialog
//        val windowHeight = resources.displayMetrics.heightPixels
//        val dialogHeight = windowHeight * 0.8 // Adjust this value as needed
//        dialog.behavior.peekHeight = dialogHeight.toInt()
  }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//val arg: VideoOptionDialogArgs by navArgs()
//        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        val view = View.inflate(context, R.layout.d_video_option, null)
//        dialog.setContentView(view)
//
//
//        // Set a specific height for the dialog
////        val windowHeight = resources.displayMetrics.heightPixels
////        val dialogHeight = windowHeight * 0.8 // Adjust this value as needed
////        dialog.behavior.peekHeight = dialogHeight.toInt()
//        return dialog
//    }
}