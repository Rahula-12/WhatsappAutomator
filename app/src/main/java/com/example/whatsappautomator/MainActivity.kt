package com.example.whatsappautomator

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
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
        val packageManager=applicationContext.packageManager
        val whatsAppIntent=Intent(Intent.ACTION_VIEW)
        whatsAppIntent.setPackage("com.whatsapp")
        whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        if(whatsAppIntent.resolveActivity(packageManager)==null) {
//            Toast.makeText(applicationContext,"Please install WhatsApp first",Toast.LENGTH_SHORT).show()
//            exitProcess(0)
//        }
        if(!isAccessibilityServiceEnabled(applicationContext,WhatsAppAccessibilityService::class.java)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        //val viewModel:AutoMessageViewModel=hiltViewModel<>()
        val workManager=WorkManager.getInstance(applicationContext)
        val phoneNumberUtil=PhoneNumberUtil.getInstance()
        setContent {
            val viewModel= viewModel<AutoMessageViewModel>()
            WhatsappAutomatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AutoMessageApp(
                        autoMessages = viewModel.allMessages.collectAsState().value,
                        addMessage = {
                            if(it.message.isEmpty()) {
                                Toast.makeText(this,"Please enter message",Toast.LENGTH_LONG).show()
                            }
                            else if(it.to.isNullOrEmpty()) {
                                Toast.makeText(this,"Please enter phone number",Toast.LENGTH_LONG).show()
                            }
                            else if(it.countryCode.isNullOrEmpty()) {
                                Toast.makeText(this,"Please enter county code",Toast.LENGTH_LONG).show()
                            }
                            else {
                                val rawNumber = Phonenumber.PhoneNumber()
                                rawNumber.countryCode=it.countryCode.toIntOrNull()?:91
                                rawNumber.nationalNumber = it.to.toLong()
                                if (phoneNumberUtil.isValidNumber(rawNumber)) {
                                    val workRequest: PeriodicWorkRequest = periodicWorkRequest(it)
                                    workManager
                                        .enqueueUniquePeriodicWork(it.messageNo, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
                                    viewModel.insertMessage(it)
                                    return@AutoMessageApp true
                                }
                                else
                                    Toast.makeText(this,"Please enter correct phone number",Toast.LENGTH_LONG).show()
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
    private fun periodicWorkRequest(it: AutoMessage): PeriodicWorkRequest {
        val calendar: Calendar = Calendar.getInstance()
        val nowMillis: Long = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, it.time.subSequence(0, 2).toString().toInt())
        calendar.set(Calendar.MINUTE, it.time.substring(3).toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        val diff: Long = calendar.timeInMillis - nowMillis
        val data:Data=Data.Builder()
            .putString("message",it.message)
            .putString("phoneNumber",it.to)
            .build()
        return PeriodicWorkRequest.Builder(
            SendMessageWorker::class.java,
            24,
            TimeUnit.HOURS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .setInitialDelay(diff, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(it.messageNo)
            .build()
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhatsappAutomatorTheme {

    }
}