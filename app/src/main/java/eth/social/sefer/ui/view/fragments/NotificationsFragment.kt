package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.sefer.R
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.ui.adapters.NotificationAdapter


class NotificationsFragment : Fragment(R.layout.f_list_raw), MenuProvider {
  private lateinit var refresh: SwipeRefreshLayout
  private lateinit var adapter: NotificationAdapter

  //    private val vm: NotificationVM? = null
  var isLoading = false

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    initNotifications(view)
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.notification_menu, menu)
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    val id = menuItem.itemId

    if (id == android.R.id.home || !isInternetAvailable()) {
      return false
    }

    refresh.isRefreshing = true

    return true
  }

  private fun initNotifications(view: View) {
    adapter = NotificationAdapter(requireContext())

    val rv: RecyclerView = view.findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.adapter = adapter
  }
}