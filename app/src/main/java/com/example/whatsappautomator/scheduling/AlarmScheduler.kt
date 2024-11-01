package com.example.whatsappautomator.scheduling

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.whatsappautomator.model.AutoMessage
import java.net.URLEncoder
import java.util.Calendar

interface AlarmScheduler {

    fun schedule(autoMessage: AutoMessage)

    fun cancel(autoMessage: AutoMessage)
}

class AlarmSchedulerImpl(
    private val context: Context
):AlarmScheduler {

    companion object {
        var isFree=true
    }

    override fun schedule(autoMessage: AutoMessage) {
        val alarmManager=context.getSystemService(AlarmManager::class.java)
        val hour=autoMessage.time.substring(0,2).toInt()
        val minute=autoMessage.time.substring(3).toInt()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val selectedTimeMillis = calendar.timeInMillis
       // val packageManager=context.packageManager
        val url="https://api.whatsapp.com/send?phone=+${autoMessage.countryCode}${autoMessage.to}&text=${URLEncoder.encode(autoMessage.message,"UTF-8")}"
        val whatsAppIntent= Intent(Intent.ACTION_VIEW)
        whatsAppIntent.setPackage("com.whatsapp")
        whatsAppIntent.data = Uri.parse(url)
        whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            selectedTimeMillis,
            10000L,
            PendingIntent.getActivity(
                context,
                autoMessage.hashCode(),
                whatsAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(autoMessage: AutoMessage) {
        val alarmManager=context.getSystemService(AlarmManager::class.java)
        val url="https://api.whatsapp.com/send?phone=+917307140364&text=${URLEncoder.encode("Hi","UTF-8")}"
        val whatsAppIntent= Intent(Intent.ACTION_VIEW)
        whatsAppIntent.setPackage("com.whatsapp")
        whatsAppIntent.data = Uri.parse(url)
        whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        alarmManager.cancel(
            PendingIntent.getActivity(
                context,
                autoMessage.hashCode(),
                whatsAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}