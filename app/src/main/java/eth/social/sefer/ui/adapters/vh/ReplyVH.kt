package eth.social.sefer.ui.adapters.vh

import android.text.format.DateUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.data.models.Reply
import eth.social.sefer.databinding.LCommentBinding

class ReplyVH(root: View) : RecyclerView.ViewHolder(root) {
  lateinit var binding: LCommentBinding

  constructor(binding: LCommentBinding) : this(binding.root) {
    this.binding = binding
  }

  fun bindView(reply: Reply, glide: RequestManager) {
    val user = reply.user!!

    binding.apply {
      // user
      userFullName.text = user.fullName
      userName.text = user.username
      glide.load(user.profileUrl)
        .placeholder(R.drawable.profile_placeholder)
        .into(userProfilePicture)
      badge.isVisible = user.verified

      // content
      postContent.text = reply.content
      createdAt.text = DateUtils.getRelativeTimeSpanString(reply.createdAt!!)
    }
  }
}
