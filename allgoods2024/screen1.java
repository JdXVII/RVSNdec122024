package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class screen1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

    }

    public void onSkipClick1(View view) {
        Intent intent = new Intent(screen1.this, splash_animation.class);
        startActivity(intent);
        finish();
    }

    public void onNextClick1(View view) {
        Intent intent = new Intent(screen1.this, screen2.class);
        startActivity(intent);
        finish();
    }
}