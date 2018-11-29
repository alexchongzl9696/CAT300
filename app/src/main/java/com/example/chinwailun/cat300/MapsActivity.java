package com.example.chinwailun.cat300;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String receiveDate;
    Date date;
    private GoogleMap mMap;
    private LocationRequest locReq;
    private LocationCallback locCallback;
    private Location currLocation;
    private FusedLocationProviderClient client;
    private Timestamp time;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Integer> rDate;
    //ArrayList<String> rData = getIntent().getStringArrayListExtra("date");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        //receiveDate = i.getStringExtra(DateTime.EXTRA_DATE);
        rDate = i.getIntegerArrayListExtra(DateTime.EXTRA_DATE);
        boolean firstRun = true;

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
        processData();
        //testfunc();
    }

    public void testfunc()
    {
        Date calDate;
        Calendar cal = Calendar.getInstance();
        cal.set(2018, 10, 30, 12, 0);
        //cal.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        ;
        calDate = cal.getTime();

        DocumentReference dr = db.collection("Users").document("Alex Chong").collection("Schedule").document("Pizza Hut");
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("log1", ""+ task.getResult().getTimestamp("Start").toDate().getTime());
                Log.d("log2", ""+ calDate.getTime());
            }
        });
    }


    public void processData()
    {
        ArrayList<DocumentSnapshot> docList = new ArrayList<>();
        ArrayList<GeoPoint> geoList = new ArrayList<GeoPoint>();
        Calendar calTerminate = Calendar.getInstance();
        Calendar calGiven = Calendar.getInstance();
        calTerminate.set(rDate.get(0), rDate.get(1), rDate.get(2), 0, 0, 0);
        calTerminate.add(Calendar.DAY_OF_MONTH, 1);
        calGiven.set(rDate.get(0), rDate.get(1), rDate.get(2), rDate.get(3), rDate.get(4), 0);
        calGiven.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

        Handler handler = new Handler();
        while(calGiven.before(calTerminate))
        {
            calGiven.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            Date givenDate = calGiven.getTime(); // get dynamic calendar
            Timestamp timestampDynamic = new Timestamp(givenDate);
            timestampDynamic.toDate().getTime();

            db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot col : task.getResult())
                        {
                            final String userDoc = col.getId();
                            db.collection("Users").document(userDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        db.collection("Users").document(userDoc).collection("Schedule").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(DocumentSnapshot place : task.getResult())
                                                    {
                                                        if(place.getTimestamp("Start").toDate().before(givenDate) &&
                                                                place.getTimestamp("End").toDate().after(givenDate))
                                                        {
                                                            //docList.add(place);
                                                            GeoPoint coordinate = place.getGeoPoint("Coordinate");
                                                            LatLng location = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
                                                            mMap.addMarker(new MarkerOptions().position(location));
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            });

            //mMap.clear();
            /*for(int i = 0; i < docList.size(); i++)
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
            }*/

            calGiven.add(Calendar.HOUR_OF_DAY, 1); // add one hour to dynamic calendar
        }
            //Toast.makeText(this, givenDate.toString(), Toast.LENGTH_LONG).show();
    }
}
        //Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
//}

/*while(c2.before(cFix))
        {   // have to add handler somewhere here to handle delayed order
            mMap.clear();
            db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot col : task.getResult())
                        {
                            final String userDoc = col.getId();
                            db.collection("Users").document(userDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {
                                        db.collection("Users").document(userDoc).collection("Schedule").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot place : task.getResult())
                                                    {
                                                        final String placeDoc = place.getId();
                                                        db.collection("Users").document(userDoc).collection("Schedule").document(placeDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    DocumentReference ref = db.collection("Users").document(userDoc).collection("Schedule").document(placeDoc);
                                                                    ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            time = ((Timestamp)task.getResult().getData().get("Date & Time"));
                                                                            if((time.toDate()).equals(c2) && (time.toDate()).before(cFix.getTime())
                                                                                    || (time.toDate()).after(c2.getTime()) && (time.toDate()).before(cFix.getTime()))
                                                                            {
                                                                                GeoPoint coordinate = null;
                                                                                coordinate = (GeoPoint) task.getResult().getData().get("Coordinate");
                                                                                LatLng sydney = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
                                                                                mMap.addMarker(new MarkerOptions().position(sydney));
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            });
            c2.add(Calendar.HOUR, 1);
        }*/