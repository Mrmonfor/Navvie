package com.example.matt.navvie;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

public class EditProfile extends AppCompatActivity {
    private Button subEditButton, cancelProfButton;
    private ImageView image;
    public static final int IMAGE_GALLERY_REQUEST = 3;
    private TextView bioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ///LALALALALALAALLALALALALALAL
        setContentView(R.layout.activity_edit_profile);


        subEditButton =(Button) findViewById(R.id.submitProfileButton);
        cancelProfButton = (Button) findViewById(R.id.cancelProfileButton);
        image = (ImageView) findViewById((R.id.profilePic));
        //image.getLayoutParams().width=150;
         bioText = (TextView) findViewById(R.id.bioText);
        bioText.setMovementMethod(new ScrollingMovementMethod());

        image.setOnClickListener(new buttonListener());
        subEditButton.setOnClickListener(new buttonListener());
        cancelProfButton.setOnClickListener(new buttonListener());

    }
    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.submitProfileButton:
                    Intent intent = new Intent(EditProfile.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.cancelProfileButton:
                    Intent intent2 = new Intent(EditProfile.this, MapsActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
                case R.id.profilePic:

                    Intent photointent;

                    if (Build.VERSION.SDK_INT < 19){
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
                    } else {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
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
                Uri imageUri = data.getData();
                // declare a stream to read image data from memory
                InputStream inputStream;
                //getting input stream based on Uri of image
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    //get bitmap from stream
                    Bitmap bitImage = BitmapFactory.decodeStream(inputStream);
                    //show image to user.
                    image.setImageBitmap(bitImage);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image",Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
