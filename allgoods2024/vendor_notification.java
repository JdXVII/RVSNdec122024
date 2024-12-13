package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class vendor_notification extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private RecyclerView recyclerViewDeclineNotifications;
    private RecyclerView recyclerViewDeclinePermit;
    private RecyclerView recyclerViewWarnings;
    private VendorNotificationAdapter notificationAdapter;
    private DeclinedProductAdapter declinedProductAdapter;
    private List<Notification> notifications;
    private List<DeclinedProduct> declinedProducts;
    private DatabaseReference declinedProductsRef;
    private DeclinedPermitAdapter declinedPermitAdapter;
    private List<DeclinedPermit> declinedPermits;
    private DatabaseReference declinedPermitsRef;
    private ImageView bcknot1;

    private DatabaseReference warningsRef;
    private List<Warning> warnings;
    private WarningAdapter warningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_notification);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bcknot1 = findViewById(R.id.bcknot1);

        bcknot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bcknot1 = new Intent(vendor_notification.this, vendor_homepage.class);
                startActivity(bcknot1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        recyclerViewWarnings = findViewById(R.id.recyclerViewWarningNotification);
        recyclerViewWarnings.setLayoutManager(new LinearLayoutManager(this));

        warnings = new ArrayList<>();
        warningAdapter = new WarningAdapter(warnings);
        recyclerViewWarnings.setAdapter(warningAdapter);

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotification);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        // Configure recyclerViewDeclineNotifications to reverse layout and stack from end
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true); // Display items in reverse order
        linearLayoutManager.setStackFromEnd(true);  // Stack items from the bottom
        recyclerViewDeclineNotifications = findViewById(R.id.recyclerViewDeclineNotification);
        recyclerViewDeclineNotifications.setLayoutManager(linearLayoutManager);

        // Configure recyclerViewDeclineNotifications to reverse layout and stack from end
        LinearLayoutManager linearLayoutManagers = new LinearLayoutManager(this);
        linearLayoutManagers.setReverseLayout(true); // Display items in reverse order
        linearLayoutManagers.setStackFromEnd(true);  // Stack items from the bottom
        recyclerViewDeclinePermit = findViewById(R.id.recyclerViewDeclinePermit);
        recyclerViewDeclinePermit.setLayoutManager(linearLayoutManagers);

        notifications = new ArrayList<>();
        declinedProducts = new ArrayList<>();
        declinedPermits = new ArrayList<>();

        notificationAdapter = new VendorNotificationAdapter(notifications);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        declinedProductsRef = FirebaseDatabase.getInstance().getReference("declined_products");
        declinedProductAdapter = new DeclinedProductAdapter(declinedProducts, userId);
        recyclerViewDeclineNotifications.setAdapter(declinedProductAdapter);

        declinedPermitsRef = FirebaseDatabase.getInstance().getReference("declined_permits");
        declinedPermitAdapter = new DeclinedPermitAdapter(declinedPermits, userId);
        recyclerViewDeclinePermit.setAdapter(declinedPermitAdapter);

        fetchNotifications();
        fetchDeclinedProducts();
        fetchDeclinedPermits();
        markNotificationsAsRead();
        markPermitNotificationsAsRead();
        fetchWarnings();
    }

    private void markNotificationsAsRead() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid(); // Get current vendor's userId
        DatabaseReference declinedProductsRef = FirebaseDatabase.getInstance().getReference("declined_products");

        // Fetch declined products and update isRead field to true where the userId matches
        declinedProductsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productUserId = snapshot.child("userId").getValue(String.class);
                    Boolean isRead = snapshot.child("isRead").getValue(Boolean.class);

                    // Check if the userId matches and the notification is unread
                    if (productUserId != null && productUserId.equals(currentUserId) && !isRead) {
                        // Set isRead to true in Firebase
                        snapshot.getRef().child("isRead").setValue(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to fetch declined products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markPermitNotificationsAsRead() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid(); // Get current vendor's userId
        DatabaseReference declinedPermitsRef = FirebaseDatabase.getInstance().getReference("declined_permits");

        // Fetch declined products and update isRead field to true where the userId matches
        declinedPermitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String permitUserId = snapshot.child("userId").getValue(String.class);
                    Boolean isRead = snapshot.child("isRead").getValue(Boolean.class);

                    // Check if the userId matches and the notification is unread
                    if (permitUserId != null && permitUserId.equals(currentUserId) && !isRead) {
                        // Set isRead to true in Firebase
                        snapshot.getRef().child("isRead").setValue(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to fetch declined permits.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchNotifications() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Retrieve the current user's ID

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("vendors").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String expirationDate = dataSnapshot.child("expirationDate").getValue(String.class);
                checkExpirationDate(expirationDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_notification.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkExpirationDate(String expirationDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date expiryDate = sdf.parse(expirationDate);
            Date currentDate = new Date();
            long diffInMillis = expiryDate.getTime() - currentDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (diffInDays <= 30 && diffInDays > 0) {
                notifications.add(new Notification("Your permit will expire soon. ", expirationDate, R.color.colorAccent));
            } else if (diffInDays <= 0) {
                notifications.add(new Notification("Your permit has expired. ", expirationDate, R.color.red));
            }

            notificationAdapter.notifyDataSetChanged();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing expiration date.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDeclinedProducts() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Retrieve the current user's ID

        declinedProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                declinedProducts.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DeclinedProduct product = snapshot.getValue(DeclinedProduct.class);
                    if (product != null && userId.equals(product.getUserId())) {
                        declinedProducts.add(product);
                    }
                }
                declinedProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_notification.this, "Failed to load declined products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDeclinedPermits() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // Retrieve the current user's ID

        declinedPermitsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                declinedPermits.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DeclinedPermit permit = snapshot.getValue(DeclinedPermit.class);
                    if (permit != null && userId.equals(permit.getUserId())) {
                        declinedPermits.add(permit);
                    }
                }
                declinedPermitAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_notification.this, "Failed to load declined permits.", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(vendor_notification.this, "Failed to load warnings.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations
        Intent back = new Intent(vendor_notification.this, vendor_homepage.class);
        startActivity(back);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}

