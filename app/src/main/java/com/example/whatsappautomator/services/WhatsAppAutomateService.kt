package com.example.whatsappautomator.services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.lang.Exception
import java.net.URLEncoder

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_WHATSAPP = "com.example.whatsappautomator.services.action.FOO"

// TODO: Rename parameters
private const val MOBILE_NUMBER = "com.example.whatsappautomator.services.extra.PARAM1"
private const val MESSAGE = "com.example.whatsappautomator.services.extra.PARAM2"
private const val COUNTRY_CODE = "com.example.whatsappautomator.services.extra.PARAM3"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class WhatsAppAutomateService : IntentService("WhatsAppAutomateService") {

    override fun onHandleIntent(intent: Intent?) {
        val mobileNumber :String?= intent!!.getStringExtra(MOBILE_NUMBER)
        val message = intent.getStringExtra(MESSAGE)
        val countryCode=intent.getStringExtra(COUNTRY_CODE)
        handleActionWhatsApp(mobileNumber, message, countryCode)
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionWhatsApp(mobileNumber: String?, message: String?,countryCode:String?) {
        try{
            val packageManager=applicationContext.packageManager
            val url="https://api.whatsapp.com/send?phone=+${countryCode}${mobileNumber}&text=${URLEncoder.encode(message,"UTF-8")}"
            val whatsAppIntent=Intent(Intent.ACTION_VIEW)
            whatsAppIntent.setPackage("com.whatsapp")
            whatsAppIntent.data = Uri.parse(url)
            whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if(whatsAppIntent.resolveActivity(packageManager)!=null) {
                applicationContext.startActivity(whatsAppIntent)
                Thread.sleep(5000)
            }
            else {
                    //Toast.makeText(applicationContext,"Please install WhatsApp.",Toast.LENGTH_SHORT).show()
                    return
            }
        }
        catch (e:Exception) {
            Log.d("ServiceError",e.toString())
        }
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionAutomateWhatsApp(context: Context, mobileNumber: String, message: String,countryCode:String) {
            val intent = Intent(context, WhatsAppAutomateService::class.java).apply {
                action = ACTION_WHATSAPP
                putExtra(MOBILE_NUMBER, mobileNumber)
                putExtra(MESSAGE, message)
                putExtra(COUNTRY_CODE,countryCode)
            }
            context.startService(intent)
        }
    }
}