package com.fourcast.a4cast.EventListeners;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.fourcast.a4cast.Activities.MainActivity;

import com.fourcast.a4cast.Utils.WebUtils;

/**
 * Event listener for when a new location to get a forecast has been set
 */
public class SetNewLocationListener  implements View.OnClickListener {
    Context context; //The application context
    String location; //The name of the location
    String latitude; //The degrees latitude of the location
    String longitude;//The degrees longiude of the location

    /**
     * Constructor for the listener
     * @param context The application context
     * @param location the location name
     */
    public SetNewLocationListener(Context context, String location) {
        this.context = context;
        this.location = location;

        //Obtains the longitude and latitude from the location name
        switch (location) {

            case("London"):
                latitude = "51.509865";
                longitude = "-0.118092";
                break;
            case("New York"):
                latitude = "40.71427";
                longitude = "-74.00597";
                break;
            case("Barcelona"):
                latitude = "41.390205";
                longitude = "2.154007";
                break;
            case("Paris"):
                latitude = "48.85341";
                longitude = "2.3488";
                break;
            default:
                latitude = "50.7236";
                longitude = "-3.5339";
        }
    }

    @Override
    public void onClick(View v) {
        if(WebUtils.currentlyOnline(context)) {
            //Case that the app is online:
            SharedPreferences.Editor customisationEditor = context.getSharedPreferences("customisation", Context.MODE_PRIVATE).edit();
            customisationEditor.putString("latitude", latitude);//Sets latitude and longitude in shared preferences to pass back to the main activity
            customisationEditor.putString("longitude", longitude);
            customisationEditor.apply();//Applies changes
            Intent redirectToMain = new Intent(context,	MainActivity.class);
            redirectToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(redirectToMain);//Redirects to and restarts the main activity to attempt to load in the data for the new coordinates
        }
        else{
            //Case that the app is offline: tells the user that the data is unavailable
            Toast.makeText(context, "No Web connection: unable to obtain new data", Toast.LENGTH_LONG).show();
        }


    }



}
