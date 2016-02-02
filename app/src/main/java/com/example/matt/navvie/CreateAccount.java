package com.example.matt.navvie;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CreateAccount extends Activity {

    private Button SubmitButton, CancelButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        SubmitButton =(Button) findViewById(R.id.SubmitButton);
        CancelButton2 = (Button) findViewById(R.id.CancelButton2);

        SubmitButton.setOnClickListener(new buttonListener());
        CancelButton2.setOnClickListener(new buttonListener());
    }

    private class buttonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.SubmitButton:
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
