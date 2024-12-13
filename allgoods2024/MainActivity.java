package com.example.allgoods2024;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    TextView buyer, vendor, titleText,term_con;
    ImageView logo;

    private static final String PREFS_NAME = "app_preferences";
    private static final String KEY_NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested";


    // Launcher for requesting notification permission
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Notification permission granted
                    Toast.makeText(MainActivity.this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Notification permission denied
                    showPermissionRationale(); // Show rationale if permission denied
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        TextView textView = findViewById(R.id.term_con);
        SpannableString content = new SpannableString("Terms and Conditions");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        buyer = findViewById(R.id.buyer);
        vendor = findViewById(R.id.vendor);
        logo = findViewById(R.id.logo);
        titleText = findViewById(R.id.title_text);
        term_con = findViewById(R.id.term_con);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide);

        // Apply animations
        logo.startAnimation(slideUp);
        titleText.startAnimation(fadeIn);
        vendor.startAnimation(fadeIn);
        buyer.startAnimation(fadeIn);

        // Check and request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission();
        }

        vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vendor = new Intent(MainActivity.this, transition.class);
                startActivity(vendor);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buyer = new Intent(MainActivity.this, buyer_transition.class);
                startActivity(buyer);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        term_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent terms = new Intent(MainActivity.this, Terms_Condition.class);
                startActivity(terms);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void checkNotificationPermission() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean permissionRequested = sharedPreferences.getBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, false);

        // Only request permission if it hasn't been requested before
        if (!permissionRequested) {
            showPermissionRationale();

            // Update the flag so that the permission request won't show again
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_NOTIFICATION_PERMISSION_REQUESTED, true);
            editor.apply();
        }
    }

    // Show a dialog explaining why notification permission is needed
    private void showPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle("Notification Permission Needed")
                .setMessage("This app requires notification permission to send you notifications. Please enable it.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request permission
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle denial case
                        Toast.makeText(MainActivity.this, "Notification permission is required for sending notifications", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }


}