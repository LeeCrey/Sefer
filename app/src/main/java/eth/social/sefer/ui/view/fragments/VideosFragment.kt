package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.R
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.callbacks.VideoCallBack
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.ui.adapters.VideoAdapter
import eth.social.sefer.ui.vm.ShortVideoVM
import eth.social.sefer.ui.vm.vmf.VideoVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class VideosFragment : Fragment(), VideoCallBack {
  private lateinit var vm: ShortVideoVM
  private lateinit var navController: NavController

  private var _adapter: VideoAdapter? = null
  private val adapter get() = _adapter!!
  private var isLoading = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_list_raw, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initRecycler()
    vm = initVM()
    navController = findNavController()

    val msg: TextView? = view.findViewById(R.id.message)

    // observer
    vm.shortVideos.observe(viewLifecycleOwner) { list ->
      if (list == null) {
        return@observe
      }

      // change with meta data
      msg?.visibility = if (list.isEmpty()) {
        View.VISIBLE
      } else {
        View.GONE
      }

      isLoading = false
      adapter.submitList(list)
    }

    initRefreshLayout(false)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _adapter = null
  }

  override fun videoClicked(video: ShortVideo) {
    // empty
  }

  protected open fun initRefreshLayout(isProfile: Boolean) {
    // in profile section view contains only recycler view.
    if (isProfile) {
      return
    }

    val refresh: SwipeRefreshLayout = requireView().findViewById(R.id.refresh_layout)
    refresh.isRefreshing = true

    // observer
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          refresh.isRefreshing = false
        }
      }
    }

    //
    refresh.setOnRefreshListener {
      if (!isInternetAvailable() || isLoading) {
        return@setOnRefreshListener
      }

      vm.listOfVideos(null, false)
    }
  }

  protected open fun initVM(): ShortVideoVM {
    return ViewModelProvider(vmOwner(), VideoVMF(endPoint()!!))[ShortVideoVM::class.java]
  }

  // if a user is visiting it's profile, then view model should be scoped to activity else
  // scoped to fragment because user profile is top level destination.
  // others people's data should not be persisted
  // third condition here is in videos fr
  protected open fun vmOwner(): ViewModelStoreOwner {
    val isMine = requireArguments().getBoolean("is_mine", false)
    return if (isMine) requireActivity() else this
  }

  protected open fun endPoint(): String? {
    return requireArguments().getString("url")
  }

  private fun initRecycler() {
    _adapter = VideoAdapter(requireContext(), this)
    val rv: RecyclerView = requireView().findViewById(R.id.recycler_view)
    val lm = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    rv.layoutManager = lm
    rv.setHasFixedSize(true)
    rv.adapter = adapter

    // event
    adapter.loadMore = object : OnLoadMoreListener {
      override fun loadMore() {
        if (!isLoading && vm.isNextAvailable()) {
          vm.loadMore()
        }
      }
    }
  }
}