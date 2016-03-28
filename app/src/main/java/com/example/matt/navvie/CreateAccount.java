package com.example.matt.navvie;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Looper;
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

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

public class CreateAccount extends Activity {

    private Button SubmitButton, CancelButton2;
    private EditText fName, lName, pword, pword2, email;
    private boolean send = false;
    private boolean threadIsFinished= false;
    private String requestResponse= null;
    private boolean accountCreated = false;

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
                Looper.prepare();
                DatagramSocket socket;
                while(true){
                    //maybe sleep(100)
                    if(send){

                        try {
                            InetAddress server = InetAddress.getByName("162.243.203.154"); //server ip
                            int servPort = 3020; //server port
                            Log.d("UDP","Connection...");
                            socket = new DatagramSocket(); //client socket
                            //createuser -> test
                            int localPort = socket.getLocalPort();
                            String output = "createuser,"+fName.getText().toString() + "," +
                                    lName.getText().toString() + "," +
                                    pword.getText().toString() + "," +
                                    email.getText().toString()+",";
                            //
                            byte[] buffer = output.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer,buffer.length, server, servPort);
                            socket.send(packet);

                            packet.setData(new byte[50]); //this needs to be set to some other value probably
                            String incomingData2 = "";
                            String incomingData = "";
                            //response 1
                            while(true){
                                //Thread.sleep(1000);
                                try {
                                    socket.receive(packet);
                                    incomingData = new String(packet.getData());
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
                            //response 2
                            socket = new DatagramSocket(localPort);
                            String port = incomingData.substring(0, 4);
                            packet.setPort(Integer.parseInt(port));
                            socket.send(packet);
                            while(true){
                                try{
                                    //wait(2000);
                                    socket.receive(packet);
                                    incomingData2 = new String(packet.getData());
                                    if(incomingData2.compareTo(incomingData)!=0){
                                        Log.d("UDP loop 2", incomingData2.substring(0,7));
                                        //do something with incomingData2
                                        if(incomingData2.substring(0,7).equals("success")){
                                            accountCreated = true;
                                        }
                                        threadIsFinished = true;
                                        break;
                                    }
                                } catch(IOException e){
                                    e.printStackTrace();
                                }
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
                    if(pword.getText().toString().equals(pword2.getText().toString())) {
                        send = true;
                        //Toast toast = Toast.makeText(getApplicationContext(), "Wait for a response from the server", Toast.LENGTH_SHORT);
                        //toast.show();
                        while(!threadIsFinished){

                        }
                        if(accountCreated) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Account created.", Toast.LENGTH_SHORT); //check email?
                            toast.show();
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Account not created. Check your details.", Toast.LENGTH_SHORT); //check email?
                            toast.show();
                        }
                        Intent intent = new Intent(CreateAccount.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(), "The passwords don't match.", Toast.LENGTH_LONG);
                        toast.show();
                    }
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
