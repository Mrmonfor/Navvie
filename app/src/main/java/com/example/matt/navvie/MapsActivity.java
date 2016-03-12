package com.example.matt.navvie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
    private String yourName = "Matt Monfort";
    private Button options, editProfileButton, logoutButton, manageButton, buildingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMap();

        options = (Button) findViewById(R.id.optionsButton);
        editProfileButton = (Button) findViewById(R.id.editProfile);
        logoutButton = (Button) findViewById(R.id.logout);
        manageButton = (Button) findViewById(R.id.manage);
        buildingButton = (Button) findViewById(R.id.buildings);

        editProfileButton.setOnClickListener(new buttonListener());
        logoutButton.setOnClickListener(new buttonListener());
        manageButton.setOnClickListener(new buttonListener());
        buildingButton.setOnClickListener(new buttonListener());

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), options);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }


    private void setUpMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onSearch(View view) {
        EditText searchBar = (EditText) findViewById(R.id.searchLocation);
        CharSequence place = searchBar.getText().toString();

        if (place != null || !place.equals("")) {

        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
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
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        // Holds boundaries of mapView
        LatLng northBound = new LatLng(36.073995, -79.804514);
        LatLng southBound = new LatLng(36.060396, -79.816273);
        LatLngBounds bounds = new LatLngBounds(southBound, northBound);

        //Holds cords of center of campus
        LatLng campus = new LatLng(36.066311, -79.808892);

        //This holds the markers name////
        mMap.addMarker(new MarkerOptions().position(campus).title("UNCG"));
        //this will find your current location.

        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(yourName));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        //to add image icon
       // mMap.addMarker(new MarkerOptions().position(campus).title("UNCG").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)));

        //sets default marker location and how zoomed in

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((campus), 15.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, -10));


        //Makes screen not scrollable so users cannot view outside campus.

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    public void onCameraChange(CameraPosition position) {
        float maxZoom = 15.0f;
        if (position.zoom > maxZoom)
            mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
    }
    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.editProfile:
                    Intent intent = new Intent(MapsActivity.this, EditProfile.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.logout:
                    Intent intent2 = new Intent(MapsActivity.this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.buildings:
                    Intent intent3 = new Intent(MapsActivity.this, BuildingActivity.class);
                    startActivity(intent3);
                    finish();
                    break;
                case R.id.manage:
                    Intent intent4 = new Intent(MapsActivity.this, ManageActivity.class);
                    startActivity(intent4);
                    finish();
                    break;

            }
        }

    }
}
