package com.example.matt.navvie;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.Handler;

public class LoginActivity extends Activity {

    private Button Login2, CancelButton;
    private boolean send = false;
    private boolean threadIsFinished = false;
    private EditText emailInput, passInput;
    private boolean correctCredentials;
    private boolean finishThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        //
        // this.setContentView(R.layout.activity_login);
        emailInput = (EditText) findViewById(R.id.LoginEmail);
        passInput = (EditText) findViewById(R.id.LoginPass);
        Login2 = (Button) findViewById(R.id.Login2);
        CancelButton = (Button) findViewById(R.id.CancelButton);

        Login2.setOnClickListener(new buttonListener());
        CancelButton.setOnClickListener(new buttonListener());

        final Thread udpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                while (true) {
                    //maybe sleep(100)
                    DatagramSocket socket;
                    if (send) {
                        threadIsFinished = false;
                        try {
                            InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                            int servPort = 3020; //server port
                            Log.d("UDP", "Connection...");
                            socket = new DatagramSocket(); //client socket
                            int localPort = socket.getLocalPort();
                            String output = "login," + emailInput.getText().toString() + ","
                                    + passInput.getText().toString() + ",";
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
                            String port = incomingData.substring(0, 4);
                            packet.setPort(Integer.parseInt(port));
                            socket.send(packet);
                            while (true) {
                                try {
                                    //wait(2000);
                                    socket.receive(packet);
                                    incomingData2 = new String(packet.getData());
                                    if (incomingData2.compareTo(incomingData) != 0) {
                                        Log.d("UDP loop 2", incomingData2.substring(0, 7));
                                        //do something with incomingData2
                                        if (incomingData2.substring(0, 7).equals("success")) {
                                            correctCredentials = true;
                                        } else {
                                            correctCredentials = false;
                                        }
                                        break;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            socket.close();
                            send = false; //break out of loop
                            Log.d("UDP", "COMPLETED!");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        threadIsFinished = true;
                    }
                    if (finishThread) {
                        break;
                    }
                }
            }
        });
        udpThread.start();
    }

    private class buttonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Login2:
                    send = true;
                    while (!threadIsFinished) ;

                    if (correctCredentials) {
                        finishThread = true;
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        intent.putExtra("key", emailInput.getText().toString());
                        startActivity(intent);
                        finish();
                    } else {
                        send = false;

                        /*
                            This delays the check for 250ms. It's needed because the ui thread
                            and the udpthread are asyncronous and UI tends to get ahead.
                        */
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (correctCredentials) {
                                    finishThread = true;
                                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                    intent.putExtra("key", emailInput.getText().toString());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    passInput.setText("");
                                    Toast toast = Toast.makeText(getApplicationContext(), "Check your details.", Toast.LENGTH_SHORT); //check email?
                                    toast.show();
                                }
                            }
                        }, 250);
                    }
                    break;
                case R.id.CancelButton:
                    Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }

        }

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
}
