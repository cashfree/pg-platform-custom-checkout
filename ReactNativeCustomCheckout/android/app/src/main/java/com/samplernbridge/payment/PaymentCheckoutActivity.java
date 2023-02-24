package com.samplernbridge.payment;

import static com.samplernbridge.payment.Constants.REQ_CODE_UPI;
import static com.samplernbridge.payment.Constants.WB_INTENT_BRIDGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.samplernbridge.databinding.PaymentCheckoutActivityBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentCheckoutActivity extends AppCompatActivity {
    private static final String TAG = "PaymentCheckoutActivity";

    private PaymentCheckoutActivityBinding binding;
    private String returnUrl = "";

    private final WebJSInterfaceImpl webJsBridge = new WebJSInterfaceImpl(new WebHelperInterface() {
        @Override
        public List<ResolveInfo> getAppList(String link) {
            Log.v(TAG, "Called getAppList");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("upi://pay"));
            PackageManager pm = getPackageManager();
            return pm.queryIntentActivities(intent, 0);
        }

        @Override
        public void onResponseReceived(Map<String, String> returnedParams) {
            Log.v(TAG, "Called onResponseReceived");
            Toast.makeText(
                    PaymentCheckoutActivity.this,
                    "ResponseReceived", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void openApp(String appPkg, String url) {
            Log.v(TAG, "Called openApp");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            PackageManager pm = getPackageManager();
            List<ResolveInfo> resInfoList = pm.queryIntentActivities(intent, 0);
            boolean foundPackageFlag = false;
            ResolveInfo upiClientResolveInfo = null;
            for (ResolveInfo info : resInfoList) {
                if (info.activityInfo.packageName == appPkg) {
                    foundPackageFlag = true;
                    upiClientResolveInfo = info;
                    break;
                }
            }
            if (foundPackageFlag) {
                intent.setClassName(
                        upiClientResolveInfo.activityInfo.packageName,
                        upiClientResolveInfo.activityInfo.name
                );
                startActivityForResult(intent, REQ_CODE_UPI);
            }
        }

        @Override
        public String getAppName(ApplicationInfo pkg) {
            return getPackageManager().getApplicationLabel(pkg).toString();
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PaymentCheckoutActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initWebView();

        String sessionId = getIntent().getExtras().getString(Constants.SESSION_ID, "");
        returnUrl = getIntent().getExtras().getString(Constants.RETURN_URL, "");
        startPayment(sessionId);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        binding.paymentWebView.addJavascriptInterface(webJsBridge, WB_INTENT_BRIDGE);
        binding.paymentWebView.getSettings().setJavaScriptEnabled(true);
        binding.paymentWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        handleWebViewClient();
    }


    private void startPayment(String sessionId) {
        String url = Constants.BASE_URL + Constants.CHECKOUT_URL;
        HashMap<String, String> input = new HashMap<>();
        input.put("payment_session_id", sessionId);
        input.put("paymentModes", "[\"nb\", \"upi\"]");
        input.put("integrity_token", "dummy");
        String formBody = new WebHelper().generateForm(this, input, url);
        Log.v(TAG, "FormBody::->> " + formBody);
        binding.paymentWebView.loadDataWithBaseURL(
                "",
                formBody,
                "text/html",
                Xml.Encoding.UTF_8.name(),
                ""
        );
    }


    private void handleWebViewClient() {
        binding.paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.v(TAG, "onPageStarted---->> " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.v(TAG, "ShouldOverrideUrlLoading---->> " + url);
                if (url != null) {
                    if (url.startsWith(returnUrl)) {
                        Intent intent = new Intent();
                        intent.putExtra("returnUrl", url);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                HashMap<String, String> addHeaders = new HashMap<>();
                addHeaders.put("referer", "https://www.cashfree.com/");
                if (url != null && url.startsWith("http://")) {
                    url.replace("http://", "https://");
                }
                binding.paymentWebView.loadUrl(url, addHeaders);
                return false;
            }


            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                Log.v(TAG, "doUpdateVisitedHistory---->> " + url);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.v(TAG, "onReceivedHttpError");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_UPI) {
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "Return From UPI App");
                binding.paymentWebView.evaluateJavascript("window.showVerifyUI()", s -> {
                });
            }
        }
    }
}