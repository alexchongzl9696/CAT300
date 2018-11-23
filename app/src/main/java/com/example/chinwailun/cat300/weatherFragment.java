package com.example.chinwailun.cat300;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

// Here ada satu fragment containing the recycler view to put in with weather info
public class weatherFragment extends Fragment {

    private RecyclerView recyclerView;

    public weatherFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // get the network handler instance
        NetworkController networkController = NetworkController.getInstance(getActivity().getApplicationContext());

        // inflate the view
        View view = inflater.inflate(R.layout.activity_weather_fragment, container, false);

        // set the tips view
        final TextView tips = (TextView) view.findViewById(R.id.textViewTips);

        // set the recycler view
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewWeather);

        // set the required layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // here I do the GET call to the API to tell me whether success or failure
        networkController.GETWeather(new NetworkListener<ArrayList>()
        {
            @Override
            public void onResult(ArrayList object)
            {
                tips.setText("Tips: Scroll down to view more info!");

                // here use the result set to make an adapter
                weatherAdapter weatherAdapter = new weatherAdapter(getActivity(), object);

                // pass the result set to the recycler view
                recyclerView.setAdapter(weatherAdapter);
            }
        }, new NetworkListener()
        {
            @Override
            public void onResult(Object object)
            {
                //when the API key ada problem, display this
                tips.setText("Oops! Please check your network connection.");
            }
        });

        return view;
    }

}
