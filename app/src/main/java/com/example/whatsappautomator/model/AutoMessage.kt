package com.example.whatsappautomator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AutoMessage(
    @ColumnInfo(name="s_no")
    @PrimaryKey(autoGenerate = true)
    val messageNo:Int=0,
    val message:String,
    val to:String,
    val time:String
)
