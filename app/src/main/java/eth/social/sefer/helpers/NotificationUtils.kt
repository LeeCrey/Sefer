/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eth.social.sefer.helpers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import eth.social.sefer.Constants
import eth.social.sefer.Constants.CHANNEL_ID
import eth.social.sefer.R
import eth.social.sefer.helpers.ToastUtility.showToast

class NotificationUtils private constructor() {
  init {
    throw UnsupportedOperationException("u can't instantiate me...")
  }

  companion object {
    fun makeStatusNotification(title: String?, message: String?, context: Context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = Constants.VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val nM =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nM.createNotificationChannel(channel)
      }

      // Create the notification
      val builder: NotificationCompat.Builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
          .setSmallIcon(R.mipmap.ic_launcher)
          .setContentTitle(title)
          .setContentText(message)
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setVibrate(LongArray(0))
      if (ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
      ) {
        NotificationManagerCompat.from(context)
          .notify(Constants.NOTIFICATION_ID, builder.build())
      } else {
        showToast("Notification permission is required", context)
      }
    }
  }
}