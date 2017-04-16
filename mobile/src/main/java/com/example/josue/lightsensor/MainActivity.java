package com.example.josue.lightsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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



import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {


    ////ejemplo 3 bluetooth////
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

//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        View loginView = getLayoutInflater().inflate(R.layout.dialog_login, null);
//        EditText loginEtEmail = (EditText) loginView.findViewById(R.id.etEmail);
//        EditText loginEtPassword = (EditText) loginView.findViewById(R.id.etPassword);
//        Button loginBtnLogIn = (Button) loginView.findViewById(R.id.btnLogIn);
//        Button loginBtnSingIn = (Button) loginView.findViewById(R.id.btnSingIn);
//
//        loginBtnLogIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ListView listView = (ListView) findViewById(R.id.listViewDevices);
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


/////////////make device list/////
//        ArrayList<Device> devices = new ArrayList<Device>();

//        Device device = new Device("prueba", "desconectado", false);
//        AzureDataBase.getInstace().fillUsersDeviceList();


        AdapterDevice devicesAdapter = new AdapterDevice(this, DeviceList.getInstance());

        listView.setAdapter(devicesAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Long clicked","pos:"+String.valueOf(position));
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Options")
                        .setItems(new String[]{"Refresh","Delete","Toggle Alarm"}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this,"Selectec #:"+ String.valueOf(which),Toast.LENGTH_LONG).show();
                                switch (which){
                                    case 1:

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
//                findBT("B8:27:EB:6D:E5:A0");
//                try {
//                    openBT();
//                    sendData("enable");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                stopService(new Intent(MainActivity.this, DeviceService.class));

            }

        });

       txtBtInput = (TextView) findViewById(R.id.textViewBluetoothInput);


        txtInput = (EditText) findViewById(R.id.editTextInput);

        button = (Button) findViewById(R.id.buttonSend);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //txtHelloWorld.setText(txtInput.getText().toString());
                try {
                    sendData(txtInput.getText().toString());
                    Log.d(txtInput.getText().toString(),"yes");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Log.d("heloooooooooooooooo","yes");
        txtBtInput.setText(AzureDataBase.getInstace().executeASDF("SELECT * FROM REGISTROMALETA"));


        startService(new Intent(MainActivity.this, DeviceService.class));

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
       // txtHelloWorld.setText("Bluetooth Closed");
    }

    void processData(String data){
        txtBtInput.setText(data);
        switch(data){
            case "enabled":

                try {
                    sendData("yes");
                    closeBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }



        }
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
