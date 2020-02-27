package com.appleto.loppydriver.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.appleto.loppydriver.activity.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var notificationHelper: NotificationHelper? = null
    private val imageUrl: String? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.from!!)

        if (remoteMessage == null)
            return

        // Check if message contains a notification payload.(For FCM)
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.notification!!.body!!)
            fcmHandleNotification(remoteMessage.notification!!.body)
        }

        // Check if message contains a data payload.(For Custom Data)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, remoteMessage.data.toString())
            handleDataMessage(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        println("TOKEN::$token")
        super.onNewToken(token)
    }

    override fun onMessageSent(s: String) {
        super.onMessageSent(s)
    }

    private fun handleDataMessage(customData: Map<String, String>) {
//        println("customerDATA==>$customData")
        Log.d("customerDATA==>", customData.toString())
        val badge = customData["badge"]
        val image = customData["image"]
        val sound = customData["sound"]
        val title = customData["title"]
        val message = customData["message"]

        if (!NotificationHelper.isAppIsInBackground(applicationContext)) {
            val pushNotification = Intent(PUSH_NOTIFICATION)
            pushNotification.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            val isImageAttached: Boolean = image != ""
            image?.let {
                showNotificationMessage(
                    applicationContext,
                    title,
                    message,
                    isImageAttached,
                    it,
                    pushNotification
                )
            }
        } else {
            val resultIntent = Intent(applicationContext, MainActivity::class.java)
            resultIntent.putExtra("badge", badge)
            resultIntent.putExtra("image", image)
            resultIntent.putExtra("sound", sound)
            resultIntent.putExtra("title", title)
            resultIntent.putExtra("message", message)
            val isImageAttached: Boolean = image != ""
            image?.let {
                showNotificationMessage(
                    applicationContext,
                    title,
                    message,
                    isImageAttached,
                    it,
                    resultIntent
                )
            }
        }
    }

    private fun fcmHandleNotification(message: String?) {
        if (!NotificationHelper.isAppIsInBackground(applicationContext)) {
            // app is in foreground, broadcast the push message
            val pushNotification = Intent(PUSH_NOTIFICATION)
            pushNotification.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

            val notificationHelper = NotificationHelper(this)
            notificationHelper.playNotificationSound()
        }
    }


    private fun showNotificationMessage(
        context: Context,
        title: String?,
        message: String?,
        isBigNotif: Boolean,
        imageUrl: String,
        intent: Intent
    ) {
        notificationHelper = NotificationHelper(context)
        title?.let {
            message?.let { it1 ->
                notificationHelper!!.createNotification(
                    it,
                    it1, isBigNotif, imageUrl, intent
                )
            }
        }
    }

    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
        val PUSH_NOTIFICATION = "pushNotification"
    }
}