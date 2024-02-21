package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.CommunityCallBack
import eth.social.sefer.data.models.Community
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.CommunityAdapter
import eth.social.sefer.ui.vm.CommunityVM
import eth.social.sefer.ui.vm.vmf.CommunityVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class CommunityDiscover : Fragment(), CommunityCallBack {
  private var isLoading = false

  private val vm: CommunityVM by lazy {
    initVM()
  }
  protected lateinit var navController: NavController

  private var _adapter: CommunityAdapter? = null
  private var _refresh: SwipeRefreshLayout? = null

  private val refresh get() = _refresh!!
  private val adapter get() = _adapter!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_list_raw, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    _refresh = view.findViewById(R.id.refresh_layout)
    navController = findNavController()
    refresh.isRefreshing = !vm.isLoaded
    initRV(view)

    // observer
    vm.communities.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      vm.isLoaded = true
      refresh.isRefreshing = false
      adapter.submitList(it)
    }
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }

          if (it.unauthorized) {
            onUnAuthorized()
          }

          refresh.isRefreshing = false
          isLoading = false
        }
      }
    }

    // event
    refresh.setOnRefreshListener { vm.communityList(null, false) }
  }

  override fun onDestroyView() {
    _refresh = null
    _adapter = null

    super.onDestroyView()
  }

  private fun initRV(view: View) {
    _adapter = CommunityAdapter(requireContext(), this)

    val rv: RecyclerView = view.findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.setHasFixedSize(true)
    rv.adapter = adapter
  }

  override fun onCommunityClick(community: Community) {
  }

  override fun onLongClick(community: Community) {
    ToastUtility.showToast(getString(R.string.request_sent_wait), requireContext())
    vm.joinCommunity(community)
  }

  override fun loadMore() {
    if (!isLoading && vm.isNextAvailable()) {
      vm.loadMore()
      isLoading = true
    }
  }

  protected open fun initVM(): CommunityVM {
    return ViewModelProvider(
      this, CommunityVMF(Constants.DISCOVER_COMMUNITY_URL)
    )[CommunityVM::class.java]
  }

  protected open fun onUnAuthorized() {
    ApplicationHelper.backToLogin(requireActivity())
  }
}