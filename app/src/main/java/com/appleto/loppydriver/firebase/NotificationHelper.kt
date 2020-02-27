package com.appleto.loppydriver.firebase

import android.media.RingtoneManager
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.graphics.BitmapFactory
import android.text.Html
import androidx.core.app.NotificationCompat
import android.graphics.Bitmap
import android.app.PendingIntent
import android.os.AsyncTask
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import com.appleto.loppydriver.R
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class NotificationHelper(private val mContext: Context) {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private val bitmapUrl = ""
    private val myBitmap: Bitmap? = null
    private val bm: Bitmap? = null
    private var resultPendingIntent: PendingIntent? = null

    /**
     * Create and push the notification
     */
    fun createNotification(
        title: String,
        message: String,
        isBigNotif: Boolean,
        imageUrl: String,
        intent: Intent?
    ) {
        /**Creates an explicit intent for an Activity in your app */

        mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (intent != null) {
            resultPendingIntent = PendingIntent.getActivity(
                mContext,
                0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        mBuilder = NotificationCompat.Builder(mContext, title)
        if (isBigNotif) {
            showBigNotification(imageUrl, title, message, resultPendingIntent)
        } else {
            showSmallNotification(title, message, resultPendingIntent)
        }

    }

    private fun showSmallNotification(
        title: String,
        message: String,
        resultPendingIntent: PendingIntent?
    ) {
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.bigText(message)
        mBuilder?.setSmallIcon(R.mipmap.ic_launcher)
        mBuilder?.setContentTitle(title)
            ?.setContentText(message)
            ?.setStyle(bigTextStyle)
            ?.setAutoCancel(true)
            ?.setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            ?.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
        if (resultPendingIntent != null) {
            mBuilder?.setContentIntent(resultPendingIntent)
        }

        mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(mNotificationManager != null)
            mBuilder?.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager?.notify(System.currentTimeMillis().toInt(), mBuilder?.build())
    }

    private fun showBigNotification(
        imageUrl: String,
        title: String,
        message: String,
        resultPendingIntent: PendingIntent?
    ) {
        sendNotification(mContext, resultPendingIntent).execute(imageUrl, title, message)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class sendNotification(
        internal var ctx: Context,
        internal var resultPendingIntent: PendingIntent?
    ) : AsyncTask<String, Void, Bitmap>() {
        internal var message: String = ""
        internal var title: String = ""

        override fun doInBackground(vararg params: String): Bitmap? {

            val `in`: InputStream
            title = params[1]
            message = params[2]
            try {
                val url = URL(params[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                `in` = connection.getInputStream()
                return BitmapFactory.decodeStream(`in`)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            if (result != null) {
                val bigPictureStyle = NotificationCompat.BigPictureStyle()
                bigPictureStyle.setBigContentTitle(title)
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
                bigPictureStyle.bigPicture(result)
                mBuilder?.setStyle(bigPictureStyle)
            }
            mBuilder?.setSmallIcon(R.mipmap.ic_launcher)
            mBuilder?.setContentTitle(title)
                ?.setContentText(message)
                ?.setAutoCancel(true)
                ?.setLargeIcon(
                    BitmapFactory.decodeResource(
                        mContext.getResources(),
                        R.mipmap.ic_launcher
                    )
                )
                ?.setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                ?.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            if (resultPendingIntent != null) {
                mBuilder?.setContentIntent(resultPendingIntent)
            }

            mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME",
                    importance
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern =
                    longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                assert(mNotificationManager != null)
                mBuilder?.setChannelId(NOTIFICATION_CHANNEL_ID)
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            assert(mNotificationManager != null)
            mNotificationManager?.notify(System.currentTimeMillis().toInt(), mBuilder?.build())
        }

    }

    fun playNotificationSound() {
        try {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(mContext, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        val NOTIFICATION_CHANNEL_ID = "10001"

        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.getPackageName()) {
                            isInBackground = false
                        }
                    }
                }
            }

            return isInBackground
        }
    }
}