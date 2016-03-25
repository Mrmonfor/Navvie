package com.example.matt.navvie;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import java.net.URL;
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

        final Thread udpThread = new Thread(new Runnable() {
            @Override
            public void run(){

                while(true){
                    //maybe sleep(100)
                    if(send){

                        try {

                            InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                            int servPort = 3020; //server port
                            Log.d("UDP","Connection...");
                            DatagramSocket socket = new DatagramSocket(); //client socket

                            String output = ",createuser,"+fName.getText().toString() + "," +
                                    lName.getText().toString() + "," +
                                    pword.getText().toString() + "," +
                                    pword2.getText().toString() + "," +
                                    email.getText().toString()+",";
                            //
                            byte[] buffer = output.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, server, servPort);

                            socket.send(packet);

                            packet.setData(new byte[50]); //this needs to be set to some other value probably
                            while(true){
                                Thread.sleep(1);
                                try {
                                    socket.receive(packet);
                                    String incomingData = new String(packet.getData());
                                    if(incomingData.compareTo(output)!=0){
                                        Log.d("UDP", incomingData); //might not be right
                                        break;
                                    }
                                    else{
                                        Log.d("UDP", "No Reply so far.");
                                    }
                                } catch(Exception e){
                                    Log.d("UDP", "Socket Receive Error");
                                }
                                //we might need to start some sort of counter to break out of this loop if a response is not received
                                //by a certain amount of time
                            }
                            socket.close();
                            send = false; //break out of loop
                            Log.d("UDP", "COMPLETED!");
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
