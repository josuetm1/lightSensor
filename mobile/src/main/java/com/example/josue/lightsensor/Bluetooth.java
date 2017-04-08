package com.example.josue.lightsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;

/**
 * Created by Josue on 04/04/17.
 */

public class Bluetooth {

    //////Singleton//////////////
    private static Bluetooth singleton = null;

    private Bluetooth() {

    }

    public static Bluetooth getInstace() {
        if (singleton == null)
            singleton = new Bluetooth();
        return singleton;
    }
///////Singleton end///////////////

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private BlockingDeque<String> bluetoothInput;


    void connectTo(String deviceAddress, AppCompatActivity activity) throws IOException {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            // txtHelloWorld.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {

                if(device.getAddress().equals(deviceAddress))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        // txtHelloWorld.setText("Bluetooth com.example.josue.lightsensor.Device Found");
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();
    }


    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character


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
                            Log.d("if bluetooth ","1");
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            Log.d("if bluetooth ","2");
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

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            try {
                                                bluetoothInput.put(data);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
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
                        try {
                            bluetoothInput.put("error");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    void sendData(String msg) throws IOException {
        // String msg = txtInput.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        //    txtHelloWorld.setText("Data Sent");
    }

    public String receiveData() throws InterruptedException {
        return bluetoothInput.take();
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        // txtHelloWorld.setText("Bluetooth Closed");
    }


}
