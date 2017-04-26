package com.example.josue.lightsensor;

import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Josue on 02/12/16.
 */

public class Device {
    protected String name; //this is the macAddress
    private String nameUser;
    protected String brand;
    protected String color;
    protected String size;
    protected String state;
    protected boolean enable;
    protected long id;
    protected String macAddress;
    protected LatLng latLng;
    protected Timestamp lastSeen = Timestamp.valueOf("1999-01-01 01:01:01");



    public Device(){

    }

    public Device(String name, String state, boolean enable) {
        this.name = name;
        this.state = state;
        this.enable = enable;
        //this.id = id;
    }
    public Device(String name, String state, boolean enable, String macAddress, LatLng latLng) {
        this.name = name;
        this.state = state;
        this.enable = enable;
        this.macAddress = macAddress;
        this.latLng = latLng;
        //this.lastSeen = Timestamp.valueOf("1999-1-1 1:1:1");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;

    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Timestamp lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
