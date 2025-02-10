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
        CLog.debug("NEW NOTIFY", "KKK")
        remoteMessage.notification?.let {
            showNotification(it.title ?: "New Message", it.body ?: "You have a new notification")
        }
    }


    private fun showNotification(title: String, message: String) {
        val channelId = "message"
        val notificationId = System.currentTimeMillis().toInt()

        val intent = Intent(this, MainActivity::class.java) // Change this to your activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}