package eth.social.sefer.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eth.social.sefer.data.apis.OperationApi
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.helpers.NotificationUtils
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import retrofit2.Response
import java.io.IOException

class PushNotificationService : FirebaseMessagingService() {
  private val api: OperationApi = retrofitInstance.create(OperationApi::class.java)
  private val auth: String? = PrefHelper.authorizationToken

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    if (remoteMessage.notification != null) {
      // Since the notification is received directly
      // from FCM, the title and the body can be
      // fetched directly as below.
      val title = remoteMessage.notification!!.title
      val body = remoteMessage.notification!!.body
      NotificationUtils.makeStatusNotification(title, body, applicationContext)
    }
  }

  override fun onNewToken(token: String) {
    // send token to server
    try {
      val resp: Response<ApiResponse> = api.sendPostNotificationToken(auth, token).execute()
      PrefHelper.markReadyForNotification(resp.isSuccessful)
    } catch (ignore: IOException) {
      PrefHelper.markReadyForNotification(false)
    }
  }
}