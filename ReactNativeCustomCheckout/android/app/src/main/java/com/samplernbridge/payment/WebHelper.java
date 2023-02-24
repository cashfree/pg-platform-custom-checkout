package com.samplernbridge.payment;

import android.content.Context;

import com.samplernbridge.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class WebHelper {
    public String generateForm(Context context, HashMap<String, String> input, String url) {
        String inputTag = "<input type=\"hidden\" name=\"%s\" value=\"%s\"/>";
        InputStream iStream = context.getResources().openRawResource(R.raw.pay_form);
        StringBuilder formTemplate = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                formTemplate.append(line);
            }
        } catch (IOException ignored) {
        }

        StringBuilder formBody = new StringBuilder();
        for (String key : input.keySet()) {
            formBody.append(String.format(inputTag, key, input.get(key)));
        }
        formBody.append(String.format(inputTag, "hideHeader", true));
        formBody.append(String.format(inputTag, "platform", "andx-c-2.0.4-x-m-w-x-a-33"));

        //Generate body from ingredients
        return String.format(formTemplate.toString(), url, formBody);
    }
}