package eth.social.sefer.callbacks

import eth.social.sefer.data.models.Community

interface ProfilePostCallBack: PostCallBack {
    fun openCommunity(community: Community)
}