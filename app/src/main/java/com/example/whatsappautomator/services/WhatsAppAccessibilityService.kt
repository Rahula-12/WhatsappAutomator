package com.example.whatsappautomator.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.example.whatsappautomator.scheduling.AlarmSchedulerImpl
import com.example.whatsappautomator.scheduling.SendMessageWorker

class WhatsAppAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames = arrayOf("com.whatsapp")
            feedbackType = FEEDBACK_GENERIC
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (rootInActiveWindow == null) return
        val rootNodeInfo: AccessibilityNodeInfoCompat =
            AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)
        val messageNodeList: List<AccessibilityNodeInfoCompat>? =
            rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
        if (messageNodeList.isNullOrEmpty()) return
        val messageField = messageNodeList[0]
        Log.d("WhatsAppAccessibilityService", "Message Field Text: ${messageField.text}")

        if (!messageField.text.toString().contains("   ")) {
            Log.d("WhatsAppAccessibilityService", "Message field text condition not met")
            return
        }

        val sendMessageNodeList: List<AccessibilityNodeInfoCompat>? =
            rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
        if (sendMessageNodeList.isNullOrEmpty()) return
        val sendButton = sendMessageNodeList[0]
        if (!sendButton.isVisibleToUser) return

        Log.d("WhatsAppAccessibilityService", "Clicking send button")
        sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        try {
            Thread.sleep(3600)
            performGlobalAction(GLOBAL_ACTION_BACK)
            Thread.sleep(2000)
        } catch (e: InterruptedException) {
            Log.e("WhatsAppAccessibilityService", "InterruptedException: ${e.message}")
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
        SendMessageWorker.serviceStarted=false
    }

    override fun onInterrupt() {
        Log.d("WhatsAppAccessibilityService", "Service Interrupted")
    }
}