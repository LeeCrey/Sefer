@file:Suppress("unused")

package eth.social.sefer.helpers

import android.content.Context
import eth.social.sefer.Constants
import eth.social.sefer.MyApp
import eth.social.sefer.data.models.User

object PrefHelper {
  private const val PREFS_MODE = Context.MODE_PRIVATE

  // keys
  private const val authToken = "eth.social.sefer.auth_token"
  private const val postNotification = "eth.social.sefer.post_notification"
  private const val unreadNotificationsCount = "eth.social.sefer.unread_notifications_count"

  // authorization token
  val authorizationToken: String?
    get() {
      val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
      return pref.getString(authToken, null)
    }

  fun putAuthToken(value: String?) {
    val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
    val editor = pref.edit()
    editor.putString(authToken, value)
    editor.apply()
  }

  fun wipeStoredData(context: Context) {
    val pref = context.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
    val editor = pref.edit()
    editor.clear()
    editor.apply()
  }

  val currentUserId: Long
    get() {
      val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
      return pref.getLong(Constants.USER_ID, -1L)
    }

  fun putUser(student: User) {
    val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
    val editor = pref.edit()
    editor.putString(Constants.FULL_NAME, student.fullName)
    editor.putString(Constants.PROFILE_URL, student.profileUrl)
    editor.putLong(Constants.USER_ID, student.id)
    editor.putString(Constants.EMAIL, student.email)
    editor.apply()
  }

  val currentUser: User
    get() {
      val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
      val student = User()
      student.fullName = pref.getString(Constants.FULL_NAME, null)
      student.profileUrl = pref.getString(Constants.PROFILE_URL, null)
      student.id = pref.getLong(Constants.USER_ID, -1L)
      student.email = pref.getString(Constants.EMAIL, null)

      return student
    }

  fun markReadyForNotification(ready: Boolean) {
    val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
    val editor = pref.edit()
    editor.putBoolean(postNotification, ready)
    editor.apply()
  }

  val isReadyForNotification: Boolean
    get() {
      val pref = MyApp.instance.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
      return pref.getBoolean(postNotification, false)
    }

  fun getUnreadNotificationsCount(context: Context): Int {
    val pref = context.getSharedPreferences(Constants.PREFS_FILE, PREFS_MODE)
    return pref.getInt(unreadNotificationsCount, 0)
  }

  fun updateBio(bio: String) {
    val user = currentUser
    user.bio = bio
    putUser(user)
  }
}