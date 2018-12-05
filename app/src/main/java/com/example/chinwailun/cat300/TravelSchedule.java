package com.example.chinwailun.cat300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class TravelSchedule extends AppCompatActivity {

    public static String userID;
    public static String userPassword;
    String namePlace;
    String Time;
    String End;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_schedule);

        setAdap();

    }

    public void getIntentValue(){
        Intent g = getIntent();
        if (g.hasExtra("ID")){
            userID = getIntent().getExtras().getString("ID");
        }
        if (g.hasExtra("Password")){
            userPassword = getIntent().getExtras().getString("Password");
        }
    }

    public void setAdap(){
        getIntentValue();
        ArrayList<Item> items = new ArrayList<Item>();
        db.collection("Users").document(userID).collection("Schedule").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int numOfDoc = task.getResult().size();
                for (QueryDocumentSnapshot col : task.getResult()){
                    namePlace = col.get("Name").toString();
                    Log.d("testfb", namePlace);
                    Time = col.getTimestamp("Start").toDate().toString();
                    Log.d("time",Time.toString());
                    End = col.getTimestamp("End").toDate().toString();
                    items.add(new Item(namePlace,Time,End,userID));
                    numOfDoc--;
                }
                Log.d("items mou", Integer.toString(items.size()));
                final scheduleAdapter adapter= new scheduleAdapter(getApplicationContext(),items);
                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);
            }
        });

    }

    public void changeIntent(){
        //Intent back = new Intent(TravelSchedule.this,Main.class);
        finish();
    }
}
