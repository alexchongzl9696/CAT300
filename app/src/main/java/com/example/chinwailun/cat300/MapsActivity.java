package com.example.chinwailun.cat300;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    Date date;
    int numOfDoc = 0;
    final Handler handler = new Handler();
    private GoogleMap mMap;
    private LocationRequest locReq;
    private LocationCallback locCallback;
    private Location currLocation;
    private FusedLocationProviderClient client;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Integer> rDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setClickable(true);

        Intent i = getIntent();
        rDate = i.getIntegerArrayListExtra(DateTime.EXTRA_DATE);

        client = LocationServices.getFusedLocationProviderClient(this);

        locReq = new LocationRequest();
        locReq.setInterval(5000);
        locReq.setFastestInterval(2000);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locCallback = new LocationCallback() {
            boolean firstRun = true;

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                currLocation = locationResult.getLastLocation();
                float zoomLevel = 15.0f;
                LatLng sydney = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                Log.d("hahaha1", ""+currLocation.getLatitude());
                //mMap.addMarker(new MarkerOptions().position(sydney).title("You are HERE"));
                if(firstRun == true)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
                    firstRun = false;
                }
            }
        };
        if ((ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        client.requestLocationUpdates(locReq, locCallback, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("hahaha2", "asdasd");
        processData();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    public void processData()
    {
        int numDoc = 0;
        Calendar calTerminate = Calendar.getInstance();
        Calendar calGiven = Calendar.getInstance();
        calTerminate.set(rDate.get(0), rDate.get(1), rDate.get(2), 0, 0, 0);
        calTerminate.add(Calendar.DAY_OF_MONTH, 1);
        calGiven.set(rDate.get(0), rDate.get(1), rDate.get(2), rDate.get(3), rDate.get(4), 0);
        calGiven.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        ArrayList<DocumentSnapshot> docList = new ArrayList<>();

        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int numOfUser = task.getResult().size();
                    for (QueryDocumentSnapshot col : task.getResult()) {
                        numOfUser--;
                        final int userNum = numOfUser;
                        final String userDoc = col.getId();
                        Log.d("hahaha3", col.getId());
                        db.collection("Users").document(userDoc).collection("Schedule").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int numOfDoc = task.getResult().size();
                                    for (DocumentSnapshot place : task.getResult()) {
                                        docList.add(place);
                                        numOfDoc--;

                                        if (numOfDoc == 0 && userNum == 0)
                                        {
                                            filterDoc(docList);
                                            return;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    public void filterDoc(ArrayList<DocumentSnapshot> docList)
    {
        int i = 0;
        ArrayList<DocumentSnapshot> docList2 = docList;
        Calendar calTerminate = Calendar.getInstance();
        Calendar calGiven = Calendar.getInstance();
        calTerminate.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        calGiven.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        calTerminate.set(rDate.get(0), rDate.get(1), rDate.get(2), 0, 0, 0);
        calTerminate.add(Calendar.DAY_OF_MONTH, 1);
        calGiven.set(rDate.get(0), rDate.get(1), rDate.get(2), rDate.get(3), rDate.get(4), 0);

        while(calGiven.before(calTerminate)) {
            i++;
            Date givenDate = calGiven.getTime(); // get dynamic calendar
            Log.d("hahaha4", givenDate.toString());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int retValue;
                    retValue = plotMap(docList, givenDate);
                    if(retValue == 0)
                        return;
                }
            },i*3000);

            //Toast.makeText(this, givenDate.toString(), Toast.LENGTH_LONG).show();
            calGiven.add(Calendar.HOUR_OF_DAY, 1); // add one hour to dynamic calendar
        }
    }

    public int plotMap(ArrayList<DocumentSnapshot> docList, Date givenDate)
    {
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c.setTime(givenDate);
        c2.setTime(givenDate);
        c2.add(Calendar.HOUR, 1);
        c.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        c2.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        mMap.clear();
        Toast.makeText(MapsActivity.this, "From : " + c.getTime().toString() + "\nTo      : " + c2.getTime().toString(), Toast.LENGTH_LONG).show();
        for(int i = 0; i < docList.size(); i++)
        {
            if(docList.get(i).getTimestamp("Start").toDate().before(givenDate)
                    && docList.get(i).getTimestamp("End").toDate().after(givenDate))
            {
                GeoPoint coordinate = docList.get(i).getGeoPoint("Coordinate");
                LatLng location = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
                mMap.addMarker(new MarkerOptions().position(location));
                docList.remove(i);
                i--;
            }
        }
        return docList.size();
    }
}
