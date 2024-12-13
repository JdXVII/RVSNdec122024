package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class buyer_purchase_done extends AppCompatActivity {

    private ImageView bckdel;
    private RecyclerView recyclerView;
    private BuyerPurchaseDoneAdapter adapter;
    private List<Purchase> purchaseList;
    private DatabaseReference databaseReference;
    private String currentUserId;
    private String currentUserFirstName; // Add field to store user's first name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_purchase_done);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        bckdel = findViewById(R.id.bckdel);

        bckdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bckdel = new Intent(buyer_purchase_done.this, buyer_purchases.class);
                startActivity(bckdel);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();  // Get current user ID

        // Fetch current user's first name
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("buyers").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Buyer buyer = dataSnapshot.getValue(Buyer.class); // Assuming you have a User class
                    if (buyer != null) {
                        currentUserFirstName = buyer.getFirstName();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_purchase_done.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);

        // Set up LinearLayoutManager with reversed layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);  // Reverse the order
        layoutManager.setStackFromEnd(true);   // Ensure items start from the end of the list
        recyclerView.setLayoutManager(layoutManager);

        purchaseList = new ArrayList<>();
        adapter = new BuyerPurchaseDoneAdapter(purchaseList, this::showPurchaseDetails, this::showRatingDialog);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("purchases");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                purchaseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Purchase purchase = snapshot.getValue(Purchase.class);
                    if (purchase != null && "Completed".equals(purchase.getStatus()) && currentUserId.equals(purchase.getUserId())) {
                        purchaseList.add(purchase);
                    }
                }

                // Sort the list by purchaseDate in descending order
                Collections.sort(purchaseList, new Comparator<Purchase>() {
                    @Override
                    public int compare(Purchase o1, Purchase o2) {
                        return o2.getPurchaseDate().compareTo(o1.getPurchaseDate());
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_purchase_done.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPurchaseDetails(Purchase purchase) {
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.buyer_dialog_purchase_receipt, null);

        // Find and set data to views
        TextView storeName = dialogView.findViewById(R.id.store_name);
        TextView productName = dialogView.findViewById(R.id.product_name);
        TextView quantity = dialogView.findViewById(R.id.quantity);
        TextView totalPrice = dialogView.findViewById(R.id.total_price);
        TextView total = dialogView.findViewById(R.id.total);
        TextView deliveryPayment = dialogView.findViewById(R.id.delivery_payment);
        TextView deliveryMethod = dialogView.findViewById(R.id.delivery);
        TextView paymentMethod = dialogView.findViewById(R.id.payment);
        TextView date = dialogView.findViewById(R.id.date);
        TextView address = dialogView.findViewById(R.id.address);

        storeName.setText("Store Name: " + purchase.getStoreName());
        productName.setText("Product Name: " + purchase.getProductName());
        quantity.setText("Quantity: " + purchase.getQuantity());
        deliveryPayment.setText("Delivery Payment: ₱" + purchase.getDeliveryPayment());
        total.setText("Total Payment: ₱" + purchase.getTotal());
        totalPrice.setText("Total Price: ₱" + purchase.getTotalPrice());
        deliveryMethod.setText("Delivery: " + purchase.getDeliveryMethod());
        paymentMethod.setText("Payment: " + purchase.getPaymentMethod());
        date.setText("Date: " + purchase.getPurchaseDate());
        address.setText("Address: " + purchase.getZone() + " " + purchase.getBarangay() + ", " + purchase.getCity() + ", " + purchase.getProvince() + " " + purchase.getZipCode());

        // Create and show dialog with custom theme
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Review", (dialog, which) -> showRatingDialog(purchase));
        builder.show();
    }

    private void showRatingDialog(Purchase purchase) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_rate_product, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Find the views in the dialog
        ImageView star1 = view.findViewById(R.id.star1);
        ImageView star2 = view.findViewById(R.id.star2);
        ImageView star3 = view.findViewById(R.id.star3);
        ImageView star4 = view.findViewById(R.id.star4);
        ImageView star5 = view.findViewById(R.id.star5);
        Button submitRating = view.findViewById(R.id.submit_rating);

        // Handle star selection
        View.OnClickListener starClickListener = v -> {
            int rating = 0;

            if (v.getId() == R.id.star1) {
                rating = 1;
            } else if (v.getId() == R.id.star2) {
                rating = 2;
            } else if (v.getId() == R.id.star3) {
                rating = 3;
            } else if (v.getId() == R.id.star4) {
                rating = 4;
            } else if (v.getId() == R.id.star5) {
                rating = 5;
            }

            updateStarSelection(rating, star1, star2, star3, star4, star5);
        };

        star1.setOnClickListener(starClickListener);
        star2.setOnClickListener(starClickListener);
        star3.setOnClickListener(starClickListener);
        star4.setOnClickListener(starClickListener);
        star5.setOnClickListener(starClickListener);

        submitRating.setOnClickListener(v -> {
            int rating = getSelectedRating(star1, star2, star3, star4, star5);
            if (rating > 0) {
                submitRatingToDatabase(purchase.getProductId(), rating);  // Use purchaseId and productId
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStarSelection(int rating, ImageView... stars) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.star_filled);
            } else {
                stars[i].setImageResource(R.drawable.star_empty);
            }
        }
    }

    private int getSelectedRating(ImageView... stars) {
        for (int i = stars.length - 1; i >= 0; i--) {
            if (stars[i].getDrawable().getConstantState().equals(getDrawable(R.drawable.star_filled).getConstantState())) {
                return i + 1;
            }
        }
        return 0;
    }

    private void submitRatingToDatabase(String productId, int rating) {
        // Include the buyer's first name when submitting the rating
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings").child(productId).child(currentUserId);
        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(buyer_purchase_done.this, "You have already rated this product.", Toast.LENGTH_SHORT).show();
                    return; // Prevent multiple ratings for the same product
                }

                // Record the rating along with the buyer's first name
                ratingsRef.setValue(new Rating(rating, currentUserFirstName)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the product rating data
                        updateProductRating(productId, rating);
                        Toast.makeText(buyer_purchase_done.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(buyer_purchase_done.this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_purchase_done.this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductRating(String productId, int rating) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        int newUserRatingCount = product.getUserRatingCount() + 1;
                        int newTotalRatings = product.getTotalRatings() + rating;
                        float newAverageRating = (float) newTotalRatings / newUserRatingCount;

                        productRef.child("userRatingCount").setValue(newUserRatingCount);
                        productRef.child("totalRatings").setValue(newTotalRatings);
                        productRef.child("averageRating").setValue(newAverageRating);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_purchase_done.this, "Failed to update product rating", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
