package eth.social.sefer.callbacks

import android.view.MenuItem
import android.widget.TextView
import eth.social.like_button.LikeButton
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User

interface CommentsCallBack : OnLoadMoreListener {
  fun toggleVote(likeButton: LikeButton, comment: Comment, numberOfLikes: TextView)
  fun userProfileClick(user: User)
  fun opeProfile(user: User)
  fun onOptionItemSelected(comment: Comment, it: MenuItem, position: Int)
  fun openReplies(comment: Comment)
  fun openCreateComment(post: Post)
  fun openLikedBy(id: Long)
  fun togglePostVote(post: Post, upvote: LikeButton)
}