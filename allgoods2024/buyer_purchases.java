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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class buyer_purchases extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchPurchases();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(buyer_purchases.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecyclerView recyclerView;
    private BuyerPurchaseAdapter adapter;
    private List<Purchase> purchaseList;
    private List<Purchase> filteredList;
    private String currentUserId;
    private Spinner productStatusSpinner;
    private Spinner productTypeSpinner;
    private TextView messageBadge;
    private static final String CHANNEL_ID = "purchase_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;
    private LinearLayout category, cart, messages, market;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_purchases);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        productTypeSpinner = findViewById(R.id.productTypeSpinner);
        // Populate Spinner with product types
        ArrayAdapter<CharSequence> adapters = ArrayAdapter.createFromResource(this, R.array.product_type, R.layout.custom_spinner_item);
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productTypeSpinner.setAdapter(adapters);

        // Set listener for the Spinner
        productTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Fetch purchases based on selected type
                fetchPurchases();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        messageBadge = findViewById(R.id.message_badge);
        messages = findViewById(R.id.messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messages = new Intent(buyer_purchases.this, buyer_messages.class);
                startActivity(messages);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        category = findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent category = new Intent(buyer_purchases.this, buyer_messages.class);
                startActivity(category);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        market = findViewById(R.id.market);
        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent market = new Intent(buyer_purchases.this, buyer_homepage.class);
                startActivity(market);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(buyer_purchases.this, buyer_addtocart.class);
                startActivity(cart);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView purchase_done = findViewById(R.id.purchase_done);
        purchase_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mark notifications as read when navigating to buyer_purchase_done
                markNotificationsAsRead();
                Intent purchase_done = new Intent(buyer_purchases.this, buyer_purchase_done.class);
                startActivity(purchase_done);
            }
        });

        productStatusSpinner = findViewById(R.id.productStatusSpinner);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.buyer_purchase_status, R.layout.custom_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productStatusSpinner.setAdapter(adapterSpinner);

        productStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterPurchases(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        purchaseList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BuyerPurchaseAdapter(this, filteredList, currentUserId);
        recyclerView.setAdapter(adapter);

        // Create the notification channel
        createNotificationChannel();

        fetchPurchases();
        checkForUnreadMessages();
        checkAndRemoveExpiredPurchases();
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

    private void createNotificationChannel() {
        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Purchase Notifications";
            String description = "Channel for purchase notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void markNotificationsAsRead() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("purchases");
        databaseReference.orderByChild("status").equalTo("Completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Purchase purchase = snapshot.getValue(Purchase.class);
                    if (purchase != null && purchase.getUserId().equals(currentUserId)) {
                        // Mark purchase as read
                        snapshot.getRef().child("isRead").setValue(true);
                    }
                }
                updatePurchaseDoneBadge(); // Refresh badge
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(buyer_purchases.this, "Failed to mark notifications as read. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPurchases() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("purchases");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                purchaseList.clear();
                String selectedType = productTypeSpinner.getSelectedItem().toString();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productType = snapshot.child("productType").getValue(String.class);

                    Purchase purchase = snapshot.getValue(Purchase.class);
                    if (purchase != null && purchase.getUserId().equals(currentUserId) && !purchase.getStatus().equals("Completed") &&
                            (selectedType.equals("All") || productType.equals(selectedType))) {
                        // Add the purchase at the beginning of the list
                        purchaseList.add(0, purchase);
                    }
                }
                filterPurchases(productStatusSpinner.getSelectedItem().toString());
                updatePurchaseDoneBadge(); // Check for new delivered items
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(buyer_purchases.this, "Failed to load purchases", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPurchases(String status) {
        filteredList.clear();
        if (status.equals("All")) {
            filteredList.addAll(purchaseList);
        } else {
            for (Purchase purchase : purchaseList) {
                if (purchase.getStatus().equals(status)) {
                    filteredList.add(purchase);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updatePurchaseDoneBadge() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("purchases");
        databaseReference.orderByChild("status").equalTo("Completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int deliveredCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Purchase purchase = snapshot.getValue(Purchase.class);
                    if (purchase != null && purchase.getUserId().equals(currentUserId) && !purchase.getIsRead()) {
                        deliveredCount++;
                    }
                }

                ImageView purchaseDone = findViewById(R.id.purchase_done);
                TextView badgeText = findViewById(R.id.badge_text);

                // Show badge with deliveredCount
                if (deliveredCount > 0) {
                    badgeText.setVisibility(View.VISIBLE);
                    badgeText.setText(String.valueOf(deliveredCount));

                    // Get last notified count from SharedPreferences
                    int lastNotifiedCount = sharedPreferences.getInt(PREF_LAST_NOTIFIED_COUNT, -1);

                    // Send notification if deliveredCount is different from lastNotifiedCount
                    if (deliveredCount != lastNotifiedCount) {
                        sendNotification(deliveredCount);
                    }
                    // Update last notified count in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PREF_LAST_NOTIFIED_COUNT, deliveredCount);
                    editor.apply();
                } else {
                    // Hide badge if no delivered items
                    badgeText.setVisibility(View.GONE);

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
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(buyer_purchases.this, "Failed to update badge. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(int deliveredCount) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, buyer_purchases.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Use FLAG_IMMUTABLE to ensure the PendingIntent is immutable
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Set your notification icon here
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo)) // Optional large icon
                .setContentTitle("New Orders")
                .setContentText("You have " + deliveredCount + " new delivered orders.")
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
                Toast.makeText(buyer_purchases.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(buyer_purchases.this, buyer_homepage.class);
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

    public void checkAndRemoveExpiredPurchases() {
        // Reference to the purchases node in the database
        DatabaseReference purchasesRef = FirebaseDatabase.getInstance().getReference("purchases");

        purchasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot purchaseSnapshot : snapshot.getChildren()) {
                        try {
                            String timestampStr = purchaseSnapshot.child("timestamp").getValue(String.class);
                            String purchaseId = purchaseSnapshot.getKey();
                            String productId = purchaseSnapshot.child("productId").getValue(String.class);
                            int quantity = purchaseSnapshot.child("quantity").getValue(Integer.class);

                            // Parse the timestamp to a Date object
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date purchaseTimestamp = dateFormat.parse(timestampStr);

                            // Get current date and time
                            Date currentDate = new Date();

                            if (purchaseTimestamp != null && purchaseTimestamp.before(currentDate)) {
                                // Timestamp has passed, remove the purchase and update product stock
                                revertProductSoldAndRemovePurchase(productId, quantity, purchaseId);
                            }
                        } catch (Exception e) {
                            Log.e("TimestampCheck", "Error parsing or processing timestamp", e);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TimestampCheck", "Database error: " + error.getMessage());
            }
        });
    }

    private void revertProductSoldAndRemovePurchase(String productId, int quantity, String purchaseId) {
        // Reference to the product node
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        int currentStock = Integer.parseInt(snapshot.child("stock").getValue(String.class));
                        int soldQuantity = Integer.parseInt(snapshot.child("sold").getValue(String.class));

                        // Update stock and sold values
                        int updatedStock = currentStock + quantity;
                        int updatedSold = soldQuantity - quantity;

                        productRef.child("stock").setValue(String.valueOf(updatedStock))
                                .addOnSuccessListener(aVoid -> productRef.child("sold").setValue(String.valueOf(updatedSold))
                                        .addOnSuccessListener(aVoid1 -> {
                                            // Remove purchase entry
                                            DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchases").child(purchaseId);
                                            purchaseRef.removeValue()
                                                    .addOnSuccessListener(aVoid2 -> Log.i("TimestampCheck", "Purchase removed successfully"))
                                                    .addOnFailureListener(e -> Log.e("TimestampCheck", "Failed to remove purchase", e));
                                        })
                                        .addOnFailureListener(e -> Log.e("TimestampCheck", "Failed to update sold value", e)))
                                .addOnFailureListener(e -> Log.e("TimestampCheck", "Failed to update stock value", e));
                    } catch (Exception e) {
                        Log.e("TimestampCheck", "Error updating stock or sold values", e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TimestampCheck", "Failed to fetch product details: " + error.getMessage());
            }
        });
    }
}