package com.samplernbridge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule
import com.samplernbridge.payment.PaymentCheckoutActivity

@ReactModule(name = RNBridgeModule.NAME)
class RNBridgeModule(private val context: ReactApplicationContext?) :
    ReactContextBaseJavaModule(context),
    ActivityEventListener {
    /**
     * We will use this name as module bridge.
     * Using this name, we call module class methods.
     */
    override fun getName(): String {
        return NAME
    }

    init {
        context?.addActivityEventListener(this)
    }

    /**
     * Using this callback , we pass payment checkout result to App.tsx file
     */
    private lateinit var callback: Callback

    /**
     * We will call this method from App.tsx file. This method will invoke PaymentCheckoutActivity.
     */
    @ReactMethod
    fun doPayment(sessionId: String?, returnUrl: String?, callback: Callback) {
        Log.d("RNBridgeModule", sessionId!!)
        this.callback = callback
        try {
            val activity = currentActivity
            if (activity != null) {
                val bundle = Bundle()
                bundle.putString("session_id", sessionId)
                bundle.putString("return_url", returnUrl)
                val intent = Intent(activity, PaymentCheckoutActivity::class.java)
                intent.putExtras(bundle)
                activity.startActivityForResult(intent, PAYMENT_RESULT_CODE)
            } else {
                throw IllegalStateException("activity is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val NAME = "PaymentSDKApi"
        const val PAYMENT_RESULT_CODE = 1005
    }

    /**
     * Here We will receive response from PaymentCheckoutActivity.
     */
    override fun onActivityResult(p0: Activity?, p1: Int, p2: Int, p3: Intent?) {
        if (p1 == PAYMENT_RESULT_CODE && p2 == Activity.RESULT_OK) {
            val returnUrl = p3?.getStringExtra("returnUrl")
            callback.invoke(returnUrl, "Start Verify Payment")
        }
    }

    override fun onNewIntent(p0: Intent?) {}
}