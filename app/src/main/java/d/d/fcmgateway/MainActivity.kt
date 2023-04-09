package d.d.fcmgateway

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getFCMToken()
    }

    fun getFCMToken(){
        val registrationText = findViewById<TextView>(R.id.text_registration_message)

        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener(OnCompleteListener { task ->
                if(!task.isSuccessful){
                    Log.e(TAG, "getFCMToken failed", task.exception)
                    registrationText.text = getString(R.string.msg_registration_error, task.exception?.message)
                    return@OnCompleteListener
                }
                val token = task.result

                registrationText.setOnClickListener {
                    val clipboardService = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardService.setPrimaryClip(ClipData(
                        ClipDescription("FCM Gateway registration token", arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)),
                        ClipData.Item(token)
                    ))
                    Toast.makeText(this, getString(R.string.msg_registration_token_copied), Toast.LENGTH_SHORT).show()
                }

                registrationText.text = getString(R.string.msg_registration_tap_here)
                Log.d(TAG, "getFCMToken: token: $token")
            })
    }
}