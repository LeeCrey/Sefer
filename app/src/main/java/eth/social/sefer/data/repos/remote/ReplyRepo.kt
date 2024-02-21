package eth.social.sefer.data.repos.remote

import androidx.lifecycle.MutableLiveData
import eth.social.sefer.data.apis.ReplyApi
import eth.social.sefer.data.models.Reply
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReplyRepo(
  private val commentId: Long,
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val api: ReplyApi = retrofitInstance.create(ReplyApi::class.java)
  private val _mReplies: MutableLiveData<List<Reply>> = MutableLiveData()
  val replies get() = _mReplies
  private var totalReplies = 0

  fun loadReplies(page: Int?, append: Boolean) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.loadReplies(authorization, commentId, page)
      if (response.isSuccessful) {
        val resp = response.body() ?: return@launch

        meta = resp.meta

        val list: MutableList<Reply> = resp.replies!!

        if (append) {
          val currentList: MutableList<Reply> = ArrayList(_mReplies.value!!)
          currentList.removeAt(totalReplies)
          list.addAll(0, currentList)
        }

        val data = meta!!
        if (append) {
          totalReplies += data.count
        } else {
          totalReplies = data.count
        }

        if (data.isNextPageAvailable) {
          list.add(placeHolder)
        }

        _mReplies.postValue(list)
      } else {
        emitRequestError(response.errorBody(), response.code(), true)
      }
    }
  }

  fun deleteReply(reply: Reply) {
  }

  fun updateReply(reply: Reply) {

  }

  fun voteReply(reply: Reply) {

  }

  fun loadMore() {
    loadReplies(meta!!.next, true)
  }

  private val placeHolder: Reply = Reply().apply { id = -1L }
}