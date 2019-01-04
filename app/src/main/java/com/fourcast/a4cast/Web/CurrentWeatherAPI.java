package com.fourcast.a4cast.Web;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.fourcast.a4cast.WeatherSnapshot;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;

import com.fourcast.a4cast.Utils.WebUtils;

/**
 * AsyncTask class that consults the openWeatherMap API to get weatherDetails for today
 */
public class CurrentWeatherAPI extends AsyncTask<String, Void, JSONObject> {

    private AsyncWeatherResponse response = null;//response to be implemented in the activity

    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&appid=%s"; //URL for the api

    private static final String API_KEY = "39324192e0e8d3f0aa6f9d1cb03331db"; //Specific key to enable access to the api

    private Context context; //The application context

    /**
     * Constructor for the API
     * @param response The asyncResponse that is implemented in the activity to enable interaction with the activity's fields
     * @param context the application context
     */
    public CurrentWeatherAPI(AsyncWeatherResponse response, Context context) {
        this.response = response;
        this.context = context;
    }




    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            //Attempts to obtain today's forecast from the API, in a JSON format
            return WebUtils.getWeatherJSON(params[0], params[1],WEATHER_URL,API_KEY);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    protected void onPostExecute(JSONObject json) {
        try {
            if(json != null){
                //Case that json has been returned- can iterate over the details
                JSONObject weatherSummary = json.getJSONArray("weather").getJSONObject(0);
                JSONObject mainDetails = json.getJSONObject("main");
                JSONObject windDetails = json.getJSONObject("wind");

                //Constructs a WeatherSnapshot object with the information from the json object
                WeatherSnapshot currentWeather = new WeatherSnapshot(
                        1,
                        json.getString("name").toUpperCase() + "-" + json.getJSONObject("sys").getString("country"),
                        WebUtils.capitalizeFirstWord(weatherSummary.getString("description")),
                        mainDetails.getString("temp"),
                        mainDetails.getString("humidity"),
                        windDetails.getString("speed"),
                        new Date(json.getLong("dt")*1000),
                        json.getJSONObject("sys").getLong("sunrise") * 1000,
                        json.getJSONObject("sys").getLong("sunset") * 1000
                );

                //Adds the object to an arrayList so it can be processes by the AsyncWeatherResponse
                ArrayList<WeatherSnapshot> list = new ArrayList<>();
                list.add(currentWeather);

                response.processFinish(list); //Completes the method with the interface implemented in the activity

            }
            //In the following 2 cases some error has occurred
            else{
                Toast.makeText(context, "Error obtaining data: try again later", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(context, "Error obtaining data: try again later", Toast.LENGTH_LONG).show();
        }
    }
}
