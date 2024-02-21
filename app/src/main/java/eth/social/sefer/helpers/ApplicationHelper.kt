package eth.social.sefer.helpers

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.callbacks.MediaCallBack
import eth.social.sefer.ui.view.AuthActivity
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog


object ApplicationHelper {
  @JvmStatic
  fun isInternetAvailable(): Boolean {
    val connectivityManager =
      MyApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
  }

  @JvmStatic
  fun setUpToolBar(root: View, toolbarId: Int, fragment: Fragment): NavController {
    val navController = NavHostFragment.findNavController(fragment)
    val toolbar: Toolbar = root.findViewById(toolbarId)
    setupWithNavController(toolbar, navController)

    return navController
  }

  @JvmStatic
  fun backToLogin(currentActivity: FragmentActivity) {
    PrefHelper.wipeStoredData(currentActivity)
    val intent = Intent(currentActivity, AuthActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    currentActivity.startActivity(intent)
    currentActivity.finishAffinity()
    currentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
  }

  @JvmStatic
  val slideTopDown: NavOptions
    get() =
      NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_bottom)
        .setExitAnim(R.anim.wait)
        .build()

  @JvmStatic
  val slideAnimation: NavOptions
    get() =
      NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()

  @JvmStatic
  fun setupPickMedia(
    context: Fragment,
    callback: MediaCallBack
  ): ActivityResultLauncher<PickVisualMediaRequest> {
    return context.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      if (uri == null) {
        return@registerForActivityResult
      }

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(context.childFragmentManager, null)
        return@registerForActivityResult
      }

      callback.onMediaSelected(uri)
    }
  }
}