package com.example.allgoods2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class vendor_commission extends AppCompatActivity {

    private ImageView bckdet;
    private TextView totalCommissionTextView, totalSalesTextView, vegetablesTextView, fruitsTextView, driedFoodsTextView, othersTextView;
    private RecyclerView commissionRecyclerView;
    private double totalCommission = 0.0;
    private double totalSales = 0.0;
    private double vegetablesSales = 0.0;
    private double fruitsSales = 0.0;
    private double driedFoodsSales = 0.0;
    private double othersSales = 0.0;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference purchasesRef, productsRef;

    private CommissionAdapter commissionAdapter;
    private List<Purchase> vendorPurchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_commission);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bckdet = findViewById(R.id.bckdet);

        bckdet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bckdet = new Intent(vendor_commission.this, vendor_account.class);
                startActivity(bckdet);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // Initialize views
        totalCommissionTextView = findViewById(R.id.total_commission);
        totalSalesTextView = findViewById(R.id.total_sales);
        vegetablesTextView = findViewById(R.id.vegetables);
        fruitsTextView = findViewById(R.id.fruits);
        driedFoodsTextView = findViewById(R.id.dried_foods);
        othersTextView = findViewById(R.id.others);
        commissionRecyclerView = findViewById(R.id.commissionRecyclerView);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        purchasesRef = database.getReference("purchases");
        productsRef = database.getReference("upload_products");

        // Initialize RecyclerView and adapter
        vendorPurchases = new ArrayList<>();
        commissionAdapter = new CommissionAdapter(vendorPurchases);
        commissionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commissionRecyclerView.setAdapter(commissionAdapter);

        // Fetch data
        fetchVendorPurchases();

        ImageView rightImage = findViewById(R.id.right_image);
        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current vendor's userId
                String currentVendorId = mAuth.getCurrentUser().getUid();

                // Create an intent to navigate to vendor_commission_receipt
                Intent intent = new Intent(vendor_commission.this, vendor_commission_receipt.class);
                // Pass the userId to the next activity
                intent.putExtra("userId", currentVendorId);
                startActivity(intent);
                finish();
            }
        });

    }

    private void fetchVendorPurchases() {
        String currentVendorId = mAuth.getCurrentUser().getUid();

        purchasesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                vendorPurchases.clear();
                totalCommission = 0.0;
                totalSales = 0.0;
                vegetablesSales = 0.0;
                fruitsSales = 0.0;
                driedFoodsSales = 0.0;
                othersSales = 0.0;

                for (DataSnapshot purchaseSnapshot : snapshot.getChildren()) {
                    Purchase purchase = purchaseSnapshot.getValue(Purchase.class);
                    // Check if the purchase is completed
                    if (purchase != null && "Completed".equals(purchase.getStatus()) && purchase.getFee() > 0) {
                        // Check ownership in the upload_products table
                        checkOwnershipAndFetchProduct(purchase, currentVendorId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
                Log.e("FirebaseError", "Error fetching data: " + error.getMessage());
                Toast.makeText(vendor_commission.this, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOwnershipAndFetchProduct(Purchase purchase, String currentVendorId) {
        String productId = purchase.getProductId();

        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String productOwnerId = snapshot.child("userId").getValue(String.class);
                    String category = snapshot.child("category").getValue(String.class);

                    if (currentVendorId.equals(productOwnerId)) {
                        // Add purchase to the list if the current vendor owns the product
                        vendorPurchases.add(purchase);
                        updateTotalValues(purchase, category);
                        commissionAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    private void updateTotalValues(Purchase purchase, String category) {
        double totalPrice = Double.parseDouble(purchase.getTotalPrice());
        double fee = purchase.getFee();

        // Update total commission and sales
        totalCommission += fee;
        totalSales += totalPrice;

        // Update category-specific totals
        switch (category) {
            case "Vegetables":
                vegetablesSales += totalPrice;
                break;
            case "Fruits":
                fruitsSales += totalPrice;
                break;
            case "Dried Foods":
                driedFoodsSales += totalPrice;
                break;
            default:
                othersSales += totalPrice;
                break;
        }

        // Update TextViews
        totalCommissionTextView.setText(String.format("₱%.2f", totalCommission));
        totalSalesTextView.setText(String.format("₱%.2f", totalSales));
        vegetablesTextView.setText(String.format("₱%.2f", vegetablesSales));
        fruitsTextView.setText(String.format("₱%.2f", fruitsSales));
        driedFoodsTextView.setText(String.format("₱%.2f", driedFoodsSales));
        othersTextView.setText(String.format("₱%.2f", othersSales));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_commission.this, vendor_account.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}