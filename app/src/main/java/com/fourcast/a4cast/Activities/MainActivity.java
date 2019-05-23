package com.fourcast.a4cast.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.fourcast.a4cast.Database.DatabaseInteractionAPI;
import com.fourcast.a4cast.Utils.DayCustomiser;
import com.fourcast.a4cast.WeatherSnapshot;
import com.fourcast.a4cast.Web.CurrentWeather;
import com.fourcast.a4cast.EventListeners.DayClickListener;
import com.fourcast.a4cast.Web.ForecastWeather;
import com.fourcast.a4cast.R;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fourcast.a4cast.Utils.GeneralCustomiser;
import com.fourcast.a4cast.Utils.WebUtils;
import com.fourcast.a4cast.Web.WeatherApi;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The main activity class for the application
 */
public class MainActivity extends AppCompatActivity {
    LinearLayout mainView; //The entire activity view
    boolean isDayTime; //Boolean indicating whether it is currently day or night, used as a property as multiple methods reference it
    DatabaseInteractionAPI db; //
    WeatherApi weatherApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Customises action bar
        setSupportActionBar((Toolbar) findViewById(R.id.app_toolbar));
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);//Disables so we can directly edit the text of the bar

        mainView = findViewById(R.id.main_view);

        DayClickListener dayClickListener = new DayClickListener(mainView,getApplicationContext());
        for (int i=2; i<6;i++){ //Iterates through every day that's not the active one
            RelativeLayout day = (RelativeLayout) mainView.getChildAt(i);

            day.setOnClickListener(dayClickListener);//Sets the listeners for clicking on these days
        }

        db = new DatabaseInteractionAPI();//Initialises the db

        //Checks to see if app is online- decides how the data is obtained
        if(WebUtils.currentlyOnline(getApplicationContext())){



            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            HttpUrl url = originalRequest.url().newBuilder()
                                    .addQueryParameter("appId",getString(R.string.api_key))
                                    .addQueryParameter("units", "metric")
                                    .build();

                            Request newRequest = originalRequest.newBuilder()
                                    .url(url)
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            weatherApi = retrofit.create(WeatherApi.class);

            SharedPreferences sp = getSharedPreferences("customisation",Context.MODE_PRIVATE);//Gets customisation shared preferences

            String latitude = sp.getString("latitude","50.7236");//Finds the latitude and longitude of location to get forecast for
            String longitude = sp.getString("longitude","-3.5339");

            currentWeather(latitude, longitude);

            forecastWeather(latitude, longitude);

        }
        else{//Case that device is offline, get's most recent forecast information from the db
            try {
                ArrayList<WeatherSnapshot> forecast = db.getForecast();
                //If nothing in database
                if(forecast.size() == 0){
                    Toast.makeText(getApplicationContext(),"Device is offline and no recent weather data set- Please reconnect your device",Toast.LENGTH_LONG).show();
                }
                else {
                    //Assign values for first day
                    WeatherSnapshot day1 = forecast.get(0);

                    setCurrentDayCustomisation(day1);
                    GeneralCustomiser.setNavBarText(mainView,day1.getLocation(),true);//Sets nav bar text, stating that the device is offline
                    setFutureDayCustomisation(forecast);
                    Toast.makeText(getApplicationContext(), "Device is offline, retrieved the most recent weather data (May not match your desired location)", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {

            }
            db.closeDB();//Database no longer needed so closed

        }

        //If the settings button is clicked, the app redirects to the settings page
        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),	SettingsActivity.class);
                startActivity(intent);
            }
        }
        );

        //If the refresh button is clicked, the app restarts the activity, hence refreshing the page
        findViewById(R.id.refresh_button).setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        recreate();
                    }
                }
        );
    }

    public void currentWeather(String lat, String lon){
        Call<CurrentWeather> call = weatherApi.getCurrentWeather(lat, lon);

        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                CurrentWeather cw = response.body();

                WeatherSnapshot day1 = new WeatherSnapshot(
                        0,
                        cw.getLocation(),
                        cw.getDescription(),
                        cw.getTemp(),
                        cw.getHumidity(),
                        cw.getSpeed(),
                        cw.getDate(),
                        cw.getSunrise(),
                        cw.getSunset()
                );
                setCurrentDayCustomisation(day1);//Sets customisation for today
                GeneralCustomiser.setNavBarText(mainView, day1.getLocation(), false);//Sets the nav bar text
                db.clearTable();
                db.setDay(day1);//Clears the db and replaces it with the data for today

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }


    public void forecastWeather(String lat, String lon){
        Call<ForecastWeather> call = weatherApi.getForecastWeather(lat, lon);

        call.enqueue(new Callback<ForecastWeather>() {
            @Override
            public void onResponse(Call<ForecastWeather> call, Response<ForecastWeather> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                ForecastWeather body = response.body();
                List<CurrentWeather> weatherList = body.getForecast();
                ArrayList<WeatherSnapshot> allWeatherData = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    CurrentWeather weatherEntry = weatherList.get(i * 8);//Finds a specific weather entry for each day, 8 indexes = 24 hours


                    //Constructs a weather snapshot for this particular day
                    WeatherSnapshot futureWeather = new WeatherSnapshot(
                            i + 1,//Establishes what day it is
                            "",
                            weatherEntry.getDescription(),
                            weatherEntry.getTemp(),
                            weatherEntry.getHumidity(),
                            weatherEntry.getSpeed(),
                            weatherEntry.getDate(),
                            0,
                            0
                    );
                    allWeatherData.add(futureWeather);//Adds this day to the forecase

                }
                setFutureDayCustomisation(allWeatherData);
            }

            @Override
            public void onFailure(Call<ForecastWeather> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }


    /**
     * Method to call the various customisation methods for the current day
     * @param day1 Weather information for the current day
     */
    public void setCurrentDayCustomisation(WeatherSnapshot day1){
        RelativeLayout currentWeatherLayout = mainView.findViewById(R.id.day1);

        isDayTime = day1.isDayTime();

        DayCustomiser.setDayValues(currentWeatherLayout,day1,getApplicationContext(),isDayTime);
        ImageView weatherImage = currentWeatherLayout.findViewById(R.id.weather_image);

        DayCustomiser.setWeatherImage(day1.getDesc(),weatherImage,isDayTime,getApplicationContext());
        GeneralCustomiser.setAppColourScheme(isDayTime,mainView,getApplicationContext()); //Sets the color scheme for current day as this contains the relevant weather data
    }



    /**
     * Method to call the various customisation methods for each of the remaining days in the forecase
     * @param forecast Weather information for the remaining days
     */

    public void setFutureDayCustomisation(ArrayList<WeatherSnapshot> forecast){
        WeatherSnapshot weatherToAdd;
        for (int i =2; i<6;i++){

            RelativeLayout day = (RelativeLayout) mainView.getChildAt(i);
            weatherToAdd = forecast.get(i-1);
            DayCustomiser.setDayValues(day, weatherToAdd,getApplicationContext(),isDayTime);
            DayCustomiser.shrinkDayLayout(day,getApplicationContext());
            db.setDay(weatherToAdd);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
