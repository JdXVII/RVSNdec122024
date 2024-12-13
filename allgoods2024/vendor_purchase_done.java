package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class vendor_purchase_done extends AppCompatActivity {

    private RecyclerView recyclerViewDelivered;
    private VendorPurchaseDoneAdapter adapter;
    private List<Purchase> purchaseList;
    private TextView errorTextViewDelivered;
    private ImageView bckdo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_purchase_done);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bckdo = findViewById(R.id.bckdo);

        bckdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bckdo = new Intent(vendor_purchase_done.this, vendor_orders.class);
                startActivity(bckdo);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        recyclerViewDelivered = findViewById(R.id.recyclerViewDelivered);

        // Set up LinearLayoutManager with reversed layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);  // Reverse the order
        layoutManager.setStackFromEnd(true);   // Ensure items start from the end of the list
        recyclerViewDelivered.setLayoutManager(layoutManager);

        purchaseList = new ArrayList<>();
        adapter = new VendorPurchaseDoneAdapter(purchaseList, this::showPurchaseDetails);// Pass context to adapter
        recyclerViewDelivered.setAdapter(adapter);

        errorTextViewDelivered = findViewById(R.id.errorTextViewDelivered);

        fetchDeliveredPurchases();
    }

    private void showPurchaseDetails(Purchase purchase) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.vendor_dialog_purchase_receipt, null);

        TextView storeName = dialogView.findViewById(R.id.dialog_store_name);
        TextView productName = dialogView.findViewById(R.id.dialog_product_name);
        TextView quantity = dialogView.findViewById(R.id.dialog_quantity);
        TextView deliveryMethod = dialogView.findViewById(R.id.dialog_delivery_method);
        TextView paymentMethod = dialogView.findViewById(R.id.dialog_payment_method);
        TextView totalPrice = dialogView.findViewById(R.id.dialog_total_price);
        TextView price = dialogView.findViewById(R.id.dialog_price);
        TextView deliveryPrice = dialogView.findViewById(R.id.dialog_delivery_price);
        TextView allPrice = dialogView.findViewById(R.id.dialog_all_price);
        TextView type = dialogView.findViewById(R.id.dialog_type);
        TextView buyerName = dialogView.findViewById(R.id.dialog_buyer_name);
        TextView province = dialogView.findViewById(R.id.dialog_province);
        TextView city = dialogView.findViewById(R.id.dialog_city);
        TextView barangay = dialogView.findViewById(R.id.dialog_barangay);
        TextView zipCode = dialogView.findViewById(R.id.dialog_zip_code);
        TextView zone = dialogView.findViewById(R.id.dialog_zone);
        TextView date = dialogView.findViewById(R.id.dialog_date);

        storeName.setText(purchase.getStoreName());
        productName.setText("Product Name: " + purchase.getProductName());
        quantity.setText("Quantity: " + purchase.getQuantity());
        deliveryMethod.setText("Delivery Method: " + purchase.getDeliveryMethod());
        paymentMethod.setText("Payment: " + purchase.getPaymentMethod());
        type.setText(purchase.gettype());
        totalPrice.setText("Total Price: " + purchase.getTotalPrice());
        price.setText("Product Price: " + purchase.getprice());
        deliveryPrice.setText("Delivery Payment: " + purchase.getDeliveryPayment());
        allPrice.setText("Total Payment: " + purchase.getTotal());
        buyerName.setText("Buyer Name: " + purchase.getFirstName() + " " + purchase.getLastName());
        province.setText("Province: " + purchase.getProvince());
        city.setText("City/Municipality: " + purchase.getCity());
        barangay.setText("Barangay: " + purchase.getBarangay());
        zipCode.setText("Zip Code: " + purchase.getZipCode());
        zone.setText("Zone: " + purchase.getZone());
        date.setText("Date: " + purchase.getPurchaseDate());

        // Create and show dialog with custom theme
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    private void fetchDeliveredPurchases() {
        String currentVendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("upload_products")
                .orderByChild("userId")
                .equalTo(currentVendorId)
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
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Purchase purchase = snapshot.getValue(Purchase.class);
                                                if (purchase != null && productIds.contains(purchase.getProductId()) && "Completed".equals(purchase.getStatus())) {
                                                    purchaseList.add(purchase);
                                                }
                                            }

                                            // Sort purchases by date (assuming getPurchaseDate() returns a date string in a sortable format)
                                            Collections.sort(purchaseList, new Comparator<Purchase>() {
                                                @Override
                                                public int compare(Purchase p1, Purchase p2) {
                                                    return p2.getPurchaseDate().compareTo(p1.getPurchaseDate()); // Most recent first
                                                }
                                            });

                                            adapter.notifyDataSetChanged();
                                            if (purchaseList.isEmpty()) {
                                                showError("No delivered purchases found.");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            showError("Error fetching purchases: " + databaseError.getMessage());
                                        }
                                    });
                        } else {
                            showError("No products found for the current vendor.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showError("Error fetching products: " + databaseError.getMessage());
                    }
                });
    }

    private void showError(String message) {
        errorTextViewDelivered.setText(message);
        errorTextViewDelivered.setVisibility(View.VISIBLE);
    }
}

