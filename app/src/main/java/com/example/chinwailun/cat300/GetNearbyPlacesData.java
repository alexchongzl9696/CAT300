package com.example.chinwailun.cat300;


import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0]; //object is the parameter that we passed in this doInBackground method
        url = (String)objects[1];

        com.example.chinwailun.cat300.DownloadURL downloadURL = new com.example.chinwailun.cat300.DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);//passing the string url here
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData; //here very important punya , must return this
    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList;
        com.example.chinwailun.cat300.DataParser parser = new com.example.chinwailun.cat300.DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(nearbyPlaceList);
    }

    //THIS going to take a list of hashmap
    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    { //this method going to show all the places in the list so we create marker options
        //and add a marker to map then move the camera to the marker
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i); //fecth the i element and store it in google place

            //fetch place name and vicinity
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat")); //googlePlace.get(lat) will return a string and need to
            //parse that string in double value
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng); //create object
            //set position and title for marker option
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}
