package eth.social.sefer.data.repos.remote

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.like_button.LikeButton
import eth.social.sefer.MyApp
import eth.social.sefer.data.apis.ShortVideoApi
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.FileUtils
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody


class ShortVideoRepo(
  private val url: String,
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val mShortVideos: MutableLiveData<List<ShortVideo>> = MutableLiveData()
  private val api: ShortVideoApi = retrofitInstance.create(ShortVideoApi::class.java)

  val shortVideos: LiveData<List<ShortVideo>> get() = mShortVideos

  fun listOf(page: Int?, append: Boolean) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch(Dispatchers.Main) {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      // since it's from cache delay til the navigation finish
      if (!isInternetAvailable()) {
        delay(1_800)
      }

      val response = api.listOf(url, authorization, page)
      if (response.isSuccessful) {
        meta = response.body()?.meta

        val list: MutableList<ShortVideo>? = response.body()?.videos

        if (append) {
          val currentList: MutableList<ShortVideo> = ArrayList(mShortVideos.value!!)
          currentList.removeAt(0)

          list?.let {
            list.addAll(0, currentList)
          }
        }

        mShortVideos.postValue(list!!)
      } else {
        emitRequestError(response.errorBody(), response.code(), true)
      }
    }
  }

  fun uploadVideo(uri: Uri) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val file = FileUtils.getFile(MyApp.instance, uri) ?: return@launch

      val builder: MultipartBody.Builder = MultipartBody.Builder()
        .setType(MultipartBody.FORM)

      val videoRequestBody: RequestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
      builder.addFormDataPart("video", file.name, videoRequestBody)

      val requestFile: RequestBody =
        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

//            val videoFile: MultipartBody.Part = MultipartBody.Part.createFormData(
//                "video", file.name, requestFile
//            )

//            val builder: MultipartBody.Builder = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("caption", shortVideo.getCaption())
//                .addFormDataPart("name", shortVideo.getName())
//                .addFormDataPart("video", shortVideo.getVideoUrl())

      val response = api.uploadVideo(authorization, builder.build())

      if (response.isSuccessful) {
        val resp = ApiResponse().also {
          it.okay = response.isSuccessful
          it.message = response.body()?.message
        }
        mResponse.emit(resp)
      } else {
        emitRequestError(response.errorBody(), response.code(), false)
      }
    }
  }

  fun toggleVote(likeButton: LikeButton, video: ShortVideo) {
    setLikeButtonStatus(likeButton, !video.isLiked, false)

    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        withContext(Dispatchers.Main) {
          setLikeButtonStatus(likeButton, video.isLiked, true)
        }
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      val response = api.toggleLike(authorization, video.id)

      if (response.isSuccessful) {
        withContext(Dispatchers.Main) {
          response.body()?.video?.let {
            video.likesCount = it.likesCount
            video.isLiked = it.isLiked
          }
          // TODO: update number of likes
        }
      } else {
        // Un authorized to vote means, either blocked by user or session expired
        if (response.code() == unauthorizedCode) {
          val list: MutableList<ShortVideo> = ArrayList(shortVideos.value!!)
          list.remove(video)
          mShortVideos.postValue(list)

          return@launch
        }

        withContext(Dispatchers.Main) {
          setLikeButtonStatus(likeButton, video.isLiked, true)
        }
      }
    }
  }

  fun loadMore() {
    if (isNextAvailable()) {
      listOf(meta?.next, true)
    }
  }
}
