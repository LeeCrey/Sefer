package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.PrefHelper.getUnreadNotificationsCount


class HomeFragment : PostsFragment(), MenuProvider {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_home, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

    val fab: FloatingActionButton = view.findViewById(R.id.fab)
    fab.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit))

    // event
    fab.setOnClickListener {
      openWriteResource(R.id.navigation_new_post, null)
    }
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.home_menu, menu)

    // notifications badge
    val item = menu.findItem(R.id.open_notifications)
    val actionView = item.actionView ?: return

    val notification: View = actionView.findViewById(R.id.badge)
    notification.setOnClickListener { onMenuItemSelected(item) }

    val v: TextView = actionView.findViewById(R.id.notification_value)
    val unreadCount: Int = getUnreadNotificationsCount(requireContext())

    v.visibility = if (unreadCount == 0) {
      View.GONE
    } else {
      v.text = unreadCount.toString()
      View.VISIBLE
    }
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    return when (val id = menuItem.itemId) {
      android.R.id.home -> false
      else -> {
        navController.navigate(id)

        true
      }
    }
  }

  override fun openUserProfile(user: User) {
    // posts in home screen are from people who current user is following
    user.following = true
    navController.navigate(HomeFragmentDirections.showUserProfile(user.fullName!!, user))
  }

  override fun postsUrl(): String {
    return Constants.POSTS
  }

  override fun vmOwner(): ViewModelStoreOwner {
    return requireActivity()
  }

  override fun onUnAuthorized() {
    ApplicationHelper.backToLogin(requireActivity())
  }
}