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
import com.fourcast.a4cast.Web.AsyncWeatherResponse;
import com.fourcast.a4cast.Web.CurrentWeatherAPI;
import com.fourcast.a4cast.EventListeners.DayClickListener;
import com.fourcast.a4cast.Web.ForecastWeatherAPI;
import com.fourcast.a4cast.R;
import java.text.ParseException;
import java.util.ArrayList;

import com.fourcast.a4cast.Utils.GeneralCustomiser;
import com.fourcast.a4cast.Utils.WebUtils;

/**
 * The main activity class for the application
 */
public class MainActivity extends AppCompatActivity {
    LinearLayout mainView; //The entire activity view
    boolean isDayTime; //Boolean indicating whether it is currently day or night, used as a property as multiple methods reference it
    DatabaseInteractionAPI db; //


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

            CurrentWeatherAPI currentWeatherTask = new CurrentWeatherAPI(new AsyncWeatherResponse() {
                @Override
                public void processFinish(ArrayList<WeatherSnapshot> currentWeatherData) {
                    WeatherSnapshot day1 = currentWeatherData.get(0);
                    setCurrentDayCustomisation(day1);//Sets customisation for today
                    GeneralCustomiser.setNavBarText(mainView,day1.getLocation(),false);//Sets the nav bar text
                    db.clearTable();
                    db.setDay(day1);//Clears the db and replaces it with the data for today
                }
            },getApplicationContext());

            ForecastWeatherAPI forecastTask = new ForecastWeatherAPI(new AsyncWeatherResponse() {
                @Override
                public void processFinish(ArrayList<WeatherSnapshot> currentWeatherData) {
                    setFutureDayCustomisation(currentWeatherData);//Performs customisation for the remaining days in the forecast
                }
            },
            getApplicationContext());

            SharedPreferences sp = getSharedPreferences("customisation",Context.MODE_PRIVATE);//Gets customisation shared preferences

            String latitude = sp.getString("latitude","50.7236");//Finds the latitude and longitude of location to get forecast for
            String longitude = sp.getString("longitude","-3.5339");

            currentWeatherTask.execute(latitude, longitude); //  asyncTask.execute("Latitude", "Longitude")

            forecastTask.execute(latitude, longitude);


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
