package eth.social.sefer.ui.adapters

import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.data.models.Reply
import eth.social.sefer.databinding.LCommentBinding
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.ui.adapters.vh.ReplyVH

class ReplyAdapter(context: Context) :
  ListAdapter<Reply, ReplyVH>(ReplyDiffCalc()) {
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val glide: RequestManager = Glide.with(context)
  private val loadMore = -1 /* Item Type */
  var loadMoreListener: OnLoadMoreListener? = null
  private val userId: Long = PrefHelper.currentUserId
  private val cw: ContextWrapper = ContextThemeWrapper(context, R.style.Theme_App_PopupMenu)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyVH {
    val binding: LCommentBinding = LCommentBinding.inflate(inflater, parent, false)
    val vh = ReplyVH(binding)

    binding.repliesBtn.isVisible = false
    binding.subresourceCount.isVisible = false
    // event

    return vh
  }

  override fun onBindViewHolder(holder: ReplyVH, position: Int) {
    holder.bindView(getItem(position), glide)
  }

  override fun getItemViewType(position: Int): Int {
    val reply = getItem(position)

    return if (reply.id == -1L) {
      loadMore
    } else {
      2
    }
  }

  class ReplyDiffCalc : DiffUtil.ItemCallback<Reply>() {
    override fun areItemsTheSame(oldItem: Reply, newItem: Reply): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reply, newItem: Reply): Boolean {
      return oldItem.likesCount == newItem.likesCount &&
          oldItem.user!!.areContentSame(newItem.user!!)
    }
  }
}