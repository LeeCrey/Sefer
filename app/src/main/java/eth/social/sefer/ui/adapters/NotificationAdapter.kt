package eth.social.sefer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.data.models.Notification
import eth.social.sefer.databinding.LNotificationBinding
import eth.social.sefer.ui.adapters.vh.NotificationVH

class NotificationAdapter(context: Context) :
  ListAdapter<Notification, NotificationVH>(NotificationDiffCalc()) {
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val glide: RequestManager = Glide.with(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
    val binding = LNotificationBinding.inflate(inflater, parent, false)

    // event
    return NotificationVH(binding)
  }

  override fun onBindViewHolder(holder: NotificationVH, position: Int) {
    holder.bindView(getItem(position))
  }

  class NotificationDiffCalc : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
      return oldItem.isRead == newItem.isRead
    }
  }

  fun removeItemFrom(pos: Int) {
    val list: MutableList<Notification> = ArrayList(currentList)
    list.removeAt(pos)
    submitList(list)
  }

  fun restoreItem(notification: Notification, pos: Int) {
    val list: MutableList<Notification> = ArrayList(currentList)
    list.add(pos, notification)
    submitList(list)
  }

  fun getItemFromList(pos: Int): Notification {
    return getItem(pos)
  }
}
