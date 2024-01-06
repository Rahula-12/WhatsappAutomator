package com.example.whatsappautomator.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappautomator.model.AutoMessage
import com.example.whatsappautomator.repository.AutoMessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutoMessageViewModel @Inject constructor(private val autoMessageRepository: AutoMessageRepository) : ViewModel() {
         val allMessages:StateFlow<List<AutoMessage>> = autoMessageRepository.getAllAutoMessages().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        insertMessage()
    }

    fun insertMessage(
        //autoMessage: AutoMessage
    ) {
        viewModelScope.launch {
            autoMessageRepository.insertAutoMessage(AutoMessage(message = "Hi",to="Diya",time="hh:mm:ss"))
            //Log.d("Size22",allMessages.value.size.toString())
        }
    }

    fun deleteMessage(autoMessage: AutoMessage) {
        viewModelScope.launch {
            autoMessageRepository.deleteAutoMessage(autoMessage)
        }
    }

    fun returnSize():String {
        //insertMessage()
       // Log.d("Size33",allMessages.value.size.toString())
        return allMessages.value.toString()
    }



}