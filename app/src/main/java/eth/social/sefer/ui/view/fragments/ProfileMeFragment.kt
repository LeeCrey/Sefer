package eth.social.sefer.ui.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.MediaCallBack
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.FileUtils
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.ToastUtility


// In this fragment, vm should be scope to activity
class ProfileMeFragment : ProfileOtherFragment(), MediaCallBack {
  private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    pickMedia = ApplicationHelper.setupPickMedia(this, this)

    // event
    binding.editProfileBtn.visibility = View.VISIBLE
    binding.changeCoverPicture.visibility = View.VISIBLE
    binding.changeCoverPicture.setOnClickListener {
      pickMedia?.launch(
        PickVisualMediaRequest.Builder()
          .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
      )
    }
    binding.editProfileBtn.setOnClickListener {
      navController.navigate(ProfileMeFragmentDirections.openEditProfile(userShow))
    }
  }

  override fun onDestroyView() {
    // since it nullify vm
    super.onDestroyView()

    pickMedia = null
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.profile_mine_menu, menu)
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    return when (val id = menuItem.itemId) {
      android.R.id.home -> false

      R.id.open_user_info -> {
        navController.navigate(id)

        true
      }

      R.id.edit_bio -> {
        navController.navigate(ProfileMeFragmentDirections.editBio(userShow))

        true
      }

      R.id.open_chats -> true

      else -> {
        vm.loadUser()

        true
      }
    }
  }

  // scope to activity
  override fun vmContext(): ViewModelStoreOwner {
    return requireActivity()
  }

  // One extra tab is need. Which is liked posts
  override fun fragmentList(): MutableList<Fragment> {
    val likedPosts = ProfilePostsFragment().apply {
      arguments = Bundle().apply {
        putString(Constants.URL, Constants.LIKED_POSTS_URL)
        putBoolean("is_profile", true)
        putBoolean(Constants.IS_ME, true)
      }
    }
    val list = super.fragmentList()
    list.add(likedPosts)

    return list
  }

  override fun getUserToShow(): User {
    return PrefHelper.currentUser
  }

  override fun onMediaSelected(uri: Uri) {
    glide.load(uri).into(binding.userCoverPicture)

    val file = FileUtils.getFile(requireContext(), uri)
    val fileSize = file!!.length() / (1024 * 1024)
    if (fileSize > 2L) {
      ToastUtility.showToast(getString(R.string.incorrect_file), requireContext())
      return
    }

    if (isInternetAvailable()) {
      vm.changeCoverPicture(uri)
    }
  }
}