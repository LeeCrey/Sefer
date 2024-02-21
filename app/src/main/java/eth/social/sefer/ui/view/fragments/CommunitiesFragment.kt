package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.leinardi.android.speeddial.SpeedDialView
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.CommunityCallBack
import eth.social.sefer.data.models.Community
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.vm.CommunityVM
import eth.social.sefer.ui.vm.vmf.CommunityVMF

class CommunitiesFragment : CommunityDiscover(), CommunityCallBack {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_communities, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val fb: SpeedDialView = view.findViewById(R.id.fab)
    fb.inflate(R.menu.sp_menu)
    fb.setOnActionSelectedListener {
      when (it.id) {
        R.id.create_community ->
          navController.navigate(CommunitiesFragmentDirections.openCreateCommunity())

        R.id.discover_community -> {
          navController.navigate(
            R.id.navigation_community_discover,
            null,
            ApplicationHelper.slideAnimation
          )
        }
      }
      false
    }
  }

  override fun onCommunityClick(community: Community) {
    val arg = Bundle().apply {
      putString(Constants.LBL_NAME, community.name)
      putParcelable(Constants.COMMUNITY, community)
    }
    navController.navigate(R.id.navigation_community_show, arg, ApplicationHelper.slideAnimation)
  }

  override fun onLongClick(community: Community) {
    ToastUtility.showToast("onLong click", requireContext())
  }

  override fun initVM(): CommunityVM {
    return ViewModelProvider(
      requireActivity(), CommunityVMF(Constants.COMMUNITIES_URL)
    )[CommunityVM::class.java]
  }
}