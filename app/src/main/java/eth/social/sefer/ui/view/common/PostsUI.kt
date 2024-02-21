package eth.social.sefer.ui.view.common

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.like_button.LikeButton
import eth.social.sefer.Constants
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.callbacks.PostCallBack
import eth.social.sefer.data.models.Post
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ClipboardUtils
import eth.social.sefer.helpers.LocaleHelper
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.PostVM
import java.text.NumberFormat

/*
  **********************************************
  @Owner Solomon Boloshe(lee_crey), 2024 Jan, 20
  **********************************************

  partial implementation for post list
  common for post list screen
 */
abstract class PostsUI : Fragment(), PostCallBack {
  protected var mRefresh: SwipeRefreshLayout? = null
  private var _textToSpeech: TextToSpeech? = null

  protected lateinit var navController: NavController
  protected val vm: PostVM by lazy { initViewModel() }

  protected var isLoading = false
  protected val refresh get() = mRefresh!!
  private val textToSpeech get() = _textToSpeech!!
  protected val numberFormatter: NumberFormat by lazy {
    NumberFormat.getInstance(LocaleHelper.getConfiguredLocale(requireContext()))
  }

  // This implementation might be override
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_list_raw, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    navController = findNavController()
    mRefresh = view.findViewById(R.id.refresh_layout)
    _textToSpeech = TextToSpeech(MyApp.instance) {}

    // never change the order
    initRecyclerView()
    attachObserver()
  }

  override fun onDestroyView() {
    super.onDestroyView()

    textToSpeech.shutdown()
    mRefresh = null
    _textToSpeech = null

    // prevent memory leak
    vm.posts.removeObservers(viewLifecycleOwner)
  }

  override fun readText(content: String) {
    textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
  }

  override fun showLikedUsers(post: Post) {
    val arg = Bundle().apply {
      putString(Constants.URL, Constants.postLikedUsers(post.id))
      putBoolean(Constants.IS_SEARCH, false)
      putString(Constants.LBL_NAME, getString(R.string.liked_by))
    }
    navController.navigate(R.id.navigation_users, arg, ApplicationHelper.slideAnimation)
  }

  override fun onImageClick(url: String) {
    val arg = Bundle().apply { putString(Constants.URL, url) }
    navController.navigate(R.id.navigation_image_show, arg)
  }

  override fun onVoteClick(
    post: Post, likeButton: LikeButton, numberOfLikes: TextView
  ) {
    if (!isInternetAvailable()) {
      ConnectionErrorDialog().show(childFragmentManager, null)
      return
    }

    vm.votePost(post, likeButton, numberOfLikes, numberFormatter)
  }

  override fun showCommentSection(post: Post) {
    val arg = Bundle().apply { putParcelable(Constants.POST, post) }
    openWriteResource(R.id.navigation_new_comment_activity, arg)
  }

  override fun showPost(post: Post) {
    val arg = Bundle().apply {
      putParcelable("commentable", post)
      putString(Constants.URL, Constants.postCommentsUrl(post.id))
    }
    navController.navigate(R.id.navigation_comments, arg, ApplicationHelper.slideAnimation)
  }

  override fun onHashTagClicked(hashTag: String?) {
    if (hashTag == null) {
      return
    }

    val arg = Bundle().apply {
      putString(Constants.LBL_NAME, "#$hashTag")
      putString(Constants.URL, "/api/v1/posts/search?tag=#$hashTag")
    }

    navController.navigate(R.id.navigation_posts, arg, ApplicationHelper.slideAnimation)
  }

  override fun onOptionItemSelected(post: Post, item: MenuItem) {
    when (item.itemId) {
      R.id.copy_link -> ClipboardUtils.copyText(
        Constants.postUrl(post.id),
        requireContext()
      )

      R.id.resource_hide -> vm.reportPost(post)

      R.id.edit_post -> {
        val arg = Bundle().apply {
          putParcelable(Constants.POST, post)
        }
        navController.navigate(R.id.navigation_new_post, arg)
      }

      // only in profile
      R.id.delete_post -> vm.deletePost(post)

      else -> {}
    }
  }

  protected fun openWriteResource(direction: Int, arg: Bundle?) {
    navController.navigate(direction, arg, ApplicationHelper.slideTopDown)
  }

  // These may be different deepens up on the screen
  // Example uri that data should be fetched can be different
  // refresh layout may not exist
  // action to be taken on unauthorized request can be different
  // adapter to be attached to recycler view can be(Post, PostCommunity, extra can be added)
  // view model scope?(fragment or activity)
  protected abstract fun initViewModel(): PostVM
  protected abstract fun attachObserver()
  protected abstract fun onUnAuthorized()
  protected abstract fun initRecyclerView()
  protected abstract fun vmOwner(): ViewModelStoreOwner
  protected abstract fun postsUrl(): String
}
