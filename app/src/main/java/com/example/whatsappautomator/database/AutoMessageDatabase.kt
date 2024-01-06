package com.example.whatsappautomator.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.whatsappautomator.model.AutoMessage

@Database(entities=[AutoMessage::class], version = 2, exportSchema = false)
abstract class AutoMessageDatabase: RoomDatabase() {
    abstract fun getAutoMessageDao():AutoMessageDao

}