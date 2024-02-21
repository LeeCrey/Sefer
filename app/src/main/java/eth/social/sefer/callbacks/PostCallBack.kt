package eth.social.sefer.callbacks

import android.view.MenuItem
import android.widget.TextView
import eth.social.like_button.LikeButton
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User
import eth.social.sefer.ui.custom.HashTagHelper

interface PostCallBack : HashTagHelper.OnHashTagClickListener, OnLoadMoreListener {
  fun onImageClick(url: String)

  fun onVoteClick(
    post: Post, likeButton: LikeButton, numberOfLikes: TextView
  )

  fun showPost(post: Post)

  fun showLikedUsers(post: Post)

  fun showCommentSection(post: Post)

  fun openUserProfile(user: User)

  fun onOptionItemSelected(post: Post, item: MenuItem)

  fun readText(content: String)
}