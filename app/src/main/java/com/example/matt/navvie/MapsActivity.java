package com.example.matt.navvie;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMarkerClickListener, ViewProfileFrag.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private ArrayList<LatLng> mMarkerPoints;
    private String yourEmail = "Matt Monfort";
    private Button options, editProfileButton, logoutButton, manageButton, buildingButton, routeButton, cancelViewButton;
    LatLng origin, dest;
    static final double MAXLEFT = -79.816136, MAXRIGHT = -79.804061, MAXUP = 36.074605, MAXDOWN = 36.060645;
    static Location location;
    private Location curLocation;
    ArrayList<FriendObject> yourFriends = new ArrayList<>();
    boolean state = false, bundleFlag = true;
    private Marker marker;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private ViewProfileFrag f1 = new ViewProfileFrag();
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private GoogleApiClient mLocationClient;
    final LatLng campus = new LatLng(36.066311, -79.808892);
    LatLng campus2 = new LatLng(36.071407, -79.811010);
    LocationRequest request;
    private static float logicalDensity;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private SensorManager sm;
    private Sensor accel;
    private static final double SHAKES = .0000009;
    private float previousX = 0;
    private Handler handler;
    private boolean flg = false;
    private boolean isHandlerLive = false;
    private boolean designTrade = false;
    private boolean friendsRetreived = false;
    private String friendData = "";
    private boolean retreivingFriendData;
    private boolean endThreads = false;
    private FriendObject track;
    /* FOR DESIGN TRADE OFF********************************************************************/
    private final Runnable processSensor = new Runnable() {
        @Override
        public void run() {
            flg = true;
            handler.postDelayed(this, 20000);
        }
    };
    /* FOR DESIGN TRADE OFF********************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_maps);
            Intent i = getIntent();
            yourEmail = i.getStringExtra("key");
            //getFriendData();
            //FriendObject Adam = new FriendObject("Adam", "Southgate", "alsouthgate@uncg.edu", 36.068321, -79.807677, "Stone/STN", "in Class", "i have 3 classes this semester", true, BitmapFactory.decodeResource(this.getResources(),
            //        R.drawable.mypic));
            //FriendObject Chase = new FriendObject("Chase", "Patton", "scpatton@uncg.edu", 36.070280, -79.813256, "MHRA?", "doing stuff", "i graduate this semester", true, BitmapFactory.decodeResource(this.getResources(), R.drawable.mypic2));
            //yourFriends.add(Adam);
            //yourFriends.add(Chase);
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
        handler = new Handler();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);/* FOR DESIGN TRADE OFF********************************************************************/
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);/* FOR DESIGN TRADE OFF********************************************************************/

        mMarkerPoints = new ArrayList<LatLng>();
        mMarkerPoints.add(new LatLng(0, 0));

        options = (Button) findViewById(R.id.optionsButton);
        editProfileButton = (Button) findViewById(R.id.editProfile);
        logoutButton = (Button) findViewById(R.id.logout);
        manageButton = (Button) findViewById(R.id.manage);
        buildingButton = (Button) findViewById(R.id.buildings);
        routeButton = (Button) findViewById(R.id.routeToButton);
        cancelViewButton = (Button) findViewById(R.id.cancelFriendButton);
        SensorManager sensorManager;
        Sensor sen;


        editProfileButton.setOnClickListener(new buttonListener());
        logoutButton.setOnClickListener(new buttonListener());
        manageButton.setOnClickListener(new buttonListener());
        buildingButton.setOnClickListener(new buttonListener());
        routeButton.setOnClickListener(new buttonListener());
        cancelViewButton.setOnClickListener(new buttonListener());


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), options);
    }

    private void getFriendData() {
        if (!retreivingFriendData && !endThreads) {
            yourFriends.clear();
            final Thread friendDataThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    retreivingFriendData = true;
                    Looper.prepare();
                    DatagramSocket socket;
                    try {
                        InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                        int servPort = 3020; //server port
                        Log.d("UDP", "Connection for FriendData...");
                        socket = new DatagramSocket(); //client socket
                        socket.setSoTimeout(1000);
                        int localPort = socket.getLocalPort();
                        //getfriends,email.uncg.edu,
                        String output = "getFriends," + yourEmail + ",";
                        byte[] buffer = output.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server, servPort);
                        socket.send(packet);

                        packet.setData(new byte[2000]); //this needs to be set to some other value probably
                        String incomingData2 = "";
                        String incomingData = "";
                        //response 1
                        while (true) {
                            //Thread.sleep(1000);
                            try {
                                socket.receive(packet);
                                incomingData = new String(packet.getData());
                                if (incomingData.compareTo(output) != 0) {
                                    Log.d("UDP", incomingData); //might not be right
                                    break;
                                } else {
                                    Log.d("UDP", "No Reply so far.");
                                }
                            } catch (Exception e) {
                                Log.d("UDP", "Socket Receive Error");
                            }
                            //we might need to start some sort of counter to break out of this loop if a response is not received
                            //by a certain amount of time
                        }
                        socket.close();
                        //response 2
                        socket = new DatagramSocket(localPort);
                        socket.setSoTimeout(1000);
                        String port = incomingData.substring(0, 5);
                        packet.setPort(Integer.parseInt(port));
                        socket.send(packet);
                        friendData = "";
                        while (true) {
                            try {
                                //wait(2000);
                                socket.receive(packet);
                                incomingData2 = new String(packet.getData());
                                if (!incomingData2.substring(0, 5).equals(port)) {
                                    Log.d("UDP loop 2", incomingData2);
                                    //do something with incomingData2
                                    for (int i = 0; i < incomingData2.length(); i++) {
                                        //when the null char is found
                                        if (incomingData2.charAt(i) == 0) {
                                            break;
                                        }
                                        friendData += incomingData2.charAt(i);
                                    }
                                    friendsRetreived = true;
                                    break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        socket.close();
                        Log.d("UDP", "COMPLETED!");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    retreivingFriendData = false;
                }

            });
            friendDataThread.start();
            //How long does this handler need to wait for a response from the server?
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (friendsRetreived && !endThreads) {
                        //if friendData.equals("failure"){ dont set any friends}
                        //take friendData and make friendObjects from that.
                        String friendFirst, friendLast, friendemail, friendLocationName, friendStatus, friendBio, friendToggle;
                        Double friendlat, friendlong;
                        boolean friendLocationToggle;
                        String friendType;
                        ArrayList friendStuff = new ArrayList();
                        //Bitmap friendImage;
                        int endOfLast = 0;
                        boolean finishedParsing = false;
                        int i = 0;
                        while (!finishedParsing) {
                            for (; i < friendData.length(); i++) {
                                if (friendData.charAt(i) == ',') {
                                    friendStuff.add(friendData.substring(endOfLast, i));
                                    endOfLast = i + 1;
                                }
                                if (i == friendData.length() - 1) {
                                    friendStuff.add(friendData.substring(endOfLast, i));
                                    finishedParsing = true;
                                    break;
                                }
                                if (friendData.charAt(i) == '|') {
                                    friendStuff.add(friendData.substring(endOfLast, i));
                                    endOfLast = i + 1;
                                    i++;
                                    break;
                                }
                            }
                            if (friendStuff.size() != 0) {
                                friendFirst = (String) friendStuff.get(0);
                                friendLast = (String) friendStuff.get(1);
                                friendemail = (String) friendStuff.get(2);
                                String friendlatstring = (String) friendStuff.get(3);
                                if (!friendlatstring.equals(" ")) {
                                    friendlat = Double.parseDouble(friendlatstring);
                                } else {
                                    friendlat = 0.0;
                                }
                                String friendlongstring = (String) friendStuff.get(4);

                                if (!friendlongstring.equals(" ")) {
                                    friendlong = Double.parseDouble(friendlongstring);
                                } else {
                                    friendlong = 0.0;
                                }
                                friendLocationName = (String) friendStuff.get(5);
                                friendStatus = (String) friendStuff.get(6);
                                friendBio = (String) friendStuff.get(7);
                                friendToggle = (String) friendStuff.get(8);
                                if (Integer.parseInt(friendToggle) == 1) {
                                    friendLocationToggle = true;
                                } else {
                                    friendLocationToggle = false;
                                }
                                friendType = (String) friendStuff.get(9);
                                String friendImageString = (String) friendStuff.get(10);
                            /*
                                Transform friendImageString into bitmap
                             */

                                //bitmap is incorrect.
                                FriendObject friend = new FriendObject(friendFirst, friendLast, friendemail, friendlat, friendlong, friendLocationName, friendStatus, friendBio, friendLocationToggle, friendType,null);
                            /*if (yourFriends.size() > 0) {
                                for (int p = 0; p < yourFriends.size(); p++) {
                                    if (yourFriends.get(p).getFname().equalsIgnoreCase(friendFirst)) {
                                        yourFriends.set(p, friend);
                                    } else {
                                        yourFriends.add(friend);
                                    }
                                }
                            } else {*/
                                yourFriends.add(friend);
                                //}
                            }
                            friendStuff = new ArrayList();
                        }
                    }
                }
            }, 1000);
        }

    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Google Play", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        endThreads = false;
        //getFriendData();
        setUpMap();
        if (designTrade == true) {
            sm.registerListener(this, accel,/* FOR DESIGN TRADE OFF********************************************************************/
                    SensorManager.SENSOR_DELAY_UI);/* FOR DESIGN TRADE OFF********************************************************************/
            handler.post(processSensor);/* FOR DESIGN TRADE OFF********************************************************************/
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // sm.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        endThreads = true;
        super.onStop();
        request.setInterval(60000);
    }

    //initMap
    private boolean setUpMap() {
        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            //mapFragment.getMapAsync(this);
            mMap = mapFrag.getMap();
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            logicalDensity = metrics.density;

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker1) {
                    //!marker1.getPosition().equals(mMarkerPoints.get(0)) &&
                    if (marker1.getTitle() != null) {
                        marker = marker1;
                        if (!state) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("myData", yourFriends);
                            int index = 0;
                            for (int z = 0; z < yourFriends.size(); z++) {
                                if (marker1.getTitle().equalsIgnoreCase(yourFriends.get(z).getFname() + yourFriends.get(z).getLname())) {
                                    index = z;
                                    break;
                                }
                            }


                            bundle.putInt("index", index);
                            f1.setArguments(bundle);
                            //fragmentTransaction.add(R.id.map, f1);
                            fragmentTransaction.addToBackStack(null);
                            getSupportFragmentManager().beginTransaction().add(R.id.map, f1).commit();
                            state = true;
                            routeButton.setVisibility(View.VISIBLE);
                            cancelViewButton.setVisibility(View.VISIBLE);


                        }
                    }
                    return false;
                }
            });
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng point) {
                    if (point.longitude > MAXLEFT && point.longitude < MAXRIGHT && point.latitude > MAXDOWN && point.latitude < MAXUP) {//longitude/latitudes may be reversed
                        track = null;

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

    /**
     * A method to download json data from url
     */
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
        if (currentLocation == null) {
            Toast.makeText(this, "Current Location isnt available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Current Location is available", Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(10000); //updates location every 10 secs.
            request.setFastestInterval(10000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /* FOR DESIGN TRADE OFF********************************************************************/
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flg) {
            if (event.sensor == accel) {
                float dx = event.values[0];
                float dy = event.values[1];
                float dz = event.values[2];
                TextView tv;
                float lastx = dx;
                float lasty = dy;
                float lastz = dz;
                // previousX = dx;

                float shake = Math.abs(dx + dy + dz - lastx - lasty - lastz);

                if (dx > previousX || dx < previousX) {
                    //tv = (TextView) findViewById(R.id.textView12);
                    // tv.setText("SHAKE SHAKE SHAKE");
                    Toast.makeText(this, "shake detected", Toast.LENGTH_SHORT).show();
                    previousX = dx;

                }
                flg = false;
            }
            // sm.unregisterListener(this);
        }
    }

    /* FOR DESIGN TRADE OFF********************************************************************/
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
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

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
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
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

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
            }

            // Drawing polyline in the Google Map for the i-th route
            if (mMarkerPoints.size() > 1 && mMarkerPoints.get(1).latitude != 0 && lineOptions != null) {
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
            switch (v.getId()) {
                case R.id.editProfile:
                    endThreads = true;
                    Intent intent = new Intent(MapsActivity.this, EditProfile.class);
                    intent.putExtra("key", yourEmail);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.logout:
                    endThreads = true;
                    Intent intent2 = new Intent(MapsActivity.this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.buildings:
                    endThreads = true;
                    Intent intent3 = new Intent(MapsActivity.this, BuildingActivity.class);
                    startActivity(intent3);
                    finish();
                    break;
                case R.id.manage:
                    endThreads = true;
                    Intent intent4 = new Intent(MapsActivity.this, ManageActivity.class);
                    startActivity(intent4);
                    finish();
                    break;
                case R.id.routeToButton:
                    if (marker.getPosition() != mMarkerPoints.get(0)) {
                        refreshMap2();
                        mMarkerPoints.set(1, marker.getPosition());
                        //drawMarker(marker.getPosition());
                        origin = mMarkerPoints.get(0);
                        dest = mMarkerPoints.get(1);
                        if (yourFriends.size() > 0) {
                            for (int p = 0; p < yourFriends.size(); p++) {
                                String fullName = yourFriends.get(p).getFname() + yourFriends.get(p).getLname();
                                if (fullName.equalsIgnoreCase(marker.getTitle())) {
                                    track = yourFriends.get(p);
                                }
                            }
                        }

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                    getSupportFragmentManager().beginTransaction().remove(f1).commit();
                    routeButton.setVisibility(View.INVISIBLE);
                    cancelViewButton.setVisibility(View.INVISIBLE);
                    state = false;

                    break;
                case R.id.cancelFriendButton:
                    //fragmentTransaction.remove(f1).commit();
                    getSupportFragmentManager().beginTransaction().remove(f1).commit();
                    routeButton.setVisibility(View.INVISIBLE);
                    cancelViewButton.setVisibility(View.INVISIBLE);
                    state = false;


                    break;

            }
        }

    }

    public void refreshMap() {
        //clears map and array, adds a default point to map, adds current position to array and map

        mMarkerPoints.clear();
        mMap.clear();
        if (curLocation != null) {
            mMarkerPoints.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
            MarkerOptions options = new MarkerOptions();
            // Setting the position of the marker
            options.position(mMarkerPoints.get(0));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }
        refreshFriendArray();
        // mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        //drawMarker(new LatLng (mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));


    }

    public void refreshMap2() {
        //clears map and array, adds a default point to map, adds current position to array and map


        mMap.clear();
        mMarkerPoints.clear();
        mMarkerPoints.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
        mMarkerPoints.add(marker.getPosition());

        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(mMarkerPoints.get(0));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options);

        refreshFriendArray();
        // mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        //drawMarker(new LatLng (mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()));


    }

    private void drawMarker(LatLng point) {

        if (mMarkerPoints.size() < 2) {
            mMarkerPoints.add(point);
        }

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (mMarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        } else if (mMarkerPoints.size() > 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        //Bitmap bitmap = Bitmap.createScaledBitmap()
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void refreshFriendArray() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getFriendData();
            }
        }, 5000);
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        for (int z = 0; z < yourFriends.size(); z++) {
            MarkerOptions options = new MarkerOptions();
            LatLng loc = new LatLng(yourFriends.get(z).getLatc(), yourFriends.get(z).getLongc());

            if (loc.longitude > MAXLEFT && loc.longitude < MAXRIGHT && loc.latitude > MAXDOWN && loc.latitude < MAXUP && !yourFriends.get(z).getToggle()) {


                // Setting the position of the marker
                options.position(loc);
                options.title(yourFriends.get(z).getFname() + yourFriends.get(z).getLname());
                ImageView profile = (ImageView) marker.findViewById(R.id.profile_pic);
                Bitmap bitmap;
                if (yourFriends.get(z).getPicture() != null) {
                    bitmap = yourFriends.get(z).getPicture();

                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous);
                    ;
                }
                int px = (int) Math.ceil(34 * logicalDensity);
                int px2 = (int) Math.ceil(23 * logicalDensity);
                bitmap = Bitmap.createScaledBitmap(bitmap, px, px2, false);
                profile.setImageBitmap(bitmap);
                options.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker)));
                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);
                if (track != null) {
                    mMarkerPoints.set(1, new LatLng(track.getLatc(), track.getLongc()));
                    origin = mMarkerPoints.get(0);
                    dest = mMarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                    //mMarkerPoints.set(2,null);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateSelfLocation(location);

        //String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        curLocation = location;
        //View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        mMap.clear();
        refreshFriendArray();
        //mMap.addMarker(new MarkerOptions().position(new LatLng(36.071407, -79.811010)));//campus2
        mMarkerPoints.set(0, new LatLng(location.getLatitude(), location.getLongitude()));
        for (int i = 0; i < mMarkerPoints.size(); i++) {
            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            boolean drop = true;
            // Setting the position of the marker
            options.position(mMarkerPoints.get(i));

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if (i == 0) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(options);
            } else {//dont drop new marker if dest is a friend
                for (int z = 0; z < yourFriends.size(); z++) {
                    LatLng temp = new LatLng(yourFriends.get(z).getLatc(), yourFriends.get(z).getLongc());
                    LatLng temp2 = new LatLng(mMarkerPoints.get(1).latitude, mMarkerPoints.get(1).longitude);
                    if (temp.equals(temp2)) {
                        drop = false;
                    }
                }
                if (drop) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(options);
                }
            }

            // Add new marker to the Google Map Android API V2
            //mMap.addMarker(options);
        }
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
        if (bundleFlag == true) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                track = null;
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
                bundleFlag = false;
            }
        }
    }

    private void updateSelfLocation(final Location loc) {
        final Thread updateLocationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!endThreads) {
                    Looper.prepare();
                    DatagramSocket socket;
                    try {
                        InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                        int servPort = 3020; //server port
                        Log.d("UDP", "Connection for update Self Location...");
                        socket = new DatagramSocket(); //client socket
                        socket.setSoTimeout(1000);
                        int localPort = socket.getLocalPort();
                        String output = "updateLocation," + yourEmail + "," + loc.getLatitude() + "," + loc.getLongitude() + ",";
                        byte[] buffer = output.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server, servPort);
                        socket.send(packet);

                        packet.setData(new byte[50]); //this needs to be set to some other value probably
                        String incomingData2 = "";
                        String incomingData = "";
                        //response 1
                        while (true) {
                            //Thread.sleep(1000);
                            try {
                                socket.receive(packet);
                                incomingData = new String(packet.getData());
                                if (incomingData.compareTo(output) != 0) {
                                    Log.d("UDP", incomingData); //might not be right
                                    break;
                                } else {
                                    Log.d("UDP", "No Reply so far.");
                                }
                            } catch (Exception e) {
                                Log.d("UDP", "Socket Receive Error");
                            }
                            //we might need to start some sort of counter to break out of this loop if a response is not received
                            //by a certain amount of time
                        }
                        socket.close();
                        //response 2
                        socket = new DatagramSocket(localPort);
                        socket.setSoTimeout(1000);
                        String port = incomingData.substring(0, 5);
                        packet.setPort(Integer.parseInt(port));
                        socket.send(packet);
                        while (true) {
                            try {
                                socket.receive(packet);
                                incomingData2 = new String(packet.getData());
                                if (incomingData2.compareTo(incomingData) != 0) {
                                    break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        socket.close();
                        Log.d("UDP", "COMPLETED!");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        updateLocationThread.start();
    }


}
