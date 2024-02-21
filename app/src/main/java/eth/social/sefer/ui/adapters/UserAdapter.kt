package eth.social.sefer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.callbacks.UserCallBack
import eth.social.sefer.data.models.User
import eth.social.sefer.databinding.LUserBinding
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.ui.adapters.vh.UserVh

open class UserAdapter(
  context: Context, private val callBack: UserCallBack? = null
) : ListAdapter<User, UserVh>(UserDiffCalc()) {
  private val moreLayout = -1
  private var inflater = LayoutInflater.from(context)
  protected val manager: RequestManager = Glide.with(context)
  private val currentUserId = PrefHelper.currentUserId
  var loadMoreListener: OnLoadMoreListener? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVh {
    if (viewType == moreLayout) {
      val view = inflater.inflate(R.layout.l_loading, parent, false)

      return UserVh(view)
    }

    val userBinding = LUserBinding.inflate(inflater, parent, false)
    val vh = UserVh(userBinding)

    bindEventListener(vh)

    return vh
  }

  override fun onBindViewHolder(holder: UserVh, position: Int) {
    val user = getItem(position)
    if (user.id == -1L) {
      loadMoreListener?.loadMore()
    } else {
      holder.bindView(user, inflater.context, manager, currentUserId)
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (getItem(position).id == -1L) {
      moreLayout
    } else {
      1
    }
  }

  class UserDiffCalc : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
      return oldItem.areContentSame(newItem)
    }
  }

  // override in sub class
  protected open fun bindEventListener(vh: UserVh) {
    vh.binding.follow.setOnClickListener {
      val pos = vh.bindingAdapterPosition
      callBack?.onUserFollow(getItem(pos), vh.binding.follow, pos)
    }
    vh.binding.userProfile.setOnClickListener {
      callBack?.userProfileClick(getItem(vh.bindingAdapterPosition))
    }
    vh.binding.userFullName.setOnClickListener {
      callBack?.showProfile(getItem(vh.bindingAdapterPosition), vh.binding.userProfile)
    }
  }
}