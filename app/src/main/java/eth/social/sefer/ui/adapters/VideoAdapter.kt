package eth.social.sefer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.callbacks.VideoCallBack
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.databinding.LVideoThumbBinding
import eth.social.sefer.ui.adapters.vh.VideoVH

class VideoAdapter(
  context: Context,
  private val callBack: VideoCallBack
) :
  ListAdapter<ShortVideo, VideoVH>(ShortVideoAdapter.VideoItemDiffCalc()) {
  lateinit var loadMore: OnLoadMoreListener
  private val inflater = LayoutInflater.from(context)
  private val glide = Glide.with(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
    val binding = LVideoThumbBinding.inflate(inflater, parent, false)
    val vh = VideoVH(binding)

    binding.upvote.isEnabled = false

    // event
    binding.root.setOnClickListener {
      callBack.videoClicked(getItem(vh.bindingAdapterPosition))
    }

    return vh
  }

  override fun onBindViewHolder(holder: VideoVH, position: Int) {
    holder.bindView(getItem(position), glide)
  }
}