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
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class buyer_messages extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchMessages();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(buyer_messages.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecyclerView recyclerViewVendorMessages;
    private BuyerMessageProfiles adapter;
    private List<Vendor> vendorList;
    private List<Vendor> filteredVendorList;
    private String currentBuyerId;
    private LinearLayout category, purchases, cart, market;
    private TextView messageBadge;
    private static final String CHANNEL_ID = "message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_messages);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Set status bar color to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        messageBadge = findViewById(R.id.message_badge);

        category = findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent category = new Intent(buyer_messages.this, buyer_category.class);
                startActivity(category);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        market = findViewById(R.id.market);
        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent market = new Intent(buyer_messages.this, buyer_homepage.class);
                startActivity(market);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(buyer_messages.this, buyer_addtocart.class);
                startActivity(cart);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        purchases = findViewById(R.id.purchases);
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent purchases = new Intent(buyer_messages.this, buyer_purchases.class);
                startActivity(purchases);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentBuyerId = currentUser.getUid(); // Get the current vendor's ID
        } else {
            // Handle the case when the user is not logged in
            Toast.makeText(this, "You need to log in to view messages.", Toast.LENGTH_LONG).show();
            finish(); // Close the current activity if not logged in
            return;
        }

        recyclerViewVendorMessages = findViewById(R.id.recyclerViewVendorMessages);
        recyclerViewVendorMessages.setLayoutManager(new LinearLayoutManager(this));
        vendorList = new ArrayList<>();
        filteredVendorList = new ArrayList<>();
        adapter = new BuyerMessageProfiles(this, filteredVendorList, currentBuyerId);
        recyclerViewVendorMessages.setAdapter(adapter);

        // Fetch data from Firebase
        fetchMessages();

        // Set up search functionality
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        checkForUnreadMessages(); // Call this function to check for unread messages
        createNotificationChannel();
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

    private void fetchMessages() {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vendorList.clear();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot usersSnapshot = chatSnapshot.child("users");

                    String receiverId = null;
                    String senderId = null;

                    if (usersSnapshot.child("receiverId").exists()) {
                        senderId = usersSnapshot.child("receiverId").getValue(String.class);
                    }

                    if (usersSnapshot.child("senderId").exists()) {
                        receiverId = usersSnapshot.child("senderId").getValue(String.class);
                    }

                    if (receiverId != null && receiverId.equals(currentBuyerId)) {
                        DatabaseReference messagesRef = chatSnapshot.child("messages").getRef();
                        String finalSenderId = senderId;
                        messagesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot messagesSnapshot) {
                                int unreadCount = 0;
                                for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                                    Boolean isRead = messageSnapshot.child("isRead").getValue(Boolean.class);
                                    String category = messageSnapshot.child("category").getValue(String.class);

                                    if (category != null && category.equals("vendor") && isRead != null && !isRead) {
                                        unreadCount++;
                                    }
                                }

                                DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference("vendors");
                                int finalUnreadCount = unreadCount;
                                vendorsRef.child(finalSenderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot vendorSnapshot) {
                                        Vendor vendor = vendorSnapshot.getValue(Vendor.class);
                                        if (vendor != null) {
                                            vendor.setUnreadMessageCount(finalUnreadCount);
                                            updateVendorList(vendor);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(buyer_messages.this, "Failed to load vendor details.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(buyer_messages.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(buyer_messages.this, "Failed to load chats.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVendorList(Vendor vendor) {
        boolean vendorExists = false;
        for (Vendor v : vendorList) {
            if (v.getUserId().equals(vendor.getUserId())) {
                v.setUnreadMessageCount(vendor.getUnreadMessageCount());
                vendorExists = true;
                break;
            }
        }

        if (!vendorExists) {
            vendorList.add(vendor);
        }

        // Sort by unread message count, descending
        vendorList.sort((v1, v2) -> Integer.compare(v2.getUnreadMessageCount(), v1.getUnreadMessageCount()));

        // Update filtered list
        filter(((SearchView) findViewById(R.id.search_bar)).getQuery().toString());
    }

    private void filter(String query) {
        filteredVendorList.clear();
        for (Vendor vendor : vendorList) {
            if (vendor.getStoreName().toLowerCase().contains(query.toLowerCase())) {
                filteredVendorList.add(vendor);
            }
        }
        adapter.notifyDataSetChanged();
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
        Intent intent = new Intent(this, buyer_messages.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Use FLAG_IMMUTABLE to ensure the PendingIntent is immutable
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Set your notification icon here
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo)) // Optional large icon
                .setContentTitle("Unread Messages")
                .setContentText("You have " + unreadCount + " unread messages from vendors.")
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

                    String senderId = usersSnapshot.child("senderId").getValue(String.class);
                    if (senderId != null && senderId.equals(userId)) {
                        for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                            String category = messageSnapshot.child("category").getValue(String.class);
                            Boolean isRead = messageSnapshot.child("isRead").getValue(Boolean.class);
                            if ("vendor".equals(category) && Boolean.FALSE.equals(isRead)) {
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
                Toast.makeText(buyer_messages.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(buyer_messages.this, buyer_homepage.class);
        startActivity(back);
        // Apply fade in and fade out animations
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}
