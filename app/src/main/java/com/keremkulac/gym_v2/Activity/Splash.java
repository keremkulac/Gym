package com.keremkulac.gym_v2.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.keremkulac.gym_v2.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // ActionBar gizlenir
        getSupportActionBar().hide();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 2 saniye Splash screen gösterilir ve Giriş ekranına gider
                Intent loginIntent = new Intent(Splash.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        },2000);
    }
}