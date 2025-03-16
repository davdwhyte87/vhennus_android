package com.vhennus.general.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vhennus.MainActivity
import com.vhennus.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CLog.debug("FCM", "New Token: $token")
        // Send the token to your server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        CLog.debug("NEW NOTIFY", "data[\"user_name\"]?:\"\"")
        remoteMessage.data.let {data->
            showNotification(data["title"] ?: "New Message", data["body"] ?: "You have a new notification",
                data["user_name"]?:""
                )
        }
    }


    private fun showNotification(title: String, message: String, user_name:String) {
        CLog.debug("NEW NOTIFY", user_name)
        val channelId = "message"
        val notificationId = System.currentTimeMillis().toInt()

        val deepLinkUri = Uri.parse("https://vhennus.com/single_chat/${user_name}")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val clickedIntent = Intent(Intent.ACTION_VIEW, deepLinkUri, this, MainActivity::class.java)
        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else 0

        val clickedPendingIntent = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(clickedIntent)
            getPendingIntent(1,flag)
        }
        CLog.debug("Notification", "Deep link URI: $deepLinkUri")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val soundUri = Uri.parse("android.resource://${packageName}/raw/bellsound")
        CLog.debug("FCM", "Sound URI: $soundUri")
        val existingChannel = notificationManager.getNotificationChannel(channelId)
        if (existingChannel == null) {
            val channel = NotificationChannel(
                channelId,
                "Message Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(
                    soundUri, AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }

//        // ✅ Create a notification channel for Android 8+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_HIGH).apply {
//                setSound(soundUri, null)
//            }
//            notificationManager.createNotificationChannel(channel)
//
//        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.tlogo)
            .setContentTitle(user_name)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(clickedPendingIntent)


        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}