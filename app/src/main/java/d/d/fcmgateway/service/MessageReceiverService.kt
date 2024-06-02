package d.d.fcmgateway.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import d.d.fcmgateway.R
import org.json.JSONObject

class MessageReceiverService : FirebaseMessagingService() {
    private val TAG = "MessageReceiverService"
    private val NOTIFICATION_CHANNEL = "Notifications"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data.withDefault { "intent" }["type"]
        val action = data["action"]
        val application = data["application"]
        val applicationClass = data["class"]
        val extrasSerialized = data["extras"]
        val uri = data["uri"]


        if(!listOf("broadcast", "activity", "notification").contains(type)){
            return
        }

        val intent = Intent()
        action?.let { intent.action = action }
        application?.let { intent.`package` = application }
        uri?.let { intent.data = Uri.parse(uri) }
        extrasSerialized?.let {
            val extras = JSONObject(extrasSerialized)
            extras.keys().forEach {
                when (val value = extras[it]) {
                    is Int -> intent.putExtra(it, value)
                    is Double -> intent.putExtra(it, value)
                    is Boolean -> intent.putExtra(it, value)
                    is String -> intent.putExtra(it, value)
                    else -> Log.e(
                        TAG,
                        "onMessageReceived: key $it contains invalid class: ${value.javaClass}"
                    )
                }
            }
        }
        Log.d(TAG, "onMessageReceived: sending $type")
        when(type){
            "broadcast" -> sendBroadcast(intent)
            "activity" -> {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if(application != null){
                    if(applicationClass == null) {
                        val launchIntent = packageManager.getLaunchIntentForPackage(application)
                        if (launchIntent == null) {
                            Log.e(
                                TAG,
                                "onMessageReceived: Could not find launch intent for app '$application'"
                            )
                            return
                        }

                        uri?.let { intent.data = Uri.parse(uri) }
                        startActivity(launchIntent)
                        return
                    }else{
                        intent.setClassName(application, applicationClass)
                    }
                }
                // use launchIntent here!!
                uri?.let { intent.data = Uri.parse(uri) }
                startActivity(intent)
            }
            "notification" -> {
                val channel = data["channel"] ?: "Notifications"
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(NotificationChannel(
                        channel,
                        channel,
                        NotificationManager.IMPORTANCE_HIGH
                    ))
                }
                val builder = NotificationCompat.Builder(this, channel)
                builder
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(data["title"])
                    .setContentText(data["text"])
                    .setAutoCancel(true)

                var launchIntent: Intent? = null
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if(application != null){
                    if(applicationClass == null) {
                        launchIntent = packageManager.getLaunchIntentForPackage(application)
                        if (launchIntent == null) {
                            Log.e(
                                TAG,
                                "onMessageReceived: Could not find launch intent for app '$application'"
                            )
                            return
                        }
                    }else{
                        launchIntent = Intent()
                        launchIntent.setClassName(application, applicationClass)
                    }
                    uri?.let { launchIntent.data = Uri.parse(uri) }
                    builder.setContentIntent(
                        PendingIntent.getActivity(this, 0, launchIntent, 0)
                    )
                }

                var notificationId = data["notification_id"]?.toInt() ?: System.currentTimeMillis().toInt()

                notificationManager.notify(
                    notificationId,
                    builder.build()
                )
            }
        }
    }
}
