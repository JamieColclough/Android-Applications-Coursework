package com.fourcast.a4cast.Web;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi{

    @GET("weather")
    Call<CurrentWeather> getCurrentWeather(@Query("lat")String lat, @Query("lon")String lon);

    @GET("forecast")
    Call<ForecastWeather> getForecastWeather(@Query("lat")String lat, @Query("lon")String lon);
}
