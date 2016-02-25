package com.example.matt.navvie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class EditProfile extends AppCompatActivity {

    private Button subEditButton, cancelProfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subEditButton =(Button) findViewById(R.id.submitProfileButton);
        cancelProfButton = (Button) findViewById(R.id.cancelProfileButton);

        subEditButton.setOnClickListener(new buttonListener());
        cancelProfButton.setOnClickListener(new buttonListener());







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}