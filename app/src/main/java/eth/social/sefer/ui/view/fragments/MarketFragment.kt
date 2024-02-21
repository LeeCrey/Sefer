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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.R
import eth.social.sefer.callbacks.MarketCallBack
import eth.social.sefer.data.models.Product
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.ProductAdapter
import eth.social.sefer.ui.vm.MarketVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarketFragment : Fragment(R.layout.f_list_raw), MarketCallBack {
  private lateinit var navController: NavController
  private lateinit var refresh: SwipeRefreshLayout
  private val vm: MarketVM by viewModels()

  private lateinit var adapter: ProductAdapter
  private var isLoading = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    navController = findNavController()

    refresh = view as SwipeRefreshLayout
    refresh.isRefreshing = true

    initProductRV(view)

    // observer
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          if (it.unauthorized) {
            ApplicationHelper.backToLogin(requireActivity())
          } else {
            it.message?.let { msg ->
              ToastUtility.showToast(msg, requireContext())
            }
            isLoading = false
            refresh.isRefreshing = false
          }
        }
      }
    }
    vm.products.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      refresh.isRefreshing = false
      adapter.submitList(it)
    }

    // event
    refresh.setOnRefreshListener {
      if (!isInternetAvailable()) {
        refresh.isRefreshing = false
        return@setOnRefreshListener
      }

      vm.loadProducts(null, false)
    }
  }

  override fun openDetailScreen(product: Product) {
    navController.navigate(MarketFragmentDirections.openProductDetail(product))
  }

  override fun loadMore() {
    if (!isLoading && vm.isNextAvailable()) {
      vm.loadMore()
      isLoading = true
    }
  }

  private fun initProductRV(view: View) {
    adapter = ProductAdapter(requireContext(), this)

    val rv: RecyclerView = view.findViewById(R.id.recycler_view)
    rv.layoutManager = GridLayoutManager(requireContext(), 2)
    rv.setHasFixedSize(true)
    rv.adapter = adapter
  }
}