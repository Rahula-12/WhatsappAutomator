package com.example.whatsappautomator.scheduling

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.whatsappautomator.services.WhatsAppAutomateService


class SendMessageWorker(val context: Context, workerParameters: WorkerParameters):
    CoroutineWorker(context,workerParameters){
    companion object {
        var serviceStarted=false
    }
    override suspend fun doWork(): Result {
        val keyguardManager =
            applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val started=System.currentTimeMillis()
            while(keyguardManager.isKeyguardLocked && System.currentTimeMillis()-started<=15*60000L) {

            }
            if(System.currentTimeMillis()-started>15*60000L)    return Result.retry()
            while (serviceStarted) {

            }
        return try {
//            val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//            val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"myapp:mywakelocktag")
//            wakeLock.acquire(60 * 1000L)
            val phoneNumber=inputData.getString("phoneNumber")
            val message=inputData.getString("message")
//            if(!serviceStarted) {
//                synchronized(this) {
//                    if (!serviceStarted) {
                        serviceStarted=true
                        WhatsAppAutomateService.startActionAutomateWhatsApp(
                            context,
                            phoneNumber!!,
                            message!!
                        )
//                    }
//                }
//            }
            Log.d("WorkerTest","Test passed")
           // serviceStarted=false
//            wakeLock.release()
            Result.success()
        } catch (e:Exception) {
            Result.retry()
        }
    }

}