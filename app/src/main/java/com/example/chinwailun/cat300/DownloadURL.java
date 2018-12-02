package com.example.chinwailun.cat300;


import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//data returned from web will be in JSON format and I can get this data using HTTP URL connection

//retrieve data from URL using HTTP URL connection and file handling methods
public class DownloadURL {

    //read the URL
    public String readUrl(String myUrl) throws IOException
    {
        String data = "";
        InputStream inputStream = null; //file handling method to read the URL
        HttpURLConnection urlConnection = null; //create object

        try {

            //below 3 lines : create the URL, then open the connection, then connect it
            URL url = new URL(myUrl);
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.connect(); //connect it


            //read the data from URL
            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            //read each line one by one usng while loop
            String line = ""; //will store each line and later append it to the string buffer
            while((line = br.readLine()) != null) //check if it is null or not
            {   //if not null then we are appending it to the string buffer
                sb.append(line);
            }

            //convert the string buffer to string and store in data variable
            data = sb.toString();
            br.close(); //close the buffer reader because we have read all the data that we need

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //if there is something that I need to execute even if there is an exception , then use finally block la
        finally {
            //whatever we add in the finally block will always get executed even if there is an exception in try block
            inputStream.close();
            urlConnection.disconnect();
        }
        Log.d("DownloadURL","Returning data= "+data);

        return data;
    }
}
