package com.example.clock_alarm_webview_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.id_web_view);
        // config web view
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // Load html
        String htmlFileName = "alarm_clock/index.html";
        String baseUrl = "file:///android_asset/";
        String htmlPath = baseUrl + htmlFileName;
        webView.loadUrl(htmlPath);

        // Create an instance of the WebAppInterface and bind it to the WebView
        WebAppInterface webAppInterface = new WebAppInterface(this); // 'this' refers to the current activity context
        webView.addJavascriptInterface(webAppInterface, "Android");

        Log.d("alert", "hello");
    }

    // onBackPressed method
    @Override
    public void onBackPressed() {
        if(webView !=null && webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    // Define a Java class to act as the interface between JavaScript and Android
    public static class WebAppInterface {

        Context mContext;

        WebAppInterface(Context context) {
            mContext = context;
        }

        // Method to receive alarms array from JavaScript as a JSON string
        @JavascriptInterface
        public void receiveAlarms(String alarmsJSON) {
            try {
                Log.d("data 1", "data"+alarmsJSON);
                // Convert the JSON string to a Java array or perform operations as needed
                JSONArray jsonArray = new JSONArray(alarmsJSON);

                Log.d("json 1", "json"+jsonArray);
                // Process the received alarms array in your Java code
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject alarmObject = jsonArray.getJSONObject(i);

                    // Retrieve properties of each alarm object
                    String id = alarmObject.getString("id");
                    Log.d("obj id", "json"+id);

                    String timeAlarm = alarmObject.getString("timeAlarm");
                    Log.d("obj timeAlarm", "json"+timeAlarm);

                    boolean exists = alarmObject.getBoolean("isActive");
                    Log.d("obj exists", "json"+exists);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
