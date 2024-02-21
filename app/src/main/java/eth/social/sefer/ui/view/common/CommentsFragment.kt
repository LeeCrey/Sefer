package eth.social.sefer.ui.view.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import eth.social.like_button.LikeButton
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.callbacks.CommentsCallBack
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.User
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.ClipboardUtils
import eth.social.sefer.helpers.LocaleHelper
import eth.social.sefer.ui.adapters.CommentAdapter
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.CommentVM
import eth.social.sefer.ui.vm.vmf.CommentVMF
import java.text.NumberFormat

abstract class CommentsFragment : Fragment(), CommentsCallBack {
  protected lateinit var navController: NavController
  protected lateinit var adapter: CommentAdapter
  protected lateinit var rv: RecyclerView

  private val numberFormatter: NumberFormat by lazy {
    NumberFormat.getInstance(LocaleHelper.getConfiguredLocale(requireContext()))
  }

  private var _msg: TextView? = null
  private var _refreshLayout: SwipeRefreshLayout? = null

  protected var isLoading = false
  protected var isShow = false

  protected val msg get() = _msg!!
  protected val refreshLayout get() = _refreshLayout!!

  private val vmf: CommentVMF by lazy { CommentVMF(endPoint(), isShow) }
  protected val vm: CommentVM by viewModels { vmf }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_list_raw, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    navController = findNavController()
    initRecyclerView(view)
    _msg = view.findViewById(R.id.message)
    _refreshLayout = view as SwipeRefreshLayout
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _msg = null
    _refreshLayout = null
  }

  override fun toggleVote(likeButton: LikeButton, comment: Comment, numberOfLikes: TextView) {
    if (!isInternetAvailable()) {
      ConnectionErrorDialog().show(childFragmentManager, null)
      return
    }

    vm.toggleCommentVote(comment, likeButton, numberOfLikes)
  }

  override fun onOptionItemSelected(comment: Comment, it: MenuItem, position: Int) {
    when (it.itemId) {
      R.id.copy_link -> ClipboardUtils.copyText(
        Constants.DOMAIN + "comments/${comment.id}", requireContext()
      )

      R.id.resource_hide -> {}

      R.id.delete_post -> {
        if (!isInternetAvailable()) {
          ConnectionErrorDialog().show(childFragmentManager, null)
        }
        vm.deleteComment(comment, position)
      }

      R.id.edit_post -> {
        openWriteComment(Bundle().apply {
          putParcelable(Constants.COMMENT, comment)
        })
      }
    }
  }

  override fun userProfileClick(user: User) {
    user.profileUrl?.let {
      val arg = Bundle().apply { putString(Constants.URL, it) }
      navController.navigate(R.id.navigation_image_show, arg)
    }
  }

  private fun initRecyclerView(view: View) {
    adapter = CommentAdapter(requireContext(), this)
    adapter.nf = numberFormatter
    rv = view.findViewById(R.id.recycler_view)
    rv.layoutManager = LinearLayoutManager(requireContext())
    rv.adapter = adapter
  }

  override fun loadMore() {
    if (!isLoading && vm.isNextAvailable()) {
      vm.loadMoreComments()
      isLoading = true
    }
  }

  override fun openLikedBy(id: Long) {
    val arg = Bundle().apply {
      putString(Constants.URL, Constants.postLikedUsers(id))
      putString(Constants.LBL_NAME, getString(R.string.liked_by))
    }
    navController.navigate(R.id.navigation_users, arg, ApplicationHelper.slideAnimation)
  }

  protected open fun attachViewModelObserver() {
    vm.comments.observe(viewLifecycleOwner) { list ->
      adapter.submitList(list)

      msg.isVisible = vm.shouldShowError()
    }
  }

  protected fun openWriteComment(arg: Bundle?) {
    navController.navigate(
      R.id.navigation_new_comment_activity, arg, ApplicationHelper.slideTopDown
    )
  }

  // varies in different screen
  abstract fun endPoint(): String
}