package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.R
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.ReplyAdapter
import eth.social.sefer.ui.vm.ReplyVM
import eth.social.sefer.ui.vm.vmf.ReplyVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReplyFragment : Fragment(R.layout.f_list_raw) {
  private var isLoading: Boolean = false
  private var _adapter: ReplyAdapter? = null

  private val args: ReplyFragmentArgs by navArgs()
  private val vm: ReplyVM by viewModels { ReplyVMF(args.commentId) }
  private lateinit var navController: NavController

  private val adapter get() = _adapter!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initRv()

    navController = findNavController()
    val refresh: SwipeRefreshLayout = view.findViewById(R.id.refresh_layout)
    refresh.isRefreshing = !vm.isLoaded

    // observer
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          if (it.unauthorized) {
            navController.navigateUp()
          }
          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }
          refresh.isRefreshing = false
        }
      }
    }
    vm.replies.observe(viewLifecycleOwner) {
      adapter.submitList(it)
      isLoading = false
      refresh.isRefreshing = false
    }

    //
    refresh.setOnRefreshListener {
      if (!isInternetAvailable()) {
        refresh.isRefreshing = false
        return@setOnRefreshListener
      }

      vm.loadReplies(null)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _adapter = null
  }

  private fun initRv() {
    _adapter = ReplyAdapter(requireContext())

    val rv: RecyclerView = requireView().findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.adapter = adapter

    //
    adapter.loadMoreListener = object : OnLoadMoreListener {
      override fun loadMore() {
        if (!isLoading && vm.isNextAvailable) {
          vm.loadMore()
          isLoading = true
        }
      }
    }
  }
}