package com.fourcast.a4cast.Web;

import com.fourcast.a4cast.Utils.WebUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class CurrentWeather {
    private String name;
    private Sys sys;

    private Main main;

    private Wind wind;

    private List<Weather> weather;

    @SerializedName("dt")
    private long dateNum;


    public String getLocation(){
        return name.toUpperCase() + " - " + sys.country;
    }

    public String getDescription(){
        return WebUtils.capitalizeFirstWord(weather.get(0).description);
    }

    public String getHumidity(){
        return String.valueOf(main.humidity);
    }

    public String getTemp(){
        return String.valueOf(main.temp);
    }

    public String getSpeed(){
        return String.valueOf(wind.speed);
    }

    public Date getDate(){
        return new Date(dateNum * 1000);
    }

    public long getSunrise(){
        return sys.sunrise;
    }

    public long getSunset(){
        return sys.sunset;
    }

}

 class Sys{
    String country;
    long sunrise;
    long sunset;
}

class Main{
    String temp;
    int humidity;
}

class Wind{
    float speed;
}

class Weather{
    String description;
}

