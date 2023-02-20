package com.samplernbridge.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.samplernbridge.databinding.PaymentCheckoutActivityBinding
import com.samplernbridge.payment.Constants.REQ_CODE_UPI
import com.samplernbridge.payment.Constants.WB_INTENT_BRIDGE

class PaymentCheckoutActivity : AppCompatActivity() {
    private val TAG = "PaymentCheckoutActivity"

    private lateinit var binding: PaymentCheckoutActivityBinding
    private var returnUrl: String = ""

    private val webJsBridge: WebJSInterfaceImpl by lazy {
        WebJSInterfaceImpl(object : WebHelperInterface {
            override fun getAppList(link: String): List<ResolveInfo> {
                Log.v(TAG, "Called getAppList")
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("upi://pay")
                val pm = packageManager
                return pm.queryIntentActivities(intent, 0)
            }

            override fun onResponseReceived(returnedParams: Map<String, String>) {
                Log.v(TAG, "Called onResponseReceived")
                Toast.makeText(this@PaymentCheckoutActivity, "ResponseReceived", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun openApp(appPkg: String, url: String) {
                Log.v(TAG, "Called openApp")
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse(url)
                val pm = packageManager
                val resInfo = pm.queryIntentActivities(intent, 0)
                var foundPackageFlag = false
                var upiClientResolveInfo: ResolveInfo? = null
                for (info in resInfo) {
                    if (info.activityInfo.packageName == appPkg) {
                        foundPackageFlag = true
                        upiClientResolveInfo = info
                        break
                    }
                }
                if (foundPackageFlag) {
                    intent.setClassName(
                        upiClientResolveInfo!!.activityInfo.packageName,
                        upiClientResolveInfo.activityInfo.name
                    )
                    startActivityForResult(intent, REQ_CODE_UPI)
                }
            }

            override fun getAppName(pkg: ApplicationInfo): String =
                packageManager.getApplicationLabel(pkg).toString()
        })
    }

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentCheckoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()

        val sessionId = intent.extras?.getString(Constants.SESSION_ID) ?: ""
        returnUrl = intent.extras?.getString(Constants.RETURN_URL) ?: ""
        startPayment(sessionId)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.paymentWebView.addJavascriptInterface(webJsBridge, WB_INTENT_BRIDGE)
        with(binding.paymentWebView) {
            settings.javaScriptEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        handleWebViewClient()
    }


    private fun startPayment(sessionId: String) {
        val url = Constants.BASE_URL + Constants.CHECKOUT_URL
        val input: MutableMap<String, String> = mutableMapOf()
        input["payment_session_id"] = sessionId
        input["paymentModes"] = arrayOf("nb", "upi").contentToString()
        input["integrity_token"] = "dummy"
        val formBody = WebHelper.generateForm(this, input, url)
        Log.v(TAG, "Form ::->> $formBody")
        binding.paymentWebView.loadDataWithBaseURL(
            "",
            formBody,
            "text/html",
            Xml.Encoding.UTF_8.name,
            ""
        )
    }


    private fun handleWebViewClient() {
        binding.paymentWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.v(TAG, "onPageStarted---->> $url")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.v(TAG, "ShouldOverrideUrlLoading---->> $url")
                url?.let {
                    if (url.startsWith(returnUrl)) {
                        val intent = Intent()
                        intent.putExtra("returnUrl", url)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
                val addHeaders = HashMap<String, String>()
                addHeaders["referer"] = "https://www.cashfree.com/"
                url?.let {
                    if (it.startsWith("http://")) {
                        url.replace("http://", "https://")
                    }
                    binding.paymentWebView.loadUrl(url, addHeaders)
                }
                return false
            }


            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                Log.v(TAG, "doUpdateVisitedHistory---->> $url")
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.v(TAG, "payment method failed.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_UPI) {
            Log.v(TAG, "Return From UPI App")
            binding.paymentWebView.evaluateJavascript("window.showVerifyUI()") { _ -> }
        }
    }
}