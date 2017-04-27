package com.example.josue.lightsensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDeviceActivity extends AppCompatActivity {

    Device device;
    EditText editTextName;
    EditText editTextBrand;
    EditText editTextColor;
    EditText editTextSize;
    Button buttonNext;
    Button buttonCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        Intent intent = getIntent();
        this.device = (Device) intent.getParcelableExtra("device");

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextBrand = (EditText) findViewById(R.id.editTextBrand);
        editTextColor = (EditText) findViewById(R.id.editTextColor);
        editTextSize = (EditText) findViewById(R.id.editTextSize);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        if(device.getName() != null)
            editTextName.setText(device.getName());
        if(device.getBrand() != null)
            editTextBrand.setText(device.getBrand());
        if(device.getColor() != null)
            editTextColor.setText(device.getColor());
        if(device.getSize() != null)
            editTextSize.setText(device.getSize());

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("add Dev 1",editTextName.getText().toString());
                Log.d("add Dev 1","heloooooooooooooo:D");
                if(!editTextName.getText().toString().equals("")  &&
                        !editTextBrand.getText().toString().equals("") &&
                        !editTextColor.getText().toString().equals("") &&
                        !editTextSize.getText().toString().equals("")) {
                    device.setNameUser(editTextName.getText().toString());
                    device.setBrand(editTextBrand.getText().toString());
                    device.setColor(editTextColor.getText().toString());
                    device.setSize(editTextSize.getText().toString());
                    launchAddActivity2(device);
                    AddDeviceActivity.this.finish();
                } else {
                    Toast.makeText(AddDeviceActivity.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                    //launchAddDialog1(devicePosition);
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void launchAddActivity2(Device device) {

    }
}
