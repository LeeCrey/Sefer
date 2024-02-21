@file:Suppress("DEPRECATION", "unused", "MemberVisibilityCanBePrivate")

package eth.social.sefer.helpers

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import eth.social.sefer.R


enum class SystemBarColors { WHITE, DARK }

object ViewUtils {
  private const val darkStatusIcons = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

  private const val whiteStatusIcons = 0

  fun changeSystemBars(activity: Activity, systemBarColors: SystemBarColors) {
    setUpStatusBarAndNavigationBar(activity)

    if (systemBarColors == SystemBarColors.WHITE) {
      changeStatusBarIcons(activity, isWhiteIcons = false)
      changeStatusBarColor(activity, android.R.color.white)
      changeSystemNavigationBarColor(activity, android.R.color.white)
    } else {
      changeStatusBarIcons(activity, isWhiteIcons = true)
      changeStatusBarColor(activity, android.R.color.transparent)
      changeSystemNavigationBarColor(activity, R.color.onPrimary)
    }
  }

  fun setUpStatusBarAndNavigationBar(activity: Activity) {
    with(activity) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
  }

  // STATUS BAR
  fun changeStatusBarIcons(activity: Activity, isWhiteIcons: Boolean) {
    setUpStatusBarAndNavigationBar(activity)
    activity.window?.decorView?.systemUiVisibility =
      if (isWhiteIcons) whiteStatusIcons else darkStatusIcons
  }

  fun changeStatusBarColor(activity: Activity, @ColorRes colorRes: Int) {
    setUpStatusBarAndNavigationBar(activity)
    activity.window?.statusBarColor = ContextCompat.getColor(activity, colorRes)
  }

  // SYSTEM Bar
  fun changeSystemNavigationBarColor(activity: Activity, @ColorRes colorRes: Int) {
    activity.window?.navigationBarColor = ContextCompat.getColor(activity, colorRes)
  }

  fun showStatusAndNavBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowInsetsController = activity.window.insetsController
      windowInsetsController?.show(WindowInsets.Type.statusBars())
      windowInsetsController?.show(WindowInsets.Type.systemBars())
    }
  }

  fun hideStatusBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
      activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
    else {
      activity.window.decorView.systemUiVisibility = (
          View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Prevents layout resize
              or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Initially load things behind the navigation bars. Prevents resize
              or View.SYSTEM_UI_FLAG_IMMERSIVE // Prevents the screen from showing the status bar, on tapping
              or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide the status bar only
          )
    }
  }

  fun makeViewExtendToStatusBar(activity: Activity) {
    val decorView: View = activity.window.decorView

    decorView.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
  }

  fun undoExtendView(activity: Activity) {
    val decorView: View = activity.window.decorView

    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
  }

  fun hideStatusAndNavBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val windowInsetsController = activity.window.insetsController
      windowInsetsController?.hide(WindowInsets.Type.statusBars())
      windowInsetsController?.hide(WindowInsets.Type.systemBars())
    } else {
      activity.window.decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Prevents layout resize
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Initially load things behind the navigation bar. Prevents resize
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Initially load things behind the navigation bars. Prevents resize
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Load things behind the navigation bars
            or View.SYSTEM_UI_FLAG_FULLSCREEN) // Load things behind the navigation bars
    }
  }

  fun changeSystemNavColor(activity: Activity) {
    changeSystemNavigationBarColor(activity, R.color.secondary)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val view: View = activity.window.decorView
      view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
  }

  fun transparentStatusBar(activity: FragmentActivity) {
    val window = activity.window
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = activity.resources.getColor(R.color.white)
  }
}