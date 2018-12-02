package com.example.chinwailun.cat300;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class currentWeather extends Fragment {

    TextView tempTextView;
    TextView dateTextView;
    TextView weatherDescTextView;
    TextView cityTextView;
    ImageView weatherImageView;
    TextView humidityTextView;
    TextView pressureTextView;
    // TextView minTextView;
    // TextView maxTextView;

    public currentWeather()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_current_weather, container, false);


        tempTextView = view.findViewById(R.id.tempTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        weatherDescTextView = view.findViewById(R.id.weatherDescTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        humidityTextView = view.findViewById(R.id.humidityTextView);
        pressureTextView = view.findViewById(R.id.pressureTextView);
        //minTextView = view.findViewById(R.id.minTextView);
        //maxTextView = view.findViewById(R.id.maxTextView);

        weatherImageView = view.findViewById(R.id.weatherImageView);


        dateTextView.setText(getCurrentDate()); //call function to get the date

        String url = "http://api.openweathermap.org/data/2.5/weather?q=Penang,malaysia&appid=9eb962a0f991ef83ac25ac500d1db716&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject) { //the response here will give u everything like weather, base, main, visibility wind, clouds...

                        // **tempTextView.setText("Response: " + response.toString());** =>
                        //this line will output "Response:{"coord".....blabla" too big

                        Log.v("WEATHER", "Response: " + responseObject.toString());


                        //get info from response, but it will show u "coord":{"lon":-122.09,"lat":37.39},"weather": ........."
                        //but we need only the main part
                        try {
                            JSONObject mainJSONObject = responseObject.getJSONObject("main");
                            //inside main ada temp, pressure, humidity blabla,we need only temp
                            String temp = Integer.toString((int) Math.round(mainJSONObject.getDouble("temp")));
                            //got the value as double value decimal , then if it is 5 or more then add 1 into the value,
                            //then convert to integer
                            tempTextView.setText(temp);


                            JSONArray weatherArray = responseObject.getJSONArray("weather");
                            JSONObject firstWeatherObject = weatherArray.getJSONObject(0);
                            String weatherDescription = firstWeatherObject.getString("description");
                            String city = responseObject.getString("name");

                            String humidity = Integer.toString((int) Math.round(mainJSONObject.getDouble("humidity")));
                            String pressure = Integer.toString((int) Math.round(mainJSONObject.getDouble("pressure")));
                            // double min = mainJSONObject.getDouble("temp_min");
                            //double max = mainJSONObject.getDouble("temp_max");


                            weatherDescTextView.setText(weatherDescription);
                            cityTextView.setText(city);
                            humidityTextView.setText(humidity + " %");

                            pressureTextView.setText(pressure + " hPa");
                            //minTextView.setText(min + " °C");

                            // maxTextView.setText(max + " °C");

                            //update weather image accordingly
                            // int iconResourceId = getResources().getIdentifier
                            //         ("icon_" + weatherDescription.replace(" ", ""), "drawable", getActivity().getPackageName());

                            // weatherImageView.setImageResource(iconResourceId);

                            if(weatherDescription.equals("thunderstorm with light rain")||weatherDescription.equals("thunderstorm with rain")||weatherDescription.equals("thunderstorm with heavy rain")||weatherDescription.equals("light thunderstorm")||weatherDescription.equals("thunderstorm")||weatherDescription.equals("heavy thunderstorm")||weatherDescription.equals("ragged thunderstorm")||weatherDescription.equals("thunderstorm with light drizzle")||weatherDescription.equals("thunderstorm with drizzle")||weatherDescription.equals("thunderstorm with heavy drizzle"))
                            {
                                weatherImageView.setImageResource(R.drawable.icon_thunderstorm);
                            }
                            else if (weatherDescription.equals("light intensity drizzle")||weatherDescription.equals("drizzle")||weatherDescription.equals("heavy intensity drizzle")||weatherDescription.equals("light intensity drizzle rain")||weatherDescription.equals("drizzle rain")||weatherDescription.equals("heavy intensity drizzle rain")||weatherDescription.equals("shower rain and drizzle")||weatherDescription.equals("heavy shower rain and drizzle")||weatherDescription.equals("shower drizzle"))
                            {
                                weatherImageView.setImageResource(R.drawable.icon_rain);
                            }
                            else if (weatherDescription.equals("clear sky"))
                                weatherImageView.setImageResource(R.drawable.icon_clearsky);
                            else if(weatherDescription.equals("few clouds")||weatherDescription.equals("scattered clouds")||weatherDescription.equals("broken clouds")||weatherDescription.equals("overcast clouds"))
                            {
                                weatherImageView.setImageResource(R.drawable.icon_brokenclouds);
                            }
                            else
                                weatherImageView.setImageResource(R.drawable.icon_rain);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(jsonObjectRequest);

        return view;
    }

    //get the date
    private String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d");
        String formattedDate = dateFormat.format(calendar.getTime());

        return formattedDate;
    }
}
