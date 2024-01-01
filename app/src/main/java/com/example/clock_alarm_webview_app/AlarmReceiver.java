package com.example.clock_alarm_webview_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the alarm trigger here if needed
        // For simplicity, showing a notification directly
        String savedAlarmTime = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
                .getString("ALARM_PREF_KEY", "");

        ((MainActivity) context).showNotification("Alarm triggered for " + savedAlarmTime);
    }
}
