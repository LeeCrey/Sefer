package eth.social.sefer.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import eth.social.sefer.R
import eth.social.sefer.helpers.DataProvider

class LauncherActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    val screen: SplashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)

    screen.setOnExitAnimationListener {
      DataProvider.initSharedPref()
      openTargetActivity(
        if (DataProvider.isSignedIn) MainActivity::class.java
        else AuthActivity::class.java
      )
    }
  }

  private fun openTargetActivity(t: Class<*>) {
    startActivity(Intent(this, t))
    overridePendingTransition(R.anim.fade_in_zero, R.anim.fade_out_zero)
  }
}