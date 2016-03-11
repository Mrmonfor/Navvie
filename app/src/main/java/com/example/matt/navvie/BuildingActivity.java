package com.example.matt.navvie;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private ArrayList<Integer> buildingList;
    private ArrayAdapter<Integer> buildingAdapter;
    private EditText SearchBuildingText;
    private Button findButton;
    private Integer[] buildings;
    private String[] buildingNames;
    private ArrayList<String>buildingNameList;
    private ArrayAdapter<String>buildingNameAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);


        buildingView = (ListView) findViewById(R.id.listViewBuilding);
        SearchBuildingText = (EditText) findViewById(R.id.searchBuildingText);
        initList();
    }
    public void initList(){
        buildingNames = new String[]{"Bryan Building BRYN","Curry Building CURY", "Eberhart Building EBER"};
        buildingNameList = new ArrayList<>(Arrays.asList(buildingNames));
        buildingNameAdapter = new ArrayAdapter<String>(this, R.layout.list_buildings, R.id.BuildingItemText, buildingNameList);
        buildingView.setAdapter(buildingNameAdapter);

        //Building b1 = new Building("Bryan Building BRYN", R.drawable.bryanpic);
        //Building b2 = new Building("Curry Building CURY", R.drawable.currypic);
       //Building b3 = new Building("Eberhart Building EBER", R.drawable.eberhartpic);
        //buildings = new Building[]{b1,b2,b3};
       // buildings = new Integer[]{R.drawable.bryanpic,R.drawable.currypic,R.drawable.eberhartpic};
      //  buildingList = new ArrayList<>(Arrays.asList(buildings));
       // buildingAdapter = new ArrayAdapter(this, R.layout.list_buildings, R.id.buildingImage, buildingList);
       // buildingView.setAdapter(new MyListAdapter2(this,R.layout.list_buildings, buildingList));
    }
    private static class Building{
        public String name;
        public Integer img;
        //no gets and sets yet, so public
        public Building(String n,Integer i){
            name = n;
            img = i;
        }
    }
}


      class BuildingHolder {
              public TextView buildingName;
              public ImageView buildingImage;
              public Button buildingButton;
    }
     class MyListAdapter2 extends ArrayAdapter<String>{
        private int layout;
        private List<String> mObjects;
        public MyListAdapter2(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            BuildingHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
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
            mainViewholder.buildingName.setText(getItem(position));
            mainViewholder.buildingImage.setImageResource(position);

            return convertView;
        }

    }