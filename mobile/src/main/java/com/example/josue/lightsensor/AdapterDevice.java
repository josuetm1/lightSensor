package com.example.josue.lightsensor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Josue on 02/12/16.
 */

public class AdapterDevice extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<Device> devices;

    public AdapterDevice(Activity activity, ArrayList<Device> devices) {
        this.activity = activity;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return devices.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_list, null);
        }
        final Device device = devices.get(position);

        final Switch enableSwitch = (Switch) v.findViewById(R.id.switchEnable);
        enableSwitch.setChecked(device.getEnable());

        enableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                device.setEnable(enableSwitch.isChecked());
            }
        });

        TextView deviceName = (TextView) v.findViewById(R.id.textViewDevice);
        deviceName.setText(device.getNameUser());

        TextView deviceState = (TextView) v.findViewById(R.id.textViewState);
        if(device.getLastSeen().toString().equals("1999-01-01 01:01:01.0")){
            deviceState.setText("refresh lastseen");
        }

        else {
            deviceState.setText(device.getLastSeen().toString());
        }

        return v;
    }
}
