package com.example.whatsappautomator.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.whatsappautomator.model.AutoMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoMessageDao {

    @Insert
    suspend fun insertAutoMessage(autoMessage: AutoMessage)

    @Delete
    suspend fun deleteAutoMessage(autoMessage: AutoMessage)

    @Query("Select * from AutoMessage")
    fun getAllMessages(): Flow<List<AutoMessage>>

}