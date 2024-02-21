package eth.social.sefer.ui.adapters

import android.content.Context
import android.icu.text.CompactDecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.callbacks.ShortVideoCallBack
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.databinding.LShortVideoBinding
import eth.social.sefer.helpers.LocaleHelper
import eth.social.sefer.ui.adapters.vh.ShortVideoVH


class ShortVideoAdapter(
  context: Context, private val callBack: ShortVideoCallBack
) : ListAdapter<ShortVideo, ShortVideoVH>(VideoItemDiffCalc()) {

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val imageLoader: RequestManager = Glide.with(context)
  private val formatter: CompactDecimalFormat = CompactDecimalFormat.getInstance(
    LocaleHelper.getConfiguredLocale(context), CompactDecimalFormat.CompactStyle.SHORT
  )

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortVideoVH {
    val binding: LShortVideoBinding = LShortVideoBinding.inflate(inflater, parent, false)
    val vh = ShortVideoVH(binding)
    // for custom recycler view
    binding.root.tag = vh

    // show bottom sheet
    binding.comments.setOnClickListener {
      callBack.onCommentClick(getItem(vh.bindingAdapterPosition))
    }
    // navigate to user show activity
    binding.userProfilePicture.setOnClickListener {
      callBack.onProfileClick(getItem(vh.bindingAdapterPosition))
    }
    binding.options.setOnClickListener {
      callBack.onOptionClicked(getItem(vh.bindingAdapterPosition))
    }
    // send request
    binding.voteIcon.setOnClickListener {
      callBack.onVoteClick(binding.voteIcon, getItem(vh.bindingAdapterPosition))
    }

    return vh
  }

  override fun onBindViewHolder(holder: ShortVideoVH, position: Int) {
    callBack.enableOrDisableBackPress(position != 0)
    holder.bindView(getItem(position), imageLoader, formatter)
  }

  class VideoItemDiffCalc : DiffUtil.ItemCallback<ShortVideo>() {
    override fun areItemsTheSame(oldItem: ShortVideo, newItem: ShortVideo): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShortVideo, newItem: ShortVideo): Boolean {
      return oldItem.url == newItem.url
    }
  }
}