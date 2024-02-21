package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.ProfileCallBack
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.FProfileBinding
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.LocaleHelper
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.CustomTabAdapter
import eth.social.sefer.ui.vm.ProfileVM
import eth.social.sefer.ui.vm.vmf.ProfileVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat


open class ProfileOtherFragment : Fragment(), ProfileCallBack, MenuProvider {
  protected lateinit var navController: NavController
  protected val vm: ProfileVM by lazy {
    ViewModelProvider(vmContext(), ProfileVMF(userShow))[ProfileVM::class.java]
  }
  protected lateinit var userShow: User
  protected val glide: RequestManager by lazy {
    Glide.with(requireContext())
  }
  private val numberFormatter: NumberFormat by lazy {
    NumberFormat.getInstance(LocaleHelper.getConfiguredLocale(requireContext()))
  }

  private var _backPressCB: OnBackPressedCallback? = null
  private var _binding: FProfileBinding? = null
  protected val binding get() = _binding!!
  protected val backPressCB get() = _backPressCB!!
  private var isMe: Boolean = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FProfileBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

    navController = findNavController()
    userShow = getUserToShow()

    isMe = userShow.id == PrefHelper.currentUserId

    // transition animation
    if (!isMe) {
      animateTransitionIfNeeded()
    }

    val pager: ViewPager2 = binding.viewPager

    pager.adapter = initAdapter()
    TabLayoutMediator(binding.tabLayout, pager) { tab: TabLayout.Tab, position: Int ->
      tab.text = getString(
        when (position) {
          0 -> R.string.posts
          1 -> R.string.videos
          2 -> R.string.likes
          else -> throw IndexOutOfBoundsException("position was $position")
        }
      )
    }.attach()
    registerBackClickListener(pager)

    // observers
    vm.user.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      applyUserInfo(it)
      userShow = it
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          if (it.unauthorized) {
            navController.navigateUp()
          }
          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }
        }
      }
    }

    // event
    val callBack = object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        backPressCB.isEnabled = position != 0
      }
    }
    pager.registerOnPageChangeCallback(callBack)

    binding.userCoverPicture.setOnClickListener {
      userShow.coverUrl?.let { cover ->
        navController.navigate(
          R.id.navigation_image_show,
          Bundle().also { arg -> arg.putString(Constants.URL, cover) })
      }
    }
    binding.followingValue.setOnClickListener {
      val fCount: Int = userShow.followingCount ?: 0

      if (fCount == 0) return@setOnClickListener

      showUsers(
        getString(R.string.followings), getString(R.string.following_user_url, userShow.id)
      )
    }
    binding.followersValue.setOnClickListener {
      val fCount: Int = userShow.followersCount ?: 0
      if (fCount == 0) return@setOnClickListener

      showUsers(
        getString(R.string.followers), getString(R.string.followers_user_url, userShow.id)
      )
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    backPressCB.remove()

    _binding = null
    _backPressCB = null
  }

  protected open fun applyUserInfo(user: User) {
    binding.apply {
      userFullName.text = user.fullName
      user.followersCount?.let {
        followersValue.text = numberFormatter.format(it)
        followersValue.isEnabled = it > 0
      }
      user.followingCount?.let {
        followingValue.text = numberFormatter.format(it)
        followingValue.isEnabled = it > 0
      }
      user.postsCount?.let {
        postsCountValue.text = numberFormatter.format(it)
      }

      user.username?.let {
        userName.text = it
        userName.isVisible = true
      }
      user.bio?.let { msg ->
        bioMessage.text = msg
        bioMessage.visibility = View.VISIBLE
      }
      badge.isVisible = user.verified

      glide.load(user.profileUrl).placeholder(R.drawable.profile_placeholder).into(profileImage)

      user.coverUrl?.let { coverUrl -> glide.load(coverUrl).into(userCoverPicture) }
    }
    buttonStatus(user)
  }

  protected open fun buttonStatus(user: User) {
    if (user.id == PrefHelper.currentUserId) {
      return
    }

    binding.editProfileBtn.text = getString(
      if (user.following) {
        R.string.following
      } else {
        R.string.follow
      }
    )
  }

  protected open fun vmContext(): ViewModelStoreOwner {
    return this
  }

  protected open fun getUserToShow(): User {
    val arg: ProfileOtherFragmentArgs by navArgs()

    return arg.user
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.profile_other_menu, menu)
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    return when (val id = menuItem.itemId) {
      android.R.id.home -> false

      R.id.user_block -> {

        true
      }

      else -> {
        navController.navigate(id)

        true
      }
    }
  }

  private fun initAdapter(): CustomTabAdapter {
    return CustomTabAdapter(childFragmentManager, viewLifecycleOwner.lifecycle).apply {
      setList(fragmentList())
    }
  }

  // list of tab items
  protected open fun fragmentList(): MutableList<Fragment> {
    // posts
    val list: MutableList<Fragment> = mutableListOf()
    val posts = ProfilePostsFragment().apply {
      arguments = Bundle().apply {
        putString(Constants.URL, Constants.userPosts(userShow.id))
        putBoolean(Constants.IS_ME, isMe)
      }
    }
    list.add(posts)

    // videos
    val shortVideo = ProfileVideosFragment()
    shortVideo.arguments = Bundle().apply {
      putString(Constants.URL, Constants.userVideos(userShow.id))
      putBoolean("is_mine", PrefHelper.currentUserId == userShow.id)
    }
    list.add(shortVideo)

    return list
  }

  // scroll to first tab if current tab item is not in index 0
  private fun registerBackClickListener(pager: ViewPager2) {
    _backPressCB = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (pager.currentItem != 0)
          pager.setCurrentItem(0, true)
      }
    }

    requireActivity().onBackPressedDispatcher.addCallback(backPressCB)
  }

  private fun showUsers(lbl: String, url: String) {
    val arg = Bundle().apply {
      putString(Constants.LBL_NAME, lbl)
      putString(Constants.URL, url)
      putBoolean(Constants.IS_SEARCH, false)
    }
    navController.navigate(R.id.open_users, arg, ApplicationHelper.slideAnimation)
  }

  private fun animateTransitionIfNeeded() {
    val transition: Transition =
      TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    sharedElementEnterTransition = transition
    sharedElementReturnTransition = transition
  }

  override fun openVideoShow(video: ShortVideo) {
    val arg = Bundle().apply {
      putParcelable(Constants.VIDEO, video)
    }
    navController.navigate(R.id.navigation_show_video, arg)
  }
}
