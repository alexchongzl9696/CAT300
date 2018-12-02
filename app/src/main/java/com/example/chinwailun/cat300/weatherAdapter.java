package com.example.chinwailun.cat300;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// here is an adapter used to put in recycleView
// Glide adalah a image loading library for Android focused on smooth scrolling.

public class weatherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    ArrayList<DateTimeEntry> data; //DateTimeEntry is a class

    public weatherAdapter(Context context, ArrayList<DateTimeEntry> data)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.activity_weather_adapter, parent, false);
        WeatherItemHolder holder = new WeatherItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        //here fet the current position for the item in recycleView, then bind data and give values from list
        WeatherItemHolder weatherItemHolder = (WeatherItemHolder) holder;

        // Get the data
        DateTimeEntry current = data.get(position);

        // If the date is today, make the background color
        if ( DatesHelper.isToday(current.date))
        {
            weatherItemHolder.weatherLayout.setBackground(context.getResources().getDrawable(R.drawable.todays_weather));
        }
        else
        {
            weatherItemHolder.weatherLayout.setBackground(context.getResources().getDrawable(R.drawable.fivedays_weather));
        }

        // get the date in Date format
        Date date = DatesHelper.getDate(current.date);

        // change the date to the format , no seconds
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM d        hh:mm a");
        String displayDate = format.format(date);

        // store values in the holder
        weatherItemHolder.textDateTime.setText(displayDate);
        weatherItemHolder.textMain.setText(current.mainHeadline);
        weatherItemHolder.textDescription.setText(current.description);
        // weatherItemHolder.textTemp.setText(current.temp);

        //use glide to put the weather image that made from the icon
        // Glide is a fast and efficient image loading library for Android focused on smooth scrolling.
        Glide.with(context).load("http://openweathermap.org/img/w/" + current.icon + ".png")
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(weatherItemHolder.imageIcon);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    //here create a view holder for all weather info
    class WeatherItemHolder extends RecyclerView.ViewHolder
    {
        LinearLayout weatherLayout;
        TextView textDateTime;
        TextView textDescription;
        ImageView imageIcon;
        TextView textMain;
        // TextView textTemp;

        public WeatherItemHolder(View itemView)
        {
            super(itemView);

            weatherLayout = (LinearLayout) itemView.findViewById(R.id.weatherLayout);
            textDateTime = (TextView) itemView.findViewById(R.id.textViewDateTime);
            //   textTemp=(TextView)itemView.findViewById(R.id.textViewTemp);
            textMain = (TextView) itemView.findViewById(R.id.textViewWeather);
            textDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            imageIcon = (ImageView) itemView.findViewById(R.id.imageWeather);
        }
    }

}




