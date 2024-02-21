package eth.social.sefer.ui.adapters.vh

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.LUserBinding

class UserVh(root: View) : RecyclerView.ViewHolder(root) {
    lateinit var binding: LUserBinding

    constructor(userBinding: LUserBinding) : this(userBinding.root) {
        binding = userBinding
    }

    fun bindView(user: User, context: Context, glide: RequestManager, currentUserId: Long) {
        bindUser(user, glide)

        if (user.verified) {
            binding.badge.visibility = View.VISIBLE
        }

        if (user.id == currentUserId) {
            binding.follow.visibility = View.GONE
            return
        }

        binding.follow.text = if (user.following) {
            context.getText(R.string.following)
        } else {
            context.getText(R.string.follow)
        }
    }

    fun bindUser(user: User, glide: RequestManager) {
        binding.apply {
            userFullName.text = user.fullName
            userName.text = user.username
        }

        glide.load(user.profileUrl)
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.userProfile)

    }
}