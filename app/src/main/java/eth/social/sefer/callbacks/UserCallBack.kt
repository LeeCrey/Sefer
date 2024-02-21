package eth.social.sefer.callbacks

import android.widget.ImageView
import android.widget.TextView
import eth.social.sefer.data.models.User

interface UserCallBack {
  fun onUserFollow(user: User, button: TextView, position: Int)

  fun userProfileClick(user: User)

  fun showProfile(user: User, profile: ImageView)
}