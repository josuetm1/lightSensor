package com.example.josue.lightsensor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Josue on 17/04/17.
 */
public class Bluetooth {
    private static Bluetooth ourInstance = new Bluetooth();

    public static Bluetooth getInstance() {
        return ourInstance;
    }

    private Bluetooth() {
    }
    private MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    //  private ReentrantLock btLock = new ReentrantLock();
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    void findBT(String deviceID) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            // txtHelloWorld.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getAddress().equals(deviceID))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        // txtHelloWorld.setText("Bluetooth com.example.josue.lightsensor.Device Found");
    }

    void openBT() throws IOException {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        //btLock.lock();
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        // txtHelloWorld.setText("Bluetooth Opened");
    }

    void beginListenForData() {
//        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

      //  txtBtInput.setText("1");
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            //Integer tryingCounter = 0;
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        //txtBtInput.setText("trying");
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            Log.d("if: ","1");
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            Log.d("if: ","2");
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    Log.d("if: ","3");
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;


//                                    handler.post(new Runnable()
//                                    {
//                                        public void run()
//                                        {
                                            processData(data);
//                                        }
//                                    });
                                }
                                else
                                {
                                    Log.d("if: ","4");
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                        // tryingCounter++;
                    }
                    catch (IOException ex)
                    {
                      //  txtBtInput.setText("error");
                        stopWorker = true;
                    }
                }
            }
        });
       // txtBtInput.setText("2");
        workerThread.start();
       // txtBtInput.setText("3");
    }

    void sendData(String msg) throws IOException {
        // String msg = txtInput.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        //    txtHelloWorld.setText("Data Sent");
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
      //  btLock.unlock();
        // txtHelloWorld.setText("Bluetooth Closed");
    }

    void processData(String data){
        Log.d("bluetoothRecv", data);

        if(data.equals("enabled")){
            try {
             //   sendData("yes");
                closeBT();
                mainActivity.enableOrDisableAlarm = "Disable";
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity,"Alarm in device enabled",Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(data.equals("disabled")){
            try {
              //  sendData("yes");
                closeBT();
                mainActivity.enableOrDisableAlarm = "Enable";
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity,"Alarm in device disabled",Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(data.equals("set out")){
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.launchDialog("set out");
                }
            });
        } else if(data.equals("set open")){
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.launchDialog("set open");
                }
            });
        } else if(data.equals("set closed")){
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.launchDialog("set closed");
                }
            });
        } else if(data.split(" ")[0].equals("configured")){
            try {
                mainActivity.deviceCheckTask.toSend.put("okidoki");
                closeBT();
                Log.d("bluetooth pross", "okidoki in queue");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            String[] aux = data.split(" ");
            //check if alarm was enabled
            if (Boolean.valueOf(aux[1])) {
                mainActivity.launchNotification();
                try {

                    ArrayList<Apertura> listAperturas = new ArrayList<>();

                    String[] aperturasStrings = aux[4].split("///");

                    for(String aperturaString : aperturasStrings){
                        listAperturas.add(new Apertura(aperturaString, DeviceList.getInstance().get(0)));
                    }

                    AzureDataBase.getInstace().addAperturas(listAperturas);
                    sendData("alarm ack");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            DeviceList.getInstance().get(0).setLastSeen(new Timestamp(System.currentTimeMillis()));
            DeviceList.getInstance().get(0).setLatLng(new LatLng(Double.valueOf(aux[2]), Double.valueOf(aux[3])));
            try {
                closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
