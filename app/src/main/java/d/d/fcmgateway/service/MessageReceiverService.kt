package d.d.fcmgateway.service

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.NonNull

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MessageReceiverService : FirebaseMessagingService() {
    private val TAG = "MessageReceiverService"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data.withDefault { "intent" }["type"]
        val action = data["action"]
        val application = data["application"]
        val applicationClass = data["class"]
        val extrasSerialized = data["extras"]
        val uri = data["uri"]

        if(listOf("broadcast", "activity").contains(type)){
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
            }
        }
    }
}
