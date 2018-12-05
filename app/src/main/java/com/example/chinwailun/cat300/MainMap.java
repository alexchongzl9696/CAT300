package com.example.chinwailun.cat300;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.maps.MapboxMap;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMap extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        MapboxMap.OnMarkerClickListener{

    String pPlace;
    ArrayList<String> multiDataTransfer = new ArrayList<>();
    double mLat, mLong;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Object dataTransfer[] = new Object[2];
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent mapI = getIntent();
        pPlace = mapI.getStringExtra("user_preference");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permission is granted

                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient(); //this method create new client
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else //permission is denied
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        String[] url = new String[3];
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        //if client is equal to NUll, that means there is no location set currently
        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }



        com.example.chinwailun.cat300.GetNearbyPlacesData getNearbyPlacesData = new com.example.chinwailun.cat300.GetNearbyPlacesData();
        mMap.clear(); //clear the map if there are any markers present
        url[0] = getUrl(latitude,longitude, pPlace);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url[0];

        getNearbyPlacesData.execute(dataTransfer);
        Toast.makeText(MainMap.this, "Nearby " + pPlace + " you might be interested in : ", Toast.LENGTH_SHORT).show();
    }

    public void onClick(View v)
    {
        //check if the user clicked on the source button or not

        //create object array, it will store two objects so the first object will be mMap and
        //second is URL

        //create an object of class GetNearbyPLacesData
        com.example.chinwailun.cat300.GetNearbyPlacesData getNearbyPlacesData = new com.example.chinwailun.cat300.GetNearbyPlacesData();

        switch(v.getId())
        {
            case R.id.B_search: //take whatever the user entered in the text field and then search for that place
                EditText search =  findViewById(R.id.search);
                String location = search.getText().toString(); //convert it to string
                List<Address> addressList;

                //check if the user actually entered something or not
                if(!location.equals("")) //if not an empty string
                {
                    /* Geocoder class - whose responses will be localized for the given Locale  */
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5); //this can return a list of
                        //maybe two or three addresses


                        if(addressList != null)
                        {
                            //put a marker on all of those three addresses
                            for(int i = 0;i<addressList.size();i++)
                            {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                //mMap.clear();
                                mMap.addMarker(markerOptions);

                                //animate the camera to that location but it can give me multiple results,
                                //so when i do this the camera will focus on the last result given
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break; //museum, hotel, cafe, bank, mosque, aquarium,
            case R.id.B_museum:
                mMap.clear(); //clear the map if there are any markers present
                String museum = "museum";
                String url = getUrl(latitude, longitude, museum);
                //create object array, it will store two objects so the first object will be mMap and
                //second is URL
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainMap.this, "Showing Nearby Museum", Toast.LENGTH_SHORT).show();
                break;


            case R.id.B_hotel:
                mMap.clear();
                String hotel = "hotel";
                url = getUrl(latitude, longitude, hotel);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainMap.this, "Showing Nearby Hotels", Toast.LENGTH_SHORT).show();
                break;
            case R.id.B_cafe:
                mMap.clear();
                String cafe = "cafe";
                url = getUrl(latitude, longitude, cafe);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainMap.this, "Showing Nearby Cafe", Toast.LENGTH_SHORT).show();
                break;
            case R.id.B_bank:
                mMap.clear();
                String bank = "bank";
                url = getUrl(latitude, longitude, bank);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainMap.this, "Showing Nearby Bank", Toast.LENGTH_SHORT).show();
                break;
            case R.id.B_mosque:
                mMap.clear();
                String mosque = "mosque";
                url = getUrl(latitude, longitude, mosque);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);
                Toast.makeText(MainMap.this, "Showing Nearby Mosque", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        //add all the paramaters
        googlePlaceUrl.append("location="+latitude+","+longitude); //cannot have space inside bracket
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        //googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE"); //API key for places
        googlePlaceUrl.append("&key="+"AIzaSyDk34nj6G8yQga6OLKNDThRn7-zJrNxsoI");
        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            //use FusedLocationApi to get current location of the user
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        //if permission not granted, then
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            { //this return true if the apps had required permission previously and the user denied the request

                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false; //return false if the user chosen dont ask again option

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onMarkerClick(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {
        mLat = marker.getPosition().getLatitude();
        mLong = marker.getPosition().getLongitude();
        Log.d("abc123", ""+mLat+","+mLong);
        return false;
    }
}
