package com.fourcast.a4cast.Web;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.fourcast.a4cast.WeatherSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import com.fourcast.a4cast.Utils.WebUtils;

/**
 * AsyncTask class that consults the openWeatherMap API to get weatherDetails for the next 4 days
 */
public class ForecastWeatherAPI extends AsyncTask<String, Void, JSONObject> {

    public AsyncWeatherResponse response = null;//Call back interface

    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&appid=%s";//url for forecast call

    private static final String API_KEY = "39324192e0e8d3f0aa6f9d1cb03331db";//User key

    private Context context; //The application context

    /**
     * Constructor for the API
     * @param response The asyncResponse that is implemented in the activity to enable interaction with the activity's fields
     * @param context the application context
     */
    public ForecastWeatherAPI(AsyncWeatherResponse response,Context context) {
        this.response = response;
        this.context = context;
    }



    @Override
    protected JSONObject doInBackground(String... params) {
        try {
             return WebUtils.getWeatherJSON(params[0], params[1],WEATHER_URL,API_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        try {
            if(json != null){
                //Obtains the location string
                String location = json.getJSONObject("city").getString("name").toUpperCase() + "-" + json.getJSONObject("city").getString("country");

                //Gets the list of all the weather entries
                JSONArray weatherEntries = json.getJSONArray("list");
                ArrayList<WeatherSnapshot> allWeatherData = new ArrayList<>();
                for(int i=0; i< 5; i++) {
                    JSONObject weatherEntry = weatherEntries.getJSONObject(i*8);//Finds a specific weather entry for each day, 8 indexes = 24 hours

                    JSONObject mainDetails = weatherEntry.getJSONObject("main");
                    JSONObject weatherDetails = weatherEntry.getJSONArray("weather").getJSONObject(0);
                    JSONObject windDetails = weatherEntry.getJSONObject("wind");

                    //Constructs a weather snapshot for this particular day
                    WeatherSnapshot currentWeather = new WeatherSnapshot(
                            i+1,//Establishes what day it is
                            location,
                            WebUtils.capitalizeFirstWord(weatherDetails.getString("description")),
                            mainDetails.getString("temp"),
                            mainDetails.getString("humidity"),
                            windDetails.getString("speed"),
                            new Date(weatherEntry.getLong("dt") * 1000),
                            0,
                            0
                    );

                    allWeatherData.add(currentWeather);//Adds this day to the forecase
                }


                response.processFinish(allWeatherData);//Finishes computation inside the main activity

            }
            //In the following 2 cases some error has occurred
            else{
                Toast.makeText(context, "Error obtaining data: try again later", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error obtaining data: try again later", Toast.LENGTH_LONG).show();

        }
    }


}
