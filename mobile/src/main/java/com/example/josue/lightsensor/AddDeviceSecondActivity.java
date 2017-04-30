package com.example.josue.lightsensor;

import android.content.Intent;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class AddDeviceSecondActivity extends AppCompatActivity {
    Device device;
    EditText editTextMACAddress;
    EditText editTextConfirm;
    Button buttonFinish;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_second);
        Intent intent = getIntent();
        this.device = (Device) intent.getParcelableExtra("device");

        editTextMACAddress = (EditText) findViewById(R.id.editTextMacAddress);
        editTextConfirm = (EditText) findViewById(R.id.editTextConfirm);
        buttonFinish = (Button) findViewById(R.id.buttonFinish);
        buttonBack = (Button) findViewById(R.id.buttonBack);




        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Add Dev 2","on click"+editTextMACAddress.getText().toString());


                if(editTextMACAddress.getText().toString().equals(editTextConfirm.getText().toString())){
                    if(Pattern.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",editTextMACAddress.getText().toString())){
                        device.setName(editTextMACAddress.getText().toString());
                        if(!DeviceList.getInstance().contains(device)) {
                            if(AzureDataBase.getInstace().addDevice(device)) {
                                startActivity(new Intent(AddDeviceSecondActivity.this, FinishedAddDeviceActivity.class));
                                finish();
                            }else{
                                Toast.makeText(AddDeviceSecondActivity.this,
                                        "Another user has this device",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }else {
                            Toast.makeText(AddDeviceSecondActivity.this,
                                    "This device already exist",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(AddDeviceSecondActivity.this,
                                "Enter the correct MAC Address format: 'XX:XX:XX:XX:XX:XX'",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddDeviceSecondActivity.this,
                            "Both fields must be equals",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDeviceSecondActivity.this, AddDeviceActivity.class);
                intent.putExtra("device",device);
                startActivity(intent);
                finish();
            }
        });


    }
}
