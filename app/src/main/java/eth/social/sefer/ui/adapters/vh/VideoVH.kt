package eth.social.sefer.ui.adapters.vh

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.databinding.LVideoThumbBinding

class VideoVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var binding: LVideoThumbBinding

    constructor(binding: LVideoThumbBinding) : this(binding.root) {
        this.binding = binding
    }

    fun bindView(video: ShortVideo, imageLoader: RequestManager) {
        binding.apply {
            imageLoader.load(video.thumbnail)
                .placeholder(R.drawable.placeholder)
                .into(thumbNail)
            upvote.setChecked(video.isLiked)

            // TODO fix this
            numberOfLikes.text = "${video.likesCount}"
        }
    }
}