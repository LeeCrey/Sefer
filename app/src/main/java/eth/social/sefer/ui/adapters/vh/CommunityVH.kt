package eth.social.sefer.ui.adapters.vh

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.data.models.Community
import eth.social.sefer.databinding.LCommunityBinding

class CommunityVH(root: View) : RecyclerView.ViewHolder(root) {
    private lateinit var binding: LCommunityBinding

    constructor(binding: LCommunityBinding) : this(binding.root) {
        this.binding = binding
    }

    fun bindView(community: Community, glide: RequestManager) {
        binding.apply {
            communityName.text = community.name
            membersCount.text = community.membersCount
        }

        if (community.coverUri == null) {
            binding.communityCover.setImageDrawable(community.placeholder)
        } else {
            glide.load(community.coverUri)
                .placeholder(community.placeholder)
                .into(binding.communityCover)
        }
    }
}