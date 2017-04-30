package com.example.josue.lightsensor;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

/**
 * Created by Josue on 29/04/17.
 */

public class Apertura {
    private Timestamp fecha;
    private LatLng latLng;
    private Device device;

    public Apertura(String string, Device device) {
        String[] aux = string.split("@@@");
        this.device = device;
        this.fecha = Timestamp.valueOf(aux[0]+" "+aux[1]);
        this.latLng = new LatLng(Double.valueOf(aux[2]),Double.valueOf(aux[3]));
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
