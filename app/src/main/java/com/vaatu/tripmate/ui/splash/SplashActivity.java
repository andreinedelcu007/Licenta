package com.vaatu.tripmate.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.vaatu.tripmate.R;
import com.vaatu.tripmate.ui.user.UserCycleActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;


    //activitatea de inceput/ecranul cu care porneste 1,5 secunde (splash screen)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, UserCycleActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);

    }


}
