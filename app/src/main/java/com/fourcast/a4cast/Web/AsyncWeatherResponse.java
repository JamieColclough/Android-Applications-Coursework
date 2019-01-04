package com.fourcast.a4cast.Web;

import com.fourcast.a4cast.WeatherSnapshot;

import java.util.ArrayList;

/**
 * Interface for the Web APIs in order to interact with the data inside the main activity
 */
public interface AsyncWeatherResponse {

    /**
     * Abstract method that will interact with the data returned by the Web APIs
     * Will be implemented by the APIs inside the activity in order to access the fields inside the activity
     * @param currentWeatherData the weatherData returned by the Web APIs
     */
    void processFinish(ArrayList<WeatherSnapshot> currentWeatherData);
}
