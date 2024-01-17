package com.example.whatsappautomator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.work.Worker
import java.util.UUID

@Entity
 class AutoMessage(
    @ColumnInfo(name="s_no")
    @PrimaryKey
    val messageNo:String=UUID.randomUUID().toString(),
    val message:String,
    val to:String,
    val time:String,
    val countryCode:String="",
    val workId:String=""
)
