package eth.social.sefer.ui.vm

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.like_button.LikeButton
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.CommentRepo
import kotlinx.coroutines.flow.SharedFlow

open class CommentVM(url: String, isPostShow: Boolean) : ViewModel() {
  protected val repo = CommentRepo(url, isPostShow, viewModelScope)
  val response: SharedFlow<ApiResponse> = repo.response

  // comment to edit/update
  var comment: Comment? = null

  // list of comments
  val comments: LiveData<List<Comment>> = repo.comments

  init {
    commentsList(null, false)
  }

  fun commentsList(page: Int?, append: Boolean) = repo.commentsList(page, append)

  fun deleteComment(
    comment: Comment, position: Int
  ) = repo.deleteComment(comment, position)

  fun toggleCommentVote(comment: Comment, likeButton: LikeButton, numberOfLikes: TextView) =
    repo.toggleVote(comment, likeButton, numberOfLikes)

  fun togglePostVotes(post: Post, upvote: LikeButton) = repo.togglePostVote(post, upvote)

  fun loadMoreComments() = repo.loadMoreComments()

  fun isNextAvailable(): Boolean = repo.isNextAvailable()
  fun shouldShowError(): Boolean = repo.shouldShowError()
}