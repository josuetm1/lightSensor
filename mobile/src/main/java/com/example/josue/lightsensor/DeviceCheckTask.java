package com.example.josue.lightsensor;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Josue on 16/04/17.
 */

public class DeviceCheckTask extends AsyncTask<Void,Void,Void> {
    MainActivity mainActivity;
    LinkedBlockingQueue<String> toSend = new LinkedBlockingQueue<>();
    Boolean isConfiguring = new Boolean(false);
    Boolean waitForSend = new Boolean(false);

    DeviceCheckTask(MainActivity mainActivity){
        super();
        this.mainActivity = mainActivity;
        Bluetooth.getInstance().setMainActivity(mainActivity);
        try {
            toSend.put("status");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
//                    mainActivity.findBT(device.getName());
                    Bluetooth.getInstance().findBT(device.getName());
                    try {
                        Log.d("In Background!!!!!!!!", device.getName()+" 2");
//                        mainActivity.openBT();
                        Bluetooth.getInstance().openBT();
                        Log.d("In Background!!!!!!!!", device.getName()+" 3");
                        if(toSend.peek().equals("config high")){
                            Log.d("In Background que",toSend.toString());
                            toSend.clear();
                            toSend.put("config high");
                            isConfiguring = true;
                            waitForSend = true;
                        } else {
//                            mainActivity.sendData(toSend.take());

                            Bluetooth.getInstance().sendData(toSend.take());
                            toSend.put("status");
                        }

                        while (isConfiguring) {
                            Log.d("In Background conf", "1");
//                            Log.d("In Backgorund peek", toSend.peek().split(" ")[0]);
                            if(toSend.peek() != null && toSend.peek().split(" ")[0].equals("set") && ! toSend.peek().equals("okidoki")) {
                                Log.d("In Background conf", "2");
                                waitForSend = true;
                            }
                            if(toSend.peek().equals("okidoki")){
                                Log.d("In Background conf", "3");
                                isConfiguring = false;
                                waitForSend = false;
                                toSend.take();
                                toSend.put("status");
                                Bluetooth.getInstance().closeBT();
                                break;
                            }
//                            mainActivity.sendData(toSend.take());
                            Log.d("In Background conf", "4");
                            Bluetooth.getInstance().sendData(toSend.take());



                            while (waitForSend) {
                                Log.d("In Background conf", "5");
//                                Log.d("In Backgorund peek", toSend.peek().split(" ")[0]);
                                if(toSend.peek() != null && (toSend.peek().split(" ")[0].equals("set")|| toSend.peek().equals("okidoki"))) {
                                    Log.d("In Background conf", "6");
                                    waitForSend = false;
                                }
                                else {
                                    Log.d("In Background conf", "7");
                                    Thread.sleep(1000);
                                }

                            }
                        }

                        Log.d("In Background!!!!!!!!", device.getName()+" 4");
                    } catch (IOException e) {
                        AzureDataBase.getInstace().getDeviceStatus(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//            try {
//              //  if(!toSend.peek().equals("config high"))
//                    toSend.put("status");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Log.d("In Backgroun que", toSend.toString());


        }


    }



}
