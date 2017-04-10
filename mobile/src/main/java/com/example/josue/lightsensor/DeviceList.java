package com.example.josue.lightsensor;

import java.util.ArrayList;

/**
 * Created by Josue on 08/04/17.
 */
public class DeviceList {
    private static ArrayList<Device> ourInstance = new ArrayList<Device>();

    public static ArrayList<Device> getInstance() {
        return ourInstance;
    }

    private DeviceList() {
    }

}
