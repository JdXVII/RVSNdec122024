package com.example.allgoods2024;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class vendor_homepage extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchApprovedProducts();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(vendor_homepage.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    ImageView notification;
    LinearLayout events, orders, message, sales, add_product;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<Product> productList, filteredProductList;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference vendorsRef, declinedProductsRef, declinedPermitsRef, warningsRef;
    SearchView searchView;
    Spinner categorySpinner;
    ShapeableImageView profileImageView;
    private TextView messageBadge;
    private static final String CHANNEL_ID = "vendor_message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;
    private TextView notifBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_homepage);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        profileImageView = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        add_product = findViewById(R.id.add_product);
        recyclerView = findViewById(R.id.recycler_view);
        notification = findViewById(R.id.notification);
        notifBadge = findViewById(R.id.notif_badge);
        searchView = findViewById(R.id.search_bar);
        categorySpinner = findViewById(R.id.productCategorySpinner);
        orders = findViewById(R.id.orders);

        events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent events = new Intent(vendor_homepage.this, vendor_events.class);
                startActivity(events);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification = new Intent(vendor_homepage.this, vendor_notification.class);
                startActivity(notification);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        messageBadge = findViewById(R.id.message_badge);
        message = findViewById(R.id.message);
        sales = findViewById(R.id.sales);
        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sales = new Intent(vendor_homepage.this, vendor_report_generation.class);
                startActivity(sales);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        vendorsRef = FirebaseDatabase.getInstance().getReference("vendors");
        declinedProductsRef = FirebaseDatabase.getInstance().getReference("declined_products");
        declinedPermitsRef = FirebaseDatabase.getInstance().getReference("declined_permits");
        warningsRef = FirebaseDatabase.getInstance().getReference("warnings");


        profileImageView.setOnClickListener(view -> {
            Intent account = new Intent(vendor_homepage.this, vendor_account.class);
            startActivity(account);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        add_product.setOnClickListener(view -> {
            Intent add_product = new Intent(vendor_homepage.this, vendor_addproduct.class);
            startActivity(add_product);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        orders.setOnClickListener(view -> {
            Intent orders = new Intent(vendor_homepage.this, vendor_orders.class);
            startActivity(orders);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent message = new Intent(vendor_homepage.this, vendor_messages.class);
                startActivity(message);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, filteredProductList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String productId) {
                Intent intent = new Intent(vendor_homepage.this, vendor_edit_product.class);
                intent.putExtra("productId", productId);
                startActivity(intent);
                finish();
            }
        });
        recyclerView.setAdapter(productAdapter);


        fetchApprovedProducts();
        // Load profile image URL
        loadProfileImage();
        checkForUnreadMessages(); // Call this function to check for unread messages
        createNotificationChannel();
        setupNotificationBadge();

        checkVendorStatus(currentUser.getUid());

        // Set SearchView text color to black
        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = (TextView) searchView.findViewById(searchTextId);
        searchText.setTextColor(Color.BLACK);

        populateCategorySpinner(); // Populate spinner dynamically

        // Set up the listener for category selection
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // SearchView query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts();
                return false;
            }
        });
    }

    // Method to check internet availability
    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    // Function to populate the spinner
    private void populateCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("All"); // Add the "All" option

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }

                // Set up the adapter with the updated list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(vendor_homepage.this,
                        R.layout.custom_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(vendor_homepage.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVendorStatus(String userId) {
        vendorsRef = vendorsRef.child(userId);

        vendorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);

                if ("deactivated".equals(status)) {

                    FirebaseAuth.getInstance().signOut();

                    getSharedPreferences("loginPrefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedInAsVendor", false)
                            .putBoolean("isLoggedInAsBuyer", false)
                            .apply();

                    Toast.makeText(vendor_homepage.this, "Your account has been deactivated.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(vendor_homepage.this, vendor_login.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_homepage.this, "Failed to check vendor status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNotificationBadge() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid(); // Get current vendor's userId
        DatabaseReference declinedProductsRef = FirebaseDatabase.getInstance().getReference("declined_products");

        // Initialize unread count
        int[] unreadCount = {0}; // Using an array to modify inside listener

        // Attach listener to fetch declined products where userId matches and isRead is false
        declinedProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through all declined products
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String productUserId = snapshot.child("userId").getValue(String.class);
                        Boolean isRead = snapshot.child("isRead").getValue(Boolean.class);

                        // Check if the userId matches the current vendor's userId and isRead is false
                        if (productUserId != null && productUserId.equals(currentUserId) && Boolean.FALSE.equals(isRead)) {
                            unreadCount[0]++;
                        }
                    } catch (Exception e) {
                        Log.e("NotificationBadge", "Error processing declined product data", e);
                    }
                }

                // After fetching declined products, fetch declined permits
                fetchDeclinedPermits(unreadCount[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationBadge", "Error fetching declined products", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to fetch declined products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch declined permits and then fetch warnings
    private void fetchDeclinedPermits(int currentUnreadCount) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid(); // Get current vendor's userId
        DatabaseReference declinedPermitsRef = FirebaseDatabase.getInstance().getReference("declined_permits");
        DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference("vendors").child(currentUserId);

        declinedPermitsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int permitCount = 0;

                // Loop through all declined permits
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String permitUserId = snapshot.child("userId").getValue(String.class);
                        Boolean isRead = snapshot.child("isRead").getValue(Boolean.class);

                        // Check if the userId matches the current vendor's userId and isRead is false
                        if (permitUserId != null && permitUserId.equals(currentUserId) && Boolean.FALSE.equals(isRead)) {
                            permitCount++;
                        }
                    } catch (Exception e) {
                        Log.e("NotificationBadge", "Error processing declined permit data", e);
                    }
                }

                // Now check for permit expiration and warnings
                int finalUnreadCount = currentUnreadCount + permitCount; // Combine counts
                vendorRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot vendorSnapshot = task.getResult();
                        if (vendorSnapshot != null) {
                            String expirationDate = vendorSnapshot.child("expirationDate").getValue(String.class);
                            if (expirationDate != null) {
                                checkExpirationDate(expirationDate, finalUnreadCount); // Check expiration date
                            } else {
                                fetchUnreadWarnings(finalUnreadCount); // Fetch warnings directly if no expiration date
                            }
                        }
                    } else {
                        Log.e("NotificationBadge", "Error fetching vendor permit details", task.getException());
                        Toast.makeText(getApplicationContext(), "Failed to fetch vendor permit details.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Log.e("NotificationBadge", "Vendor permit fetch failure", e);
                    Toast.makeText(getApplicationContext(), "Failed to fetch vendor permit details.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationBadge", "Error fetching declined permits", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to fetch declined permits.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adjust the expiration check
    private void checkExpirationDate(String expirationDate, int currentUnreadCount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date expiryDate = sdf.parse(expirationDate);
            Date currentDate = new Date();
            long diffInMillis = expiryDate.getTime() - currentDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            // If the permit is about to expire or has expired, increment unread count
            if (diffInDays <= 30 && diffInDays > 0) {
                currentUnreadCount++;
            } else if (diffInDays <= 0) {
                currentUnreadCount++;
            }

            fetchUnreadWarnings(currentUnreadCount); // After checking expiration, fetch warnings

        } catch (ParseException e) {
            Log.e("NotificationBadge", "Error parsing expiration date", e);
            Toast.makeText(getApplicationContext(), "Error parsing expiration date.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to fetch unread warnings
    private void fetchUnreadWarnings(int currentUnreadCount) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid(); // Get current vendor's userId

        DatabaseReference warningsRef = FirebaseDatabase.getInstance().getReference("warnings");
        warningsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int warningCount = 0;

                // Loop through all warnings
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String warningUserId = snapshot.child("userId").getValue(String.class);
                        Boolean isRead = snapshot.child("isRead").getValue(Boolean.class);

                        // Check if the userId matches the current vendor's userId and isRead is false
                        if (warningUserId != null && warningUserId.equals(currentUserId) && Boolean.FALSE.equals(isRead)) {
                            warningCount++;
                        }
                    } catch (Exception e) {
                        Log.e("NotificationBadge", "Error processing warning data", e);
                    }
                }

                // Combine warning count with other unread counts
                int finalUnreadCount = currentUnreadCount + warningCount;
                updateNotificationBadge(finalUnreadCount); // Update the badge with total unread count
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationBadge", "Error fetching warnings", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to fetch warnings.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update the notification badge visibility and text
    private void updateNotificationBadge(int unreadCount) {
        if (unreadCount > 0) {
            notifBadge.setText(String.valueOf(unreadCount));
            notifBadge.setVisibility(View.VISIBLE);
        } else {
            notifBadge.setVisibility(View.GONE);
        }
    }


    private void loadProfileImage() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            vendorsRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null) {
                        Glide.with(vendor_homepage.this)
                                .load(profileImageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.user1) // Optional placeholder
                                        .override(100, 100) // Set dimensions if needed
                                        .centerCrop() // Maintain aspect ratio
                                )
                                .into(profileImageView);
                    } else {
                        // Handle case where URL is null, e.g., show a default image
                        profileImageView.setImageResource(R.drawable.user1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(vendor_homepage.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchApprovedProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("upload_products");
        Query approvedProductsQuery = productsRef.orderByChild("userId").equalTo(currentUser.getUid());

        approvedProductsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    if (product != null && "approved".equals(product.getStatus())) {
                        productList.add(product);
                    }
                }
                Collections.reverse(productList);
                filterProducts();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(vendor_homepage.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts() {
        String query = searchView.getQuery().toString().toLowerCase().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        filteredProductList.clear();

        for (Product product : productList) {
            boolean matchesCategory = selectedCategory.equals("All") || product.getCategory().equals(selectedCategory);
            boolean matchesQuery = product.getName().toLowerCase().contains(query);

            if (matchesCategory && matchesQuery) {
                filteredProductList.add(product);
            }
        }

        productAdapter.notifyDataSetChanged();
    }

    // Method to create a notification channel (required for Android 8.0 and above)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Message Notifications";
            String description = "Channel for unread message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(int unreadCount) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, vendor_messages.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Use FLAG_IMMUTABLE to ensure the PendingIntent is immutable
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Set your notification icon here
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo)) // Optional large icon
                .setContentTitle("Unread Messages")
                .setContentText("You have " + unreadCount + " unread messages from buyers.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Get the NotificationManagerCompat instance
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        // Display the notification
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(1, builder.build());
    }

    // Modified checkForUnreadMessages method to include notification
    private void checkForUnreadMessages() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unreadCount = 0;
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot usersSnapshot = chatSnapshot.child("users");
                    DataSnapshot messagesSnapshot = chatSnapshot.child("messages");

                    String receiverId = usersSnapshot.child("receiverId").getValue(String.class);
                    if (receiverId != null && receiverId.equals(userId)) {
                        for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                            String category = messageSnapshot.child("category").getValue(String.class);
                            Boolean isRead = messageSnapshot.child("isRead").getValue(Boolean.class);
                            if ("buyer".equals(category) && Boolean.FALSE.equals(isRead)) {
                                unreadCount++;
                            }
                        }
                    }
                }

                if (unreadCount > 0) {
                    messageBadge.setVisibility(View.VISIBLE);
                    messageBadge.setText(String.valueOf(unreadCount));

                    // Get last notified count from SharedPreferences
                    int lastNotifiedCount = sharedPreferences.getInt(PREF_LAST_NOTIFIED_COUNT, -1);

                    // Only send notification if unreadCount is greater than lastNotifiedCount
                    if (unreadCount > lastNotifiedCount) {
                        sendNotification(unreadCount);
                    }

                    // Update last notified count in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PREF_LAST_NOTIFIED_COUNT, unreadCount);
                    editor.apply();
                } else {
                    messageBadge.setVisibility(View.GONE);

                    // Update SharedPreferences to reflect zero count
                    int lastNotifiedCount = sharedPreferences.getInt(PREF_LAST_NOTIFIED_COUNT, -1);

                    if (lastNotifiedCount > 0) {
                        // Update the count to zero in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(PREF_LAST_NOTIFIED_COUNT, 0);
                        editor.apply();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_homepage.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(vendor_homepage.this, MainActivity.class);
        startActivity(back);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}
