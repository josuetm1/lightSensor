package com.example.josue.lightsensor;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, Marker> markersHashMap = new HashMap<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);
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
                return true;
            case R.id.action_map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_list:
                return true;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(19.461011, -70.682417);
        add("12D", latLng);
        latLng = new LatLng(19.461011, -71.682417);
        add("12D42342342", latLng);
        add("3", latLng);
        update("3", new LatLng(19.461011, -72.682417));
        remove("12D42342342");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        final MapsActivity act = this;
        GoogleMap.OnMarkerClickListener OnMarkerClickListener =new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Toast.makeText(act,
                        "Esto mostrará más info del dispositivo.\n"+
                        "Línea2\n" +
                                "Línea3",
                        Toast.LENGTH_SHORT).show();
                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            }
        };
        mMap.setOnMarkerClickListener(OnMarkerClickListener);
    }

    private void remove(String macAddress){
        Marker marker = markersHashMap.get(macAddress);
        if (marker==null) return;
        markersHashMap.remove(marker);
        marker.remove();
    }

    private void update(String macAddress, LatLng latLng){
        Marker marker = markersHashMap.get(macAddress);
        if (marker==null) return;
        marker.setPosition(latLng);
    }

    private void add(String macAddress, LatLng latLng){
        if (markersHashMap.containsKey(macAddress)){

            return;
        }
        String title = macAddress;
        String snippet = "Line1 Line2";
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(snippet));
        markersHashMap.put(macAddress, marker);
    }


}
