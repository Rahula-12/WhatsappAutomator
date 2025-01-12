package com.example.whatsappautomator

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.whatsappautomator.model.AutoMessage
import com.example.whatsappautomator.ui.theme.DarkGreen
import com.example.whatsappautomator.ui.theme.LightGreen
import java.time.LocalDateTime
import java.util.Calendar

//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoMessageApp(
    modifier: Modifier = Modifier,
    autoMessages: List<AutoMessage> = listOf(
        AutoMessage(
            message = "",
            to = "",
            time = "",
            countryCode = ""
        )
    ),
    addMessage: (AutoMessage) -> Boolean = { false },
    updateAutoMessage:(AutoMessage)->Boolean={false},
    deleteAutoMessage: (AutoMessage) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        "WhatsApp Automator",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.W600,
                        fontSize = TextUnit(25f,TextUnitType.Sp)
                    )
                })
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Center
            ) {
                items(autoMessages) {
                    AutoMessageItem(
                        autoMessage = it,
                        deleteAutoMessage = deleteAutoMessage,
                        updateAutoMessage = updateAutoMessage
                    )
                }
            }
            AddAutoMessage(modifier,addMessage)
        }
    }
}

//@Preview
@Composable
fun AutoMessageItem(
    modifier: Modifier = Modifier,
    autoMessage: AutoMessage = AutoMessage(message = "Hi", time = "hh:mm", to = "7307140364"),
    deleteAutoMessage: (AutoMessage) -> Unit = {},
    updateAutoMessage:(AutoMessage)->Boolean={false}
) {
    val showUpdateDialog= remember {
        mutableStateOf(false)
    }
    if(showUpdateDialog.value) {
        AutoMessageDialog(showUpdateDialog, modifier, updateAutoMessage, isUpdating = true, autoMessage)
    }
    Row(
        modifier = modifier
            .clickable {
                showUpdateDialog.value = true
            }
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier=modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Rounded.Person,
                contentDescription = "person",
                modifier = modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = modifier.padding(
                    start = 15.dp
                )
            ) {
                Row(
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = autoMessage.to,
                        fontWeight = FontWeight.SemiBold,
                        modifier = modifier.weight(3f),
                        fontSize = TextUnit(25f, type = TextUnitType.Sp)
                    )
                    Text(
                        text = autoMessage.time,
                        textAlign = TextAlign.End,
                        color = LightGreen
//                modifier = modifier.align(
//                    Alignment.Top
//                ),
                        //fontSize = TextUnit(20f, TextUnitType.Sp)
                    )
                }
                Row(
                    modifier = modifier.fillMaxWidth()
                ) {
                    Text(
                        text = autoMessage.message,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = modifier
                            .padding(
                                bottom = 10.dp
                            )
                            .weight(3f)
                    )
                    Image(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete",
                        alignment = Alignment.TopEnd,
                        colorFilter = ColorFilter
                            .tint(Color.Red),
                        modifier = modifier.clickable {
                            deleteAutoMessage(autoMessage)
                        }
                    )
                }

                Divider(
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun AddAutoMessage(
    modifier: Modifier,
    addMessage: (AutoMessage) -> Boolean
) {
    val showDialog= remember {
        mutableStateOf(false)
    }
    if(showDialog.value) {
        AutoMessageDialog(showDialog, modifier, addMessage, isUpdating = false, AutoMessage(
            message = "",
            to = "",
            time = "",
            countryCode = ""
        ))
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
            onClick = {
                showDialog.value = true
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(5.dp),
            modifier = modifier.size(50.dp)
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        }
    }
}

//@Preview
@Composable
private fun AutoMessageDialog(
    showDialog: MutableState<Boolean> = mutableStateOf(false),
    modifier: Modifier = Modifier,
    operation: (AutoMessage) -> Boolean = { false },
    isUpdating: Boolean,
    autoMessage: AutoMessage
) {
    Dialog(onDismissRequest = { showDialog.value = false }) {
        val message = remember {
            mutableStateOf(autoMessage.message)
        }
        val phoneNumber = remember {
            mutableStateOf(autoMessage.to)
        }
        val countryCode = remember {
            mutableStateOf(autoMessage.countryCode)
        }
        val time = LocalDateTime.now().plusMinutes(1L)
        val timePickerState = remember {
            TimePickerState(
                is24Hour = true,
                initialHour = time.hour,
                initialMinute = time.minute
            )
        }
        val startDate= remember {
            mutableStateOf("Start Date")
        }
        val endDate= remember {
            mutableStateOf("End Date")
        }
        val forever= remember {
            mutableStateOf(false)
        }
        val datePickerDialog1=DatePickerDialog(
            LocalContext.current,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                startDate.value="$mDayOfMonth/${mMonth+1}/$mYear"
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        val datePickerDialog2=DatePickerDialog(
            LocalContext.current,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                endDate.value="$mDayOfMonth/${mMonth+1}/$mYear"
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        Column(
            modifier = modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            InputMessage(message, modifier)
            PhoneNumberWithCode(modifier, countryCode, phoneNumber)
            InputTime(modifier, timePickerState)

//            datePickerDialog.show()
            //StartAndEndDate(modifier, datePickerDialog1,datePickerDialog2,forever,startDate,endDate)
           // ForeverSchedule(modifier,forever)
            DecisionButtons(
                isUpdating,
                modifier,
                operation,
                message,
                phoneNumber,
                countryCode,
                timePickerState,
                showDialog,
                autoMessage
            )
        }
    }
}

@Composable
private fun ForeverSchedule(
    modifier: Modifier,
    forever:MutableState<Boolean>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier.padding(
            start = 5.dp
        )
    ) {
        Text(
            text = "Forever:",
            modifier = modifier.padding(end = 5.dp),
            fontSize = TextUnit(20f, type = TextUnitType.Sp)
        )
        Switch(
            //modifier=Modifier.fillMaxWidth(),
            checked = forever.value, onCheckedChange = {
                forever.value=it
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor=DarkGreen,
            ),
            thumbContent = {
                if(forever.value)
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "forever"
                )
                else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "forever"
                    )
                }
            })
    }
}

