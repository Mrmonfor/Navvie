package com.example.matt.navvie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class EditProfile extends AppCompatActivity {
    private Button subEditButton, cancelProfButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_edit_profile);


        subEditButton =(Button) findViewById(R.id.submitProfileButton);
        cancelProfButton = (Button) findViewById(R.id.cancelProfileButton);

        subEditButton.setOnClickListener(new buttonListener());
        cancelProfButton.setOnClickListener(new buttonListener());

    }
    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.submitProfileButton:
                    Intent intent = new Intent(EditProfile.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.cancelProfileButton:
                    Intent intent2 = new Intent(EditProfile.this, MainActivity.class);
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
}
