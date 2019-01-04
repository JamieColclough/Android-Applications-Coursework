package com.fourcast.a4cast.Utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourcast.a4cast.R;

/**
 * Class containing static methods to make changes to the layout of the entire app
 */
public class GeneralCustomiser {

    /**
     * Static method to set the text value of the navigation bar
     * @param mainView The main view of the activity
     * @param location The location-Will be inside the description
     * @param offline Boolean indicating whether  the phone is offline
     */
    public static void setNavBarText(LinearLayout mainView, String location, boolean offline){

        TextView toolbar = mainView.findViewById(R.id.toolbar_title);
        if(offline){
            toolbar.setTextColor(Color.GRAY);//Sets the toolbar colour to grey if the devic is offline
        }
        toolbar.setText("4Cast: "+location);//Sets the text
    }

    /**
     * Sets the colour scheme of the app
     * @param isDayTime Boolean indicating if the app is during the day- has different colour scheme in the day to the night
     * @param mainView The main view of the activity
     * @param context The application context
     */
    public static void setAppColourScheme(boolean isDayTime, LinearLayout mainView,Context context){
        int color1,color2,color3,color4,color5;

        if(isDayTime) {
            //If app is during the day, sets this lighter colour scheme
            color1 = getColorWrapper(R.color.colorDay1,context);
            color2 = getColorWrapper(R.color.colorDay2,context);
            color3 = getColorWrapper(R.color.colorDay3,context);
            color4 = getColorWrapper(R.color.colorDay4,context);
            color5 = getColorWrapper(R.color.colorDay5,context);
        }
        else{
            //If app is during the night, sets this darker colour scheme
            color1 = getColorWrapper(R.color.colorNight1,context);
            color2 = getColorWrapper(R.color.colorNight2,context);
            color3 = getColorWrapper(R.color.colorNight3,context);
            color4 = getColorWrapper(R.color.colorNight4,context);
            color5 = getColorWrapper(R.color.colorNight5,context);
        }

        //Sets the background colour
        (mainView.findViewById(R.id.day1)).setBackgroundColor(color1);
        (mainView.findViewById(R.id.day2)).setBackgroundColor(color2);
        (mainView.findViewById(R.id.day3)).setBackgroundColor(color3);
        (mainView.findViewById(R.id.day4)).setBackgroundColor(color4);
        (mainView.findViewById(R.id.day5)).setBackgroundColor(color5);

    }

    /**
     * Static method to get the colour id- done because app has api level 21
     * @param id The id to return
     * @param context The application context
     * @return The id
     */
    private static int getColorWrapper(int id,Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }
}
