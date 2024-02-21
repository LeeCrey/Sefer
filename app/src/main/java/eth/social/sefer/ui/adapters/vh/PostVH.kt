package eth.social.sefer.ui.adapters.vh

import android.text.format.DateUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.data.models.Post
import eth.social.sefer.databinding.LPostBinding
import eth.social.sefer.lib.ResourceReadMore
import io.noties.markwon.Markwon
import java.text.NumberFormat


open class PostVH(root: View) : RecyclerView.ViewHolder(root) {
  lateinit var readMore: ResourceReadMore
  private lateinit var nf: NumberFormat
  lateinit var binding: LPostBinding
  lateinit var markdown: Markwon

  constructor(binding: LPostBinding, nf: NumberFormat) : this(binding.root) {
    this.binding = binding
    this.nf = nf
  }

  fun bindView(
    post: Post,
    glide: RequestManager,
    position: Int
  ) {
    val user = post.user!!

    glide.load(user.profileUrl)
      .placeholder(R.drawable.profile_placeholder)
      .into(binding.userProfilePicture)

    // user
    binding.apply {
      userFullName.text = user.fullName
      userName.text = user.username
      binding.badge.isVisible = user.verified
      createdAt.text = DateUtils.getRelativeTimeSpanString(post.createdAt!!)
      upvote.setChecked(post.isVoted ?: false)
      hashTagGroup.text = post.hashTags

      if (post.votesCount > 0) {
        numberOfLikes.text = nf.format(post.votesCount)
      }
      if (post.commentsCount > 0) {
        subresourceCount.text = nf.format(post.commentsCount)
      }

      post.content?.let { content ->
        readMore.addReadMoreTo(postContent, content, position)
      }
      communityName.text = post.community?.name
    }
    post.photoUrl?.let {
      binding.postImage.visibility = View.VISIBLE
      glide.load(post.photoUrl).placeholder(R.drawable.placeholder)
//                .override(600, 700)
        .into(binding.postImage)
    }
  }
}
