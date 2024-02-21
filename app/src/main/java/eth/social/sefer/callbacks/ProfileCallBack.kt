package eth.social.sefer.callbacks

import eth.social.sefer.data.models.ShortVideo

interface ProfileCallBack {
    fun openVideoShow(video: ShortVideo)
}