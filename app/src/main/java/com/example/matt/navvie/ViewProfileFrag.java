package com.example.matt.navvie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewProfileFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewProfileFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProfileFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView fnameText, emailText, locText, stateText, bioText;
    private FriendObject info;

    private ArrayList<FriendObject> localities = new ArrayList<FriendObject>();

    private OnFragmentInteractionListener mListener;

    public ViewProfileFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewProfileFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProfileFrag newInstance(String param1, String param2) {
        ViewProfileFrag fragment = new ViewProfileFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        //info = (FriendObject) b.get("myData");
        localities = this.getArguments().getParcelableArrayList("myData");
        int i = this.getArguments().getInt("index");
        fnameText = (TextView) view.findViewById(R.id.friendNameText);
        emailText = (TextView) view.findViewById(R.id.friendEmailText);
        locText = (TextView) view.findViewById(R.id.friendLocationText);
        stateText = (TextView) view.findViewById(R.id.friendStatusText);
        bioText = (TextView) view.findViewById(R.id.friendBioText);
        if (localities.size() !=0) {
            info = localities.get(i);
            fnameText.setText(info.getFname() + " " + info.getLname());
            if (info.getLocname().equals("")) {
                locText.setText("Location:" + info.getLocname());
            } else {
                locText.setText("Location:On Campus");
                //always on campus at the moment, seems as we cannot view profiles from
                //the manageFriends activity they will never be offline
            }
            emailText.setText(info.getEmail());
            stateText.setText("Status:" + info.getStatus());
            bioText.setText(info.getBio());
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
