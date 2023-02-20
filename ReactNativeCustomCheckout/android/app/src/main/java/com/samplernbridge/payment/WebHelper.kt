package com.samplernbridge.payment

import android.content.Context
import com.samplernbridge.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object WebHelper {
    fun generateForm(context: Context, input: MutableMap<String, String>, url: String): String {
        val inputTag = "<input type=\"hidden\" name=\"%s\" value=\"%s\"/>"
        val iStream = context.resources.openRawResource(R.raw.pay_form)
        val formTemplate = StringBuilder()
        try {
            val reader = BufferedReader(InputStreamReader(iStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                formTemplate.append(line)
            }
        } catch (ignored: IOException) {
        }

        val formBody = StringBuilder()
        for (key in input.keys) {
            formBody.append(String.format(inputTag, key, input[key]))
        }
        formBody.append(String.format(inputTag, "hideHeader", true))
        formBody.append(String.format(inputTag, "platform", "andx-c-2.0.4-x-m-w-x-a-33"))

        //Generate body from ingredients
        return String.format(formTemplate.toString(), url, formBody.toString())
    }
}