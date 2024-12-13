package com.example.allgoods2024;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class vendor_orders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PurchaseAdapter adapter;
    private List<Purchase> purchaseList;
    private Spinner productTypeSpinner;
    private TextView errorTextView;
    private Spinner statusSpinner;
    private LinearLayout events, sales, message, home;
    private TextView messageBadge;
    private String VendorId;
    private static final String CHANNEL_ID = "vendor_message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchPurchases();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(vendor_orders.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_orders);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

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

        errorTextView = findViewById(R.id.errorTextView);
        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent events = new Intent(vendor_orders.this, vendor_events.class);
                startActivity(events);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        sales = findViewById(R.id.sales);
        sales.setOnClickListener(view -> {
            Intent sales = new Intent(vendor_orders.this, vendor_report_generation.class);
            startActivity(sales);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent home = new Intent(vendor_orders.this, vendor_homepage.class);
            startActivity(home);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        messageBadge = findViewById(R.id.message_badge);
        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent message = new Intent(vendor_orders.this, vendor_messages.class);
                startActivity(message);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            VendorId = currentUser.getUid();
        } else {
            Toast.makeText(this, "Vendor not login.", Toast.LENGTH_SHORT).show();
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        // Your RecyclerView and ScrollToTop ImageView
        ImageView scrollToTopButton = findViewById(R.id.scroll_to_top_button);

        // Make sure the ImageView stays at the center of the screen
        scrollToTopButton.setVisibility(View.GONE);

        scrollToTopButton.setOnClickListener(v -> {
            // Get the total number of items in the RecyclerView
            int itemCount = recyclerView.getAdapter().getItemCount();

            // Smoothly scroll to the last item (position itemCount - 1)
            recyclerView.smoothScrollToPosition(itemCount - 1);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the user has scrolled down enough
                if (recyclerView.computeVerticalScrollOffset() > 0) {
                    // Show the ImageView when the RecyclerView is scrolled down
                    scrollToTopButton.setVisibility(View.VISIBLE);
                } else {
                    // Hide the ImageView when at the top
                    scrollToTopButton.setVisibility(View.GONE);
                }
            }
        });

        statusSpinner = findViewById(R.id.productStatusSpinner);

        TextView selectOrders = findViewById(R.id.select_orders);
        CheckBox selectAllCheckbox = findViewById(R.id.select_all_checkbox);
        LinearLayout selectionButtonsLayout = findViewById(R.id.selection_buttons_layout);
        ImageView updateButton = findViewById(R.id.update_button);
        ImageView cancelButton = findViewById(R.id.cancel_button);

        selectOrders.setOnClickListener(v -> {
            adapter.toggleSelectionMode(true);
            selectionButtonsLayout.setVisibility(View.VISIBLE);
            selectAllCheckbox.setVisibility(View.VISIBLE);
            selectAllCheckbox.setChecked(false); // Reset Select All when entering selection mode
        });

        cancelButton.setOnClickListener(v -> {
            adapter.toggleSelectionMode(false);
            selectionButtonsLayout.setVisibility(View.GONE);
            selectAllCheckbox.setVisibility(View.GONE);
        });

        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.selectAll(isChecked);
        });

        updateButton.setOnClickListener(v -> {
            List<Purchase> selectedPurchases = adapter.getSelectedPurchases();  // Get the selected purchases

            if (selectedPurchases.isEmpty()) {
                Toast.makeText(this, "No purchases selected", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] options = {"Order Processed", "Completed"};
            final int[] selectedStatusIndex = {-1};  // Track the selected status index

            // Show the status update dialog
            new AlertDialog.Builder(this)
                    .setTitle("Update Status")
                    .setSingleChoiceItems(options, selectedStatusIndex[0], (dialog, which) -> {
                        selectedStatusIndex[0] = which;  // Store the selected status index
                    })
                    .setPositiveButton("Select", (dialog, which) -> {
                        if (selectedStatusIndex[0] == -1) {
                            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get the selected status
                        String selectedStatus = options[selectedStatusIndex[0]];

                        // Show confirmation dialog
                        new AlertDialog.Builder(this)
                                .setTitle("Confirm Update")
                                .setMessage("Are you sure you want to update the status to " + selectedStatus + "?")
                                .setPositiveButton("Yes", (innerDialog, which1) -> {
                                    // Show ProgressDialog
                                    ProgressDialog progressDialog = new ProgressDialog(this);
                                    progressDialog.setMessage("Updating status...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    // Update the status in Firebase for each selected purchase
                                    for (Purchase purchase : selectedPurchases) {
                                        DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchases").child(purchase.getPurchaseId());
                                        purchaseRef.child("status").setValue(selectedStatus)
                                                .addOnSuccessListener(aVoid -> {
                                                    if (selectedStatus.equals("Completed")) {
                                                        // Delete the timestamp field
                                                        purchaseRef.child("timestamp").removeValue()
                                                                .addOnSuccessListener(aVoid1 -> {
                                                                    Toast.makeText(this, "Timestamp removed", Toast.LENGTH_SHORT).show();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(this, "Failed to remove timestamp", Toast.LENGTH_SHORT).show();
                                                                });
                                                        // Create the sales report if the status is "Completed"
                                                        createSalesReport(
                                                                purchase.getProductId(),
                                                                purchase.getCategory(),
                                                                Double.parseDouble(purchase.getTotalPrice()),
                                                                purchase.getQuantity()
                                                        );
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();  // Dismiss ProgressDialog on failure
                                                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                                                });
                                    }

                                    progressDialog.dismiss();  // Dismiss ProgressDialog on success
                                    Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show();

                                    // Hide the multi-select UI elements
                                    adapter.toggleSelectionMode(false);
                                    selectionButtonsLayout.setVisibility(View.GONE);
                                    selectAllCheckbox.setVisibility(View.GONE);

                                    innerDialog.dismiss();
                                    dialog.dismiss();  // Close the original dialog
                                })
                                .setNegativeButton("No", (innerDialog, which1) -> innerDialog.dismiss())
                                .show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // Reverse the layout
        layoutManager.setStackFromEnd(true);
        VendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recyclerView.setLayoutManager(layoutManager);
        purchaseList = new ArrayList<>();
        adapter = new PurchaseAdapter(purchaseList, this, VendorId);
        recyclerView.setAdapter(adapter);

        // Set up Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.buyer_purchase_status, R.layout.custom_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(spinnerAdapter);

        // Spinner item selection listener
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchPurchases(); // Fetch data based on the selected status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up ImageView for navigation
        ImageView purchase_done = findViewById(R.id.purchase_done);
        purchase_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent purchase_done = new Intent(vendor_orders.this, vendor_purchase_done.class);
                startActivity(purchase_done);
            }
        });

        // Fetch purchases on activity start
        fetchPurchases();
        checkForUnreadMessages();
        createNotificationChannel();
        checkAndRemoveExpiredPurchases();

    }

    // Method to create the sales report
    private void createSalesReport(String productId, String category, double totalPrice, int quantity) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);

                if (product != null) {
                    String productName = product.getProductName();
                    String vendorId = product.getUserId();
                    String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
                    String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());

                    // Get current calendar instance and set the correct start and end of the week
                    Calendar cal = Calendar.getInstance();
                    int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
                    String weekKey = "week_" + weekOfMonth;

                    // Set the calendar to the start of the week (e.g., Monday)
                    cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                    String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

                    // Set the calendar to the end of the week (e.g., Sunday)
                    cal.add(Calendar.DATE, 7);
                    String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

                    DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports")
                            .child(vendorId).child("sales").child(currentYear).child(currentMonth).child(weekKey);

                    reportRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            SalesReport salesReport = mutableData.getValue(SalesReport.class);

                            // Create a new week if the existing report does not cover the current date
                            if (salesReport == null || !isDateWithinRange(salesReport.getStartDate(), salesReport.getEndDate())) {
                                salesReport = new SalesReport(startDate, endDate, 0, 0, new HashMap<>());
                            }

                            // Update sales report totals
                            salesReport.setTotalSales(salesReport.getTotalSales() + totalPrice);
                            salesReport.setTotalQuantity(salesReport.getTotalQuantity() + quantity);

                            Map<String, CategorySales> categorySalesMap = salesReport.getCategorySalesMap();
                            CategorySales categorySales = categorySalesMap.get(category);

                            if (categorySales == null) {
                                categorySales = new CategorySales(new HashMap<>());
                            }

                            // Create a new itemId for every new unique entry
                            String newItemId = reportRef.push().getKey();
                            ItemSales itemSales = new ItemSales(productName, quantity, totalPrice);

                            categorySales.getItemSalesMap().put(newItemId, itemSales);
                            categorySalesMap.put(category, categorySales);
                            salesReport.setCategorySalesMap(categorySalesMap);

                            mutableData.setValue(salesReport);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                            if (databaseError != null) {
                                Toast.makeText(vendor_orders.this, "Failed to update sales report", Toast.LENGTH_SHORT).show();
                            } else if (committed) {
                                Toast.makeText(vendor_orders.this, "Sales report updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_orders.this, "Failed to fetch product data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Utility method to check if the current date is within the range
    private boolean isDateWithinRange(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = new Date();
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            return currentDate.compareTo(start) >= 0 && currentDate.compareTo(end) <= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fetchPurchases() {
        // Check Internet Connection
        if (!isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your connection and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching purchases...");
        progressDialog.setCancelable(false); // Prevent user from dismissing the dialog
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference("upload_products")
                .orderByChild("userId")
                .equalTo(VendorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> productIds = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            productIds.add(snapshot.child("productId").getValue(String.class));
                        }

                        if (!productIds.isEmpty()) {
                            FirebaseDatabase.getInstance().getReference("purchases")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            purchaseList.clear();
                                            String selectedStatus = statusSpinner.getSelectedItem().toString();
                                            String selectedType = productTypeSpinner.getSelectedItem().toString(); // Get selected type

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String productId = snapshot.child("productId").getValue(String.class);
                                                String status = snapshot.child("status").getValue(String.class);
                                                String productType = snapshot.child("productType").getValue(String.class); // Get product type

                                                // Filter by product ID, status, and product type
                                                if (productIds.contains(productId) &&
                                                        !status.equals("Completed") && // Exclude "Completed" status
                                                        (selectedStatus.equals("All") || status.equals(selectedStatus)) &&
                                                        (selectedType.equals("All") || productType.equals(selectedType))) {
                                                    Purchase purchase = snapshot.getValue(Purchase.class);
                                                    purchaseList.add(purchase);
                                                }
                                            }

                                            // Sort purchases by date
                                            Collections.sort(purchaseList, new Comparator<Purchase>() {
                                                @Override
                                                public int compare(Purchase p1, Purchase p2) {
                                                    return p2.getPurchaseDate().compareTo(p1.getPurchaseDate());
                                                }
                                            });

                                            adapter.notifyDataSetChanged();
                                            progressDialog.dismiss(); // Dismiss ProgressDialog after data is fetched
                                            if (purchaseList.isEmpty()) {
                                                showError("No purchases found for the selected status and type.");
                                            } else {
                                                errorTextView.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            progressDialog.dismiss(); // Dismiss ProgressDialog on error
                                            showError("Error fetching purchases: " + databaseError.getMessage());
                                        }
                                    });
                        } else {
                            progressDialog.dismiss(); // Dismiss ProgressDialog if no products found
                            showError("No products found for the current vendor.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressDialog.dismiss(); // Dismiss ProgressDialog on error
                        showError("Error fetching products: " + databaseError.getMessage());
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


    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
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
                Toast.makeText(vendor_orders.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_orders.this, vendor_homepage.class);
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
