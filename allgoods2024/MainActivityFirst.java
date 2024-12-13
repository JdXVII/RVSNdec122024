package com.example.allgoods2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivityFirst extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            setContentView(R.layout.activity_main_first);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        } else {
            Intent intent = new Intent(MainActivityFirst.this, splash_animation.class);
            startActivity(intent);
            finish();
        }
    }

    public void onSkipClick(View view) {
        Intent intent = new Intent(MainActivityFirst.this, splash_animation.class);
        startActivity(intent);
        finish();
    }

    public void onNextClick(View view) {
        Intent intent = new Intent(MainActivityFirst.this, screen1.class);
        startActivity(intent);
        finish();
    }
}