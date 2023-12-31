package com.example.whatsappautomator.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.whatsappautomator.database.AutoMessageDao
import com.example.whatsappautomator.database.AutoMessageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AutoMessageModule {

    @Singleton
    @Provides
    fun providesAutoMessageDb(@ApplicationContext context: Context):AutoMessageDatabase{
        return Room.databaseBuilder(
            context = context,
            name="auto_message_db",
            klass = AutoMessageDatabase::class.java
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun providesAutoMessageDao(autoMessageDatabase: AutoMessageDatabase):AutoMessageDao{
        return autoMessageDatabase.getAutoMessageDao()
    }


}