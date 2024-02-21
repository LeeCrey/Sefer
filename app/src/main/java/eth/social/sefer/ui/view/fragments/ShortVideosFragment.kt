package eth.social.sefer.ui.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.VideoOnly
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.like_button.LikeButton
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.ShortVideoCallBack
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.databinding.FShortsBinding
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.backToLogin
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.adapters.ShortVideoAdapter
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.ShortVideoVM
import eth.social.sefer.ui.vm.vmf.VideoVMF
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


open class ShortVideosFragment : Fragment(), ShortVideoCallBack, MenuProvider {
  private val vmf: VideoVMF by lazy { VideoVMF(Constants.SHORT_VIDEOS) }
  private val vm: ShortVideoVM by activityViewModels { vmf }

  private lateinit var navController: NavController
  private lateinit var backPressCB: OnBackPressedCallback

  //  private var _rv: SwipeVideoPlayerView? = null
//  private var _rv: RecyclerView? = null

  private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
  private var _binding: FShortsBinding? = null
  private val binding get() = _binding!!
//  private val rv get() = _rv!!

  private var isLoading = false

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FShortsBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

    navController = findNavController()
    val adapter = initRecycler()
    val refresh: SwipeRefreshLayout = view as SwipeRefreshLayout
    refresh.isRefreshing = true
    pickMedia = ApplicationHelper.setupPickMedia(this, this)

    // observer
    vm.shortVideos.observe(viewLifecycleOwner) {
      if (it == null) {
        return@observe
      }

      isLoading = false
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
            backToLogin(requireActivity())
            return@collectLatest
          }

          isLoading = false
          refresh.isRefreshing = false
        }
      }
    }

    // event
    registerBackPress()
    refresh.setOnRefreshListener {
      if (!isInternetAvailable() || isLoading) {
        refresh.isRefreshing = false
        return@setOnRefreshListener
      }

      vm.listOfVideos(null, false)
    }
  }

//  override fun onResume() {
//    super.onResume()
//
//    rv.startPlaying()
//  }

//  override fun onPause() {
//    super.onPause()
//
//    rv.pauseVideo()
//  }

  override fun onDestroyView() {
    super.onDestroyView()

    backPressCB.remove()
//    rv.releasePlayer()

    pickMedia = null
    _binding = null
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.shorts_menu, menu)
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    return when (menuItem.itemId) {
      R.id.upload_video -> {
        if (isInternetAvailable()) {
          pickMedia?.launch(
            PickVisualMediaRequest.Builder().setMediaType(VideoOnly).build()
          )
        } else {
          ConnectionErrorDialog().show(childFragmentManager, null)
        }
        true
      }

      R.id.speaker -> {
//        rv.toggleVolume()
//        menuItem.setIcon(
//          ContextCompat.getDrawable(
//            requireContext(), if (rv.isVolumeStateMuted()) {
//              R.drawable.ic_muted
//            } else {
//              R.drawable.ic_speaker
//            }
//          )
//        )
        true
      }

      else -> false
    }
  }

  override fun onMediaSelected(uri: Uri) {
    vm.uploadVideo(uri)
  }

  override fun onCommentClick(video: ShortVideo) {
    navController.navigate(
      ShortVideosFragmentDirections.openComments(Constants.videoCommentsUrl(video.id))
    )
  }

  override fun onVoteClick(btn: LikeButton, video: ShortVideo) {
    if (isInternetAvailable()) {
      vm.voteVideo(btn, video)
    }
  }

  override fun onProfileClick(video: ShortVideo) {
    ToastUtility.showToast("Not implemented6", requireContext())
  }

  override fun onOptionClicked(video: ShortVideo) {
    navController.navigate(ShortVideosFragmentDirections.openOption(video))
  }

  override fun enableOrDisableBackPress(enable: Boolean) {
    backPressCB.isEnabled = enable
  }

  override fun currentPosition(position: Int) {
    println("currentPosition: $position")
  }

  private fun registerBackPress() {
    backPressCB = object : OnBackPressedCallback(false) {
      override fun handleOnBackPressed() {
//        rv.smoothScrollToPosition(0)
      }
    }
    requireActivity().onBackPressedDispatcher.addCallback(backPressCB)
  }

  private fun initRecycler(): ShortVideoAdapter {
    val adapter = ShortVideoAdapter(requireContext(), this)

    val pager = binding.recyclerView
//    pager.setPageTransformer()
    pager.adapter = adapter
//    _rv = binding.recyclerView
//    rv.vm = vm
//    rv.adapter = adapter
//    rv.setHasFixedSize(true)
//    rv.layoutManager =
//      LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//    PagerSnapHelper().attachToRecyclerView(rv)

//    rv.loadMore = object : OnLoadMoreListener {
//      override fun loadMore() {
//        if (!isLoading) {
//          vm.loadMore()
//          isLoading = true
//        }
//      }
//    }

    return adapter
  }
}