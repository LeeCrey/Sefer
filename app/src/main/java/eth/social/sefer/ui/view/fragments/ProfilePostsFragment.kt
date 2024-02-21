package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.ProfilePostCallBack
import eth.social.sefer.data.models.Community
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.ui.adapters.PostAdapter
import eth.social.sefer.ui.view.common.PostsUI
import eth.social.sefer.ui.vm.PostVM
import eth.social.sefer.ui.vm.vmf.PostVMF

// posts on Profile Section either liked or posts
class ProfilePostsFragment : PostsUI(), ProfilePostCallBack {
  private var _adapter: PostAdapter? = null

  private val adapter get() = _adapter!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_list, container, false)

  override fun onDestroyView() {
    super.onDestroyView()

    _adapter = null
  }

  override fun initViewModel(): PostVM {
    return ViewModelProvider(vmOwner(), PostVMF(postsUrl()))[PostVM::class.java]
  }

  override fun attachObserver() {
    vm.posts.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      adapter.submitList(it)
      isLoading = false
    }
  }

  override fun onUnAuthorized() {
    // leave empty
    // unauthorized may happen when a user visits another person's profile and that
    // person profile is private and the user is not following but
    // if for me, logout which means jwt token is expired
    if (requireArguments().getBoolean(Constants.IS_ME, false)) {
      ApplicationHelper.backToLogin(requireActivity())
    }
  }

  override fun initRecyclerView() {
    _adapter = PostAdapter(requireContext(), this)

    adapter.nf = numberFormatter
    val rv: RecyclerView = requireView().findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.adapter = adapter
  }

  override fun loadMore() {
    if (!isLoading && vm.isNextAvailable()) {
      vm.loadMorePosts()
      isLoading = true
    }
  }

  override fun vmOwner(): ViewModelStoreOwner {
    return this
  }

  override fun postsUrl(): String {
    return requireArguments().getString(Constants.URL)!!
  }

  override fun openCommunity(community: Community) {
    val arg = Bundle().apply {
      putString(Constants.LBL_NAME, community.name)
      putParcelable(Constants.COMMUNITY, community)
    }
    navController.navigate(
      R.id.navigation_community_show,
      arg,
      ApplicationHelper.slideAnimation
    )
  }

  override fun openUserProfile(user: User) {}
}