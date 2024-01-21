package com.example.whatsappautomator.scheduling

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.whatsappautomator.services.WhatsAppAutomateService

class SendMessageWorker(val context: Context, workerParameters: WorkerParameters):Worker(context,workerParameters){
    override fun doWork(): Result {
        val phoneNumber=inputData.getString("phoneNumber")
        val message=inputData.getString("message")
        WhatsAppAutomateService.startActionAutomateWhatsApp(context,phoneNumber!!,message!!)
        return Result.success()
    }

}