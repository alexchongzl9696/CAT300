package com.example.chinwailun.cat300;


import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//the flow for the first time launching =>
// map view is loaded so we call enable location (onMapReady)
// but we not granted permission yet so we request permission (enableLocation de "else")
//then permission is granted we go back to enableLocation "if"(onPermissionResult)

//i implement some interface called 'onmapreadycallback' which is the interface that allows us to know
//what and the map view has finished initializing and is ready to go
public class Navigation extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener,
        PermissionsListener, MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap map;
    private Button startButton;
    private PermissionsManager permissionsManager; //make requesting permission
    private LocationEngine locationEngine; //get access to the location GPS stuff
    private LocationLayerPlugin locationLayerPlugin; //to show the nice UI for us knowing where the location is, provide location awareness to our mobile application, show an icon representing the users current location
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private NavigationMapRoute navigationMapRoute; //a class that provides the runtime styling for the selected route that we see drawn on the map
    private static final String TAG="Navigation"; //tag for some logs later on


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //give maxbox the info that it needs being the application context and our access token
        Mapbox.getInstance(this,getString(R.string.access_token));
        setContentView(R.layout.navigation);
        mapView = findViewById(R.id.mapView);
        startButton=findViewById(R.id.startButton);

        mapView.onCreate(savedInstanceState); //map view contains its own lifecycle methods for mananging android open gl lifecycle which must be called directly from the containing activity
        //so in order to correctly call the map views lifecycle methods we need to override the activity lifecycle methods and add the map views corresponding lifecycle method here

        mapView.getMapAsync(this); //set callback for the onMapReady


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavigationLauncherOptions options  = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(Navigation.this,options);
            }
        });

    }

    //part of onMapReadyCallback
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
//initialize our map, we have our MapboxMap up there which is returned to us when the on map ready, callback is called then we get the mapbox map here
        map = mapboxMap;
        map.addOnMapClickListener(this);
        enableLocation(); //call from our onMapReady to enableLocation
    }

    //handle permission
    private void enableLocation(){
//determine whether or not the user has granted permission for location
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            initializeLocationLayer();

        }
        else {
            //if we have not granted permissions, then we going to request it, so we initialize our permission manager
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    @SuppressWarnings("MissingPermission")//use this so that error go away (need get permission for "locationEngine.getLastLocation()")
    private void initializeLocationEngine(){
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        //this is a feature which if there was a last location then we can get access to it
        Location lastLocation = locationEngine.getLastLocation();
        if(lastLocation!=null){
            //if there actually was one location, set that to our origin location
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        }
        else {    //if last location din exist
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer(){

        //need all these three so that be able to display the users a location on our map view
        locationLayerPlugin = new LocationLayerPlugin(mapView,map,locationEngine);

        //this is a method that will show or hide the location icon and enable or disable
        // the camera tracking the location  so it accept a boolan true or false so if you wan to be do all of that then u just pass in true and
        //and then we need to supress the permissions
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    //make sure our camera move to current location
    private void setCameraPosition(Location location){

        //this function call set camera position passing to it a location and then call animate camera to zoom to that new location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()),13.0));
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if(destinationMarker!=null){
            map.removeMarker(destinationMarker);
        }

        //whenever we tap we are getting the point from that location we are getting its latitude and longitude
        destinationMarker=map.addMarker(new MarkerOptions().position(point));
        destinationPosition= Point.fromLngLat(point.getLongitude(),point.getLatitude());
        originPosition=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());


        getRoute(originPosition,destinationPosition);
        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.color.mapboxBlue);
    }

    //function to get route
    private void getRoute(Point origin, Point destination){
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body()==null){
                            Log.e(TAG,"No routes found, check right user and access token");
                            return;
                        }else if (response.body().routes().size()==0){
                            Log.e(TAG,"No routes found");
                            return;
                        }

                        //have at least one route
                        DirectionsRoute currentRoute= response.body().routes().get(0);

                        if (navigationMapRoute!=null){
                            navigationMapRoute.removeRoute();
                        }else {
                            navigationMapRoute=new NavigationMapRoute(null,mapView,map);
                        }
                        navigationMapRoute.addRoute(currentRoute);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG,"Error:"+t.getMessage());
                    }
                });


    }



    //part of locationEngineListener
    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        //when we connect we wan to have the provider begin sending updates so we need to request the location updates
        locationEngine.requestLocationUpdates();

    }

    //part of locationEngineListener
    @Override
    public void onLocationChanged(Location location) {
        //whenever the location changes we wan to change our origin location and change our camera to new location
        if (location!=null)
        {
            originLocation=location;
            setCameraPosition(location);
        }

    }

    //part of locationListener
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
//this is when the user denies access the first time and then the second time that we request it
        //present a toast or a dialog to explain why they need to grant access
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }

    }

    //part of locationListener
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }



    /************************************************************************************/
    //below are the activity lifecycle in which mapview will access it

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if(locationEngine!=null){
            locationEngine.requestLocationUpdates();
        }
        if(locationLayerPlugin!=null)
        {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //make sure we dont have any memory leak
        if(locationEngine!=null)
        {
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin!=null)
        {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null)
        {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }
}
