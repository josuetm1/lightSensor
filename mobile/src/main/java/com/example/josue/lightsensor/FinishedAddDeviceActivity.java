package com.example.josue.lightsensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FinishedAddDeviceActivity extends AppCompatActivity {

    Button buttonStartCalibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_add_device);

        buttonStartCalibration = (Button) findViewById(R.id.buttonStartCalibration);

        buttonStartCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
