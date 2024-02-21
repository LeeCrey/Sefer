package eth.social.sefer.ui.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eth.social.sefer.databinding.DNetworkErrorBinding

class ConnectionErrorDialog : BottomSheetDialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(requireContext())
    val binding = DNetworkErrorBinding.inflate(layoutInflater)
    builder.setView(binding.root)

    // event
    binding.wifiConnection.setOnClickListener {
      dismiss()
      startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }
    binding.mobileData.setOnClickListener {
      val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Intent(Settings.ACTION_DATA_USAGE_SETTINGS)
      } else {
        Intent().apply {
          component = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$DataUsageSummaryActivity"
          )
        }
      }
      dismiss()
      startActivity(intent)
    }

    return builder.create()
  }
}