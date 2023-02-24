package com.samplernbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.samplernbridge.payment.PaymentCheckoutActivity;

@ReactModule(name = RNBridgeModule.NAME)
public class RNBridgeModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    public static final String NAME = "PaymentSDKApi";
    private final int PAYMENT_RESULT_CODE = 1005;

    /**
     * Using this callback , we pass payment checkout result to App.tsx file
     */
    private Callback callback;

    public RNBridgeModule(ReactApplicationContext reactContext) {
        super(reactContext);
        if (reactContext != null) {
            reactContext.addActivityEventListener(this);
        }
    }

    /**
     * We will use this name as module bridge.
     * Using this name, we call module class methods.
     */
    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    /**
     * We will call this method from App.tsx file. This method will invoke PaymentCheckoutActivity.
     */
    @ReactMethod
    public void doPayment(String sessionId, String returnUrl, Callback callback) {
        Log.d("RNBridgeModule", sessionId);
        this.callback = callback;
        try {
            Activity activity = getCurrentActivity();
            if (activity != null) {
                Bundle bundle = new Bundle();
                bundle.putString("session_id", sessionId);
                bundle.putString("return_url", returnUrl);
                Intent intent = new Intent(activity, PaymentCheckoutActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, PAYMENT_RESULT_CODE);
            } else {
                throw new IllegalStateException("activity is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Here We will receive response from PaymentCheckoutActivity.
     */

    @Override
    public void onActivityResult(Activity p0, int p1, int p2, Intent p3) {
        if (p1 == PAYMENT_RESULT_CODE && p2 == Activity.RESULT_OK) {
            String returnUrl = p3.getStringExtra("returnUrl");
            callback.invoke(returnUrl, "Start Verify Payment");
        }
    }

    public void onNewIntent(Intent intent) {
    }
}