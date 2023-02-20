package com.samplernbridge.payment

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

interface WebHelperInterface {
    fun getAppList(link: String): List<ResolveInfo>
    fun onResponseReceived(returnedParams: Map<String, String>)
    fun openApp(appPkg: String, url: String)
    fun getAppName(pkg: ApplicationInfo): String
}

class WebJSInterfaceImpl(private val callback: WebHelperInterface) {
    private val TAG: String = "WebIntentJSInterface"


    @JavascriptInterface
    fun paymentResult(result: String): String {
        val returnedParams: MutableMap<String, String> = mutableMapOf()
        try {
            val response = JSONObject(result)
            val iterator: Iterator<String> = response.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                returnedParams[key] = response.get(key).toString()
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
        callback.onResponseReceived(returnedParams)
        return "true"
    }


    @JavascriptInterface
    fun getAppList(name: String): String {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW;
        intent.data = Uri.parse("upi://pay");
        val resInfo: List<ResolveInfo> = callback.getAppList(name)
        val packageNames = JSONArray()
        try {
            for (info: ResolveInfo in resInfo) {
                val appInfo = JSONObject()
                appInfo.put("appName", callback.getAppName(info.activityInfo.applicationInfo))
                appInfo.put("appPackage", info.activityInfo.packageName)
                packageNames.put(appInfo)
            }
        } catch (ex: Exception) {
            ex.printStackTrace();
        }
        return packageNames.toString();
    }

    @JavascriptInterface
    fun openApp(upiClientPackage: String, upiURL: String): Boolean {
        callback.openApp(upiClientPackage, upiURL);
        return true
    }
}