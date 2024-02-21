package eth.social.sefer.ui.adapters.vh

import android.view.View
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import eth.social.sefer.data.models.Notification
import eth.social.sefer.databinding.LNotificationBinding

class NotificationVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private lateinit var _binding: LNotificationBinding

  constructor(binding: LNotificationBinding) : this(binding.root) {
    _binding = binding
  }

  fun bindView(notification: Notification) {
    _binding.apply {
      message.text = notification.message?.let { HtmlCompat.fromHtml(it, 0) }
    }
//        bindUser(notification.user, glide)
  }
}
