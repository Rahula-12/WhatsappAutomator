package com.example.whatsappautomator.scheduling

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.whatsappautomator.R
import com.example.whatsappautomator.model.AutoMessage
import com.example.whatsappautomator.repository.AutoMessageRepository
import com.example.whatsappautomator.services.WhatsAppAutomateService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@HiltWorker
class SendMessageWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParameters: WorkerParameters,
    val autoMessageRepository: AutoMessageRepository
):
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
            val id=inputData.getString("id")
            val time=inputData.getString("time")
            val countryCode=inputData.getString("countryCode")
//            if(!serviceStarted) {
//                synchronized(this) {
//                    if (!serviceStarted) {
                        serviceStarted=true
                        val currTimeInMillis:Long=System.currentTimeMillis()
                        val calendar = Calendar.getInstance()
                        val hour: Int = time!!.substring(0, 2).toInt()
                        val minute: Int = time!!.substring(3).toInt()
                        calendar.timeInMillis = currTimeInMillis
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val selectedTimeMillis = calendar.timeInMillis
                        WhatsAppAutomateService.startActionAutomateWhatsApp(
                            context,
                            phoneNumber!!,
                            message!!,
                            countryCode!!
                        )
                        if(currTimeInMillis-selectedTimeMillis>15*60000L) {
                            val workManager=WorkManager.getInstance(applicationContext)
                            //autoMessageRepository.deleteMessageBasedOnId(id!!)
                            workManager.cancelAllWorkByTag("$id $phoneNumber $message $time")
                            workManager
                                .enqueueUniquePeriodicWork(
                                    id!!,
                                    ExistingPeriodicWorkPolicy.UPDATE,
                                    periodicWorkRequest(AutoMessage(id, message,phoneNumber,time,countryCode))
                                )
                        }
//                    }
//                }
//            }
            Log.d("WorkerTest","Test passed")
           // serviceStarted=false
//            wakeLock.release()
            sendNotification(
                phoneNumber = phoneNumber,
                countryCode = countryCode,
                message = message
            )
            Result.success()
        } catch (e:Exception) {
            Result.retry()
        }
    }

    private fun periodicWorkRequest(it: AutoMessage): PeriodicWorkRequest {
        val hour: Int = it.time.substring(0, 2).toInt()
        val minute: Int = it.time.substring(3).toInt()
        val data: Data = Data.Builder()
            .putString("phoneNumber",it.to)
            .putString("message",it.message)
            .putString("id",it.messageNo)
            .putString("time",it.time)
            .putString("countryCode",it.countryCode)
            .build()
        return PeriodicWorkRequestBuilder<SendMessageWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
            .addTag(it.messageNo+" "+it.to+" "+it.message+" "+it.time)
            .setBackoffCriteria(BackoffPolicy.LINEAR,100L, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val selectedTimeMillis = calendar.timeInMillis

        if (selectedTimeMillis <= currentTimeMillis) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis - currentTimeMillis
    }

    private fun sendNotification(phoneNumber:String,message:String,countryCode:String) {
        val id= Random.nextInt()
        val notification=NotificationCompat.Builder(
            context,"success_channel"
        )
            .setSmallIcon(R.drawable.person)
            .setContentTitle("Message Delivered")
            .setContentTitle("Message \"${message.trim()}\" delivered to +${countryCode}${phoneNumber} successfully.")
            .build()
        val notificationManager=context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(id,notification)
    }

}