package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class User_Guide extends AppCompatActivity {

    private ImageView btnUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        btnUG = findViewById(R.id.btnUG);
        btnUG.setOnClickListener(view -> {
            Intent btnUG = new Intent(User_Guide.this, vendor_account.class);
            startActivity(btnUG);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent btnUG = new Intent(User_Guide.this, vendor_account.class);
        startActivity(btnUG);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}