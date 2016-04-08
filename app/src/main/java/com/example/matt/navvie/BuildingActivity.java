package com.example.matt.navvie;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingActivity extends AppCompatActivity {
    private ListView buildingView;
    private Button cancelBuildButton;
    private EditText SearchBuildingText;

    private Integer[] buildings;
    private String[] buildingNames;
    private ArrayList<String> buildingList;
    private ArrayAdapter<String> buildingNameAdapter;
    private ArrayList<Integer> buildingPicsList;
    private ArrayAdapter<Integer> buildingPicsAdapter;
    private Intent buildingIntent;
    private static int searchNum = -1;
    private String yourEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        Intent i = getIntent();
        yourEmail = i.getStringExtra("key");
        cancelBuildButton = (Button) findViewById(R.id.cancelBuildingButton);
        cancelBuildButton.setOnClickListener(new buttonListener());

        buildingView = (ListView) findViewById(R.id.listViewBuilding);
        SearchBuildingText = (EditText) findViewById(R.id.searchBuildingText);
        initList();
        SearchBuildingText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    //reset listview
                    initList();

                } else {
                    //search
                    searchBuildings(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    public void initList() {
        buildingNames = new String[]{"Bryan/BRYN", "Curry/CURY", "Eberhart/EBER", "Elliot University Center (EUC) ELLT", "Foust/FOUS",
                "Ferguson/FERG", "Graham/GRAM", "Jackson Library/LIBR", "McIver/MCVR", "Moore(Nursing)/NMOR", "Moore Humanities and Research Administration/MHRA",
                "Mossman(Administration)", "Music/MUSI", "Petty Science/PETT", "School of Education/SOEB", "Stone/STON", "Sullivan Science/SULV",
                "Weatherspoon Art Museum"};
        buildings = new Integer[]{R.drawable.bryanpic, R.drawable.currypic, R.drawable.eberhartpic, R.drawable.eucpic, R.drawable.foustpic, R.drawable.fergusonpic, R.drawable.grahampic, R.drawable.librarypic,
                R.drawable.mciverpic, R.drawable.nursingpic, R.drawable.mhrapic, R.drawable.adminpic, R.drawable.musicpic, R.drawable.pettypic, R.drawable.soebpic, R.drawable.stonepic, R.drawable.sullivanpic,
                R.drawable.museumpic};
        buildingList = new ArrayList<>(Arrays.asList(buildingNames));
        buildingPicsList = new ArrayList<>(Arrays.asList(buildings));
        buildingNameAdapter = new MyListAdapter2(this, R.layout.list_buildings, buildingList, buildingPicsList);
        buildingView.setAdapter(buildingNameAdapter);
        searchNum = -1;


    }

    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancelBuildingButton:
                    Intent intent = new Intent(BuildingActivity.this, MapsActivity.class);
                    intent.putExtra("key",yourEmail);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }

    public void searchBuildings(String textToSearch) {
        initList();
        for (String item : buildingNames) {

            if (!item.contains(textToSearch)) {
                buildingList.remove(item);
            }
        }
        buildingNameAdapter.notifyDataSetChanged();

        if (buildingList.size() == 1) {//Search only works when narrowed down to 1 possibility
            String search;
            search = buildingList.get(0).toString();
            for (int i = 0; i < buildingNames.length; i++) {
                if (search.equalsIgnoreCase(buildingNames[i])) {
                    buildingPicsList.clear();
                    buildingPicsList.add(buildings[i]);
                    searchNum = i;
                }
            }
        }

    }


    class BuildingHolder {
        public TextView buildingName;
        public ImageView buildingImage;
        public Button buildingButton;
    }

    class MyListAdapter2 extends ArrayAdapter {
        private int layout;
        private ArrayList mObjects;
        private ArrayList<Integer> images2;

        public MyListAdapter2(Context context, int resource, ArrayList<String> names, ArrayList<Integer> images) {
            super(context, R.layout.list_buildings, names);

            mObjects = names;
            images2 = images;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BuildingHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.list_buildings, parent, false);
                BuildingHolder viewHolder = new BuildingHolder();

                viewHolder.buildingName = (TextView) convertView.findViewById(R.id.BuildingItemText);
                viewHolder.buildingImage = (ImageView) convertView.findViewById(R.id.buildingImage);
                viewHolder.buildingButton = (Button) convertView.findViewById(R.id.findBuildingButton);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (BuildingHolder) convertView.getTag();
            mainViewholder.buildingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = 0;
                    if (searchNum != -1) {
                        p = searchNum;
                    } else {
                        p = position;
                    }

                    Toast.makeText(getContext(), "Now Routing to  " + buildingNames[p], Toast.LENGTH_SHORT).show();
                    switch (p) {
                        case 0:

                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.066510);
                            buildingIntent.putExtra("long", -79.811846);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 1:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.065742);
                            buildingIntent.putExtra("long", -79.808544);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 2:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.0688775);
                            buildingIntent.putExtra("long", -79.806691);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 3:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.067503);
                            buildingIntent.putExtra("long", -79.810162);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 4:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.067085);
                            buildingIntent.putExtra("long", -79.807923);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 5:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.065897);
                            buildingIntent.putExtra("long", -79.807604);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 6:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.066084);
                            buildingIntent.putExtra("long", -79.806734);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 7:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.068200);
                            buildingIntent.putExtra("long", -79.809213);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 8:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.067568);
                            buildingIntent.putExtra("long", -79.807299);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 9:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.069298);
                            buildingIntent.putExtra("long", -79.807192);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 10:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.065683);
                            buildingIntent.putExtra("long", -79.809808);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 11:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.066558);
                            buildingIntent.putExtra("long", -79.810786);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 12:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.072603);
                            buildingIntent.putExtra("long", -79.807034);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 13:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.069273);
                            buildingIntent.putExtra("long", -79.807826);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 14:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.065985);
                            buildingIntent.putExtra("long", -79.811724);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 15:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.068365);
                            buildingIntent.putExtra("long", -79.807697);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 16:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.069878);
                            buildingIntent.putExtra("long", -79.806560);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                        case 17:
                            buildingIntent = new Intent(BuildingActivity.this, MapsActivity.class);
                            buildingIntent.putExtra("lat", 36.066189);
                            buildingIntent.putExtra("long", -79.805842);
                            buildingIntent.putExtra("key",yourEmail);
                            startActivity(buildingIntent);
                            finish();
                            break;
                    }

                }
            });
            mainViewholder.buildingName.setText(getItem(position).toString());
            mainViewholder.buildingImage.setImageResource((int) images2.get(position));
            //mainViewholder.buildingImage.

            return convertView;
        }

    }
}