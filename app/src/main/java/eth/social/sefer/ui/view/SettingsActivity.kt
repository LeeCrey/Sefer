package eth.social.sefer.ui.view

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import eth.social.sefer.R
import eth.social.sefer.helpers.ViewUtils


class SettingsActivity : AppActivity() {
  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.a_settings)

    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_settings) as NavHostFragment
    navController = navHostFragment.navController

    ViewUtils.changeStatusBarColor(this, R.color.primary)
  }

  override fun finish() {
    super.finish()

    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
  }
}