package com.example.josue.lightsensor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler(Looper.getMainLooper());
    ListView listView;
    AdapterDevice devicesAdapter;
    String enableOrDisableAlarm = "Enable";
    DeviceCheckTask deviceCheckTask;
    ////ejemplo 3 bluetooth////
    //private ReentrantLock btLock = new ReentrantLock();
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    private MainActivity thisActivity;

    TextView txtBtInput;
    Button button;
    EditText txtInput;

    /////for bluetooth////
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;
    private OutputStream outputStream;
    private InputStream inStream;

    /////prueba 2 bluetooth/////
    final byte delimiter = 33;
   // int readBufferPosition = 0;



    public void btnCTH(View v){
        try {
            sendData("ctH 700 50");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnCTL(View v){
        try {
            sendData("ctL 20 3");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnDisable(View v){
        try {
            sendData("disable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void btnGetState(View v){
        try {
            sendData("getState");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.listViewDevices);
        listView.setLongClickable(true);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
////////make tabhost///////////
        tabHost.setup();
        TabHost.TabSpec tabSpecDevices = tabHost.newTabSpec("Devices");
        tabSpecDevices.setIndicator("Devices");
        tabSpecDevices.setContent(R.id.tabDevices);
        tabHost.addTab(tabSpecDevices);

        tabHost.setup();
        TabHost.TabSpec tabSpecDebug = tabHost.newTabSpec("Debug");
        tabSpecDebug.setIndicator("Debug");
        tabSpecDebug.setContent(R.id.tabDebug);
        tabHost.addTab(tabSpecDebug);





        devicesAdapter = new AdapterDevice(this, DeviceList.getInstance());

        listView.setAdapter(devicesAdapter);



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("Long clicked","pos:"+String.valueOf(position));
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Options")
                        .setItems(new String[]{"Details","Refresh Last Seen","Delete",enableOrDisableAlarm+" Alarm","Calibrate"}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                             //   Toast.makeText(MainActivity.this,"Selectec #:"+ String.valueOf(which),Toast.LENGTH_LONG).show();
                                switch (which){
                                    case 0:
                                        showDetailsDialogOf(position);
                                        break;
                                    case 1:
                                        devicesAdapter.notifyDataSetChanged();
                                        break;
                                    case 2:
                                        AzureDataBase.getInstace().deleteDevice(position);
                                        devicesAdapter.notifyDataSetChanged();
                                        break;
                                    case 3:
                                        try {
                                            deviceCheckTask.toSend.put(enableOrDisableAlarm.toLowerCase());
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case 4:
                                        try {
                                            deviceCheckTask.toSend.put("config high");
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }
                            }
                        });
                builder.create().show();
                return false;
            }
        });
///////////////////////////////////
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Digite la MAC Address");
//
//// Set up the input
//                final EditText input = new EditText(MainActivity.this);
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);
//
//// Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AzureDataBase.getInstace().addDevice(input.getText().toString());
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
               // DeviceList.getInstance().add(new Device());
//                launchAddDialog1(DeviceList.getInstance().size() - 1);
                Intent intent = new Intent(MainActivity.this, AddDeviceActivity.class);
                intent.putExtra("device",new Device());
                startActivity(intent);

            }

        });

       txtBtInput = (TextView) findViewById(R.id.textViewBluetoothInput);


        txtInput = (EditText) findViewById(R.id.editTextInput);

        button = (Button) findViewById(R.id.buttonSend);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //txtHelloWorld.setText(txtInput.getText().toString());
                findBT("B8:27:EB:6D:E5:A0");
                try {
                    openBT();
                    sendData(txtInput.getText().toString());
                    Log.d(txtInput.getText().toString(),"yes");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Log.d("heloooooooooooooooo","yes");
        txtBtInput.setText(AzureDataBase.getInstace().executeASDF("SELECT * FROM REGISTROMALETA"));


        //startService(new Intent(MainActivity.this, DeviceService.class));

        deviceCheckTask = new DeviceCheckTask(MainActivity.this);
        deviceCheckTask.execute();

     //   launchNotification();
        if(DeviceList.getInstance().newUser != null){
            if(DeviceList.getInstance().newUser == 2){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
                builder.setTitle("Welcome");


                builder.setMessage("Thank you for choosing travel security. " +
                        "In this page you can manage your devices. Add a device clicking in the pink button. " +
                        "There is more options in the top right corner, check them out.");

                builder.setPositiveButton("OK", null);

// Create the AlertDialog
                AlertDialog dialog = builder.create();
                builder.show();
                DeviceList.getInstance().newUser = null;
            }
        }

    }

    private void showDetailsDialogOf(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
        Device device = DeviceList.getInstance().get(position);
        builder.setTitle("Details of "+device.getNameUser()+" Bag");


        builder.setMessage( "Brand: "+device.getBrand()+"\n"+
                            "Color: "+device.getColor()+"\n"+
                            "Size: "+device.getSize()+"\n"+
                            "MAC: "+device.getName()+"\n");

        builder.setPositiveButton("OK", null);

// Create the AlertDialog
        AlertDialog dialog = builder.create();
        builder.show();

    }

    protected void onRestart(){
        super.onRestart();
        if(DeviceList.getInstance().newDeviceAddedPosition != null){
            Log.d("Main Activity","New device not null");
            DeviceList.getInstance().get(DeviceList.getInstance().newDeviceAddedPosition).setEnable(true);
            DeviceList.getInstance().newDeviceAddedPosition = null;
            try {
                deviceCheckTask.toSend.put("config high");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        devicesAdapter.notifyDataSetChanged();
    }

    public void launchNotification() {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Alarm")
                        .setContentText("The device "+DeviceList.getInstance().get(0).getName()+" opened");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, LoginActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LoginActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(10, notification);

    }


    //////////inicio prueba 3 bluettoth//////////
    void findBT(String deviceID) {
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if(mBluetoothAdapter == null)
    {
       // txtHelloWorld.setText("No bluetooth adapter available");
    }

    if(!mBluetoothAdapter.isEnabled())
    {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, 0);
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
       // btLock.lock();
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

       // txtHelloWorld.setText("Bluetooth Opened");
}

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        txtBtInput.setText("1");
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
                                    Log.d("bluetoothRecv", data);

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            processData(data);
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
                        txtBtInput.setText("error");
                        stopWorker = true;
                    }
                }
            }
        });
        txtBtInput.setText("2");
        workerThread.start();
        txtBtInput.setText("3");
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
                enableOrDisableAlarm = "Disable";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else  if(data.equals("disabled")){
            try {
                //  sendData("yes");
                closeBT();
                enableOrDisableAlarm = "enable";
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if(data.equals("set out")){
            launchDialog("set out");
        } else if(data.equals("set open")){
            launchDialog("set open");
        } else if(data.equals("set close")){
            launchDialog("set close");
        } else if(data.split(" ")[0].equals("configured")){
            try {
                deviceCheckTask.toSend.put("okidoki");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            String[] aux = data.split(" ");
            //check if alarm was enabled
            if (Boolean.valueOf(aux[1])) {
                launchNotification();
                try {
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

    void launchDialog (final String toSend){
        String place = "in";
        if(toSend.equals("set out"))
            place = "out of the luggage";
        else if (toSend.equals("set closed"))
            place = "inside of the luggage closed";
        else if (toSend.equals("set open"))
            place = "inside of the luggage opened";

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// Add the buttons
        builder.setTitle("Configuring Thresholds");


        builder.setMessage("Put your device "+place+", then press OK");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    Log.d("In Dialog OK", toSend);
                    deviceCheckTask.toSend.put(toSend);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

// Create the AlertDialog
        AlertDialog dialog = builder.create();
        builder.show();
    }

    public void launchAddDialog1(final int devicePosition){
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        // Get the layout inflater
//        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//
//        builder.setView(inflater.inflate(R.layout.dialog_add_device, null))
//                // Add action buttons
//                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        EditText etName = (EditText) findViewById(R.id.editTextName);
//                        EditText etBrand = (EditText) findViewById(R.id.editTextBrand);
//                        EditText etColor = (EditText) findViewById(R.id.editTextColor);
//                        EditText etSize = (EditText) findViewById(R.id.editTextSize);
//                        Log.d("in dialog", "hello world");
//                     //   Log.d("dialogs text", etBrand.getText().toString());
//                        if(etName.getText() != null && etBrand.getText() != null && etColor.getText() != null && etSize.getText() != null) {
//                            DeviceList.getInstance().get(devicePosition).setNameUser(etName.getText().toString());
//                            DeviceList.getInstance().get(devicePosition).setBrand(etBrand.getText().toString());
//                            DeviceList.getInstance().get(devicePosition).setColor(etColor.getText().toString());
//                            DeviceList.getInstance().get(devicePosition).setSize(etSize.getText().toString());
//                            launchAddDialog2(devicePosition);
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(MainActivity.this, "Please fill every field", Toast.LENGTH_LONG);
//                            //launchAddDialog1(devicePosition);
//                        }
//
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });
//
//        final AlertDialog dialog = builder.show();
//        //final EditText etName = (EditText) dialog.findViewById(R.id.editTextName);






    }

    public void launchAddDialog2(final int devicePosition){
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        // Get the layout inflater
//        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//
//        builder.setView(inflater.inflate(R.layout.dialog_add_device, null))
//                // Add action buttons
//                .setPositiveButton("Finish", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//
//
//                    }
//                })
//                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        launchAddDialog1(devicePosition);
//                    }
//                });
//


    }

    public void handleMessage (Message inputMessage){
        launchDialog((String)inputMessage.obj);
    }



/////////////////dont know//////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_add:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_list:
                break;
            default:
                break;
        }
        if (id == R.id.action_map) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
