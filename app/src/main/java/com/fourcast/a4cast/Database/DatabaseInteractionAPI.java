package com.fourcast.a4cast.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fourcast.a4cast.WeatherSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper class for interacting with the database
 */
public class DatabaseInteractionAPI {
    private SQLiteDatabase db; //The database to interact with

    /**
     * Constructor for the API
     */
    public DatabaseInteractionAPI(){
        db = SQLiteDatabase.openDatabase(
                "data/data/com.fourcast.a4cast/lastWeatherData",
                null,
                SQLiteDatabase.CREATE_IF_NECESSARY
        );
        //Checks to see if the weatherData table exists, if it doesn't then the table is created
        //The weatherData table saves all of the information contained in a weatherSnapshot instance
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'weatherData'", null);
        if(cursor!=null) {
            db.execSQL("create table if not exists weatherData("
                        +"dayID integer PRIMARY KEY,"
                        +"location text,"
                        +"description text,"
                        +"temperature text,"
                        +"humidity text,"
                        +"windSpeed text,"
                        +"updatedOn text,"
                        +"sunrise long,"
                        +"sunset long);"
            );
        }
    }

    /**
     * Method to return the entire forecast as WeatherSnapshot objects
     * @return An Arraylist containing all of the weather forecast
     * @throws ParseException
     */
    public ArrayList<WeatherSnapshot> getForecast() throws ParseException {
        String[] columns = {"dayID","location","description","temperature","humidity","windSpeed","updatedOn","sunrise","sunset"};
        Cursor results = db.query(
                "weatherData",
                columns,
                null,
                null,
                null,
                null,
                "dayID" //Ensures that the results are ordered by day
        );
        ArrayList<WeatherSnapshot> forecastData = new ArrayList<>();

        //Creates the columns to query the database by
        int idColumn = results.getColumnIndex("dayID");
        int locationColumn = results.getColumnIndex("location");
        int descriptionColumn = results.getColumnIndex("description");
        int temperatureColumn = results.getColumnIndex("temperature");
        int humidityColumn = results.getColumnIndex("humidity");
        int windSpeedColumn = results.getColumnIndex("windSpeed");
        int updatedColumn = results.getColumnIndex("updatedOn");
        int sunriseColumn = results.getColumnIndex("sunrise");
        int sunsetColumn = results.getColumnIndex("sunset");


        while(results.moveToNext()){ //While there is a snapshot to render
            forecastData.add(
                    //Creates a WeatherSnapshot with all the information from the db
                    new WeatherSnapshot(
                            results.getInt(idColumn),
                            results.getString(locationColumn),
                            results.getString(descriptionColumn),
                            results.getString(temperatureColumn),
                            results.getString(humidityColumn),
                            results.getString(windSpeedColumn),
                            DateFormat.getDateTimeInstance().parse(results.getString(updatedColumn)),
                            results.getLong(sunriseColumn),
                            results.getLong(sunsetColumn)
                    )
            );
        }
        results.close();
        return forecastData; //Closes the cursor and returns the forecast

    }

    /**
     * Method to removes any unnecessary data from the database
     */
    public void clearTable(){
        db.delete("weatherData",null,null);
    }


    /**
     * Method to put a WeatherSnapshot object into the database
     * @param day The WeatherSnapshot object that is being inserted
     */
    public void setDay(WeatherSnapshot day){
        //Sets all of the data for the corresponding database columns
        ContentValues entry = new ContentValues();
        entry.put("dayID",day.getDayNumber());
        entry.put("location", day.getLocation());
        entry.put("description", day.getDesc());
        entry.put("temperature", day.getTempRaw());
        entry.put("humidity", day.getHumidityRaw());
        entry.put("windSpeed", day.getSpeed());
        entry.put("updatedOn", day.getUpdatedOnRaw());
        entry.put("sunrise",day.getSunrise());
        entry.put("sunset",day.getSunset());

        db.insert("weatherData", null, entry); //Inserts the data
    }

    /**
     * Method to close the db after the class has been used
     */
    public void closeDB(){
        db.close(); //Closes the db
    }
}

