package com.example.whatsappautomator.scheduling

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder
import java.util.Calendar
import java.util.Random

class ScheduleReceiver():BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if(it.action=="Schedule") {
                val keyguardManager =
                    context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                val alarmManager=context.getSystemService(AlarmManager::class.java)
                val started=System.currentTimeMillis()
                while(keyguardManager.isKeyguardLocked && System.currentTimeMillis()-started<=15*60000L) {

                }
                if(System.currentTimeMillis()-started>15*60000L) {
                    val currentTimeMillis = System.currentTimeMillis()
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = currentTimeMillis
                    calendar.add(Calendar.MINUTE,1)
                    val selectedTimeMillis = calendar.timeInMillis
                    val newIntent=Intent(context,ScheduleReceiver::class.java)
                    newIntent.action="Schedule"
                    newIntent.putExtra("countryCode",it.getStringExtra("countryCode"))
                    newIntent.putExtra("phoneNumber",it.getStringExtra("phoneNumber"))
                    newIntent.putExtra("message",it.getStringExtra("message"))
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC,selectedTimeMillis,
                        PendingIntent.getBroadcast(
                            context,
                            intent.getIntExtra("hashCode",Random().nextInt())-1,
                            newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    return
                }
                while (!AlarmSchedulerImpl.isFree) {

                }
                AlarmSchedulerImpl.isFree=false
                val countryCode=intent.getStringExtra("countryCode")
                val message=intent.getStringExtra("message")
                val phoneNumber=intent.getStringExtra("phoneNumber")
                val url="https://api.whatsapp.com/send?phone=+${countryCode}${phoneNumber}&text=${URLEncoder.encode(message,"UTF-8")}"
                val whatsAppIntent= Intent(Intent.ACTION_VIEW)
                whatsAppIntent.setPackage("com.whatsapp")
                whatsAppIntent.data = Uri.parse(url)
                whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(whatsAppIntent)
            }
        }
    }
}