@Composable
private fun StartAndEndDate(
    modifier: Modifier,
    datePickerDialog1: DatePickerDialog,
    datePickerDialog2: DatePickerDialog,
    forever:MutableState<Boolean>,
    startDate:MutableState<String>,
    endDate:MutableState<String>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 5.dp,
                end = 5.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            colors=ButtonDefaults.buttonColors(
                contentColor= DarkGreen,
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
            border= BorderStroke(width = 1.dp, color = if(!forever.value) Color.Black  else Color.Transparent),
            enabled = !forever.value,
            onClick = { if(!forever.value) datePickerDialog1.show() },
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(text = startDate.value)
        }
        OutlinedButton(
            colors=ButtonDefaults.buttonColors(
                contentColor= DarkGreen,
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
            border= BorderStroke(width = 1.dp, color = if(!forever.value) Color.Black  else Color.Transparent),
            enabled = !forever.value,
            onClick = { if(!forever.value) datePickerDialog2.show() },
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(text = endDate.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneNumberWithCode(
    modifier: Modifier,
    countryCode: MutableState<String>,
    phoneNumber: MutableState<String>
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            singleLine=true,
            value = countryCode.value,
            onValueChange = {
                countryCode.value = it
            },
            placeholder = {
                Text(
                    text = "+91",
                    color = MaterialTheme.colorScheme.onSurface
                )

            },
            modifier = modifier
                .weight(1f)
                .padding(5.dp),
            shape = RoundedCornerShape(5.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors=TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =DarkGreen
            )
        )
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = {
                phoneNumber.value = it
            },
            singleLine = true,
            placeholder = {
                Text(
                    text = "Enter Phone number",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = modifier
                .weight(3f)
                .padding(5.dp),
            shape = RoundedCornerShape(5.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors=TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor=DarkGreen
            )
        )
    }
}

@Composable
private fun DecisionButtons(
    isUpdating:Boolean=false,
    modifier: Modifier,
    operation: (AutoMessage) -> Boolean,
    message: MutableState<String>,
    phoneNumber: MutableState<String>,
    countryCode: MutableState<String>,
    timePickerState: TimePickerState,
    showDialog: MutableState<Boolean>,
    autoMessage: AutoMessage
) {
    Row(
        modifier = modifier.padding(
            bottom = 0.dp
        ),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            modifier = modifier
                .weight(1f)
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            onClick = {
                if (operation(
                        AutoMessage(
                            messageNo = autoMessage.messageNo,
                            message = message.value,
                            to = phoneNumber.value,
                            countryCode = countryCode.value,
                            time = "${
                                if (timePickerState.hour > 9)
                                    timePickerState.hour
                                else
                                    "0" + timePickerState.hour
                            }:${
                                if (timePickerState.minute > 9)
                                    timePickerState.minute
                                else
                                    "0" + timePickerState.minute
                            }",
                        )
                    )
                )
                    showDialog.value = false
            }
        ) {
            if(!isUpdating)
            Text("Add Message",textAlign=TextAlign.Center)
            else Text("Update Message",textAlign=TextAlign.Center)
        }
        Button(
            onClick = { showDialog.value = false },
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            modifier = modifier
                .weight(1f)
                .padding(
                    start = 5.dp,
                    end = 5.dp
                ),
        ) {
            Text(text = "Cancel")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun InputTime(
    modifier: Modifier,
    timePickerState: TimePickerState
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                bottom = 10.dp
            ),
    ) {
        TimePicker(
            state = timePickerState,
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            colors=TimePickerDefaults.colors(
                clockDialColor = DarkGreen,
                clockDialUnselectedContentColor=Color.White,
                clockDialSelectedContentColor=Color.DarkGray,
                selectorColor=MaterialTheme.colorScheme.surface,
                timeSelectorSelectedContainerColor = DarkGreen,
                timeSelectorSelectedContentColor=Color.White,
                timeSelectorUnselectedContainerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputMessage(
    message: MutableState<String>,
    modifier: Modifier
) {
    OutlinedTextField(
        value = message.value,
        onValueChange = {
            message.value = it
        },
        singleLine = true,
        placeholder = {
            Text(
                text = "Enter Message",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(5.dp),
        colors=TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor=DarkGreen
        )
    )
}