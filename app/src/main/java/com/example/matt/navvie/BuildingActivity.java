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
    private ArrayAdapter<String>buildingNameAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
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

    public void initList(){
        buildingNames = new String[]{"Bryan Building BRYN","Curry Building CURY", "Eberhart Building EBER", "Elliot University Center (EUC) ELLT", "Foust Building FOUS",
        "Ferguson Building FERG", "Graham Building GRAM", "Jackson Library LIBR", "McIver Building MCVR", "Moore Building (Nursing) NMOR", "Moore Humanities and Research Administration MHRA",
        "Mossman Building (Administration)", "Music Building MUSI", "Petty Science Building PETT", "School of Education Building SOEB", "Stone Building STON", "Sullivan Science Building SULV",
        "Weatherspoon Art Museum"};
        buildings = new Integer[]{R.drawable.bryanpic,R.drawable.currypic,R.drawable.eberhartpic,R.drawable.eucpic,R.drawable.foustpic,R.drawable.fergusonpic,R.drawable.grahampic,R.drawable.librarypic,
        R.drawable.mciverpic,R.drawable.nursingpic,R.drawable.mhrapic,R.drawable.adminpic,R.drawable.musicpic,R.drawable.pettypic,R.drawable.soebpic,R.drawable.stonepic,R.drawable.sullivanpic,
        R.drawable.museumpic};
        buildingList = new ArrayList<>(Arrays.asList(buildingNames));
        buildingNameAdapter = new MyListAdapter2(this, R.layout.list_buildings, buildingList, buildings);
        buildingView.setAdapter(buildingNameAdapter);


    }
    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.cancelBuildingButton:
                    Intent intent = new Intent(BuildingActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    break;}
        }
    }

    public void searchBuildings(String textToSearch){
        initList();
        for(String item: buildingNames){
            if(!item.contains(textToSearch)){
                buildingList.remove(item);
            }
        }
        buildingNameAdapter.notifyDataSetChanged();

}


      class BuildingHolder {
              public TextView buildingName;
              public ImageView buildingImage;
              public Button buildingButton;
    }
     class MyListAdapter2 extends ArrayAdapter {
         private int layout;
         private ArrayList mObjects;
         private Integer[] images2;

         public MyListAdapter2(Context context, int resource, ArrayList<String> names, Integer[] images) {
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
                     Toast.makeText(getContext(), "Find button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                 }
             });
             mainViewholder.buildingName.setText(getItem(position).toString());
             mainViewholder.buildingImage.setImageResource((int) images2[position]);
             //mainViewholder.buildingImage.

             return convertView;
         }

     }}