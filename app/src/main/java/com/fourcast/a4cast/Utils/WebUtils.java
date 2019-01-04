package com.fourcast.a4cast.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class containing static methods to interact with the internet
 */
public class WebUtils {
    /**
     * Method to determine whether the device is currently online
     * @param context The application context
     * @return Boolean indicating whether the device is online or offline
     */
    public static boolean currentlyOnline(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())//Checks to see if the device is connected to wifi
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())//Checks to see if the device is connected to wifi
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Static method to obtain a JSON object from performing a http call to the openWeatherMap API server
     * @param lat The degrees latitude of the forecast
     * @param lon The degrees longitude of the forecast
     * @param url The URL To send the data to
     * @param key The API Key
     * @return JSON Object containing all of the data
     */
    public static JSONObject getWeatherJSON(String lat, String lon, String url,String key){
        try {
            URL connectionUrl = new URL(String.format(url, lat, lon,key));
            HttpURLConnection connection =
                    (HttpURLConnection)connectionUrl.openConnection();//Tries to open a connection


            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));//Gets input stream from the connection

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");//Adds up the string file
            reader.close();

            JSONObject data = new JSONObject(json.toString());//Converts the string to a JSON Object

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null; //Ensures that the connection was successful (200)
            }
            return data;//Returns the JSON object
        }catch(Exception e){
            return null;//If exception, return null
        }
    }

    /**
     * Util method to capitalize the first letter of each word
     * @param input The string to capitalize
     * @return The capitalized string
     */
    public static String capitalizeFirstWord(String input){
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
