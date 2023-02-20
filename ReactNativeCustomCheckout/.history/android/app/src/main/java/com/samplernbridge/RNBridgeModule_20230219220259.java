package com.samplernbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.paymentservice.PaymentCheckoutActivity;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = RNBridgeModule.NAME)
public class RNBridgeModule extends ReactContextBaseJavaModule {

    public static final String NAME = "PaymentSDKApi";

    public RNBridgeModule(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void doPayment(String sessionId, String returnUrl) {
        Log.d("RNBridgeModule", sessionId);
        try {
            Activity activity = getCurrentActivity();
            if (activity != null) {
                Bundle bundle = new Bundle();
                bundle.putString("session_id", sessionId);
                bundle.putString("return_url", returnUrl);
                Intent intent = new Intent(activity, PaymentCheckoutActivity.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            } else {
                throw new IllegalStateException("activity is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
