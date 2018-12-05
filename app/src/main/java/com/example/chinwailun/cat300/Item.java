package com.example.chinwailun.cat300;

import android.util.Log;

import java.util.Date;

public class Item {
    private String placeName;
    private String Coordinate;
    String start;
    String end;
    String user;

    public Item(String place,String Start,String End,String User){
        placeName = place;
        Log.d("tag",placeName);
        start = Start;
        end = End;
        user = User;
    }

    public String getPlaceName(){return placeName;}

    public String getStart(){return start;}

    public String getEnd(){return end;}

    public String getUser(){return user;}
}
