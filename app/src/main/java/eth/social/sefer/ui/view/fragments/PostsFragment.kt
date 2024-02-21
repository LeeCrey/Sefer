package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.PostCallBack
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.PostAdapter
import eth.social.sefer.ui.view.common.PostsUI
import eth.social.sefer.ui.vm.PostVM
import eth.social.sefer.ui.vm.vmf.PostVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class PostsFragment : PostsUI(), PostCallBack {
  private var _adapter: PostAdapter? = null
  private val adapter get() = _adapter!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    refresh.isRefreshing = !vm.isLoaded

    // event
    refresh.setOnRefreshListener {
      if (!isInternetAvailable() || isLoading) {
        refresh.isRefreshing = false
        return@setOnRefreshListener
      }

      vm.listOfPosts(null, false)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _adapter = null
  }

  // leave empty
  override fun openUserProfile(user: User) {
    // TODO check if user is itself or not
  }

  override fun postsUrl(): String {
    return requireArguments().getString(Constants.URL)!!
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
      refresh.isRefreshing = false
      isLoading = false
      vm.isLoaded = true
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          it.message?.let { msg ->
            val snackBar = ToastUtility.snackBarInstance(msg, requireView())
            snackBar.setAction(R.string.retry) {
              mRefresh?.isRefreshing = true
              vm.retry()
            }
            snackBar.show()
          }

          if (it.unauthorized) {
            onUnAuthorized()
          } else {
            mRefresh?.isRefreshing = false
          }
        }
      }
    }
  }

  override fun onUnAuthorized() {
    navController.navigateUp()
  }

  override fun loadMore() {
    if (!isLoading && vm.isNextAvailable()) {
      vm.loadMorePosts()
      isLoading = true
    }
  }

  override fun initRecyclerView() {
    _adapter = PostAdapter(requireContext(), this)
    adapter.nf = numberFormatter

    val rv: RecyclerView = requireView().findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.adapter = adapter
  }

  override fun vmOwner(): ViewModelStoreOwner {
    return this
  }
}
