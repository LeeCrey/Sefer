package eth.social.sefer.data.repos.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.sefer.data.apis.CommunityApi
import eth.social.sefer.data.models.Community
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityRepo(
  private val viewModelScope: CoroutineScope,
  private val url: String
) : ApplicationRepo() {
  private val _mCommunity: MutableLiveData<List<Community>> = MutableLiveData()
  private val api: CommunityApi = retrofitInstance.create(CommunityApi::class.java)

  val communities: LiveData<List<Community>> get() = _mCommunity

  fun list(page: Int?, append: Boolean) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.communityList(url, authorization, page)
      if (response.isSuccessful) {
        val cResp = response.body() ?: return@launch

        meta = cResp.meta

        // if list from server is null
        val list: MutableList<Community> =
          cResp.communities ?: return@launch

        if (!append) {
          if (meta?.isNextPageAvailable == true) {
            list.add(placeholder())
          }
        }

        // existing list
//                val currentList: List<Community>? = communities.value
//                val updatedList: MutableList<Community> = ArrayList(currentList!!)
//                updatedList.remove(placeholder())
//                updatedList.addAll(list)

        _mCommunity.postValue(list)
      } else {
        emitRequestError(response.errorBody(), response.code())
      }
    }
  }

  fun joinCommunity(community: Community) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.joinCommunity(authorization, community.id)
      if (response.isSuccessful) {
        mResponse.emit(response.body()!!)
      } else {
        emitRequestError(response.errorBody(), response.code())
      }
    }
  }

  fun loadMore() {
    list(meta!!.next, true)
  }

  private fun placeholder(): Community = Community().apply { id = -1L }
}
