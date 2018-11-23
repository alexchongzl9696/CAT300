package com.example.chinwailun.locationtest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chinwailun.cat300.MapsActivity;
import com.example.chinwailun.cat300.R;

import java.util.ArrayList;
import java.util.Calendar;

public class DateTime extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TextView mSetDate;
    private TextView mDisplayDate;
    private TextView mInterval;
    private TextView mHour;
    private Spinner mSpinner;
    private Button mButton;

    public void dudfunction123123(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        mDisplayDate = (TextView) findViewById(R.id.displayDate);
        mSetDate = (TextView) findViewById(R.id.setDate);
        mInterval = (TextView) findViewById(R.id.interval);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mButton = (Button) findViewById(R.id.goButton);

        String[] intervalNum = {"1","2","3","4","5"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(DateTime.this, R.layout.support_simple_spinner_dropdown_item, intervalNum);
        langAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(langAdapter);

        mSetDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dDialog = new DatePickerDialog(DateTime.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dDialog.show();
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        mSpinner.setSelection(position);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month += 1;
                String date = dayOfMonth + "/" +  month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> data = new ArrayList<>();
                data.add(mSpinner.getSelectedItem().toString());
                data.add((String) mDisplayDate.getText());
                //Toast.makeText(DateTime.this, (String) mDisplayDate.getText() + " ------- " + mSpinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                Intent mI = new Intent(DateTime.this,MapsActivity.class);
                startActivity(mI);
            }
        });
    }
}
