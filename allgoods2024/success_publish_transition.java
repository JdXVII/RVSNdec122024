package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

public class success_publish_transition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_publish_transition);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        ImageView gifImageView = findViewById(R.id.success_gif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.green) // replace with your GIF resource
                .into(gifImageView);
        // Delay for 3 seconds before proceeding
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After 3 seconds, navigate to the main activity or other desired activity
                Toast.makeText(success_publish_transition.this, "Product uploaded successfully, Wait for admin approval", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(success_publish_transition.this, vendor_homepage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 2000);
    }
}