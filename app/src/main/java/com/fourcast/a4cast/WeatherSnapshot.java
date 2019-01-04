package com.fourcast.a4cast;

import java.text.DateFormat;
import java.util.Date;

/**
 * Object to represent the forecast information for a particular day
 */
public class WeatherSnapshot {
    private int dayNumber;//What day it is- i.e. today is 1, tomorrow is 2 etc
    private String location; //The location of the forecast info
    private String description; // A brief summary of the weather conditions
    private String temperature; //The temperature- in degrees celsius
    private String humidity; // The percentage humidity
    private String speed; //The wind speed
    private Date updatedOn; //The date and time that this snapshot was taken
    private long sunrise; //The time that sunrise occurs
    private long sunset; //The time that sunset occurs

    /**
     * Constructor for the forecast information
     * @param day the dayNumber
     * @param location the location
     * @param desc the brief summary
     * @param temp the temperature
     * @param hum the humidity
     * @param spd the wind speed
     * @param updated the date that it was taken
     * @param sunrise the time of sunrise
     * @param sunset the time of sunset
     */
    public WeatherSnapshot(int day,String location,String desc, String temp, String hum,String spd,Date updated,long sunrise, long sunset){
        this.dayNumber = day;
        this.location = location;
        this.description = desc;
        this.temperature = temp;
        this.humidity = hum;
        this.speed = spd;
        this.updatedOn = updated;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    /**
     * Method to return the day number
     * @return the day number
     */
    public int getDayNumber(){
        return this.dayNumber;
    }

    /**
     * Method to return the location of the snapshot
     * @return The location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     *  Method to return the summary of the snapshot
     * @return The summary
     */
    public String getDesc() {
        return this.description;
    }

    /**
     * Method to return the temperature, formatted for display
     * @return The formatted string
     */
    public String getTemp(){
        return this.temperature + "°C";
    }

    /**
     * Method to return the temperature, in a raw format
     * @return The string
     */
    public String getTempRaw(){
        return this.temperature;
    }

    /**
     * Method to return the humidity, formatted for display
     * @return The formatted string
     */
    public String getHumidity(){return "Humidity: " + this.humidity + "%";}

    /**
     * Method to return the humidity in a raw format
     * @return The formatted string
     */
    public String getHumidityRaw(){return "Humidity: " + this.humidity + "%";}

    /**
     * Method to return the wind speed, formatted for display
     * @return the formatted string
     */
    public String getSpeed(){return "Wind Speed: " + this.speed + " m/s";}

    /**
     * Method to return the date of the forecast
     * @return the date string
     */
    public String getForecastDate(){
        return DateFormat.getDateInstance(DateFormat.FULL).format(updatedOn);
    }

    /**
     * Method to return the time of the forecast, formatted for display
     * @return The formatted string
     */
    public String getForecastTime(){
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(updatedOn);
        if (dayNumber == 1){
            return "(Last Updated at " + time + ")";
        }
        else{
            return "(Weather at " + time + ")";
        }
    }

    /**
     * Method to get the date updated in a raw format
     * @return the raw string
     */
    public String getUpdatedOnRaw(){
        return DateFormat.getDateTimeInstance().format(this.updatedOn);
    }

    /**
     * Method that returns the sunrise time
     * @return the sunrise
     */
    public long getSunrise(){
        return this.sunrise;
    }

    /**
     * Method that returns the sunset time
     * @return the sunrise
     */
    public long getSunset(){
        return this.sunset;
    }


    /**
     * Method that calculates if the snapshot was taken during the day or the night
     * @return boolean indicaing whether or not it is daytime
     */
    public boolean isDayTime(){
        long currentTime = new Date().getTime();
        if(currentTime>=sunrise && currentTime<sunset){return true;}
        return false;
    }

}
