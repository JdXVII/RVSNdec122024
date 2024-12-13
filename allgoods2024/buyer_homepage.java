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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class buyer_homepage extends AppCompatActivity {

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isInternetAvailable(context)) {
                // If internet is available, reload the data
                fetchProducts();
                fetchTopProducts();
            } else {
                // Optionally, show a message indicating no connection
                Toast.makeText(buyer_homepage.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecyclerView verticalRecyclerView, horizontalRecyclerView;
    private BuyerProductAdapter productAdapter;
    private TopProductAdapter topProductAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private List<Product> topProductList;
    private Spinner productStatusSpinner;
    private LinearLayout purchases, cart, category, messages;
    private ShapeableImageView profileImage;
    private SearchView searchBar;
    private TextView messageBadge, see_more;
    private static final String CHANNEL_ID = "message_notifications";
    private static final String PREF_LAST_NOTIFIED_COUNT = "lastNotifiedCount";
    private SharedPreferences sharedPreferences;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference buyersRef;
    ImageView notification;
    private TextView notifBadge;

    private RecyclerView recyclerView;
    private LinearLayout dotsContainer;
    private DatabaseReference databaseReference;
    private EventImageAdapter adapter;
    private List<String> images = new ArrayList<>();
    private int currentImageIndex = 0;
    private RelativeLayout relativeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_homepage);

        // Register network connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        relativeContainer = findViewById(R.id.relativeContainer);
        recyclerView = findViewById(R.id.eventRecyclerView);
        dotsContainer = findViewById(R.id.dotsContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        fetchEventData();

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        buyersRef = FirebaseDatabase.getInstance().getReference("buyers");

        verticalRecyclerView = findViewById(R.id.verticalRecyclerView);
        horizontalRecyclerView = findViewById(R.id.horizontalRecyclerView);
        productStatusSpinner = findViewById(R.id.productStatusSpinner);
        searchBar = findViewById(R.id.search_bar);
        messageBadge = findViewById(R.id.message_badge);
        notification = findViewById(R.id.notification);
        notifBadge = findViewById(R.id.notif_badge);

        see_more = findViewById(R.id.see_more);
        see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent see_more = new Intent(buyer_homepage.this, buyer_preorder.class);
                startActivity(see_more);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification = new Intent(buyer_homepage.this, buyer_notification.class);
                startActivity(notification);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(buyer_homepage.this, buyer_account.class);
                startActivity(profile);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        messages = findViewById(R.id.messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messages = new Intent(buyer_homepage.this, buyer_messages.class);
                startActivity(messages);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(buyer_homepage.this, buyer_addtocart.class);
                startActivity(cart);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        category = findViewById(R.id.category);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent category = new Intent(buyer_homepage.this, buyer_category.class);
                startActivity(category);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        purchases = findViewById(R.id.purchases);
        purchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent purchases = new Intent(buyer_homepage.this, buyer_purchases.class);
                startActivity(purchases);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        verticalRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        topProductList = new ArrayList<>();
        productAdapter = new BuyerProductAdapter(filteredProductList, new BuyerProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(buyer_homepage.this, buyer_buy_product.class);
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("userId", product.getUserId());
                startActivity(intent);
            }
        });
        topProductAdapter = new TopProductAdapter(topProductList, new TopProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(buyer_homepage.this, buyer_buy_product.class);
                intent.putExtra("productId", product.getProductId());
                intent.putExtra("userId", product.getUserId());
                startActivity(intent);
            }
        });

        verticalRecyclerView.setAdapter(productAdapter);
        horizontalRecyclerView.setAdapter(topProductAdapter);
        setupSpinner();
        fetchProducts();
        fetchTopProducts();
        loadProfileImage();
        checkForUnreadMessages();
        createNotificationChannel();
        fetchUnreadWarnings();
        fetchPreOrderProducts();

        // Check if the vendor account is deactivated
        checkBuyerStatus(currentUser.getUid());

        // Add SearchView listener
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query); // Filter based on search query
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText); // Filter based on search query
                return true;
            }
        });
        searchBar.setQueryHint("Search products");
        int searchTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = findViewById(searchTextId);
        if (searchTextView != null) {
            searchTextView.setTextColor(Color.BLACK);
        }
    }


    private void fetchEventData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot event : snapshot.getChildren()) {
                        DataSnapshot imagesSnapshot = event.child("images");
                        for (DataSnapshot image : imagesSnapshot.getChildren()) {
                            images.add(image.getValue(String.class));
                            relativeContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    setupRecyclerView();
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyer_homepage.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new EventImageAdapter(this, images);
        recyclerView.setAdapter(adapter);

        createDots();

        startAutoScroll();
    }

    private void createDots() {
        dotsContainer.removeAllViews();
        for (int i = 0; i < images.size(); i++) {
            View dot = new View(this);
            dot.setBackgroundResource(R.drawable.dot_unselected); // Custom drawable for unselected dot
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(5, 0, 5, 0);
            dotsContainer.addView(dot, params);
        }
        updateDotIndicator(0);
    }

    private void updateDotIndicator(int position) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            dotsContainer.getChildAt(i).setBackgroundResource(
                    i == position ? R.drawable.dot_selected : R.drawable.dot_unselected // Custom drawable for selected/unselected dots
            );
        }
    }

    private void startAutoScroll() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentImageIndex == images.size()) {
                    currentImageIndex = 0;
                }
                recyclerView.smoothScrollToPosition(currentImageIndex);
                updateDotIndicator(currentImageIndex);
                currentImageIndex++;
                handler.postDelayed(this, 3000); // Scroll every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);
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

    // Method to fetch unread warnings
    private void fetchUnreadWarnings() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();

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
                int finalUnreadCount = warningCount;
                updateNotificationBadge(finalUnreadCount);
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

    private void checkBuyerStatus(String userId) {
        buyersRef = buyersRef.child(userId);

        buyersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    Toast.makeText(buyer_homepage.this, "Your account has been deactivated.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(buyer_homepage.this, buyer_login.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_homepage.this, "Failed to check vendor status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("All"); // Always include "All" as the first option

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("product_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    if (category != null) {
                        categories.add(category); // Add each category from the database
                    }
                }

                // Set up the adapter with the dynamically fetched categories
                ArrayAdapter<String> adapter = new ArrayAdapter<>(buyer_homepage.this,
                        R.layout.custom_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productStatusSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(buyer_homepage.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the listener for spinner item selection
        productStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterProducts(searchBar.getQuery().toString(), selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterProducts(searchBar.getQuery().toString(), "All");
            }
        });
    }


    private void fetchPreOrderProducts() {
        // Reference to RecyclerView
        RecyclerView recyclerView = findViewById(R.id.pre_order_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Firebase reference
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("upload_products");
        List<Product> preOrderList = new ArrayList<>();

        productsRef.orderByChild("productType").equalTo("Pre Order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                preOrderList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && "approved".equalsIgnoreCase(product.getStatus())) {
                        preOrderList.add(product);

                        if (preOrderList.size() >= 5) break; // Limit to 5 items
                    }
                }

                if (!preOrderList.isEmpty()) {
                    // Set adapter
                    PreOrderAdapter preOrderAdapter = new PreOrderAdapter(preOrderList, new PreOrderAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Product product) {
                            Intent intent = new Intent(buyer_homepage.this, buyer_buy_product.class);
                            intent.putExtra("productId", product.getProductId());
                            intent.putExtra("userId", product.getUserId());
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(preOrderAdapter);
                } else {
                    Toast.makeText(buyer_homepage.this, "No Pre Order products available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_homepage.this, "Failed to fetch Pre Order products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("upload_products");
        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference("vendors");

        vendorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot vendorSnapshot) {
                Map<String, Vendor> approvedVendors = new HashMap<>();

                for (DataSnapshot vendorData : vendorSnapshot.getChildren()) {
                    Vendor vendor = vendorData.getValue(Vendor.class);
                    if (vendor != null && "approved".equalsIgnoreCase(vendor.getStatus())) {
                        approvedVendors.put(vendor.getUserId(), vendor);
                    }
                }

                productsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                        List<Product> unsortedProductList = new ArrayList<>();

                        for (DataSnapshot productData : productSnapshot.getChildren()) {
                            Product product = productData.getValue(Product.class);
                            if (product != null
                                    && "approved".equalsIgnoreCase(product.getStatus())
                                    && "Regular".equalsIgnoreCase(product.getProductType())) { // Check productType
                                String productUserId = product.getUserId();

                                if (approvedVendors.containsKey(productUserId)) {
                                    unsortedProductList.add(product);
                                }
                            }
                        }

                        // Sort products by the `sold` field in descending order
                        Collections.sort(unsortedProductList, (p1, p2) -> {
                            int sold1 = Integer.parseInt(p1.getSold());
                            int sold2 = Integer.parseInt(p2.getSold());
                            return Integer.compare(sold2, sold1); // Descending order
                        });

                        // Clear the global product list and add sorted products
                        productList.clear();
                        productList.addAll(unsortedProductList);

                        // Filter products based on search query and selected category
                        filterProducts(searchBar.getQuery().toString(), productStatusSpinner.getSelectedItem().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(buyer_homepage.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_homepage.this, "Failed to load vendor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTopProducts() {
        DatabaseReference purchasesRef = FirebaseDatabase.getInstance().getReference("purchases");
        purchasesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Integer> productCountMap = new HashMap<>();
                for (DataSnapshot purchaseSnapshot : dataSnapshot.getChildren()) {
                    String productId = purchaseSnapshot.child("productId").getValue(String.class);
                    if (productId != null) {
                        int count = productCountMap.getOrDefault(productId, 0);
                        productCountMap.put(productId, count + 1);
                    }
                }

                List<Map.Entry<String, Integer>> productCountList = new ArrayList<>(productCountMap.entrySet());
                Collections.sort(productCountList, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });

                List<String> topProductIds = new ArrayList<>();
                for (int i = 0; i < Math.min(10, productCountList.size()); i++) {
                    topProductIds.add(productCountList.get(i).getKey());
                }

                fetchTopProductDetails(topProductIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_homepage.this, "Failed to load top products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTopProductDetails(List<String> topProductIds) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("upload_products");
        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference("vendors");

        // Listen for real-time updates in vendors and products
        vendorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot vendorSnapshot) {
                Map<String, Vendor> approvedVendors = new HashMap<>();

                // Store all approved vendors in a map
                for (DataSnapshot vendorData : vendorSnapshot.getChildren()) {
                    Vendor vendor = vendorData.getValue(Vendor.class);
                    if (vendor != null && "approved".equalsIgnoreCase(vendor.getStatus())) {
                        approvedVendors.put(vendor.getUserId(), vendor);
                    }
                }

                // Now fetch products and check if their vendor is approved
                productsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                        topProductList.clear();

                        for (DataSnapshot productData : productSnapshot.getChildren()) {
                            Product product = productData.getValue(Product.class);
                            if (product != null
                                    && topProductIds.contains(product.getProductId())
                                    && "approved".equalsIgnoreCase(product.getStatus())
                                    && "Regular".equalsIgnoreCase(product.getProductType())) { // Check productType
                                String productUserId = product.getUserId();

                                if (approvedVendors.containsKey(productUserId)) {
                                    topProductList.add(product);
                                }
                            }
                        }

                        // Notify the adapter of changes
                        topProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(buyer_homepage.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_homepage.this, "Failed to load vendor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String query) {
        filterProducts(query, productStatusSpinner.getSelectedItem().toString());
    }

    private void filterProducts(String query, String category) {
        filteredProductList.clear();
        if (category.equals("All")) {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        } else {
            for (Product product : productList) {
                if (product.getCategory().equalsIgnoreCase(category) &&
                        product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void loadProfileImage() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("buyers").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null) {
                        Glide.with(buyer_homepage.this)
                                .load(profileImageUrl)
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_homepage.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(buyer_homepage.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Apply fade in and fade out animations
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
