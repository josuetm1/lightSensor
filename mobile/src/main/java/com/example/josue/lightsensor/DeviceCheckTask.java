package com.example.josue.lightsensor;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Josue on 16/04/17.
 */

public class DeviceCheckTask extends AsyncTask<Void,Void,Void> {
    MainActivity mainActivity;

    DeviceCheckTask(MainActivity mainActivity){
        super();
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while(true) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("In Background!!!!!!!!", "yes");
            for (Device device: DeviceList.getInstance()) {
                if(device.getEnable()) {
                    Log.d("In Background!!!!!!!!", device.getName()+" 1");
                    Bluetooth.getInstance().findBT(device.getName());
                    try {
                        Log.d("In Background!!!!!!!!", device.getName()+" 2");
                        Bluetooth.getInstance().openBT();
                        Log.d("In Background!!!!!!!!", device.getName()+" 3");
                        Bluetooth.getInstance().sendData("status");
                        Log.d("In Background!!!!!!!!", device.getName()+" 4");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }


    }



}
