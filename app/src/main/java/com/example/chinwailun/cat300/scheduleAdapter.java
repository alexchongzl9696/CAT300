package com.example.chinwailun.cat300;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class scheduleAdapter extends ArrayAdapter<Item> {
    String name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static class ViewHolder {
        TextView placeName;
        TextView Time;
        Button delete;
    }

    public scheduleAdapter(Context context,ArrayList<Item>items){super(context, 0, items);}
    @NonNull
    public View getView(int position,@Nullable View convertView, @NonNull ViewGroup parent){
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.travel_item, parent, false);
        }
        Item currentItem = getItem(position);
        ViewHolder holder = new ViewHolder();

        Log.d("position mou", Integer.toString(position));

        holder.placeName = listItemView.findViewById(R.id.placeName);
        holder.placeName.setText(currentItem.getPlaceName());
        Log.d("ai ni mou", currentItem.getPlaceName());
        Log.d("666 mou", holder.placeName.getText().toString());

        holder.Time = listItemView.findViewById(R.id.Time);
        holder.Time.setText(currentItem.getStart());

        holder.delete = listItemView.findViewById(R.id.btnDelete);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LMAO K", holder.placeName.toString());
                db.collection("Users").document(TravelSchedule.userID).collection("Schedule").document(holder.placeName.getText().toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Operation done, please restart this page", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return listItemView;
    }
}
