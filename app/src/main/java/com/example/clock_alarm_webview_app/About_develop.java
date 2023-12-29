package com.example.clock_alarm_webview_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class About_develop extends AppCompatActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_develop); // Set the layout for this activity

        webView = findViewById(R.id.id_web_view_about_develop);

        // Configuring WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Load HTML content
        String htmlFileName = "alarm_clock/about_developer.html";
        String baseUrl = "file:///android_asset/";
        String htmlPath = baseUrl + htmlFileName;
        webView.loadUrl(htmlPath);
    }
}
