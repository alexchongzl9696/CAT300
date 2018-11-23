package com.example.chinwailun.cat300;

import android.location.Location;

import com.google.android.gms.location.LocationListener;

public class MyLocationListener implements LocationListener {
    double latitude=0, longitude=0;

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
