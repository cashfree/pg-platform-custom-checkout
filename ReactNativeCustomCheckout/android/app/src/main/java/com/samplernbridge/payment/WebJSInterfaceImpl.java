package com.samplernbridge.payment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WebJSInterfaceImpl {
    private final String TAG = "WebIntentJSInterface";

    private final WebHelperInterface callback;

    public WebJSInterfaceImpl(WebHelperInterface callback) {
        this.callback = callback;
    }

    @JavascriptInterface
    public String paymentResult(String result) {
        HashMap<String, String> returnedParams = new HashMap<>();
        try {
            JSONObject response = new JSONObject(result);
            Iterator<String> iterator = response.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                returnedParams.put(key, response.get(key).toString());
            }
            callback.onResponseReceived(returnedParams);
            return "true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    @JavascriptInterface
    public String getAppList(String name) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("upi://pay"));
        List<ResolveInfo> resolveInfoList = callback.getAppList(name);
        JSONArray packageNames = new JSONArray();
        try {
            for (ResolveInfo info : resolveInfoList) {
                JSONObject appInfo = new JSONObject();
                appInfo.put("appName", callback.getAppName(info.activityInfo.applicationInfo));
                appInfo.put("appPackage", info.activityInfo.packageName);
                packageNames.put(appInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageNames.toString();
    }

    @JavascriptInterface
    public Boolean openApp(String upiClientPackage, String upiURL) {
        callback.openApp(upiClientPackage, upiURL);
        return true;
    }
}