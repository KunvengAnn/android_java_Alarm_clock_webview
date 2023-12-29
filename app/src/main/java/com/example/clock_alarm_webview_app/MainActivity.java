package com.example.clock_alarm_webview_app;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webView;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar tool_bar;

    private SharedPreferences sharedPreferences;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //drawer tool bar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tool_bar = findViewById(R.id.toolbar);
        //tool bar
        setSupportActionBar(tool_bar);

        //navigation drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, tool_bar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();


        //web view
        webView = findViewById(R.id.id_web_view);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // After the page finishes loading, retrieve and send the SharedPreferences data
                //send data local storage to js for show
                functionGetLocalStorageSendToJavaScript();
            }
        });

        // config web view
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        WebView.setWebContentsDebuggingEnabled(true);

        // Load html
        String htmlFileName = "alarm_clock/index.html";
        String baseUrl = "file:///android_asset/";
        String htmlPath = baseUrl + htmlFileName;


        // bind it to the WebView for javaScript receive data from javaScript
        webView.addJavascriptInterface(MainActivity.this, "Android");


        webView.loadUrl(htmlPath);
    }

    //method for receive id deleted selected data from javaScript
    @JavascriptInterface
    public  void deleteAlarmFromLocalStorage(String searchId){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("alarm_" + searchId);
        editor.apply();

        // You can optionally log or perform additional actions after deleting the alarm
        Log.d("Deleted alarm", "Alarm with ID " + searchId + " deleted from local storage");
    }

    //this receive data from list alarmArray from javaScript
    @JavascriptInterface
    public void receiveAlarms(String alarmsJSON) {
        // Retrieve the stored alarms from SharedPreferences
        Map<String, ?> allAlarms = getDefaultSharedPreferences(MainActivity.this).getAll();

        try {
            Log.d("data 1", "data" + alarmsJSON);

            // Convert the JSON string to a JSONArray
            JSONArray jsonArray = new JSONArray(alarmsJSON);

            // Get a SharedPreferences editor to write data
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Loop through each object in the JSONArray
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject alarmObject = jsonArray.getJSONObject(i);

                // Retrieve properties of each alarm object
                String id = alarmObject.getString("id");

                // Check if the alarm already exists in SharedPreferences
                if (!sharedPreferences.contains("alarm_" + id)) {
                    String timeAlarm = alarmObject.getString("timeAlarm");
                    boolean isActive = alarmObject.getBoolean("isActive");

                    // Construct a JSON string for each alarm object
                    JSONObject singleAlarm = new JSONObject();
                    singleAlarm.put("id", id);
                    singleAlarm.put("timeAlarm", timeAlarm);
                    singleAlarm.put("isActive", isActive);

                    // Store each alarm object as a string in SharedPreferences
                    editor.putString("alarm_" + id, singleAlarm.toString());
                }
            }
            editor.apply();

            // (Optional) Log the stored alarms
            for (String key : sharedPreferences.getAll().keySet()) {
                Log.d("Stored alarm11", key + ": " + sharedPreferences.getString(key, ""));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        //call immediately save local when onPause app
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //call immediately save local when stop app
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //functionGetLocalStorageSendToJavaScript();
    }


    @Override
    protected void onStart() {
        super.onStart();
        functionGetLocalStorageSendToJavaScript();
    }

    public  void functionGetLocalStorageSendToJavaScript(){
        // Retrieve the stored alarms from SharedPreferences
        Map<String, ?> allAlarms = getDefaultSharedPreferences(MainActivity.this).getAll();

        JSONArray jsonArray = new JSONArray();

        // Loop through each entry in SharedPreferences
        for (Map.Entry<String, ?> entry : allAlarms.entrySet()) {
            // Get the alarm data as a string
            String alarmData = entry.getValue().toString();
            // Parse the alarm data string as a JSONObject
            try {
                JSONObject alarmObject = new JSONObject(alarmData);
                jsonArray.put(alarmObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Convert the JSONArray to a string
        String storedAlarmsJSON = jsonArray.toString();

        // Log the stored alarms for verification
        Log.d("Stored Alarms when start", storedAlarmsJSON);

        // Execute JavaScript function with stored alarms data from Android to JavaScript
        String jsFunction = "setDataFromAndroid('" + storedAlarmsJSON + "');";
        webView.evaluateJavascript(jsFunction, null); // webView is your WebView reference
    }

    //for when click drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_Info_dev) {
            Intent i = new Intent(MainActivity.this, About_develop.class);
            startActivity(i);
            return true; // Return true after starting the activity to indicate that the item selection is handled
        } else if (menuItem.getItemId() == R.id.nav_empty) {
            Toast.makeText(this, "No data to see", Toast.LENGTH_LONG).show();
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    // onBackPressed method
    @Override
    public void onBackPressed() {
        //webView Back Pressed for web view back
        // drawerLayout Back Pressed for drawer back
        if (webView != null && webView.canGoBack() || drawerLayout.isDrawerOpen(GravityCompat.START)) {
            webView.goBack();

            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

