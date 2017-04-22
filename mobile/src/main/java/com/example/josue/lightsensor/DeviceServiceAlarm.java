package com.example.josue.lightsensor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Josue on 16/04/17.
 */

public class DeviceServiceAlarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
//        wl.acquire();

        // Put here YOUR code.
        Log.d("Alarm!!!!!!!!!!!!!!", "Hello");
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_SHORT).show(); // For example

//        wl.release();

    }

    public void setAlarm(Context context) {
        Log.d("Alarm!!!!!!!!!!!!!!", "Setted");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, DeviceServiceAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pi); // Millisec * Second * Minute


    }

    public void cancelAlarm(Context context) {
        Log.d("Alarm!!!!!!!!!!!!!!", "Canceled");
        Intent intent = new Intent(context, DeviceServiceAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

}
