package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper

class CommunityShowFragment : PostsFragment(), MenuProvider {
  private val arg: CommunityShowFragmentArgs by navArgs()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
  }

  override fun postsUrl(): String {
    return Constants.communityPosts(arg.community.id)
  }

  override fun openUserProfile(user: User) {
    val arg = Bundle().apply {
      putString(Constants.USERNAME, user.fullName)
      putParcelable(Constants.USER, user)
    }
    navController.navigate(R.id.navigation_user_profile, arg)
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.community_menu, menu)
    val isVisible = arg.community.isOwner

    val leave = menu.findItem(R.id.leave)
    val edit = menu.findItem(R.id.edit)
    edit.isVisible = isVisible
    leave.isVisible = !isVisible
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    return when (menuItem.itemId) {
      android.R.id.home -> false
      R.id.navigation_create_post -> {
        val arg = Bundle().apply {
          putLong(Constants.COMMUNITY_ID, arg.community.id)
        }
        openWriteResource(R.id.navigation_new_post, arg)
        true
      }

      R.id.edit -> {
        val arg = Bundle().apply {
          putString(Constants.LBL_NAME, getString(R.string.update))
          putParcelable(Constants.COMMUNITY, arg.community)
        }
        navController.navigate(
          R.id.navigation_community_form,
          arg,
          ApplicationHelper.slideAnimation
        )
        true
      }

      else -> NavigationUI.onNavDestinationSelected(menuItem, navController)
    }
  }
}