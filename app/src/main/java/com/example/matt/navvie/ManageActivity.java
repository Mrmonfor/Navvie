package com.example.matt.navvie;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class ManageActivity extends AppCompatActivity {
    private String[] friends,requests,curFriends;
    private ArrayList<String> friendList, requestsList,curFriendsList;
    private ArrayAdapter<String> friendAdapter, requestsAdapter,curFriendsAdapter;
    private ListView friendListView,requestsListView, curFriendsListView;
    private EditText searchText,curSearchText;
    private Button cancelManageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        cancelManageButton = (Button) findViewById(R.id.cancelManageButton);
        //LALALAAL
        cancelManageButton.setOnClickListener(new buttonListener());



        friendListView = (ListView) findViewById(R.id.listView2);
        searchText = (EditText) findViewById(R.id.searchFriendText);

        requestsListView = (ListView) findViewById(R.id.listView1);
        initRequestList(); /// creates request list
        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(ManageActivity.this, "List item was clicked at " + position, Toast.LENGTH_SHORT).show();
                requestsList.remove(position);
                requestsAdapter.notifyDataSetChanged();
            }
        });

        curFriendsListView = (ListView) findViewById(R.id.listView3);
        curSearchText = (EditText) findViewById(R.id.manageFriendText);




        curSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initManageList();
                if(s.toString().equals("")){
                    //reset listview
                    for(String item: curFriends) {
                        curFriendsList.remove(item);
                    }

                }else{
                    //search
                    searchCurFriend(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


       // initList();
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initList();
                if(s.toString().equals("")){
                   //reset listview
                    for(String item: friends) {
                        friendList.remove(item);
                    }

                }else{
                    //search
                    searchFriend(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void searchFriend(String textToSearch){
        initList();
        for(String item: friends){
            if(!item.contains(textToSearch)){
                friendList.remove(item);
            }
        }
        friendAdapter.notifyDataSetChanged();

    }

    public void searchCurFriend(String textToSearch){
        initManageList();
        for(String item: curFriends){
            if(!item.contains(textToSearch)){
                curFriendsList.remove(item);
            }
        }
        curFriendsAdapter.notifyDataSetChanged();

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
        friends = new String[]{"Jane Doe","Adam Southgate", "Chase Patton"};
        friendList = new ArrayList<>(Arrays.asList(friends));
        friendAdapter = new ArrayAdapter<String>(this, R.layout.list_friends, R.id.friendItemText, friendList);
        friendListView.setAdapter(friendAdapter);
    }

    public void initRequestList(){
        requests = new String[]{"Daniel Hill","Cory Sabel", "Andrew Schaefer"};
        requestsList = new ArrayList<>(Arrays.asList(requests));
        requestsAdapter = new ArrayAdapter<String>(this, R.layout.list_requests, R.id.requestsText, requestsList);
        //requestsListView.setAdapter(requestsAdapter);
        requestsListView.setAdapter(new MyListAdapter(this,R.layout.list_requests, requestsList));
    }

    public void initManageList(){
        curFriends = new String[]{"Anthony Lumpkins","Nick Hays", "Josh Queen"};
        curFriendsList = new ArrayList<>(Arrays.asList(curFriends));
        curFriendsAdapter = new ArrayAdapter<String>(this, R.layout.list_current_friends, R.id.manageText, curFriendsList);
        curFriendsListView.setAdapter(curFriendsAdapter);
    }

    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.cancelManageButton:
                    Intent intent = new Intent(ManageActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.acceptButton:
                    //friendList.remove(2);
                    break;
            }
        }
    }

    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;
        private List<String> mObjects;
        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();

                viewHolder.title = (TextView) convertView.findViewById(R.id.requestsText);
                viewHolder.accButton = (Button) convertView.findViewById(R.id.acceptButton);
                viewHolder.decButton = (Button) convertView.findViewById(R.id.declineButton);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.accButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Accept Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                    requestsList.remove(position);
                    requestsAdapter.notifyDataSetChanged();
                }
            });
            mainViewholder.decButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Decline Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                    requestsList.remove(position);
                    requestsAdapter.notifyDataSetChanged();
                }
            });
            mainViewholder.title.setText(getItem(position));

            return convertView;
        }

    }
    public class ViewHolder {
        TextView title;
        Button accButton;
        Button decButton;
    }
    }



