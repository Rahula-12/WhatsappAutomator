package com.example.whatsappautomator

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.whatsappautomator.model.AutoMessage
import com.example.whatsappautomator.scheduling.SendMessageWorker
import com.example.whatsappautomator.services.WhatsAppAccessibilityService
import com.example.whatsappautomator.ui.theme.WhatsappAutomatorTheme
import com.example.whatsappautomator.viewModel.AutoMessageViewModel
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var viewModel: AutoMessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isWhatsAppInstalled(this)) {
            Toast.makeText(applicationContext, "Please install WhatsApp first", Toast.LENGTH_SHORT)
                .show()
            finish()
        } else {
            if (!isAccessibilityServiceEnabled(
                    applicationContext,
                    WhatsAppAccessibilityService::class.java
                )
            ) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            //val viewModel:AutoMessageViewModel=hiltViewModel<>()
            val workManager = WorkManager.getInstance(applicationContext)
            val phoneNumberUtil = PhoneNumberUtil.getInstance()
            setContent {
                val viewModel:AutoMessageViewModel by viewModels()
                WhatsappAutomatorTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AutoMessageApp(
                            autoMessages = viewModel.allMessages.collectAsState().value,
                            addMessage = {
                                if (it.message.isEmpty()) {
                                    Toast.makeText(this, "Please enter message", Toast.LENGTH_LONG)
                                        .show()
                                } else if (it.to.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this,
                                        "Please enter phone number",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (it.countryCode.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this,
                                        "Please enter county code",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    val rawNumber = Phonenumber.PhoneNumber()
                                    rawNumber.countryCode = it.countryCode.toIntOrNull() ?: 91
                                    rawNumber.nationalNumber = it.to.toLong()
                                    if (phoneNumberUtil.isValidNumber(rawNumber)) {

                                        val workRequest: PeriodicWorkRequest =
                                            periodicWorkRequest(it)
                                        workManager
                                            .enqueueUniquePeriodicWork(
                                                it.messageNo,
                                                ExistingPeriodicWorkPolicy.REPLACE,
                                                workRequest
                                            )
                                        Log.d(
                                            "temp",
                                            workManager.getWorkInfosByTag(it.messageNo).toString()
                                        )
                                        viewModel.insertMessage(it)
                                        return@AutoMessageApp true
                                    } else
                                        Toast.makeText(
                                            this,
                                            "Please enter correct phone number",
                                            Toast.LENGTH_LONG
                                        ).show()
                                }
                                return@AutoMessageApp false
                            },
                            deleteAutoMessage = {
                                workManager.cancelAllWorkByTag(it.messageNo)
                                viewModel.deleteMessage(it)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Des","OnDestroy called")
    }
    private fun periodicWorkRequest(it: AutoMessage): PeriodicWorkRequest {
        val hour: Int = it.time.substring(0, 2).toInt()
        val minute: Int = it.time.substring(3).toInt()
        val data:Data=Data.Builder().putString("phoneNumber",it.to).putString("message",it.message).build()
        return PeriodicWorkRequestBuilder<SendMessageWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MILLISECONDS)
            .addTag(it.messageNo)
            .setBackoffCriteria(BackoffPolicy.LINEAR,1000L,TimeUnit.MILLISECONDS)
            .build()
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val selectedTimeMillis = calendar.timeInMillis

        if (selectedTimeMillis <= currentTimeMillis) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis - currentTimeMillis
    }

}

fun isAccessibilityServiceEnabled(
    context: Context,
    service: Class<out AccessibilityService?>
): Boolean {
    val am: AccessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices: List<AccessibilityServiceInfo> =
        am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    for (enabledService in enabledServices) {
        val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
        if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                service.name
            )
        ) return true
    }
    return false
}

fun isWhatsAppInstalled(context: Context): Boolean {
    val packageManager = context.packageManager
    return try {
        packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
        true // WhatsApp is installed
    } catch (e: PackageManager.NameNotFoundException) {
        false // WhatsApp is not installed
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhatsappAutomatorTheme {

    }
}