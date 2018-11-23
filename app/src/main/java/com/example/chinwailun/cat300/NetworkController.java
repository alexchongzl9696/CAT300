package com.example.chinwailun.cat300;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//volley request is used here, GET and JSON parse are put here la

public class NetworkController
{


    // get 5 days weather forecast API key
    private static String WEATHER_CALL = "https://api.openweathermap.org/data/2.5/forecast?q=Penang,malaysia&appid=9eb962a0f991ef83ac25ac500d1db716&units=metric";

    private static NetworkController instance;

    private RequestQueue requestQueue;

    private static Context context;


    private NetworkController(Context context)
    {
        NetworkController.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkController getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new NetworkController(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue == null)
        {
            //getApplicationContext means I get the key, if u pass one in, it helps me see got leaking the activity or broadcastReciever or not
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }

    //do GET call to get 5 day forecast
    public void GETWeather(final NetworkListener<ArrayList> successListener, final NetworkListener failureListener)
    {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, WEATHER_CALL, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    // here parse the json response and put into arrayList
                    ArrayList result = parseWeatherObject(response);

                    successListener.onResult(result);
                }
                catch (JSONException e)
                {
                    // If nothing is sent back
                    failureListener.onResult(null);

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                // If nothing is sent back
                failureListener.onResult(null);
            }
        }
        );

        addToRequestQueue(jsObjRequest);
    }

    //create an arraylist of the object using Json response
    private ArrayList parseWeatherObject(JSONObject json)
            throws JSONException
    {
        ArrayList arrayList = new ArrayList();

        //get the list node from the json
        JSONArray list=json.getJSONArray("list");

        // use for loop to get the info of weather
        for(int i=0;i<list.length();i++)
        {
            // Get the dateTime object
            DateTimeEntry dtEntry = new DateTimeEntry();

            // get the date and put in here
            JSONObject dtItem = list.getJSONObject(i);

            // pull out the date and put it in our own data
            dtEntry.date = dtItem.getString("dt_txt");

            // create weather object
            JSONArray weatherArray = dtItem.getJSONArray("weather");
            JSONObject ob = (JSONObject) weatherArray.get(0);

            // get the specific info
            dtEntry.mainHeadline = ob.getString("main");
           // dtEntry.temp = ob.getInt("temp");
            dtEntry.description = ob.getString("description");
            dtEntry.icon = ob.getString("icon");

            arrayList.add(dtEntry);
        }

        return arrayList;
    }
}
