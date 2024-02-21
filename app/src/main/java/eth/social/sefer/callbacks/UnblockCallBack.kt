package eth.social.sefer.callbacks

import eth.social.sefer.data.models.User

interface UnblockCallBack {
    fun unblockUser(user: User)
}