package com.fourcast.a4cast.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.fourcast.a4cast.EventListeners.SetNewLocationListener;
import com.fourcast.a4cast.R;

/**
 * Activity class for the application settings
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Same action bar settings as main activity
        setSupportActionBar((Toolbar) findViewById(R.id.app_toolbar));
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //When the return button is clicked, close the settings activity and return to the main activity page
        findViewById(R.id.settings_return_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//Finishes the activity, activity stack redirects to main
            }
        });


        //Sets listeners for each of the options
        findViewById(R.id.current_setting).setOnClickListener(new SetNewLocationListener(getApplicationContext(),"Current"));
        findViewById(R.id.london_setting).setOnClickListener(new SetNewLocationListener(getApplicationContext(),"London"));
        findViewById(R.id.new_york_setting).setOnClickListener(new SetNewLocationListener(getApplicationContext(),"New York"));
        findViewById(R.id.barcelona_setting).setOnClickListener(new SetNewLocationListener(getApplicationContext(),"Barcelona"));
        findViewById(R.id.paris_setting).setOnClickListener(new SetNewLocationListener(getApplicationContext(),"Paris"));
    }
}
