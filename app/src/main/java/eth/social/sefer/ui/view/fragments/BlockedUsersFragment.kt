package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.callbacks.UnblockCallBack
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.BlockedUserAdapter
import eth.social.sefer.ui.vm.UsersVM
import eth.social.sefer.ui.vm.vmf.UsersVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BlockedUsersFragment : Fragment(R.layout.f_blocked_users), UnblockCallBack {
  private val vm: UsersVM by viewModels { UsersVMF(Constants.BLOCKED_USERS) }
  private lateinit var adapter: BlockedUserAdapter

  private var isLoading = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    ApplicationHelper.setUpToolBar(view, R.id.tool_bar, this)

    initRecyclerView()
    val refresh: SwipeRefreshLayout = view.findViewById(R.id.refresh_layout)
    refresh.isRefreshing = !vm.isLoaded

    vm.users.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      adapter.submitList(it)
      isLoading = false
      vm.isLoaded = true
      refresh.isRefreshing = false
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }

          refresh.isRefreshing = false
          isLoading = false
        }
      }
    }

    // event
    refresh.setOnRefreshListener {
      if (!isInternetAvailable()) {
        refresh.isRefreshing = false
        return@setOnRefreshListener
      }
      isLoading = true

      vm.listOfUsers(1, false)
    }
  }

  override fun unblockUser(user: User) {
    vm.unblockUser(user)
  }

  private fun initRecyclerView() {
    adapter = BlockedUserAdapter(requireContext(), this)

    val rv: RecyclerView = requireView().findViewById(R.id.recycler_view)
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