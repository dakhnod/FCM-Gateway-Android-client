package d.d.fcmgateway.service;

import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject

class MessageReceiverService : FirebaseMessagingService() {
    private val TAG = "MessageReceiverService"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val type = data.withDefault { "intent" }["type"]
        val action = data["action"]
        val application = data["application"]
        val extrasSerialized = data["extras"]

        if(type == "intent"){
            val intent = Intent()
            action?.let { intent.action = action }
            application?.let { intent.`package` = application }
            extrasSerialized?.let {
                val extras = JSONObject(extrasSerialized)
                extras.keys().forEach {
                    when(val value = extras[it]){
                        is Int -> intent.putExtra(it, value)
                        is Double -> intent.putExtra(it, value)
                        is Boolean -> intent.putExtra(it, value)
                        is String -> intent.putExtra(it, value)
                        else -> Log.e(TAG, "onMessageReceived: key $it contains invalid class: ${value.javaClass}")
                    }
                }
            }
            Log.d(TAG, "onMessageReceived: sending Intent")
            sendBroadcast(intent)
        }

        Log.d(TAG, "onMessageReceived: ")
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}
