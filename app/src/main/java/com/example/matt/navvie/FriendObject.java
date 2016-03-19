package com.example.matt.navvie;

import android.media.Image;

/**
 * Created by Matt on 3/18/2016.
 */
public class FriendObject {
    private String fname,lname,email,locname,bio,status;
    private double longc, latc;
    private Boolean toggle;
    private Image picture;

    public FriendObject(String FN, String LN,String e,double latCord, double longCord
                        ,String locName, String s, String b, Boolean locToggle,Image pic){
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

    public void setFname(String fn){
        fname = fn;
    }

    public void setLname(String ln){
        lname= ln;
    }

    public void setEmail(String e){email =e; }

    public void setStatus(String s){status=s;}

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

    public void setPicture(Image i){
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

    public Image getPicture(){
        return picture;
    }

    public double getLongc(){
        return longc;
    }

    public double getLatc(){
        return latc;
    }
}