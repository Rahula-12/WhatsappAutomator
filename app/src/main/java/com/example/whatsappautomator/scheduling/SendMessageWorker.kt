package com.example.whatsappautomator.scheduling

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.whatsappautomator.services.WhatsAppAutomateService


class SendMessageWorker(val context: Context, workerParameters: WorkerParameters):Worker(context,workerParameters){
    companion object {
        const val WAKE_LOCK_TAG = "MyWakeLockTag"
    }
    override fun doWork(): Result {
        val keyguardManager =
            applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val isDeviceLocked = keyguardManager != null && keyguardManager.isKeyguardLocked
            if(isDeviceLocked)  return Result.retry()
        return try {
//            val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//            val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"myapp:mywakelocktag")
//            wakeLock.acquire(60 * 1000L)
            val phoneNumber=inputData.getString("phoneNumber")
            val message=inputData.getString("message")
            WhatsAppAutomateService.startActionAutomateWhatsApp(context,phoneNumber!!,message!!)
            Log.d("WorkerTest","Test passed")
//            wakeLock.release()
            Result.success()
        } catch (e:Exception) {
            Result.retry()
        }
    }

}