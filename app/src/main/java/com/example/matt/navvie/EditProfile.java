package com.example.matt.navvie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {
    public static final int IMAGE_GALLERY_REQUEST = 3;
    boolean threadIsFinished = false;
    boolean initThreadIsFinished = false;
    private Button subEditButton, cancelProfButton;
    private ImageView image;
    private TextView bioText;
    private Bitmap bitImage;
    private InputStream inputStream;
    private Uri imageUri;
    private Intent photoIntent;
    private EditText statusInput, bioInput, oldPwInput, newPwInput, retypePWInput;
    private Switch locationToggle;
    private String yourEmail;
    private TextView firstandlastname, emailTextView, locationTextView;
    private String realname;
    private String LocationName;
    private String bio;
    private String status;
    private int locationToggleint;
    private boolean hideLocation;
    private String updateResult = "";
    private String encodedPicture;
    private boolean retrievingPicture;
    private Bitmap PictureMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ///LALALALALALAALLALALALALALAL
        setContentView(R.layout.activity_edit_profile);
        subEditButton = (Button) findViewById(R.id.submitProfileButton);
        cancelProfButton = (Button) findViewById(R.id.cancelProfileButton);
        image = (ImageView) findViewById((R.id.profilePic));
        //image.getLayoutParams().width=150;
        bioText = (TextView) findViewById(R.id.bioText);
        bioText.setMovementMethod(new ScrollingMovementMethod());
        image.setOnClickListener(new buttonListener());
        subEditButton.setOnClickListener(new buttonListener());
        cancelProfButton.setOnClickListener(new buttonListener());
        Intent i = getIntent();
        yourEmail = i.getStringExtra("key");
        statusInput = (EditText) findViewById(R.id.statusText);
        //bioInput = (EditText) findViewById(R.id.bioText);
        oldPwInput = (EditText) findViewById(R.id.oldPassText);
        newPwInput = (EditText) findViewById(R.id.newPassText);
        locationToggle = (Switch) findViewById(R.id.toggleLocation);
        firstandlastname = (TextView) findViewById(R.id.nameText);
        emailTextView = (TextView) findViewById(R.id.emailText);
        locationTextView = (TextView) findViewById(R.id.locationText);
        retypePWInput = (EditText) findViewById(R.id.retypeText);

        Bitmap bitm= null;
        if(yourEmail.equalsIgnoreCase("mrmonfor@uncg.edu")){
            bitm = BitmapFactory.decodeResource(getResources(), R.drawable.matt);
        }else if(yourEmail.equalsIgnoreCase("scpatton@uncg.edu")){
            bitm = BitmapFactory.decodeResource(getResources(), R.drawable.chase);
        }else if(yourEmail.equalsIgnoreCase("alsouthg@uncg.edu")){
            bitm = BitmapFactory.decodeResource(getResources(), R.drawable.adam);
        }else{
            bitm = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous);
        }
        image.setImageBitmap(bitm);

        initThreadIsFinished = false;
        final Thread getMyProfileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //maybe sleep(100)
                DatagramSocket socket;
                try {
                    InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                    int servPort = 3020; //server port
                    Log.d("UDP", "Connection for Edit Profile...");
                    socket = new DatagramSocket(); //client socket
                    int localPort = socket.getLocalPort();

                    //left out profile picture update.
                    String output = "getProfile," + yourEmail;
                    if (locationToggle.isChecked()) {
                        output += ",1,";
                    } else {
                        output += ",0,";
                    }
                    byte[] buffer = output.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server, servPort);
                    socket.send(packet);

                    packet.setData(new byte[500]); //this needs to be set to some other value probably
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
                    String port = incomingData.substring(0, 5);
                    packet.setPort(Integer.parseInt(port));
                    socket.send(packet);
                    while (true) {
                        try {
                            //wait(2000);
                            socket.receive(packet);
                            incomingData2 = new String(packet.getData());
                            if (incomingData2.compareTo(incomingData) != 0) {
                                Log.d("UDP loop 2", incomingData2);
                                //set textView and edittexts to saved data from server.
                                ArrayList list = new ArrayList();
                                int endOfLast = 0;
                                for (int i = 0; incomingData2.charAt(i) != 0; i++) {
                                    if (incomingData2.charAt(i) == '|') {
                                        list.add(incomingData2.substring(endOfLast, i));
                                        endOfLast = i + 1;
                                    }
                                }
                                realname = (String) list.get(0);
                                LocationName = (String) list.get(1);
                                bio = (String) list.get(2);
                                status = (String) list.get(3);
                                int locationToggleint = (int) list.get(4);
                                if (locationToggleint == 1) {
                                    hideLocation = true;
                                } else {
                                    hideLocation = false;
                                }
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
                initThreadIsFinished = true;
            }

        });
        getMyProfileThread.start();
        while (!initThreadIsFinished) {

        }
        firstandlastname.setText(realname);
        emailTextView.setText(yourEmail);
        if (!LocationName.equals(" ")) {
            locationTextView.setText(LocationName);
        }
        if (LocationName.equals(" ")) {
            locationTextView.setText("Location:Between Buildings");
        }
        if (!bio.equals(" ")) {
            bioText.setText(bio);
        }
        if (!status.equals(" ")) {
            statusInput.setText(status);
        }
        locationToggle.setChecked(hideLocation);
        retrievingPicture = true;
        /*  final Thread getMyPictureThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                DatagramSocket socket;
                try {
                    InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                    int servPort = 3020; //server port
                    Log.d("UDP", "Connection for update Self Location...");
                    socket = new DatagramSocket(); //client socket
                    socket.setSoTimeout(2000);
                    int localPort = socket.getLocalPort();
                    String output = "getPicture," + yourEmail + ",";
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
                    socket.setSoTimeout(2000);
                    String port = incomingData.substring(0, 5);
                    packet.setPort(Integer.parseInt(port));
                    socket.send(packet);
                    boolean finishedReceivingPicture = false;
                    //get the picture in chunks
                    StringBuilder sb = new StringBuilder();
                    Character ch = '\0';
                    String previous = "";
                    packet.setData(new byte[2000]);
                    while (!finishedReceivingPicture) {
                        try {
                            socket.receive(packet);
                            incomingData2 = new String(packet.getData());
                            if (incomingData2.charAt(0) == ch) {
                                retrievingPicture = true;
                                break;
                            }
                            //guard against duplicate data incoming.
                            if (!incomingData2.equals(previous)) {
                                Log.d("UDP", "Using: " + incomingData2);
                                if (incomingData2.indexOf(ch) == -1) {
                                    sb.append(incomingData2);
                                } else {
                                    sb.append(incomingData2.substring(0, incomingData2.indexOf(ch)));
                                    finishedReceivingPicture = true;
                                    byte[] decodedBytes = Base64.decode(sb.toString(), Base64.DEFAULT);
                                    PictureMap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                    //Thread.sleep(1000);
                                }
                            }
                            previous = incomingData2;
                            packet.setData(new byte[2000]);
                            Log.d("UDP", "Retreived: " + incomingData2);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    retrievingPicture = false;
                    socket.close();
                    Log.d("UDP", "COMPLETED!");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        getMyPictureThread.start();
        while (retrievingPicture) {

        }
        if (PictureMap != null) {
            image.setImageBitmap(PictureMap);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (reqCode == IMAGE_GALLERY_REQUEST) {
            //if here, heard back from image gallery
            if (resCode == RESULT_OK) {
                //if here, everything processes succesfully

                // address of the photo in memory
                imageUri = data.getData();
                // declare a stream to read image data from memory

                //getting input stream based on Uri of image
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    //get bitmap from stream
                    bitImage = BitmapFactory.decodeStream(inputStream);
                    //show image to user.
                    //image.setImageBitmap(bitImage);

                    //******************IMAGE ENCODING EXAMPLE FOLLOWS******************************
                    //encodes the image that you upload
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    encodedPicture = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                    //decodes the string from the image you uploaded
                    byte[] decodedBytes = Base64.decode(encodedPicture, 0);
                    Bitmap b1 = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    image.setImageBitmap(b1);
                    //send image in chunks
                  /*  final Thread sendPicThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            //maybe sleep(100)
                            threadIsFinished = false;
                            DatagramSocket socket;
                            try {
                                InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                                int servPort = 3020; //server port
                                Log.d("UDP", "Connection for Edit Profile...");
                                socket = new DatagramSocket(); //client socket
                                int localPort = socket.getLocalPort();

                                //left out profile picture update.
                                String output = "updatePicture," + yourEmail + ",";
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
                                String port = incomingData.substring(0, 5);
                                packet.setPort(Integer.parseInt(port));
                                socket.send(packet);
                                Thread.sleep(100);
                                boolean finishedSendingPic = false;
                                int endOfLast = 0;
                                while (!finishedSendingPic) {
                                    packet.setData(new byte[2000]); //this needs to be set to some other value probably
                                    if (encodedPicture.length() - endOfLast >= 2000) {
                                        output = encodedPicture.substring(endOfLast, endOfLast + 2000);
                                        endOfLast += 2000;
                                    } else {
                                        output = encodedPicture.substring(endOfLast);
                                        finishedSendingPic = true;
                                    }
                                    buffer = output.getBytes();
                                    packet.setData(buffer);
                                    socket.send(packet);
                                    Log.d("UDP", "sent: " + output);
                                    Thread.sleep(100);
                                }
                                socket.close();
                                Log.d("UDP", "COMPLETED!");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            threadIsFinished = true;
                        }

                    });
                    sendPicThread.start();
                    //display the image you uploaded, should be same as original bitImage
                    //****************IMAGE EXAMPLE ENDS*********************************************
                    while (!threadIsFinished) {
                        //wait for thread to finish. Might not need to be here.
                    }
                    //image.setImageBitmap(b1);

                    */
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.submitProfileButton:
                    String x = newPwInput.getText().toString();
                    String y = retypePWInput.getText().toString();
                    if ((newPwInput.getText().toString().equals("") && retypePWInput.getText().toString().equals(""))
                            || newPwInput.getText().toString().equals(retypePWInput.getText().toString())) {
                        final Thread updateProfileThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                //maybe sleep(100)
                                threadIsFinished = false;
                                DatagramSocket socket;
                                try {
                                    InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                                    int servPort = 3020; //server port
                                    Log.d("UDP", "Connection for Edit Profile...");
                                    socket = new DatagramSocket(); //client socket
                                    int localPort = socket.getLocalPort();

                                    //left out profile picture update.
                                    String output = "updateProfile," + yourEmail + ","
                                            + statusInput.getText().toString() + ","
                                            + bioText.getText().toString() + ","
                                            + oldPwInput.getText().toString() + ","
                                            + newPwInput.getText().toString() + ",";
                                    if (locationToggle.isChecked()) {
                                        output += "1,";
                                    } else {
                                        output += "0,";
                                    }

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
                                    String port = incomingData.substring(0, 5);
                                    packet.setPort(Integer.parseInt(port));
                                    socket.send(packet);
                                    while (true) {
                                        try {
                                            //wait(2000);
                                            socket.receive(packet);
                                            incomingData2 = new String(packet.getData());
                                            if (incomingData2.compareTo(incomingData) != 0) {
                                                Log.d("UDP loop 2", incomingData2.substring(0, 7));
                                                updateResult = incomingData2.substring(0, 7);
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
                                threadIsFinished = true;
                            }

                        });
                        updateProfileThread.start();
                        while (!threadIsFinished) {

                        }
                        if (updateResult.equals("success")) {
                            Intent intent = new Intent(EditProfile.this, MapsActivity.class);
                            intent.putExtra("key", yourEmail);
                            startActivity(intent);
                            finish();
                        }
                        if (updateResult.equals("failure")) {
                            Toast.makeText(getApplicationContext(), "Check your details.", Toast.LENGTH_SHORT).show(); //check email?
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "New passwords dont match.", Toast.LENGTH_SHORT).show(); //check email?

                    }
                    break;
                case R.id.cancelProfileButton:
                    Intent intent2 = new Intent(EditProfile.this, MapsActivity.class);
                    intent2.putExtra("key", yourEmail);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.profilePic:


                    if (Build.VERSION.SDK_INT < 19) {
                        photoIntent = new Intent();
                        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
                        photoIntent.setType("image/*");
                        startActivityForResult(photoIntent, IMAGE_GALLERY_REQUEST);
                    } else {
                        photoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        photoIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        photoIntent.setType("image/*");
                        startActivityForResult(photoIntent, IMAGE_GALLERY_REQUEST);
                    }



                    /*

                    //involk the image gallary
                    Intent photoIntent = new Intent(Intent.ACTION_PICK);
                    //place we want to store picture
                    File photo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String photoPath = photo.getPath();
                    //get URI representation
                    Uri data = Uri.parse(photoPath);
                    //set the picture and type
                    photoIntent.setDataAndType(data, "image/*");
                    //Invoke this activity and get photo back from it
                    startActivityForResult(photoIntent, 3);
                    */
                    break;
            }

        }
    }
}
