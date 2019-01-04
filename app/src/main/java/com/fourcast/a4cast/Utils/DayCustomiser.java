package com.fourcast.a4cast.Utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fourcast.a4cast.R;
import com.fourcast.a4cast.WeatherSnapshot;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Class containing static methods to make customisation changes to the main activity for a certain day
 */
public class DayCustomiser {

    /**
     * Static method to set the values for a certain day section of the main app
     * @param dayToEdit The day section of the activity to edit
     * @param snapshot The forecast information for a certain day; to be inserted into the layout
     * @param context The Application context
     * @param isDayTime Boolean indicating whether it is currently daytime
     */
    public static void setDayValues(RelativeLayout dayToEdit, WeatherSnapshot snapshot, Context context, boolean isDayTime){

        //Finds all of the relevant views of the section to edit
        TextView dateField = dayToEdit.findViewById(R.id.date_field);
        TextView timeField = dayToEdit.findViewById(R.id.time_field);
        TextView detailsField = dayToEdit.findViewById(R.id.details_field);
        TextView currentTemperatureField = dayToEdit.findViewById(R.id.current_temperature_field);
        TextView humidity_field = dayToEdit.findViewById(R.id.humidity_field);
        TextView pressure_field = dayToEdit.findViewById(R.id.pressure_field);
        ImageView weatherImage = dayToEdit.findViewById(R.id.weather_image);

        //Sets the date values given by the snapshot
        dateField.setText(snapshot.getForecastDate());
        timeField.setText(snapshot.getForecastTime());
        detailsField.setText(snapshot.getDesc());
        currentTemperatureField.setText(snapshot.getTemp());
        humidity_field.setText(snapshot.getHumidity());
        pressure_field.setText(snapshot.getSpeed());
        //Sets the image for the day
        setWeatherImage(snapshot.getDesc(),weatherImage,isDayTime,context);
    }


    /**
     * Static method to set the image for a certain day
     * @param description The description of the weather- used to determine what image to use
     * @param weatherImage The image view to set the image for
     * @param isDayTime Boolean indicating whether it is currently day time
     * @param context
     */
    public static void setWeatherImage(String description,ImageView weatherImage,boolean isDayTime,Context context){
        int imageId;
        description = description.toLowerCase();


        //Looks through each description for key words, assigns the relevant image id if found
        if(description.contains("clear")){
            if(isDayTime) {
                imageId = R.drawable.clear_day;
            } else {
                imageId = R.drawable.clear_night;
            }
        }
        else if(description.contains("clouds")){
            imageId = R.drawable.cloudy;
        }
        else if(description.contains("rain")||description.contains("drizzle")){
            imageId = R.drawable.raining;
        }
        else if(description.contains("snow") || description.contains("sleet")){
            imageId = R.drawable.snowing;
        }

        else if(containsMistVal(description)){
            imageId = R.drawable.misty;
        }

        else{
            imageId = R.drawable.lightning;
        }

        Drawable image;

        image = context.getDrawable(imageId);//Sets the image given by the id
        weatherImage.setImageDrawable(image);//Sets the image
    }


    /**
     * Static method to expand the selected section outwards
     * @param day The day layout to be expanded
     * @param context The application context
     */
    public static void expandDayLayout(RelativeLayout day,Context context){

        //Makes data visible that were hidden in a small layout
        day.findViewById(R.id.time_field).setVisibility(View.VISIBLE);
        day.findViewById(R.id.details_field).setVisibility(View.VISIBLE);
        day.findViewById(R.id.current_temperature_field).setVisibility(View.VISIBLE);
        day.findViewById(R.id.humidity_field).setVisibility(View.VISIBLE);
        day.findViewById(R.id.pressure_field).setVisibility(View.VISIBLE);

        //Increases the text size of the title
        ((TextView)day.findViewById(R.id.date_field)).setTextSize(COMPLEX_UNIT_SP, 24);

        //Makes changes to the image
        ImageView image = day.findViewById(R.id.weather_image);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)image.getLayoutParams();
            params.height=(int)context.getResources().getDimension(R.dimen.weather_image_size); //Increases the image size
            params.width=(int)context.getResources().getDimension(R.dimen.weather_image_size);

            //Checks the orientation of the screen
           if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
               //If the phone is portrait, The image is in the center of the screen, otherwise it is to the left
               params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
            }

        image.setLayoutParams(params); //updates the image

        float expandedLayoutWeight = context.getResources().getInteger(R.integer.selected_day_layout_weight);//Sets the new size of the section
        day.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,0,expandedLayoutWeight));//Sets the changes
    }

    /**
     * Static method to shrink a section that is not currently selected
     * @param day The day layout to shrink
     * @param context The application context
     */
    public static void shrinkDayLayout(RelativeLayout day,Context context){
        //Hides several bits of data that wouldn't fit in the shrunk view
        day.findViewById(R.id.time_field).setVisibility(View.GONE);
        day.findViewById(R.id.details_field).setVisibility(View.GONE);
        day.findViewById(R.id.current_temperature_field).setVisibility(View.GONE);
        day.findViewById(R.id.humidity_field).setVisibility(View.GONE);
        day.findViewById(R.id.pressure_field).setVisibility(View.GONE);

        //Reduces the size of the title to fir
        ((TextView)day.findViewById(R.id.date_field)).setTextSize(COMPLEX_UNIT_SP, 18);

        //Gets the image view to edit
        ImageView image = day.findViewById(R.id.weather_image);

        //Makes changes to the image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)image.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.LEFT_OF);//Makes image to the left of the section
            params.height=(int)context.getResources().getDimension(R.dimen.weather_image_mini_size);//Reduces the size of the image
            params.width=(int)context.getResources().getDimension(R.dimen.weather_image_mini_size);
        image.setLayoutParams(params); //saves changes

        float shrunkLayoutWeight = context.getResources().getInteger(R.integer.unselected_day_layout_weight); //Sets new size of the shrunk section
        day.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,0,shrunkLayoutWeight));//Sets changes
    }


    /**
     * Static method to determine whether a description is eligible to be in the mist category
     * @param description The description
     * @return Boolean indicating whether the description is mist-related
     */
    private static boolean containsMistVal(String description){
        for (String s : new String[]{"mist","smoke","haze","sand","fog","dust","ash","squalls","tornado"}){
            if(description.contains(s)){
                return true;
            }
        }
        return false;
    }


}
