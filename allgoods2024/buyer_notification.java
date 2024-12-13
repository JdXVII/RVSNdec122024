package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class buyer_notification extends AppCompatActivity {

    private ImageView bcknot;
    private RecyclerView recyclerViewWarnings;

    private DatabaseReference warningsRef;
    private List<Warning> warnings;
    private WarningAdapter warningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_notification);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bcknot = findViewById(R.id.bcknot);

        bcknot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bcknot = new Intent(buyer_notification.this, buyer_homepage.class);
                startActivity(bcknot);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        recyclerViewWarnings = findViewById(R.id.recyclerViewWarningNotification);
        recyclerViewWarnings.setLayoutManager(new LinearLayoutManager(this));

        warnings = new ArrayList<>();
        warningAdapter = new WarningAdapter(warnings);
        recyclerViewWarnings.setAdapter(warningAdapter);

        fetchWarnings();

    }
    private void fetchWarnings() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Get current vendor's userId
        warningsRef = FirebaseDatabase.getInstance().getReference("warnings");

        warningsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                warnings.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Warning warning = snapshot.getValue(Warning.class);
                    if (warning != null && userId.equals(warning.getUserId())) {
                        warnings.add(warning);
                        // Mark the warning as read if it's unread
                        if (!warning.isRead()) {
                            snapshot.getRef().child("isRead").setValue(true);
                        }
                    }
                }
                warningAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_notification.this, "Failed to load warnings.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}