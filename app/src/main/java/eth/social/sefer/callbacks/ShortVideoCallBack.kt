package eth.social.sefer.callbacks

import eth.social.like_button.LikeButton
import eth.social.sefer.data.models.ShortVideo

interface ShortVideoCallBack : MediaCallBack {
    fun onCommentClick(video: ShortVideo)

    fun onVoteClick(btn: LikeButton, video: ShortVideo)

    fun onProfileClick(video: ShortVideo)

    fun onOptionClicked(video: ShortVideo)

    fun enableOrDisableBackPress(enable: Boolean)

    fun currentPosition(position: Int)
}