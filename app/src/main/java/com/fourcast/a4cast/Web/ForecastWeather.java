package com.fourcast.a4cast.Web;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastWeather {
    @SerializedName("list")
    private List<CurrentWeather> forecast;

    public List<CurrentWeather> getForecast(){
        return forecast;
    }
}

