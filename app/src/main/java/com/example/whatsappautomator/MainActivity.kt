package com.example.whatsappautomator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
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
import com.example.whatsappautomator.services.WhatsAppAccessibilityService
import com.example.whatsappautomator.ui.theme.AutoMessageApp
import com.example.whatsappautomator.ui.theme.WhatsappAutomatorTheme
import com.example.whatsappautomator.viewModel.AutoMessageViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    @Inject
//    lateinit var viewModel: AutoMessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!isAccessibilitySettingsOn(applicationContext)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        //val viewModel:AutoMessageViewModel=hiltViewModel<>()
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
                            if(it.message.isNullOrEmpty() || it.to.isNullOrEmpty() || it.time.isNullOrEmpty())
                                Toast.makeText(this,"Please enter correct data",Toast.LENGTH_LONG).show()
                            else {
                                val rawNumber = Phonenumber.PhoneNumber()
                                rawNumber.countryCode=91
                                rawNumber.nationalNumber = it.to.toLong()
                                if (phoneNumberUtil.isValidNumber(rawNumber))
                                    viewModel.insertMessage(it)
                                else
                                    Toast.makeText(this,"Please enter correct data",Toast.LENGTH_LONG).show()
                            }
                        },
                        deleteAutoMessage = {

                            viewModel.deleteMessage(it)

                        }
                    )
                }
            }
        }
    }
}

private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
    var accessibilityEnabled = 0
    val service: String = "com/example/whatsappautomator/services" + "/" + WhatsAppAccessibilityService::class.java.canonicalName
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            mContext.applicationContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
        //Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
    } catch (e: Settings.SettingNotFoundException) {
//        Log.e(
//            TAG, "Error finding setting, default accessibility to not found: "
//                    + e.getMessage()
//        )
    }
    val mStringColonSplitter = SimpleStringSplitter(':')
    if (accessibilityEnabled == 1) {
        //Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
        val settingValue: String = Settings.Secure.getString(
            mContext.applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService = mStringColonSplitter.next()
//                Log.v(
//                    TAG,
//                    "-------------- > accessibilityService :: $accessibilityService $service"
//                )
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    //Log.v(TAG, "We've found the correct setting - accessibility is switched on!")
                    return true
                }
            }
        }
    } else {
       // Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
    }
    return false
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WhatsappAutomatorTheme {

    }
}