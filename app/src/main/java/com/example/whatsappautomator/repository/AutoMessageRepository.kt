package com.example.whatsappautomator.repository

import com.example.whatsappautomator.database.AutoMessageDao
import com.example.whatsappautomator.model.AutoMessage
import javax.inject.Inject

class AutoMessageRepository @Inject constructor(private val autoMessageDao: AutoMessageDao) {

    suspend fun insertAutoMessage(autoMessage: AutoMessage)=autoMessageDao.insertAutoMessage(autoMessage)

    suspend fun deleteAutoMessage(autoMessage: AutoMessage)=autoMessageDao.deleteAutoMessage(autoMessage)

    fun getAllAutoMessages()=autoMessageDao.getAllMessages()

    suspend fun deleteMessageBasedOnId(id:String)=autoMessageDao.deleteMessageBasedOnId(id)

}