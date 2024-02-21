package eth.social.sefer.ui.adapters

import android.content.Context
import eth.social.sefer.R
import eth.social.sefer.callbacks.UnblockCallBack
import eth.social.sefer.ui.adapters.vh.UserVh

class BlockedUserAdapter(
  context: Context,
  private val callBack: UnblockCallBack
) : UserAdapter(context, null) {

  private val unblockMsg = context.getText(R.string.unblock)

  override fun onBindViewHolder(holder: UserVh, position: Int) {
    holder.bindUser(getItem(position), manager)
  }

  override fun bindEventListener(vh: UserVh) {
    vh.binding.follow.text = unblockMsg
    vh.binding.follow.setOnClickListener {
      callBack.unblockUser(getItem(vh.bindingAdapterPosition))
    }
  }
}