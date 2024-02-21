package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import eth.social.like_button.LikeButton
import eth.social.sefer.Constants
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.view.common.CommentsFragment
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// the same as CommentsDialog except showing post
// since class can not extend multiple class, i copy pasted
class PostShowFragment : CommentsFragment() {
  private val args: PostShowFragmentArgs by navArgs()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val post = args.commentable
    isShow = true

    super.onViewCreated(view, savedInstanceState)

    adapter.post = post
    attachViewModelObserver()

    // event
    refreshLayout.setOnRefreshListener {
      refreshLayout.isRefreshing = false

      if (!isInternetAvailable()) {
        return@setOnRefreshListener
      }

      vm.commentsList(null, false)
    }
  }

  override fun openReplies(comment: Comment) {
    navController.navigate(PostShowFragmentDirections.showReplies(comment.id))
  }

  override fun openCreateComment(post: Post) {
    openWriteComment(Bundle().apply { putParcelable(Constants.POST, post) })
  }

  override fun togglePostVote(post: Post, upvote: LikeButton) {
    if (!isInternetAvailable()) {
      ConnectionErrorDialog().show(childFragmentManager, null)
      return
    }

    vm.togglePostVotes(post, upvote)
  }

  override fun opeProfile(user: User) {
    navController.navigate(
      PostShowFragmentDirections.showUserProfile(user.fullName!!, user)
    )
  }

  override fun attachViewModelObserver() {
    super.attachViewModelObserver()

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest { response ->
          response.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }

          if (response.unauthorized) navController.navigateUp()

          isLoading = false
        }
      }
    }
  }

  override fun endPoint(): String {
    return args.url
  }
}