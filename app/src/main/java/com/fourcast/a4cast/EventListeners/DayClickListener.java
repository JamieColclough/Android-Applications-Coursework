package com.fourcast.a4cast.EventListeners;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fourcast.a4cast.Utils.DayCustomiser;

/**
 * Event Listener class for the situation that a day has been clicked on
 * Facilitates the clicked day becoming the active day in the layout view
 */
public class DayClickListener implements View.OnClickListener {
    LinearLayout allDays; //The main view containing all of the days
    Context context; //The Activity's application context

    /**
     * Constructor for the class
     * @param allDays The main view
     * @param context The application context
     */
    public DayClickListener(LinearLayout allDays,Context context){
        this.allDays = allDays;
        this.context = context;
    }

    @Override
    public void onClick(View clickedDay){
        int id = clickedDay.getId();
        for (int i=1; i<allDays.getChildCount();i++){
            RelativeLayout dayToEdit =(RelativeLayout) allDays.getChildAt(i);
            if (dayToEdit.getId() == id){
                //Case that the day is the day that has been clicked:
                dayToEdit.setOnClickListener(null);//Sets the listener to null (avoids unnecessary events firing)
                DayCustomiser.expandDayLayout(dayToEdit,context);//expands the layout

            }
            else{
                //Case that the day is one of the other days:
                DayCustomiser.shrinkDayLayout(dayToEdit,context);//Sets the listener so it can be clicked on
                dayToEdit.setOnClickListener(this);//Shrinks the layout
            }
        }
    }
}
