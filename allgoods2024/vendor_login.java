package com.example.allgoods2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class vendor_login extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button btnLogin;
    private TextView btnSignup;
    private ProgressBar progressBar;
    private ImageView back;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_login);

        TextView forgotPassword = findViewById(R.id.forgot);

        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(vendor_login.this, forgot_password.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Check if the user is already logged in
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedInAsVendor", false)) {
            // User is already logged in as vendor, redirect to homepage
            Intent intent = new Intent(vendor_login.this, vendor_homepage.class);
            startActivity(intent);
            finish();
        }

        if (prefs.getBoolean("isLoggedInAsBuyer", false)) {
            // User is logged in as a buyer, prevent vendor login
            Toast.makeText(this, "Please log out from buyer account before logging in as a vendor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progress_bar);

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent back = new Intent(vendor_login.this, MainActivity.class);
            startActivity(back);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Set onClickListener for Sign Up button
        btnSignup.setOnClickListener(view -> {
            Intent intent = new Intent(vendor_login.this, vendor_signup.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Set onClickListener for Login button
        btnLogin.setOnClickListener(view -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(vendor_login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                // Authenticate user with Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(vendor_login.this, task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Check if email is verified
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    // Check admin approval from Realtime Database
                                    checkAdminApproval(user.getUid());
                                } else {
                                    Toast.makeText(vendor_login.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(vendor_login.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    // Method to check admin approval status from Realtime Database
    private void checkAdminApproval(String userId) {
        DatabaseReference userRef = mDatabase.child("vendors").child(userId);
        progressBar.setVisibility(View.VISIBLE);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue(String.class);

                    if ("approved".equals(status)) {
                        // Save login state in SharedPreferences
                        getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isLoggedInAsVendor", true) // Set vendor login state
                                .putBoolean("isLoggedInAsBuyer", false) // Ensure buyer login state is false
                                .apply();

                        // User is approved by admin, proceed to next activity
                        Intent intent = new Intent(vendor_login.this, vendor_homepage.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else if ("deactivated".equals(status)) {
                        // Fetch reason from warnings table before showing dialog
                        fetchWarningReason(userId);
                    } else {
                        Toast.makeText(vendor_login.this, "Admin approval pending", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(vendor_login.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(vendor_login.this, "Failed to check admin approval", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch reason from warnings table
    private void fetchWarningReason(String userId) {
        DatabaseReference warningsRef = mDatabase.child("warnings");
        warningsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String reason = "Permit Expired"; // Default reason if not found
                for (DataSnapshot warningSnapshot : snapshot.getChildren()) {
                    Warning warning = warningSnapshot.getValue(Warning.class);
                    if (warning != null) {
                        reason = warning.getReason(); // Get the reason from the warning
                        break; // Break after finding the first relevant warning
                    }
                }

                // Remove login state from SharedPreferences
                getSharedPreferences("loginPrefs", MODE_PRIVATE)
                        .edit()
                        .clear() // Clear all SharedPreferences
                        .apply();

                // Show dialog with the reason for deactivation
                showDeactivationDialog(reason);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(vendor_login.this, "Failed to fetch warning reason", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to show a dialog when account is deactivated with reason
    private void showDeactivationDialog(String reason) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Account Deactivated")
                .setMessage("Your account has been deactivated due to a violation of our terms and conditions.\n\nReason: " + reason)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
