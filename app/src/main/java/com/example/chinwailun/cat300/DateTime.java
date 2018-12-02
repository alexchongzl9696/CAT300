package com.example.chinwailun.cat300;

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
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateTime extends AppCompatActivity {
    public static final String EXTRA_DATE = "com.example.chinwailun.cat300.EXTRA_TEXT";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TextView mSetDate;
    private TextView mSetTime;
    private TextView mDisplayDate;
    private TextView mDisplayTime;
    private TextView mInterval;
    private TextView mHour;
    private Spinner mSpinner;
    private Button mButton;
    int mdayofmonth, mmonth, myear, mhour, mminute;
    String format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        mDisplayDate = (TextView) findViewById(R.id.displayDate);
        mDisplayTime = (TextView) findViewById(R.id.displayTime);
        mSetDate = (TextView) findViewById(R.id.setDate);
        mSetTime = (TextView) findViewById(R.id.setTime);
        mButton = (Button) findViewById(R.id.goButton);


        mSetTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int hour = 0, minute = 0;
                boolean is24 = true;

                TimePickerDialog tDialog = new TimePickerDialog(DateTime.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mTimeSetListener, hour, minute,is24);
                tDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                tDialog.show();
            }
        });

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

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month += 1;
                String date = dayOfMonth + "-" +  month + "-" + year;
                mdayofmonth = dayOfMonth;
                mmonth = month -1;
                myear = year;
                mDisplayDate.setText(date);
            }
        };

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;

                if(minute <10)
                    time = hourOfDay + ":0" + minute;
                else
                    time = hourOfDay + ":" + minute;
                mhour = hourOfDay;
                mminute = minute;
                mDisplayTime.setText(time);
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> data = new ArrayList<>();

                data.add(myear);
                data.add(mmonth);
                data.add(mdayofmonth);
                data.add(mhour);
                data.add(mminute);

                /*data.add(2018);
                data.add(10);
                data.add(30);
                data.add(12);
                data.add(0);*/

                Intent mI = new Intent(DateTime.this,MapsActivity.class);
                mI.putExtra(EXTRA_DATE, data);
                startActivity(mI);
            }
        });
    }
}
