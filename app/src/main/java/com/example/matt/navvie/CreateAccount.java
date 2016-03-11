package com.example.matt.navvie;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CreateAccount extends Activity {

    private Button SubmitButton, CancelButton2;
    private EditText fName, lName, pword, pword2, email;
    private boolean send = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        SubmitButton =(Button) findViewById(R.id.SubmitButton);
        CancelButton2 = (Button) findViewById(R.id.CancelButton2);
        fName = (EditText) findViewById(R.id.EnterFirst);
        lName = (EditText) findViewById(R.id.EnterLast);
        pword = (EditText) findViewById(R.id.CreatePass);
        pword2 = (EditText) findViewById(R.id.RetypePass);
        email = (EditText) findViewById(R.id.EnterEmail);

        SubmitButton.setOnClickListener(new buttonListener());
        CancelButton2.setOnClickListener(new buttonListener());

        Thread udpThread = new Thread(new Runnable() {
            @Override
            public void run(){
                while(true){
                    //maybe sleep(100)
                    if(send == true){
                        try {
                            InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                            int servPort = 3020; //server port
                            Log.d("UDP","Connection...");
                            DatagramSocket socket = new DatagramSocket(); //client socket

                            String output = fName.getText().toString() + ", " +
                                    lName.getText().toString() + ", " +
                                    pword.getText().toString() + ", " +
                                    pword2.getText().toString() + ", " +
                                    email.getText().toString();

                            byte[] buffer = output.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, server, servPort);
                            socket.send(packet);
                            DatagramPacket reply = packet; //make another packet for the reply to be stored in.
                            //DatagramSocket rSock = new DatagramSocket(servPort);
                            while(true){
                                socket.receive(reply);
                                if(reply.getData()!=packet.getData()){
                                    buffer = packet.getData();
                                    Log.d("UDP", "blah"); //might not be right
                                    break;
                                }
                                else{
                                    Log.d("UDP", "No Reply so far.");
                                }
                                //we might need to start some sort of counter to break out of this loop if a response is not received
                                //by a certain amount of time
                            }
                            socket.close();
                            send = false; //break out of loop

                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        udpThread.start();
    }

    private class buttonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.SubmitButton:
                    send=true;
                    Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Check Your Email for Verification", Toast.LENGTH_LONG);
                    toast.show();
                    break;
                case R.id.CancelButton2:
                    Intent intent2 = new Intent(CreateAccount.this,MainActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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
}
