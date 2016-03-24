package com.example.matt.navvie;

import android.Manifest;
import android.app.Dialog;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener, ViewProfileFrag.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private ArrayList<LatLng> mMarkerPoints;
    private String yourName = "Matt Monfort";
    private Button options, editProfileButton, logoutButton, manageButton, buildingButton, routeButton, cancelViewButton;
    LatLng origin, dest;
    static final double MAXLEFT = -79.816136, MAXRIGHT = -79.804061, MAXUP = 36.074605, MAXDOWN = 36.060645;
    static Location location;
    private Location curLocation;
    ArrayList<FriendObject> yourFriends = new ArrayList<>();
    boolean state = false, bundleFlag=true;
    private Marker marker;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private ViewProfileFrag f1 = new ViewProfileFrag();
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private GoogleApiClient mLocationClient;
    final LatLng campus = new LatLng(36.066311, -79.808892);
    LatLng campus2 = new LatLng(36.071407, -79.811010);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (servicesOK()) {
            setContentView(R.layout.activity_maps);
            FriendObject Adam = new FriendObject("Adam", "Southgate", "alsouthgate@uncg.edu", 36.068321, -79.807677, "Stone/STN", "in Class", "i have 3 classes this semester", true, null);
            FriendObject Chase = new FriendObject("Chase", "Patton", "scpatton@uncg.edu", 36.065875, -79.812076, "MHRA?", "doing stuff", "i graduate this semester", true, null);
            yourFriends.add(Adam);
            yourFriends.add(Chase);
        } else {
            setContentView(R.layout.activity_maps);
            Toast.makeText(this, "Map not Available", Toast.LENGTH_SHORT).show();
        }

        if (setUpMap()) {
            mLocationClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            mLocationClient.connect();
            // Toast.makeText(this, "Ready to Map", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Map not Available", Toast.LENGTH_SHORT).show();
        }

        mMarkerPoints = new ArrayList<LatLng>();
        mMarkerPoints.add(new LatLng(0, 0));

        options = (Button) findViewById(R.id.optionsButton);
        editProfileButton = (Button) findViewById(R.id.editProfile);
        logoutButton = (Button) findViewById(R.id.logout);
        manageButton = (Button) findViewById(R.id.manage);
        buildingButton = (Button) findViewById(R.id.buildings);
        routeButton = (Button) findViewById(R.id.routeToButton);
        cancelViewButton = (Button) findViewById(R.id.cancelFriendButton);



        editProfileButton.setOnClickListener(new buttonListener());
        logoutButton.setOnClickListener(new buttonListener());
        manageButton.setOnClickListener(new buttonListener());
        buildingButton.setOnClickListener(new buttonListener());
        routeButton.setOnClickListener(new buttonListener());
        cancelViewButton.setOnClickListener(new buttonListener());


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), options);
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connet to Google Play", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    //initMap
    private boolean setUpMap() {
        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            //mapFragment.getMapAsync(this);
            mMap = mapFrag.getMap();


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker1) {
                    marker = marker1;
                    if (!state) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("myData", yourFriends);
                        int index=0;
                        for(int z = 0; z<yourFriends.size(); z++){
                            if(marker1.getTitle().equalsIgnoreCase(yourFriends.get(z).getFname() + yourFriends.get(z).getLname())){
                                index = z;
                                break;
                            }
                        }



                        bundle.putInt("index",index);
                        f1.setArguments(bundle);
                        //fragmentTransaction.add(R.id.map, f1);
                        fragmentTransaction.addToBackStack(null);
                        getSupportFragmentManager().beginTransaction().add(R.id.map, f1).commit();
                        state = true;
                        routeButton.setVisibility(View.VISIBLE);
                        cancelViewButton.setVisibility(View.VISIBLE);


                    }
                    return false;
                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng point) {
                    if (point.longitude > MAXLEFT && point.longitude < MAXRIGHT && point.latitude > MAXDOWN && point.latitude < MAXUP) {//longitude/latitudes may be reversed


                        // Already map contain destination location
                        if (mMarkerPoints.size() > 1) {

                            FragmentManager fm = getSupportFragmentManager();
                            refreshMap();
                        }

                        // draws the marker at the currently touched location
                        drawMarker(point);

                        // Checks, whether start and end locations are captured
                        if (mMarkerPoints.size() == 2) {
                            origin = mMarkerPoints.get(0);
                            dest = mMarkerPoints.get(1);

                            // Getting URL to the Google Directions API
                            String url = getDirectionsUrl(origin, dest);

                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                            //mMarkerPoints.set(2,null);

                        }
                        if (mMarkerPoints.size() > 2) {
                            dest = mMarkerPoints.get(1);
                            String url = getDirectionsUrl(origin, dest);

                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: Location out of bounds!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        return (mMap != null);

    }

    //This method will be used to use the search bar to find people/buildings. We will work on this more later
    public void geoLocate(View v) throws IOException {
        EditText searchBar = (EditText) findViewById(R.id.searchLocation);
        CharSequence location = searchBar.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location.toString(), 1, MAXDOWN, MAXLEFT, MAXUP, MAXRIGHT);
        if (list != null) {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Location Out of Bounds", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }





    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" +
                mode;

        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mMarkerPoints.contains(marker)) {
            mMarkerPoints.remove(marker);
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "Connected to Location Services", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if(currentLocation==null){
            Toast.makeText(this, "Current Location isnt available", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Current Location is available", Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(5000); //updates location every 5 secs.
            request.setFastestInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, request,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
       @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
           if(mMarkerPoints.size()>1) {
               mMap.addPolyline(lineOptions);
           }
        }


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
                case R.id.routeToButton:
                    if (marker.getPosition() != mMarkerPoints.get(0)) {
                        refreshMap2();
                        drawMarker(marker.getPosition());
                        origin = mMarkerPoints.get(0);
                        dest =mMarkerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                    getSupportFragmentManager().beginTransaction().remove(f1).commit();
                    routeButton.setVisibility(View.INVISIBLE);
                    cancelViewButton.setVisibility(View.INVISIBLE);
                    state=false;

                    break;
                case R.id.cancelFriendButton:
                    //fragmentTransaction.remove(f1).commit();
                    getSupportFragmentManager().beginTransaction().remove(f1).commit();
                    routeButton.setVisibility(View.INVISIBLE);
                    cancelViewButton.setVisibility(View.INVISIBLE);
                    state=false;

                    break;

            }
        }

    }
    public void refreshMap() {
        //clears map and array, adds a default point to map, adds current position to array and map

        mMarkerPoints.clear();
        mMap.clear();
        if(curLocation!=null){
        mMarkerPoints.add(new LatLng(curLocation.getLatitude(),curLocation.getLongitude()));
        }
        for (int z = 0; z < yourFriends.size(); z++) {
            MarkerOptions options = new MarkerOptions();
            LatLng loc = new LatLng(yourFriends.get(z).getLatc(), yourFriends.get(z).getLongc());
            // Setting the position of the marker
            options.position(loc).title(yourFriends.get(z).getFname());


            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);
        }
        // mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        //drawMarker(new LatLng (mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));
        if (location == null) {

            //drawMarker(new LatLng(36.071407, -79.811010));//current location
        } else {
            drawMarker(new LatLng(location.getLatitude(), location.getLongitude()));
        }

    }

    public void refreshMap2() {
        //clears map and array, adds a default point to map, adds current position to array and map


        mMap.clear();
        mMarkerPoints.clear();
        mMarkerPoints.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
        mMarkerPoints.add(marker.getPosition());
        for (int z = 0; z < yourFriends.size(); z++) {
            MarkerOptions options = new MarkerOptions();
            LatLng loc = new LatLng(yourFriends.get(z).getLatc(), yourFriends.get(z).getLongc());
            // Setting the position of the marker
            options.position(loc).title(yourFriends.get(z).getFname());


            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);
        }
        // mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        //drawMarker(new LatLng (mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));
        if (location == null) {

            //drawMarker(new LatLng(36.071407, -79.811010));//current location
        } else {
          //  drawMarker(new LatLng(location.getLatitude(), location.getLongitude()));
        }

    }
    private void drawMarker(LatLng point){

       if(mMarkerPoints.size()<2)
       { mMarkerPoints.add(point);}

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if(mMarkerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(mMarkerPoints.size()>1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);
    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Location: " + location.getLatitude() + "," +location.getLongitude();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        curLocation=location;

        mMap.clear();
        for(int z = 0; z<yourFriends.size(); z++){
            MarkerOptions options = new MarkerOptions();
            LatLng loc = new LatLng(yourFriends.get(z).getLatc(), yourFriends.get(z).getLongc());
            // Setting the position of the marker
            options.position(loc);
            options.title(yourFriends.get(z).getFname() +yourFriends.get(z).getLname());

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);

        }
        //mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        mMarkerPoints.set(0, new LatLng(location.getLatitude(),location.getLongitude()));
        for (int i =0;i<mMarkerPoints.size();i++){
            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(mMarkerPoints.get(i));

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if(i==0){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);
        }
        if (mMarkerPoints.size()==2){
            origin = mMarkerPoints.get(0);
            dest = mMarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            //mMarkerPoints.set(2,null);

        }
        if(bundleFlag==true) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                double lon = extras.getDouble("long");
                double lat = extras.getDouble("lat");
                refreshMap();
                drawMarker(new LatLng(lat, lon));
                //mMarkerPoints.add(new LatLng(lat, lon));
                origin = mMarkerPoints.get(0);
                dest = mMarkerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                extras = null;
                bundleFlag=false;
            }
        }
    }




}
