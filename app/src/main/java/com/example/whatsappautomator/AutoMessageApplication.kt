package com.example.whatsappautomator

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AutoMessageApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val notificationChannel=NotificationChannel("success_channel","Message Delivered",NotificationManager.IMPORTANCE_HIGH)
        val notificationManager=getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

}