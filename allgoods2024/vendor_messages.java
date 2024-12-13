package com.example.allgoods2024;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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

public class vendor_messages extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchMessages();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(vendor_messages.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecyclerView recyclerViewVendorMessages;
    private VendorMessageProfiles adapter;
    private List<Buyer> buyerList;
    private List<Buyer> filteredBuyerList;
    private String currentVendorId;

    private LinearLayout events, orders, sales, home;
    private TextView messageBadge;
    private static final String CHANNEL_ID = "vendor_message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_messages);

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

        events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent events = new Intent(vendor_messages.this, vendor_events.class);
                startActivity(events);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        orders = findViewById(R.id.orders);
        orders.setOnClickListener(view -> {
            Intent orders = new Intent(vendor_messages.this, vendor_orders.class);
            startActivity(orders);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent home = new Intent(vendor_messages.this, vendor_homepage.class);
            startActivity(home);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        sales = findViewById(R.id.sales);
        sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sales = new Intent(vendor_messages.this, vendor_report_generation.class);
                startActivity(sales);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        messageBadge = findViewById(R.id.message_badge);

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentVendorId = currentUser.getUid(); // Get the current vendor's ID
        } else {
            // Handle the case when the user is not logged in
            Toast.makeText(this, "You need to log in to view messages.", Toast.LENGTH_LONG).show();
            finish(); // Close the current activity if not logged in
            return;
        }

        recyclerViewVendorMessages = findViewById(R.id.recyclerViewVendorMessages);
        recyclerViewVendorMessages.setLayoutManager(new LinearLayoutManager(this));
        buyerList = new ArrayList<>();
        filteredBuyerList = new ArrayList<>();
        adapter = new VendorMessageProfiles(this, filteredBuyerList, currentVendorId);
        recyclerViewVendorMessages.setAdapter(adapter);

        // Fetch data from Firebase
        fetchMessages();
        checkForUnreadMessages();
        createNotificationChannel();

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
    }

    private void fetchMessages() {
        // Check Internet Connection
        if (!isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your connection and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching messages...");
        progressDialog.setCancelable(false); // Prevent user from dismissing the dialog
        progressDialog.show();

        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buyerList.clear();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot usersSnapshot = chatSnapshot.child("users");

                    String receiverId = null;
                    String senderId = null;

                    if (usersSnapshot.child("receiverId").exists()) {
                        receiverId = usersSnapshot.child("receiverId").getValue(String.class);
                    }

                    if (usersSnapshot.child("senderId").exists()) {
                        senderId = usersSnapshot.child("senderId").getValue(String.class);
                    }

                    if (receiverId != null && receiverId.equals(currentVendorId)) {
                        DatabaseReference messagesRef = chatSnapshot.child("messages").getRef();
                        String finalSenderId = senderId;
                        messagesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot messagesSnapshot) {
                                int unreadCount = 0;
                                for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                                    Boolean isRead = messageSnapshot.child("isRead").getValue(Boolean.class);
                                    String category = messageSnapshot.child("category").getValue(String.class);

                                    if (category != null && category.equals("buyer") && isRead != null && !isRead) {
                                        unreadCount++;
                                    }
                                }

                                DatabaseReference buyersRef = FirebaseDatabase.getInstance().getReference("buyers");
                                int finalUnreadCount = unreadCount;
                                buyersRef.child(finalSenderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot buyerSnapshot) {
                                        Buyer buyer = buyerSnapshot.getValue(Buyer.class);
                                        if (buyer != null) {
                                            buyer.setUnreadMessageCount(finalUnreadCount);
                                            updateVendorList(buyer);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(vendor_messages.this, "Failed to load vendor details.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(vendor_messages.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                progressDialog.dismiss(); // Dismiss ProgressDialog after data is fetched
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss(); // Dismiss ProgressDialog on error
                Toast.makeText(vendor_messages.this, "Failed to load chats.", Toast.LENGTH_SHORT).show();
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


    private void updateVendorList(Buyer buyer) {
        boolean buyerExists = false;
        for (Buyer b : buyerList) {
            if (b.getUserId().equals(buyer.getUserId())) {
                b.setUnreadMessageCount(buyer.getUnreadMessageCount());
                buyerExists = true;
                break;
            }
        }

        if (!buyerExists) {
            buyerList.add(buyer);
        }

        // Sort by unread message count, descending
        buyerList.sort((v1, v2) -> Integer.compare(v2.getUnreadMessageCount(), v1.getUnreadMessageCount()));

        // Update filtered list
        filter(((SearchView) findViewById(R.id.search_bar)).getQuery().toString());
    }

    private void filter(String query) {
        filteredBuyerList.clear();
        for (Buyer buyer : buyerList) {
            if (buyer.getFirstName().toLowerCase().contains(query.toLowerCase()) || buyer.getLastName().toLowerCase().contains(query.toLowerCase())) {
                filteredBuyerList.add(buyer);
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
                Toast.makeText(vendor_messages.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_messages.this, vendor_homepage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}

