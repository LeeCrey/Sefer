package eth.social.sefer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.callbacks.CommunityCallBack
import eth.social.sefer.data.models.Community
import eth.social.sefer.databinding.LCommunityBinding
import eth.social.sefer.ui.adapters.vh.CommunityVH

class CommunityAdapter(
  context: Context,
  private val callback: CommunityCallBack
) : ListAdapter<Community, CommunityVH>(CommunityDiffCalc()) {
  private val loadMore: Int = 1

  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val imageLoader: RequestManager = Glide.with(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityVH {
    if (viewType == loadMore) {
      val view = inflater.inflate(R.layout.l_loading, parent, false)
      return CommunityVH(view)
    }

    val binding = LCommunityBinding.inflate(inflater, parent, false)

    val vh = CommunityVH(binding)
    val root = binding.root
    root.setOnLongClickListener {
      callback.onLongClick(getItem(vh.bindingAdapterPosition))
      true
    }

    root.setOnClickListener {
      callback.onCommunityClick(getItem(vh.bindingAdapterPosition))
    }

    // event handlers

    return vh
  }

  override fun onBindViewHolder(holder: CommunityVH, position: Int) {
    val community = getItem(position)
    if (community.id == -1L) {
      callback.loadMore()
    } else {
      holder.bindView(community, imageLoader)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (getItem(position).id == -1L) {
      loadMore
    } else {
      2
    }
  }

  class CommunityDiffCalc : DiffUtil.ItemCallback<Community>() {
    override fun areItemsTheSame(oldItem: Community, newItem: Community): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Community, newItem: Community): Boolean {
      return oldItem.name == newItem.name
    }
  }
}