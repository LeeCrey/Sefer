package eth.social.sefer.ui.view

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView
import eth.social.sefer.R
import eth.social.sefer.callbacks.MainActivityCallBack
import eth.social.sefer.data.apis.OperationApi
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.LocaleHelper
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.helpers.ViewUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat


class MainActivity : AppActivity(), MainActivityCallBack {
  private lateinit var navController: NavController
  private lateinit var appBarConfig: AppBarConfiguration
  private lateinit var drawerLayout: DrawerLayout
  private lateinit var backPressCB: OnBackPressedCallback
  private lateinit var bottomNav: BottomNavigationView
  private val numberFormatter: NumberFormat by lazy {
    NumberFormat.getInstance(LocaleHelper.getConfiguredLocale(this))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.a_main)

    // top level destinations
    val home = R.id.navigation_home
    val profile = R.id.navigation_profile
    val search = R.id.navigation_search
    val community = R.id.navigation_community
    val shorts = R.id.navigation_shorts

    val toolbar: Toolbar = findViewById(R.id.tool_bar)
    setSupportActionBar(toolbar)

    val user = PrefHelper.currentUser
    bottomNav = findViewById(R.id.bottom_nav_view)
    val navView: NavigationView = findViewById(R.id.nav_view)
    drawerLayout = findViewById(R.id.drawer_layout)

    appBarConfig = AppBarConfiguration.Builder(home, search, shorts, community, profile)
      .setOpenableLayout(drawerLayout).build()

    val navHostFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
    navController = navHostFragment.navController

    setupActionBarWithNavController(this, navController, appBarConfig)
    setupWithNavController(bottomNav, navController)
    setupWithNavController(navView, navController)
    bottomNav.setBackgroundColor(getColor(R.color.surface))
    navView.setBackgroundColor(getColor(R.color.surface))

    // event
    navController.addOnDestinationChangedListener { _, navDestination, _ ->
      val id: Int = navDestination.id
      isBottomNavVisible(appBarConfig.topLevelDestinations.contains(id))

      if (id == profile) {
        toolbar.title = user.fullName
      }
    }
    navView.setNavigationItemSelectedListener {
      when (val id = it.itemId) {
        R.id.menu_item_rate_app -> rateApp()
        R.id.menu_item_share -> shareApp()
        R.id.navigation_settings,
        R.id.navigation_rewards,
        R.id.navigation_market -> {
          navController.navigate(id, null, ApplicationHelper.slideAnimation)
        }

        else -> onNavDestinationSelected(it, navController)
      }
      drawerLayout.close()

      false
    }

    drawerLayout.addDrawerListener(object : SimpleDrawerListener() {
      override fun onDrawerOpened(drawerView: View) {
        super.onDrawerOpened(drawerView)

        backPressCB.isEnabled = true
      }

      override fun onDrawerClosed(drawerView: View) {
        super.onDrawerClosed(drawerView)

        backPressCB.isEnabled = false
      }
    })
    registerBackPress()
    setNavHeader(user, navView.getHeaderView(0))

    // >= 33
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 116)
      }
    }
//    sendFCMToken()
    ViewUtils.changeStatusBarIcons(this, isDarkMode())
  }

  override fun onSupportNavigateUp(): Boolean {
    return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp()
  }

  override fun onDestroy() {
    super.onDestroy()

    backPressCB.remove()
  }

  override fun hideBottomNavView() {
    bottomNav.visibility = View.GONE
  }

  override fun showBottomNavView() {
    bottomNav.isVisible = true
  }

  private fun isBottomNavVisible(visible: Boolean) {
    bottomNav.isVisible = visible
  }

  override fun toggleBottomNav() {
    if (isBottomNavVisible()) {
      hideBottomNavView()
    } else {
      showBottomNavView()
    }
  }

  override fun logout() {
    PrefHelper.wipeStoredData(this)
    startActivity(Intent(this, AuthActivity::class.java))
    finish()
  }

  override fun isBottomNavVisible(): Boolean {
    return bottomNav.isVisible
  }

  private fun registerBackPress() {
    backPressCB = object : OnBackPressedCallback(false) {
      override fun handleOnBackPressed() {
        drawerLayout.close()
      }
    }
    onBackPressedDispatcher.addCallback(backPressCB)
  }

  private fun setNavHeader(user: User, headerView: View) {
    bottomNav.itemIconTintList = null

    val profile: Int = R.id.navigation_profile
    val imageLoader: RequestManager = Glide.with(this)

    val profileImage: CircleImageView = headerView.findViewById(R.id.user_profile_picture)
    val fullName: TextView = headerView.findViewById(R.id.user_full_name)
    val username: TextView = headerView.findViewById(R.id.user_name)
    val followersCount: TextView = headerView.findViewById(R.id.followers_count)
    val followingCount: TextView = headerView.findViewById(R.id.following_count)
    val changeProfile: CircleImageView = headerView.findViewById(R.id.change_profile)

    changeProfile.setOnClickListener {
      ToastUtility.showToast("Clicked", this)
    }
    user.followersCount?.let {
      followersCount.text = numberFormatter.format(it)
    }
    user.followingCount?.let {
      followingCount.text = numberFormatter.format(it)
    }
    imageLoader.load(user.profileUrl)
      .placeholder(R.drawable.profile_placeholder)
      .into(profileImage)
    fullName.text = user.fullName
    user.username?.let(username::setText)

    // bottom nav
    val profileMenu: MenuItem = bottomNav.menu.findItem(profile)
    val options: RequestOptions =
      RequestOptions.circleCropTransform().placeholder(R.drawable.profile_placeholder)
    imageLoader.asBitmap().load(user.profileUrl).apply(options)
      .into(object : CustomTarget<Bitmap?>() {
        override fun onResourceReady(
          resource: Bitmap, transition: Transition<in Bitmap?>?
        ) {
          profileMenu.icon = BitmapDrawable(resources, resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
      })
  }

  private fun rateApp() {
    try {
      startActivity(
        Intent(
          Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")
        )
      )
    } catch (_: ActivityNotFoundException) {
      startActivity(
        Intent(
          Intent.ACTION_VIEW,
          Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
      )
    }
  }

  private fun shareApp() {
    val appUri = "https://play.google.com/store/apps/details?id=$packageName"
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      type = "text/plain"
      putExtra(Intent.EXTRA_TEXT, appUri)
    }
    startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
  }

  private fun sendFCMToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
      if (task.isSuccessful) {
        task.result?.let(this::sendTokenToServer)
      }
    }
  }

  private fun sendTokenToServer(token: String) {
    val auth: String? = PrefHelper.authorizationToken
    val api: OperationApi = retrofitInstance.create(OperationApi::class.java)
    val handler = CoroutineExceptionHandler { _, _ ->
      lifecycleScope.launch(Dispatchers.Main) {
        PrefHelper.markReadyForNotification(false)
      }
    }
    lifecycleScope.launch(Dispatchers.IO + handler) {
      val resp = api.sendPostNotificationToken(auth, token).execute()
      withContext(Dispatchers.Main) {
        PrefHelper.markReadyForNotification(resp.isSuccessful)
      }
    }
  }
}