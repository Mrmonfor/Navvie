package com.example.matt.navvie;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMap();

    }

    @Override
    protected void onResume(){
        super.onResume();
        setUpMap();
    }



    private void setUpMap(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onSearch(View view){
        EditText searchBar = (EditText)findViewById(R.id.searchLocation);
        CharSequence place =searchBar.getText().toString();

        if(place != null || !place.equals("")){

        }

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

        // Holds boundaries of mapView
        LatLng northBound = new LatLng(36.073995, -79.804514);
        LatLng southBound = new LatLng(36.060396, -79.816273);
        LatLngBounds bounds = new LatLngBounds(southBound, northBound);

        //Holds cords of center of campus
        LatLng campus = new LatLng(36.066311, -79.808892);
        mMap.setBuildingsEnabled(true);
        //This holds the markers name
        mMap.addMarker(new MarkerOptions().position(campus).title("UNCG"));
        //to add image icon
       // mMap.addMarker(new MarkerOptions().position(campus).title("UNCG").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)));

        //sets default marker location and how zoomed in

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((campus), 15.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, -10));


        //Makes screen not scrollable so users cannot view outside campus.
        mMap.getUiSettings().setScrollGesturesEnabled(false);


    }

    public void onCameraChange(CameraPosition position) {
        float maxZoom = 15.0f;
        if (position.zoom > maxZoom)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
    }
}
