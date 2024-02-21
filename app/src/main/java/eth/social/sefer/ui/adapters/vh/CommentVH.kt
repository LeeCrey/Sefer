package eth.social.sefer.ui.adapters.vh

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.LCommentBinding
import eth.social.sefer.databinding.SPostBinding
import eth.social.sefer.lib.ReadMoreOption
import eth.social.sefer.lib.ResourceReadMore
import java.text.NumberFormat

class CommentVH(root: View) : RecyclerView.ViewHolder(root) {
  private lateinit var binding: LCommentBinding
  private lateinit var postBinding: SPostBinding
  lateinit var nf: NumberFormat
  lateinit var imageLoader: RequestManager

  constructor(commentBinding: LCommentBinding) : this(commentBinding.root) {
    binding = commentBinding
  }

  constructor(postBinding: SPostBinding) : this(postBinding.root) {
    this.postBinding = postBinding
  }

  fun bindPost(
    post: Post, context: Context, expandCollapse: ReadMoreOption
  ) {
    bindUser(post.user!!, imageLoader, false)

    postBinding.apply {
      // post
      post.content?.let { content ->
        expandCollapse.addReadMoreTo(postContent, content)
      }
      post.createdAt?.let {
        createdAt.text = DateUtils.getRelativeTimeSpanString(it)
      }

      post.thumb?.let {
        imageLoader.load(it).into(postImage)
        postImage.visibility = View.VISIBLE
      }

      separator.isVisible = post.isCommentAndPostPositive

      if (post.commentsCount > 0) {
        subresourceCount.text =
          context.getString(R.string.comments_count, nf.format(post.commentsCount))
      }
      if (post.votesCount > 0) {
        numberOfLikes.text = context.getString(R.string.likes_count, nf.format(post.votesCount))
      }
    }
  }

  fun bindView(
    comment: Comment,
    readMoreOption: ResourceReadMore,
    position: Int
  ) {
    bindUser(comment.user!!, imageLoader)

    binding.apply {
      comment.createdAt?.let { createdAt.text = DateUtils.getRelativeTimeSpanString(it) }
      comment.likesCount?.let(numberOfLikes::setText)
      if (comment.likesCount == null) {
        numberOfLikes.isVisible = false
      }
      comment.repliesCount?.let(subresourceCount::setText)
      if (comment.repliesCount == null) {
        subresourceCount.isVisible = false
      }
      comment.imageUrl?.let {
        imageLoader.load(it).into(postImage)
        postImage.visibility = View.VISIBLE
      }
    }
    comment.content?.let {
      readMoreOption.addReadMoreTo(binding.postContent, it, position)
    }
  }

  fun bindSending(user: User, comment: Comment, imageLoader: RequestManager) {
    bindUser(user, imageLoader)
    binding.postContent.text = comment.content
  }

  private fun bindUser(user: User, imageLoader: RequestManager, isComment: Boolean = true) {
    if (isComment) {
      // user
      binding.apply {
        imageLoader.load(user.profileUrl).placeholder(R.drawable.profile_placeholder)
          .into(userProfilePicture)
        userName.text = user.username
        userFullName.text = user.fullName
        if (user.verified) {
          badge.visibility = View.VISIBLE
        }
      }
    } else {
      postBinding.apply {
        imageLoader.load(user.profileUrl).placeholder(R.drawable.profile_placeholder)
          .into(userProfilePicture)
        userName.text = user.username
        userFullName.text = user.fullName
        if (user.verified) {
          badge.visibility = View.VISIBLE
        }
      }
    }
  }
}