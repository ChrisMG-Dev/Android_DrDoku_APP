package com.project.drdoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
 
public class SplashScreen extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainScreen.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}