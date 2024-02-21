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
import eth.social.sefer.callbacks.PostCallBack
import eth.social.sefer.data.models.Post
import eth.social.sefer.databinding.LPostBinding
import eth.social.sefer.helpers.InputHelper
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.lib.ResourceReadMore
import eth.social.sefer.ui.adapters.vh.PostVH
import eth.social.sefer.ui.custom.HashTagHelper
import java.text.NumberFormat


// posts in home page(screen)
class PostAdapter(
  context: Context,
  private val callBack: PostCallBack
) : ListAdapter<Post, PostVH>(PostDiffCalc()), ResourceReadMore.ReadMore {
  private val loadMore = -1 /* Item Type */
  private val communityPost = 0

  lateinit var nf: NumberFormat

  // default is true
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val glide: RequestManager = Glide.with(context)
  private val userId: Long = PrefHelper.currentUserId
  private val cw: ContextWrapper = ContextThemeWrapper(context, R.style.Theme_App_PopupMenu)
  private val markdown = InputHelper.buildMarkWon(context)
  private val readMoreOption = ResourceReadMore(context, this)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
    if (viewType == loadMore) {
      val loadMoreView = inflater.inflate(R.layout.l_loading, parent, false)

      return PostVH(loadMoreView)
    }

    val binding = LPostBinding.inflate(inflater, parent, false)
    val vh = PostVH(binding, nf)
    vh.readMore = readMoreOption
    vh.markdown = markdown
    val isComPost = viewType == communityPost
    binding.lblIn.isVisible = isComPost
    binding.communityName.isVisible = isComPost
    registerClickEventHandler(vh)

    return vh
  }

  override fun onBindViewHolder(holder: PostVH, position: Int) {
    val post = getItem(position)
    if (post.id == -1L) {
      callBack.loadMore()
    } else {
      holder.bindView(post, glide, position)
    }
  }

  override fun getItemViewType(position: Int): Int {
    val post = getItem(position)

    return if (post.id == -1L) {
      loadMore
    } else if (post.communityId != null) {
      communityPost
    } else {
      2
    }
  }

  class PostDiffCalc : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
      return oldItem.areContentTheSame(newItem) && oldItem.user!!.areContentSame(newItem.user!!)
    }
  }

  private fun registerClickEventHandler(vh: PostVH) {
    val tagHelper =
      HashTagHelper.Creator.create(inflater.context.getColor(R.color.primary), callBack)
    tagHelper.handle(vh.binding.hashTagGroup)

    // post section
    vh.binding.dropdownMenu.setOnClickListener {
      val pos = vh.bindingAdapterPosition
      val post: Post = getItem(pos)
      val popup = PopupMenu(cw, vh.binding.dropdownMenu)
      val isUser = post.userId == userId
      popup.inflate(
        if (isUser) R.menu.my_post_menu else R.menu.other_resource_menu
      )
      popup.setForceShowIcon(true)
      popup.setOnMenuItemClickListener {
        callBack.onOptionItemSelected(post, it)

        false
      }

      popup.show()
    }
    vh.binding.upvote.setOnClickListener {
      val position = vh.bindingAdapterPosition
      callBack.onVoteClick(
        getItem(position), vh.binding.upvote, vh.binding.numberOfLikes
      )
    }
    vh.binding.commentsIcon.setOnClickListener {
      callBack.showCommentSection(getItem(vh.bindingAdapterPosition))
    }
    vh.binding.subresourceCount.setOnClickListener {
      callBack.showPost(getItem(vh.bindingAdapterPosition))
    }
    vh.binding.numberOfLikes.setOnClickListener {
      callBack.showLikedUsers(getItem(vh.bindingAdapterPosition))
    }
    // user section
    vh.binding.userProfilePicture.setOnClickListener {
      val post = getItem(vh.bindingAdapterPosition)
      post.user?.let { u -> u.profileUrl?.let(callBack::onImageClick) }
    }
    vh.binding.userFullName.setOnClickListener {
      getItem(vh.bindingAdapterPosition).user?.let(callBack::openUserProfile)
    }
    vh.binding.readText.setOnClickListener {
      val text = vh.binding.postContent.text.toString()
      callBack.readText(text)
    }
  }

  override fun openDetailActivity(position: Int) {
    callBack.showPost(getItem(position))
  }
}