package eth.social.sefer.data.repos.remote

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import eth.social.like_button.LikeButton
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.data.models.Meta
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.helpers.PrefHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.ResponseBody
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class ApplicationRepo {
  protected val unauthorizedCode: Int = 401
  protected var meta: Meta? = null
  protected var isLastRequestAppend: Boolean = false
  protected val authorization: String? = PrefHelper.authorizationToken
  protected val mResponse: MutableSharedFlow<ApiResponse> = MutableSharedFlow()

  val response: SharedFlow<ApiResponse> get() = mResponse.asSharedFlow()

  @Throws(JsonProcessingException::class)
  protected fun parseRequestResponse(response: ResponseBody): ApiResponse {
    val mapper = ObjectMapper()

    return mapper.readerFor(ApiResponse::class.java).readValue(response.string())
  }

  fun isNextAvailable(): Boolean {
    return meta?.isNextPageAvailable ?: false
  }

  private fun isNetworkError(t: Throwable): Boolean {
    return t is SocketTimeoutException || t is SocketException || t is UnknownHostException
  }

  private fun exceptionMessage(t: Throwable): String {
    return if (isNetworkError(t)) {
      MyApp.instance.getString(R.string.connection_error)
    } else {
      t.printStackTrace()
      MyApp.instance.getString(R.string.something_went_wrong)
    }
  }

  protected suspend fun setApiResponse(
    t: Throwable?,
    customMessage: String?,
    isUnAuthorized: Boolean = false
  ) {
    val resp = ApiResponse().apply {
      okay = false
      unauthorized = isUnAuthorized
      message = customMessage ?: exceptionMessage(t!!)
    }
    mResponse.emit(resp)
  }

  protected fun setLikeButtonStatus(
    likeButton: LikeButton, checked: Boolean, enable: Boolean = true
  ) {
    likeButton.setChecked(checked)
    likeButton.playAnimation()
    likeButton.isEnabled = enable
  }

  protected suspend fun emitRequestError(
    response: ResponseBody?,
    code: Int,
    includeUnAuthorized: Boolean = false,
  ) {
    val resp = parseRequestResponse(response!!).also {
      it.okay = false
      if (includeUnAuthorized) {
        it.unauthorized = code == unauthorizedCode
      }
    }
    mResponse.emit(resp)
  }

  fun page(): Int {
    return meta?.page ?: 1
  }
}
