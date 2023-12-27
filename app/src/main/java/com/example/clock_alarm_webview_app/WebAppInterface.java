package com.example.clock_alarm_webview_app;


import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//library for local storage Shared Preferences
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


// Define a Java class to act as the interface between JavaScript and Android
public class WebAppInterface {

    MainActivity mContext;

    WebAppInterface(MainActivity context) {
        mContext = context;
    }

    //method for store data in local storage
    public void saveToLocalStorage(String alarmJson){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("alarmsData",alarmJson);
        editor.apply();
    }

    //method check local storage save already or not
    public void checkLocalStorage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String storedData = preferences.getString("alarmsData", "");
        Log.d("Stored Data", "Data from SharedPreferences: " + storedData);
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

                // Save alarmsJSON to local storage using SharedPreferences
                //pass parameter to method saveToLocalStorage
                saveToLocalStorage(alarmsJSON);
            }
        } catch (JSONException e) {
            //if error show
            e.printStackTrace();
        }
    }
}

