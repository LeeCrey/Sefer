package eth.social.sefer.ui.adapters

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.callbacks.CommentsCallBack
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.LCommentBinding
import eth.social.sefer.databinding.SPostBinding
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.lib.ReadMoreOption
import eth.social.sefer.lib.ResourceReadMore
import eth.social.sefer.ui.adapters.vh.CommentVH
import java.text.NumberFormat

class CommentAdapter(
  context: Context,
  private val callBack: CommentsCallBack
) : ListAdapter<Comment, CommentVH>(CommentDiffCalc()), ResourceReadMore.ReadMore {
  // view types
  private val postShowView: Int = 1
  private val loadMoreView: Int = 2
  private val commentView: Int = 3
  private val sendingComment: Int = 4

  private var inflater: LayoutInflater = LayoutInflater.from(context)

  var post: Post? = null
  private val imageLoader: RequestManager = Glide.with(context)
  private val user: User = PrefHelper.currentUser
  private val cw: ContextWrapper = ContextThemeWrapper(context, R.style.Theme_App_PopupMenu)
  lateinit var nf: NumberFormat
  private val readMoreOpt = ResourceReadMore(context, this)
  private val expandCollapse = ReadMoreOption.Builder(context)
    .textLength(300)
    .moreLabel(context.getString(R.string.expand))
    .lessLabel(context.getString(R.string.collapse))
    .moreLabelColor(context.getColor(R.color.link))
    .lessLabelColor(context.getColor(R.color.primary))
    .labelUnderLine(true)
    .build()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
    if (viewType == loadMoreView) {
      val view = inflater.inflate(R.layout.l_loading, parent, false)

      return CommentVH(view)
    }

    if (viewType == postShowView) {
      val binding: SPostBinding = SPostBinding.inflate(inflater, parent, false)
      binding.commentsIcon.setOnClickListener {
        callBack.openCreateComment(post!!)
      }
      binding.numberOfLikes.setOnClickListener {
        callBack.openLikedBy(post!!.id)
      }
      binding.upvote.setOnClickListener {
        callBack.togglePostVote(post!!, binding.upvote)
      }

      val vh = CommentVH(binding)
      vh.nf = nf
      vh.imageLoader = imageLoader

      return vh
    } else {
      val binding = LCommentBinding.inflate(inflater, parent, false)
      val vh = CommentVH(binding)
      vh.nf = nf
      vh.imageLoader = imageLoader
      if (viewType == sendingComment) {
        binding.commentStatus.isVisible = true
        binding.dropdownMenu.isVisible = false
        binding.subresourceCount.isVisible = false
        binding.upvote.isVisible = false

        return vh
      }

      val vote = binding.upvote
      val numberOfLikes = binding.numberOfLikes
      binding.repliesBtn.setOnClickListener {
        callBack.openReplies(getItem(vh.bindingAdapterPosition))
      }
      vote.setOnClickListener {
        callBack.toggleVote(vote, getItem(vh.bindingAdapterPosition), numberOfLikes)
      }
      binding.userProfilePicture.setOnClickListener {
        val comment = getItem(vh.bindingAdapterPosition)
        callBack.userProfileClick(comment.user!!)
      }
      binding.dropdownMenu.setOnClickListener {
        val position = vh.bindingAdapterPosition
        val comment = getItem(position)
        val popup = PopupMenu(cw, binding.dropdownMenu)
        val isUser = comment.user!!.id == user.id
        popup.inflate(
          if (isUser) R.menu.my_post_menu else R.menu.other_resource_menu
        )

        popup.setForceShowIcon(true)
        popup.setOnMenuItemClickListener {
          callBack.onOptionItemSelected(comment, it, position)

          false
        }

        popup.show()
      }
      binding.userFullName.setOnClickListener {
        val position = vh.bindingAdapterPosition
        val comment = getItem(position)
        if (comment.user!!.id == user.id) {
          return@setOnClickListener
        }

        callBack.opeProfile(comment.user!!)
      }

      return vh
    }
  }

  override fun onBindViewHolder(holder: CommentVH, position: Int) {
    val comment = getItem(position)
    // -2L indicates load more layout
    if (comment.id > 0) {
      holder.bindView(comment, readMoreOpt, position)
    } else if (comment.id == -1L) {
      // if user came through deep link. post will be null till it receives data from remote
      post?.let {
        holder.bindPost(it, inflater.context, expandCollapse)
      }
    } else if (comment.id == -2L) {
      callBack.loadMore()
    } else {
      holder.bindSending(user, comment, imageLoader)
    }
  }

  override fun getItemViewType(position: Int): Int {
    val comment: Comment = getItem(position)
    return if (comment.id > 0) {
      commentView
    } else if (comment.id == -1L) {
      postShowView
    } else if (comment.id == -2L) {
      loadMoreView
    } else {
      sendingComment
    }
  }

  class CommentDiffCalc : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
      return oldItem.id == newItem.id && oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
      return oldItem.areContentTheSame(newItem)
    }
  }

  override fun openDetailActivity(position: Int) {
    callBack.openReplies(getItem(position))
  }
}