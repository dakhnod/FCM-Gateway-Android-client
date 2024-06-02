package d.d.fcmgateway.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent

class MockAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}