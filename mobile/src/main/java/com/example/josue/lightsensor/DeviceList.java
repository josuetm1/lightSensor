package com.example.josue.lightsensor;

import java.util.ArrayList;

/**
 * Created by Josue on 08/04/17.
 */
public class DeviceList extends ArrayList<Device> {
    private static DeviceList ourInstance = new DeviceList();

    public static DeviceList getInstance() {
        return ourInstance;
    }

    private DeviceList() {
    }

    public boolean contains(Device d){
        for (Device device : ourInstance) {
            if(d.getName().equals(device.getName()))
                return true;

        }
        return false;
    }

}
