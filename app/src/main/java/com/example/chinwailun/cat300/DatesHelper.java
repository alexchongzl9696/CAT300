package com.example.chinwailun.cat300;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//basically here we get a string weather format , return a date here, later we can modify it
public class DatesHelper
{
    public static Date getDate(String dt)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(dt);
            return date;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    //check if the date is today punya date, if yes then return true. later i wan to color the today weather info
    public static boolean isToday(String dt)
    {
        try
        {
            // get date from API
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date weatherdate = format.parse(dt);

            // no time for today
            Date todayWithNoTime = format.parse(format.format(new Date()));

            // return true if equal
            return weatherdate.compareTo(todayWithNoTime) == 0;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return false;
    }
}
