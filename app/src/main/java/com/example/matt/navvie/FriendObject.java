package com.example.matt.navvie;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matt on 3/18/2016.
 */
public class FriendObject implements Parcelable {
    private String fname,lname,email,locname,bio,status;
    private double longc, latc;
    private Boolean toggle;
    private Bitmap picture;

    public FriendObject(String FN, String LN,String e,double latCord, double longCord
                        ,String locName, String s, String b, Boolean locToggle,Bitmap pic){
        fname=FN;
        lname=LN;
        status=s;
        longc = longCord;
        latc = latCord;
        email=e;
        bio=b;
        locname=locName;
        toggle=locToggle;
        picture=pic;


    }

    protected FriendObject(Parcel in) {
        fname = in.readString();
        lname = in.readString();
        email = in.readString();
        locname = in.readString();
        bio = in.readString();
        status = in.readString();
        longc = in.readDouble();
        latc = in.readDouble();

    }

    public static final Creator<FriendObject> CREATOR = new Creator<FriendObject>() {
        @Override
        public FriendObject createFromParcel(Parcel in) {
            return new FriendObject(in);
        }

        @Override
        public FriendObject[] newArray(int size) {
            return new FriendObject[size];
        }
    };

    public void setFname(String fn){
        fname = fn;
    }

    public void setLname(String ln){
        lname= ln;
    }

    public void setEmail(String e){email =e; }

    public void setStatus(String si){status=si;}

    public void setBio(String b){bio=b;}

    public void setLocName(String l){locname=l;}

    public void setLongc(double lon){
        longc=lon;
    }

    public void setLatc(double lat){
        latc=lat;
    }

    public void setToggle(Boolean t){
        toggle =t;
    }

    public void setPicture(Bitmap i){
        picture=i;
    }

    public String getFname(){
        return fname;
    }

    public String getLname(){
        return lname;
    }

    public String getEmail(){
        return email;
    }

    public String getStatus(){
        return status;
    }

    public String getBio(){
        return bio;
    }

    public Boolean getToggle() {
        return toggle;
    }

    public String getLocname(){
        return locname;
    }

    public Bitmap getPicture(){
        return picture;
    }

    public double getLongc(){
        return longc;
    }

    public double getLatc(){
        return latc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeString(email);
        dest.writeString(locname);
        dest.writeString(bio);
        dest.writeString(status);
        dest.writeDouble(longc);
        dest.writeDouble(latc);
    }


}