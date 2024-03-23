package com.example.whatsappautomator.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_GENERIC
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat


class WhatsAppAccessibilityService: AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo.apply {
            eventTypes=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames= arrayOf("com.whatsapp")
            feedbackType= FEEDBACK_GENERIC
            notificationTimeout=100
            description
        }
    }
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        if(rootInActiveWindow ==null)   return
        val rootNodeInfo:AccessibilityNodeInfoCompat=AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)
        val messageNodeList:List<AccessibilityNodeInfoCompat>?= rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
        if(messageNodeList.isNullOrEmpty())  return
        val messageField=messageNodeList[0]
        if(messageField.text.isNullOrEmpty() || !messageField.text.endsWith("   ")) return
        val sendMessageNodeList:List<AccessibilityNodeInfoCompat>?= rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
        if(sendMessageNodeList.isNullOrEmpty())  return
        val sendButton=sendMessageNodeList[0]
        if(!sendButton.isVisibleToUser) return
        sendButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        try{
            Thread.sleep(3000)
            performGlobalAction(GLOBAL_ACTION_BACK)
            Thread.sleep(2000)
        }
        catch(e:InterruptedException) {

        }
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}