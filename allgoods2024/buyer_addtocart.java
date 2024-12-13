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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class buyer_addtocart extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data

            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(buyer_addtocart.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private LinearLayout purchases, category, messages, market;
    private TextView messageBadge;
    private static final String CHANNEL_ID = "message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_addtocart);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        messageBadge = findViewById(R.id.message_badge);
        messages =findViewById(R.id.messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messages = new Intent(buyer_addtocart.this, buyer_messages.class);
                startActivity(messages);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        market = findViewById(R.id.market);
        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent market = new Intent(buyer_addtocart.this, buyer_homepage.class);
                startActivity(market);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        category = findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent category = new Intent(buyer_addtocart.this, buyer_category.class);
                startActivity(category);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        purchases = findViewById(R.id.purchases);
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent purchases = new Intent(buyer_addtocart.this, buyer_purchases.class);
                startActivity(purchases);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        RecyclerView cartRecyclerView = findViewById(R.id.cartrecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configure recyclerViewDeclineNotifications to reverse layout and stack from end
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true); // Display items in reverse order
        linearLayoutManager.setStackFromEnd(true);  // Stack items from the bottom
        cartRecyclerView.setLayoutManager(linearLayoutManager);

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("addtocart");
        cartRef.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<CartItem> cartItems = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItem cartItem = snapshot.getValue(CartItem.class);
                            cartItems.add(cartItem);
                        }
                        CartAdapter cartAdapter = new CartAdapter(cartItems, buyer_addtocart.this);
                        cartRecyclerView.setAdapter(cartAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(buyer_addtocart.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(buyer_addtocart.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations
        Intent back = new Intent(buyer_addtocart.this, buyer_homepage.class);
        startActivity(back);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}