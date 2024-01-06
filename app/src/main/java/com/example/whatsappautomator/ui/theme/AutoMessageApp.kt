package com.example.whatsappautomator.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.whatsappautomator.model.AutoMessage
import com.example.whatsappautomator.viewModel.AutoMessageViewModel

@Composable
fun AutoMessageApp(
    modifier: Modifier=Modifier,
    autoMessages:List<AutoMessage> = emptyList(),
    viewModel: AutoMessageViewModel
) {
    //viewModel.insertMessage()
   // Log.d("Size3", viewModel.returnSize())
    val list=viewModel.allMessages.collectAsState().value
    //Log.e("viewModel",viewModel.allMessages.collectAsState().value.size.toString())
    Box(modifier = modifier.fillMaxSize()){
        LazyColumn{
            items(list){
                Text(text = it.message)
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    end = 10.dp,
                    bottom = 30.dp
                ),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { viewModel.insertMessage() },
                shape = CircleShape,
                containerColor=DarkGreen,
                contentColor= Color.White,
                elevation = FloatingActionButtonDefaults.elevation(5.dp),
                modifier = modifier.size(50.dp)
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    }
}