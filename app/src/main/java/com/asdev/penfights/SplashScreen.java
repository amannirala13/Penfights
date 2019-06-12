package com.asdev.penfights;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.asdev.penfights.helper.CustomToast;
import com.asdev.penfights.helper.check;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    private static Boolean PERSISTENCE_STATE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        //Offline caching of data for Firebase RealTime database
        if(!PERSISTENCE_STATE)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            PERSISTENCE_STATE = true;
        }

        // Splash screen change
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFirstRun())   // If app is run for the first time -> FirstRun activity
                {
                    Intent firstRunIntent = new Intent(SplashScreen.this,FirstRun.class);
                    startActivity(firstRunIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                else if (new check().isValidUser())    // If the user is not logged in -> Login activity
                {
                    Intent mainActivityIntent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(mainActivityIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                else   // If all gud -> Main activity
                {
                    Intent loginActivityIntent = new Intent(SplashScreen.this,Login.class);
                    startActivity(loginActivityIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        },3000); // Time after splash screen changes (3s)

    }


    private boolean isFirstRun()     // Checks if the app is run for the first time
    {
        SharedPreferences firstTimePref = this.getSharedPreferences("first_time", MODE_PRIVATE);
        boolean firstTime = firstTimePref.getBoolean("firstTime", true);   // Checking if firstTime exists and extracting is value
        if(firstTime)
        {
            SharedPreferences.Editor editor = firstTimePref.edit();
            editor.putBoolean("firstTime",false);          // Changing firstTime to false and storing is shared preference so that next time it returns false

            editor.apply();
        }
        return firstTime;   // returning first run from shared Preferences
    }
}
