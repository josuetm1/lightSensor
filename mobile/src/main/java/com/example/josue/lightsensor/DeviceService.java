package com.example.josue.lightsensor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Josue on 16/04/17.
 */

public class DeviceService extends Service {
    DeviceServiceAlarm alarm = new DeviceServiceAlarm();

    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("service!!!!!!!!!!!!!!", "started");
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT);
        alarm.setAlarm(this);
        return START_STICKY;
    }

//    @Override
//    public void onStart(Intent intent, int startId)
//    {
//        alarm.setAlarm(this);
//    }
    @Override
    public void onDestroy(){
        Log.d("service!!!!!!!!!!!!!!", "stoped");
        alarm.cancelAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


}
