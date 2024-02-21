@file:Suppress("unused", "DEPRECATION")

package eth.social.sefer.helpers

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

object VibrateUtils {
  @RequiresPermission(Manifest.permission.VIBRATE)
  fun vibrate(milliseconds: Long, context: Context) {
    if (!phoneHasVibrator(context)) {
      return
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibrator =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
      vibrator.vibrate(
        CombinedVibration.createParallel(
          VibrationEffect.createOneShot(
            milliseconds,
            VibrationEffect.DEFAULT_AMPLITUDE
          )
        )
      )
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    vibrator.vibrate(
//                        VibrationEffect.createOneShot(
//                            milliseconds,
//                            VibrationEffect.DEFAULT_AMPLITUDE
//                        )
//                    )
      } else {
        val vibrator =
          context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
        vibrator.vibrate(milliseconds)
      }
    }
  }

  @RequiresPermission(Manifest.permission.VIBRATE)
  fun vibrateWithWave(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.cancel()
    val pattern = longArrayOf(0, 200, 50)
    vibrator.vibrate(pattern, 2)
  }

  private fun phoneHasVibrator(context: Context): Boolean {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    return vibrator.hasVibrator()
  }
}