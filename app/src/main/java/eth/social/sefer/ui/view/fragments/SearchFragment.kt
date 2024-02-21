package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.callbacks.UserCallBack
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.helpers.VibrateUtils
import eth.social.sefer.ui.adapters.UserAdapter
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.UsersVM
import eth.social.sefer.ui.vm.vmf.UsersVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class SearchFragment : Fragment(R.layout.f_list_raw), UserCallBack, MenuProvider {
  private lateinit var navController: NavController
  private lateinit var vm: UsersVM
  private lateinit var adapter: UserAdapter
  private val currentUserId: Long by lazy {
    PrefHelper.currentUserId
  }

  private var _refresh: SwipeRefreshLayout? = null
  private val refresh get() = _refresh!!

  private var isLoading = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

    navController = findNavController()
    vm = getViewModel()
    _refresh = view as SwipeRefreshLayout
    refresh.isRefreshing = !vm.isLoaded

    initRecyclerView(view)

    // observer
    vm.users.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      isLoading = false
      vm.isLoaded = true
      refresh.isRefreshing = false
      adapter.submitList(it)
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          it.message?.let { msg -> ToastUtility.showToast(msg, requireContext()) }
          if (it.unauthorized) {
            navController.navigateUp()
          }

          isLoading = false
          refresh.isRefreshing = false
        }
      }
    }

    // event
    refresh.setOnRefreshListener {
      if (!isInternetAvailable()) {
        refresh.isRefreshing = false
        return@setOnRefreshListener
      }

      vm.listOfUsers(1, false)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _refresh = null
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.search_menu, menu)

    val searchItem: MenuItem = menu.findItem(R.id.search)
    val searchView: SearchView = searchItem.actionView as SearchView
    searchView.queryHint = getString(R.string.search_people)
    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String): Boolean {
        if (!searchView.isIconified) {
          searchView.isIconified = true
        }

        if (refresh.isRefreshing) {
          ToastUtility.showToast(getString(R.string.wait), requireContext())
          return false
        }

        if (!isInternetAvailable()) {
          ConnectionErrorDialog().show(childFragmentManager, null)
          return false
        }

        vm.searchUser(query, null, false)

        return true
      }

      override fun onQueryTextChange(s: String): Boolean {
        return false
      }
    })
  }

  override fun onUserFollow(user: User, button: TextView, position: Int) {
    if (!isInternetAvailable()) {
      ConnectionErrorDialog().show(childFragmentManager, null)
      return
    }

    VibrateUtils.vibrate(150, requireContext())

    vm.followOrUnfollow(user, button, position)
  }

  override fun showProfile(user: User, profile: ImageView) {
    if (currentUserId == user.id) {
      return
    }

    val extras = FragmentNavigatorExtras(profile to "user_show_profile")
    val arg = Bundle().apply {
      putString(Constants.USERNAME, user.fullName!!)
      putParcelable(Constants.USER, user)
    }
    navController.navigate(R.id.show_user_profile, arg, null, extras)
  }

  override fun userProfileClick(user: User) {
    user.profileUrl?.let {
      val arg = Bundle().apply {
        putString(Constants.URL, it)
      }
      navController.navigate(R.id.navigation_image_show, arg)
    }
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    if (menuItem.itemId == R.id.search || menuItem.itemId == android.R.id.home) {
      return false
    }

    return NavigationUI.onNavDestinationSelected(menuItem, navController)
  }

  // for users search screen bind to activity else to fragment itself
  protected open fun vmOwner(): ViewModelStoreOwner {
    return requireActivity()
  }

  private fun getViewModel(): UsersVM {
    return ViewModelProvider(vmOwner(), UsersVMF(endPoint()))[UsersVM::class.java]
  }

  protected open fun endPoint(): String {
    return Constants.USERS_URL
  }

  private fun initRecyclerView(view: View) {
    adapter = UserAdapter(requireContext(), this)

    val rv: RecyclerView = view.findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.setHasFixedSize(true)
    rv.adapter = adapter

    // event
    adapter.loadMoreListener = object : OnLoadMoreListener {
      override fun loadMore() {
        if (!isLoading && vm.isNextAvailable()) {
          vm.loadMoreUsers()
          isLoading = true
        }
      }
    }
  }
}