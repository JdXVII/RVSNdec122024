package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class buyer_transition extends AppCompatActivity {

    private static final int TRANSITION_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_transition);
        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        ImageView logo = findViewById(R.id.transition_logo);
        ProgressBar loadingBar = findViewById(R.id.loading_bar);

        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);

        logo.startAnimation(zoomIn);

        // Transition to MainActivity after 5 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(buyer_transition.this, buyer_login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Close TransitionActivity
        }, TRANSITION_DURATION);
    }
}