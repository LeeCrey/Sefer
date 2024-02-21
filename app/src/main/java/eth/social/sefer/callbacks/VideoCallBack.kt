package eth.social.sefer.callbacks

import eth.social.sefer.data.models.ShortVideo

interface VideoCallBack {
    fun videoClicked(video: ShortVideo)
}