package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class vendor_events extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Product> productList;

    private RecyclerView horizontalRecyclerView;
    private VendorEventImageAdapter vendorEventImageAdapter;
    private TextView titleTextView, descriptionTextView, dateTextView, discountTextView;

    private ImageView updateButton, cancelButton;
    private CheckBox selectAllCheckbox;
    private LinearLayout selectionButtonsLayout;

    private LinearLayout orders, sales, message, home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_events);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        orders = findViewById(R.id.orders);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent events = new Intent(vendor_events.this, vendor_orders.class);
                startActivity(events);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        sales = findViewById(R.id.sales);
        sales.setOnClickListener(view -> {
            Intent sales = new Intent(vendor_events.this, vendor_report_generation.class);
            startActivity(sales);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        home = findViewById(R.id.home);
        home.setOnClickListener(view -> {
            Intent home = new Intent(vendor_events.this, vendor_homepage.class);
            startActivity(home);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent message = new Intent(vendor_events.this, vendor_messages.class);
                startActivity(message);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, productList);
        recyclerView.setAdapter(eventAdapter);

        // Initialize horizontal RecyclerView
        horizontalRecyclerView = findViewById(R.id.horizontalRecyclerView);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize TextViews
        titleTextView = findViewById(R.id.title);
        descriptionTextView = findViewById(R.id.description);
        dateTextView = findViewById(R.id.date);
        discountTextView = findViewById(R.id.discount);

        // Initialize buttons and checkbox
        updateButton = findViewById(R.id.update_button);
        cancelButton = findViewById(R.id.cancel_button);
        selectAllCheckbox = findViewById(R.id.select_all_checkbox);
        selectionButtonsLayout = findViewById(R.id.selection_buttons_layout);

        // Initialize SearchView
        SearchView searchView = findViewById(R.id.search_bar);

        // Listen for changes in the search query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // You can leave it empty if you don't want to handle search submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        // Get current vendor ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentVendorId = auth.getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("upload_products");
        databaseReference.orderByChild("userId").equalTo(currentVendorId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Product product = dataSnapshot.getValue(Product.class);
                            if (product != null && "approved".equals(product.getStatus())) {
                                productList.add(product);
                            }
                        }
                        eventAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(vendor_events.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set Adapter
        eventAdapter = new EventAdapter(this, productList);
        recyclerView.setAdapter(eventAdapter);

        // Fetch event data
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        Event event = eventSnapshot.getValue(Event.class);
                        if (event != null) {
                            // Populate TextViews with event data
                            titleTextView.setText(event.getTitle());
                            descriptionTextView.setText(event.getDescription());
                            dateTextView.setText("Exp: " + event.getEventDate());
                            discountTextView.setText(event.getDiscount() + "%");

                            // Populate horizontal RecyclerView with event images
                            vendorEventImageAdapter = new VendorEventImageAdapter(vendor_events.this, event.getImages());
                            horizontalRecyclerView.setAdapter(vendorEventImageAdapter);

                            // Stop after first event (or modify logic for multiple events)
                            break;
                        }
                    }
                } else {
                    // Hide all UI elements except for the message
                    recyclerView.setVisibility(View.GONE);
                    horizontalRecyclerView.setVisibility(View.GONE);
                    titleTextView.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.GONE);
                    dateTextView.setVisibility(View.GONE);
                    discountTextView.setVisibility(View.GONE);
                    updateButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    selectAllCheckbox.setVisibility(View.GONE);
                    selectionButtonsLayout.setVisibility(View.GONE);
                    findViewById(R.id.select_products).setVisibility(View.GONE);
                    findViewById(R.id.discount_text).setVisibility(View.GONE);
                    findViewById(R.id.error).setVisibility(View.VISIBLE);

                    // Show a message indicating no events are available
                    Toast.makeText(vendor_events.this, "No events available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(vendor_events.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });

        // Event update button
        updateButton.setOnClickListener(v -> {
            // Show a dialog to ask whether to add or remove products
            new AlertDialog.Builder(vendor_events.this)
                    .setTitle("Select Action")
                    .setMessage("Do you want to add or remove products from the event?")
                    .setPositiveButton("Add Products", (dialog, which) -> {
                        updateProducts(true);
                    })
                    .setNegativeButton("Remove Products", (dialog, which) -> {
                        updateProducts(false);
                    })
                    .setCancelable(false)
                    .show();
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> {
            for (Product product : productList) {
                product.setSelected(false); // Uncheck all items
            }
            eventAdapter.setShowCheckboxes(false); // Hide all checkboxes
            selectionButtonsLayout.setVisibility(View.GONE);
            selectAllCheckbox.setVisibility(View.GONE);
        });


        // Show product checkboxes when select_products clicked
        TextView selectProducts = findViewById(R.id.select_products);
        selectProducts.setOnClickListener(v -> {
            eventAdapter.setShowCheckboxes(true); // Show all checkboxes
            selectionButtonsLayout.setVisibility(View.VISIBLE);
            selectAllCheckbox.setVisibility(View.VISIBLE);
        });


        // Select all checkbox
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (Product product : productList) {
                product.setSelected(isChecked);
            }
            eventAdapter.notifyDataSetChanged(); // Refresh all items
        });

    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        eventAdapter.updateProductList(filteredList);
    }

    private void updateProducts(boolean isAdding) {
        // Collect IDs of all selected products
        List<String> selectedProductIds = new ArrayList<>();
        for (Product product : productList) {
            if (product.isSelected()) { // Check the selected status from the Product object
                selectedProductIds.add(product.getProductId());
            }
        }

        if (!selectedProductIds.isEmpty()) {
            String actionMessage = isAdding ? "Add to Sale Event" : "Remove from Sale Event";
            boolean eventStatus = isAdding;

            new AlertDialog.Builder(vendor_events.this)
                    .setTitle(actionMessage)
                    .setMessage("Are you sure you want to " + (isAdding ? "add" : "remove") + " these products from the event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Track the count of completed operations
                        final int[] successCount = {0};
                        final int total = selectedProductIds.size();
                        final boolean[] failed = {false};

                        for (String productId : selectedProductIds) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("upload_products");
                            databaseReference.child(productId).child("event").setValue(eventStatus)
                                    .addOnSuccessListener(unused -> {
                                        successCount[0]++;
                                        if (successCount[0] == total && !failed[0]) {
                                            Toast.makeText(vendor_events.this, "Products updated successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        failed[0] = true;
                                        Toast.makeText(vendor_events.this, "Failed to update some products.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Toast.makeText(vendor_events.this, "No products selected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_events.this, vendor_homepage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
