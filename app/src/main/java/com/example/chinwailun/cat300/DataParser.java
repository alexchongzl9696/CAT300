package com.example.chinwailun.cat300;

import java.util.HashMap;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

//when we create data parser I am going to call the parse method and
//it will parse the JSON data then
//it will send it to "getPlaces" method then in the "getPlaces" method it will take the JSOn array then the
//"getPLaces" method will call "getPlace" method using the for loop and
//it will fetch each element from the JSON array for each place and then it will store it in placesList and then
//it will return it to the parse method then the whole things get returned to the origin

public class DataParser {

    //this method is to get places          //it going to take JSONobject googlePlaceJson
    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        //store all the parameters using string
        String placeName = "--NA--";
        String vicinity= "--NA--";
        String latitude= "";
        String longitude="";
        String reference="";

        Log.d("DataParser","jsonobject ="+googlePlaceJson.toString());

        try {
            if (!googlePlaceJson.isNull("name")) { //if not null then
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            //after feteching all the data(above statements), now (statements below) we put it in google places map
            // that is the hash map we just created

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap; //after everything, return this hash map

    }

    //this going to return a list of hashmap
    //basically to store one place I am using hashmap
    //to store all the places all the nearby places that I find, I need a list of hashmap
    private List<HashMap<String, String>>getPlaces(JSONArray jsonArray)
    {
        //store the number of elements in the JSON array
        int count = jsonArray.length();

        // so "getPlace' will return a hash map for each place
        //and "getPlaces" will all the hashmaps in a list
        List<HashMap<String, String>> placelist = new ArrayList<>();
        HashMap<String, String> placeMap = null;//create a hashmap to store each place that we fecth and then we import it in the list

        for(int i = 0; i<count;i++)
        {
            //use getplace method to fetch one place then add it to the list of hashmap that is a places list
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placelist;
    }

    //return a list of hashmap parse

    //when we create data parser we are going to call this parse method and
    //it will parse the JSON data then
    //it will send it to "getPlaces" method then in the "getPlaces" method it will take the JSOn array then the
    //"getPLaces" method will call "getPlace" method using the for loop and
    //it will fetch each element from the JSON array for each place and then it will store it in placesList and then
    //it will return it to the parse method then the whole things get returned to the origin
    public List<HashMap<String, String>> parse(String jsonData)
    {
        //create two objects
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        Log.d("json data", jsonData);

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
}
