package com.example.matt.navvie;

import android.Manifest;
import android.app.Dialog;
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
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    ArrayList<LatLng> mMarkerPoints;
    private String yourName = "Matt Monfort";
    private Button options, editProfileButton, logoutButton, manageButton, buildingButton;
    double mLatitude=0;
    double mLongitude=0;
    LatLng origin,dest,startPoint;
    static final double MAXLEFT=-79.816136,MAXRIGHT=-79.804061,MAXUP=36.074605,MAXDOWN =36.060645;



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
       //mapView = (MapView) findViewById(R.id.map);
        //mapView.onCreate(savedInstanceState);
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
       // mMap = mapView.getMap();


        // Initializing

        mMarkerPoints = new ArrayList<LatLng>();
        // turns off scrolling
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        // Holds boundaries of mapView
        LatLng northBound = new LatLng(36.073995, -79.804514);
        LatLng southBound = new LatLng(36.060396, -79.816273);
        LatLngBounds bounds = new LatLngBounds(southBound, northBound);

        //Holds cords of center of campus
        final LatLng campus = new LatLng(36.066311, -79.808892);
        LatLng campus2 = new LatLng(36.071407, -79.811010);

        //This holds the markers name////
        mMap.addMarker(new MarkerOptions().position(campus).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
       // options2 = new MarkerOptions();
       //options2.position(campus);
        //options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(new MarkerOptions().position(campus2));
        mMarkerPoints.add(campus);

        //this will find your current location.

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(yourName));

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

        /**************************************************NEW STUFF********************************************************************/

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available



            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mMap = fm.getMap();

            // Enable MyLocation Button in the Map
            mMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
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
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(provider, 20000, 0, this);

            // Setting onclick event listener for the map

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    if(marker.getPosition()!=mMarkerPoints.get(0)){
                        refreshMap();
                        drawMarker(marker.getPosition());
                        origin = mMarkerPoints.get(0);
                        dest = mMarkerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }

                    return false;
                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng point) {
                    if(point.longitude>MAXLEFT&& point.longitude<MAXRIGHT && point.latitude>MAXDOWN && point.latitude<MAXUP){//longitude/latitudes may be reversed


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
                    }else{
                         Toast.makeText(getApplicationContext(), "Error: Location out of bounds!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double lon = extras.getDouble("long");
            double lat = extras.getDouble("lat");
            refreshMap();
            drawMarker(new LatLng(lat, lon));
            origin = mMarkerPoints.get(0);
            dest = mMarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);


        }
    }
    public void refreshMap(){
        //clears map and array, adds a default point to map, adds current position to array and map
        mMarkerPoints.clear();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        drawMarker(new LatLng(36.066311, -79.808892));//campus

    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Travelling Mode
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+ "&" +
                mode;

        // Output format
        String output = "json";




        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mMarkerPoints.contains(marker)){
            mMarkerPoints.remove(marker);
        }
        return false;
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

            }
        }

    }
    private void drawMarker(LatLng point){
        mMarkerPoints.add(point);

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
        // Draw the marker, if destination location is not set
        if(mMarkerPoints.size() < 2){

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng point = new LatLng(mLatitude, mLongitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            drawMarker(point);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

   @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}
