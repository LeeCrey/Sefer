package eth.social.sefer.ui.view

import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import eth.social.sefer.R


class AuthActivity : AppActivity() {
  private lateinit var navCont: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.a_auth)

    val navHostFragment = supportFragmentManager
      .findFragmentById(R.id.nav_host_fragment_activity_auth) as NavHostFragment
    navCont = navHostFragment.navController
    window.setFlags(
      WindowManager.LayoutParams.FLAG_SECURE,
      WindowManager.LayoutParams.FLAG_SECURE
    )
  }

  override fun onSupportNavigateUp(): Boolean {
    return navCont.navigateUp()
  }
}