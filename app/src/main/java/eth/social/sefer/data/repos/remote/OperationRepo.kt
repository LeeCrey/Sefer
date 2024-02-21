package eth.social.sefer.data.repos.remote

import eth.social.sefer.Constants
import eth.social.sefer.data.apis.CommentApi
import eth.social.sefer.data.apis.OperationApi
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.Community
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.User
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.models.api_response.CommentResponse
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class OperationRepo(
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val api: OperationApi = retrofitInstance.create(OperationApi::class.java)
  private val commentApi: CommentApi = retrofitInstance.create(CommentApi::class.java)

  private val handler = CoroutineExceptionHandler { _, t ->
    viewModelScope.launch {
      setApiResponse(t, null)
    }
  }

  fun createPost(post: Post) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = if (post.communityId == null) {
        api.createPost(authorization, post)
      } else {
        api.createCommunityPost(authorization, post.communityId!!, post)
      }

      if (response.isSuccessful) {
        mResponse.emit(response.body()!!)
      } else {
        emitRequestError(response.errorBody(), response.code())
      }
    }

  fun requestInstructionLink(uEmail: String, url: String) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      doSendRequest(api.requestLink(url, User().apply { email = uEmail }))
    }

  fun logout() =
    viewModelScope.launch(Dispatchers.IO + handler) {
      doSendRequest(api.logout(authorization))
    }

  fun updatePost(id: Long, post: Post) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      doSendRequest(api.updatePost(authorization, id, post))
    }

  fun changePassword(user: User) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      doSendRequest(api.changePassword(authorization, user))
    }

  fun updateCommunity(id: Long, community: Community) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      doSendRequest(api.updateCommunity(authorization, id, community))
    }

  fun createCommunity(community: Community) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      // create multiform
      val response = api.createCommunity(authorization, community)
      if (response.isSuccessful) {
        // TODO on success
      } else {
        emitRequestError(response.errorBody(), response.code())
      }
    }

  fun submitComment(comment: Comment) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      val url = Constants.commentCreateUrl(comment.commentableId!!)
      doCommentRequest(commentApi.createComment(url, authorization, comment))
    }

  fun updateComment(id: Long, comment: Comment) =
    viewModelScope.launch(Dispatchers.IO + handler) {
      doCommentRequest(commentApi.updateComment(authorization, id, comment))
    }

  private suspend fun doSendRequest(response: Response<ApiResponse>) {
    if (response.isSuccessful) {
      mResponse.emit(response.body()!!)
    } else {
      emitRequestError(response.errorBody(), response.code())
    }
  }

  private suspend fun doCommentRequest(response: Response<CommentResponse>) {
    if (response.isSuccessful) {
      val resp = response.body()!!
      val apiResponse = ApiResponse().apply {
        okay = resp.okay
        message = resp.message
      }
      mResponse.emit(apiResponse)
    } else {
      emitRequestError(response.errorBody(), response.code(), true)
    }
  }
}