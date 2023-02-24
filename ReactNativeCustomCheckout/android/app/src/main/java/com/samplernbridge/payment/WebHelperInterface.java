package com.samplernbridge.payment;

import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;

import java.util.List;
import java.util.Map;

public interface WebHelperInterface {
    List<ResolveInfo> getAppList(String link);

    void onResponseReceived(Map<String, String> returnedParams);

    void openApp(String appPkg, String url);

    String getAppName(ApplicationInfo pkg);
}
