package com.example.josue.lightsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
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
        ListView listView = (ListView) findViewById(R.id.listViewDevices);
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
        ArrayList<Device> devices = new ArrayList<Device>();

        Device device = new Device("prueba", "desconectado", false);
        devices.add(device);

        AdapterDevice devicesAdapter = new AdapterDevice(this, devices);

        listView.setAdapter(devicesAdapter);
///////////////////////////////////
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findBT();
                try {
                    openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

//                count ++;
//                txtHelloWorld.setText(count.toString());
//
//                switch (count){
//                    case 1: bluetoothOff(); break;
//                    case 2: bluetoothOn(); break;
//                    case 3:bluetoothVisible(); break;
//                    case 4: bluetoothList(); break;
//                    //case 5: txtHelloWorld.setText((String)lv.getSelectedItem());

//                }
            }
        });

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                try {
////                    init(lv.getSelectedItemPosition());
////                } catch (IOException e) {
////                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
////                    e.printStackTrace();
////                }
////
////                try {
////                    bluetoothWrite("Hello World");
////                } catch (IOException e) {
////                   Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
////
////                    e.printStackTrace();
////                }
//
//
//            }
//        });

        Log.d("helooooooooooooo","in");
        Connection c = AzureDataBase.getInstace().CONN();
        Log.d("helooooooooooooo","out");

        String query = "select * from REFGISTROMALETA";
        Statement stmt = null;
        ResultSet rs;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if(c!=null){
            try {
                c.close();
                Log.d("helooooooooooooo","this dick");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Log.d("helooooooooooooo","close");
      //  txtBtInput.setText(rs.getNString());


    }

    public void setMmDevice(){
        pairedDevices = BA.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("raspberrypi")) { //Note, you will need to change this to match the name of your device
                    Log.e("Aquarium",device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }
    }

    public void bluetoothOn(){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void bluetoothOff(){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned off" ,Toast.LENGTH_LONG).show();
    }

    public  void bluetoothVisible(){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void bluetoothList(){
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }


    //////////try 1 bluetooth send //////
    private void init(int position) throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[position];
                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public void bluetoothWrite(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
///////////////para prueba 2 envio////////////
public void sendBtMsg(String msg2send){
    //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
    UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
    try {

       if(mmSocket == null) {
           mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
       }
        if (!mmSocket.isConnected()){
            mmSocket.connect();
        }

        String msg = msg2send;
        //msg += "\n";
        OutputStream mmOutputStream = mmSocket.getOutputStream();
        mmOutputStream.write(msg.getBytes());

        if (msg.equals("exit")){
            mmSocket.close();
            mmSocket = null;
        }

    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    (new Thread(new workerThread("temp"))).start();

}

    public void sendBluetoothString(String s){
        (new Thread(new workerThread(s))).start();
    }

    ////////////////////prueba 2 de bluetooth envio y recibir//////////////
    final Handler handler = new Handler();

    final class workerThread implements Runnable {

        private String btMsg;

        public workerThread(String msg) {
            btMsg = msg;
        }

        public void run(){

           // sendBtMsg(btMsg);
            while(!Thread.currentThread().isInterrupted()){
                int bytesAvailable;
                boolean workDone = false;

                try {
                    final InputStream mmInputStream;
                    mmInputStream = mmSocket.getInputStream();
                    bytesAvailable = mmInputStream.available();
                    if(bytesAvailable > 0){
                        byte[] packetBytes = new byte[bytesAvailable];
                        Log.e("Aquarium recv bt","bytes available");
                        byte[] readBuffer = new byte[1024];
                        mmInputStream.read(packetBytes);

                        for(int i=0;i<bytesAvailable;i++){
                            byte b = packetBytes[i];
                            if(b == delimiter){
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;

                                //The variable data now contains our full command
                                handler.post(new Runnable(){
                                    public void run()
                                    {
                                        txtBtInput.setText(data);
                                    }
                                });

                                workDone = true;
                                break;


                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }

                        if (workDone == true){
                            mmSocket.close();
                            break;
                        }

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };
//////////////////fin prueba 2 envio bluetooth////////////
//////////inicio prueba 3 bluettoth//////////
    void findBT() {
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
            if(device.getName().equals("raspberrypi"))
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
                                            txtBtInput.setText(data);
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
