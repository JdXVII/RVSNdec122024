package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class vendor_edit_product extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
    private VendorImageAdapter imageAdapter;
    private TextView nameTextView, descriptionTextView, priceTextView, stockTextView, categoryTextView, storeNameTextView, deliveryOptionTextView, soldTextView;
    private TextView online_payment, voucher_code, voucher_amount;
    private Button removeButton, editButton;
    private String productId;
    private ImageView back_button;

    private RecyclerView ratingRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<Rating> ratingList = new ArrayList<>();
    private TextView viewAllButton;
    private boolean showAllRatings = false;
    private static final int MAX_VISIBLE_RATINGS = 5;// Flag to show all ratings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_edit_product);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_button = new Intent(vendor_edit_product.this, vendor_homepage.class);
                startActivity(back_button);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        // Initialize UI components
        imagesRecyclerView = findViewById(R.id.images_recycler_view);
        nameTextView = findViewById(R.id.product_name);
        descriptionTextView = findViewById(R.id.product_description);
        priceTextView = findViewById(R.id.product_price);
        stockTextView = findViewById(R.id.product_stock);
        categoryTextView = findViewById(R.id.product_category);
        storeNameTextView = findViewById(R.id.product_store_name);
        deliveryOptionTextView = findViewById(R.id.product_delivery_option);
        soldTextView = findViewById(R.id.product_sold);
        online_payment = findViewById(R.id.online_payment);
        voucher_code = findViewById(R.id.voucher_code);
        voucher_amount = findViewById(R.id.voucher_amount);

        // Initialize footer buttons
        removeButton = findViewById(R.id.remove_button);
        editButton = findViewById(R.id.edit_button);

        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Get productId from intent
        productId = getIntent().getStringExtra("productId");

        // Fetch product details from Firebase
        fetchProductDetails(productId);

        // Set up button click listeners
        removeButton.setOnClickListener(v -> removeProduct());
        editButton.setOnClickListener(v -> showEditDialog());

        ratingRecyclerView = findViewById(R.id.ratingRecyclerView);
        viewAllButton = findViewById(R.id.view_all_button);

        // Initialize RecyclerView
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingAdapter = new RatingAdapter(ratingList, MAX_VISIBLE_RATINGS);
        ratingRecyclerView.setAdapter(ratingAdapter);

        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllRatings = !showAllRatings;
                updateViewAllButton();
                fetchRatings();
            }
        });

        // Fetch initial ratings
        fetchRatings();
    }

    private void fetchProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String price = dataSnapshot.child("price").getValue(String.class);
                    String stock = dataSnapshot.child("stock").getValue(String.class);
                    String category = dataSnapshot.child("category").getValue(String.class);
                    String storeName = dataSnapshot.child("storeName").getValue(String.class);
                    String deliveryOption = dataSnapshot.child("deliveryOption").getValue(String.class);
                    String sold = dataSnapshot.child("sold").getValue(String.class);
                    String onlinePayment = dataSnapshot.child("paymentOption").getValue(String.class);
                    String voucherCodes = dataSnapshot.child("voucherCode").getValue(String.class);
                    String voucherAmounts = dataSnapshot.child("voucherAmount").getValue(String.class);

                    String sale = dataSnapshot.child("sale").getValue(String.class);
                    String type = dataSnapshot.child("type").getValue(String.class);

                    List<String> imageUrls = new ArrayList<>();
                    for (DataSnapshot urlSnapshot : dataSnapshot.child("imageUrls").getChildren()) {
                        imageUrls.add(urlSnapshot.getValue(String.class));
                    }

                    // Update UI with product details
                    nameTextView.setText(name);
                    descriptionTextView.setText(description);
                    priceTextView.setText("₱" + price);
                    stockTextView.setText(stock);
                    categoryTextView.setText(category);
                    storeNameTextView.setText(storeName);
                    deliveryOptionTextView.setText(deliveryOption);
                    soldTextView.setText(sold);
                    online_payment.setText(onlinePayment);
                    voucher_code.setText(voucherCodes);
                    voucher_amount.setText(voucherAmounts + "%");

                    TextView productSaleTextView = findViewById(R.id.product_sale);
                    TextView productTypeTextView = findViewById(R.id.product_type);
                    productSaleTextView.setText(sale != null ? "₱" + sale : "N/A");
                    productTypeTextView.setText(type != null ? type : "N/A");

                    // Set up image adapter
                    imageAdapter = new VendorImageAdapter(vendor_edit_product.this, imageUrls);
                    imagesRecyclerView.setAdapter(imageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_edit_product.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void removeProduct() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Removal")
                .setMessage("Are you sure you want to remove this product?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
                    productRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(vendor_edit_product.this, "Product removed successfully", Toast.LENGTH_SHORT).show();
                            Intent back = new Intent(vendor_edit_product.this, vendor_homepage.class);
                            startActivity(back);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Toast.makeText(vendor_edit_product.this, "Failed to remove product", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        builder.setTitle("Edit Product");

        // Inflate the custom layout
        View view = getLayoutInflater().inflate(R.layout.vendor_dialog_edit_product, null);
        builder.setView(view);

        final EditText descriptionEditText = view.findViewById(R.id.edit_description);
        final EditText stockEditText = view.findViewById(R.id.edit_stock);
        final EditText priceEditText = view.findViewById(R.id.edit_price);
        final TextView priceRangeTextView = view.findViewById(R.id.price_range_text); // Price range TextView
        final EditText wholesalePriceEditText = view.findViewById(R.id.edit_pricewholesale);
        final TextView wholesalePriceLabel = view.findViewById(R.id.price_wholesale);

        // Fetch current product details to populate the dialog (excluding price)
        fetchProductDetailsForEditing(descriptionEditText, stockEditText);

        // Fetch price range for the dialog
        fetchPriceRangeForDialog(priceRangeTextView);

        // Fetch the product type to determine the visibility of wholesale fields
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String type = dataSnapshot.child("type").getValue(String.class);

                    // Show or hide wholesale fields based on product type
                    if ("Wholesale".equals(type)) {
                        wholesalePriceLabel.setVisibility(View.VISIBLE);
                        wholesalePriceEditText.setVisibility(View.VISIBLE);
                        wholesalePriceEditText.setEnabled(true);
                    } else {
                        wholesalePriceLabel.setVisibility(View.GONE);
                        wholesalePriceEditText.setVisibility(View.GONE);
                        wholesalePriceEditText.setEnabled(false);
                    }

                    // Fetch and set values for deliveryOption and paymentOption
                    String deliveryOption = dataSnapshot.child("deliveryOption").getValue(String.class);
                    String paymentOption = dataSnapshot.child("paymentOption").getValue(String.class);
                    String gcashName = dataSnapshot.child("gcashName").getValue(String.class);
                    String gcashNumber = dataSnapshot.child("gcashNumber").getValue(String.class);
                    String mayaName = dataSnapshot.child("mayaName").getValue(String.class);
                    String mayaNumber = dataSnapshot.child("mayaNumber").getValue(String.class);
                    String voucherCode = dataSnapshot.child("voucherCode").getValue(String.class);
                    String voucherAmount = dataSnapshot.child("voucherAmount").getValue(String.class);

                    // Set the dropdown for deliveryOption and paymentOption
                    Spinner deliveryOptionSpinner = view.findViewById(R.id.edit_deliveryOption);
                    Spinner paymentOptionSpinner = view.findViewById(R.id.edit_paymentOption);
                    EditText gcashNameEditText = view.findViewById(R.id.edit_gcashName);
                    EditText gcashNumberEditText = view.findViewById(R.id.edit_gcashNumber);
                    EditText mayaNameEditText = view.findViewById(R.id.edit_mayaName);
                    EditText mayaNumberEditText = view.findViewById(R.id.edit_mayaNumber);
                    EditText voucherCodeEditText = view.findViewById(R.id.edit_voucherCode);
                    EditText voucherAmountEditText = view.findViewById(R.id.edit_voucherAmount);

                    // Set the values for delivery option and payment option
                    ArrayAdapter<String> deliveryAdapter = new ArrayAdapter<>(vendor_edit_product.this, R.layout.custom_spinner_item, new String[]{"Available", "Not Available"});
                    deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    deliveryOptionSpinner.setAdapter(deliveryAdapter);
                    int deliveryOptionPosition = deliveryAdapter.getPosition(deliveryOption);
                    deliveryOptionSpinner.setSelection(deliveryOptionPosition);

                    ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(vendor_edit_product.this, R.layout.custom_spinner_item, new String[]{"Available", "Not Available"});
                    paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    paymentOptionSpinner.setAdapter(paymentAdapter);
                    int paymentOptionPosition = paymentAdapter.getPosition(paymentOption);
                    paymentOptionSpinner.setSelection(paymentOptionPosition);

                    // Set the values for gcash and maya fields
                    gcashNameEditText.setText(gcashName);
                    gcashNumberEditText.setText(gcashNumber);
                    mayaNameEditText.setText(mayaName);
                    mayaNumberEditText.setText(mayaNumber);
                    voucherCodeEditText.setText(voucherCode);
                    voucherAmountEditText.setText(voucherAmount);

                    // Hide the gcash and maya fields unless paymentOption is "Available"
                    if ("Available".equals(paymentOption)) {
                        gcashNameEditText.setVisibility(View.VISIBLE);
                        gcashNumberEditText.setVisibility(View.VISIBLE);
                        mayaNameEditText.setVisibility(View.VISIBLE);
                        mayaNumberEditText.setVisibility(View.VISIBLE);
                    } else {
                        gcashNameEditText.setVisibility(View.GONE);
                        gcashNumberEditText.setVisibility(View.GONE);
                        mayaNameEditText.setVisibility(View.GONE);
                        mayaNumberEditText.setVisibility(View.GONE);
                    }

                    // Toggle visibility when paymentOption changes
                    paymentOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String selectedPaymentOption = parentView.getItemAtPosition(position).toString();
                            if ("Available".equals(selectedPaymentOption)) {
                                gcashNameEditText.setVisibility(View.VISIBLE);
                                gcashNumberEditText.setVisibility(View.VISIBLE);
                                mayaNameEditText.setVisibility(View.VISIBLE);
                                mayaNumberEditText.setVisibility(View.VISIBLE);
                            } else {
                                gcashNameEditText.setVisibility(View.GONE);
                                gcashNumberEditText.setVisibility(View.GONE);
                                mayaNameEditText.setVisibility(View.GONE);
                                mayaNumberEditText.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_edit_product.this, "Failed to load product type", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDescription = descriptionEditText.getText().toString();
            String newStock = stockEditText.getText().toString();
            String newPrice = priceEditText.getText().toString();
            String newWholesalePrice = wholesalePriceEditText.getText().toString();
            String selectedDeliveryOption = ((Spinner) view.findViewById(R.id.edit_deliveryOption)).getSelectedItem().toString();
            String selectedPaymentOption = ((Spinner) view.findViewById(R.id.edit_paymentOption)).getSelectedItem().toString();
            String gcashName = ((EditText) view.findViewById(R.id.edit_gcashName)).getText().toString();
            String gcashNumber = ((EditText) view.findViewById(R.id.edit_gcashNumber)).getText().toString();
            String mayaName = ((EditText) view.findViewById(R.id.edit_mayaName)).getText().toString();
            String mayaNumber = ((EditText) view.findViewById(R.id.edit_mayaNumber)).getText().toString();
            String voucherCode = ((EditText) view.findViewById(R.id.edit_voucherCode)).getText().toString();
            String voucherAmount = ((EditText) view.findViewById(R.id.edit_voucherAmount)).getText().toString();

            // Validation for required price field
            if (newPrice.isEmpty()) {
                Toast.makeText(vendor_edit_product.this, "Price is required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validation for at least one payment option (gcash or maya) if paymentOption is "Available"
            if ("Available".equals(selectedPaymentOption)) {
                if ((gcashName.isEmpty() && gcashNumber.isEmpty()) && (mayaName.isEmpty() && mayaNumber.isEmpty())) {
                    Toast.makeText(vendor_edit_product.this, "Please fill in either Gcash or Maya payment details", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // Update product details including wholesale sale if applicable
            updateProduct(newDescription, newStock, newPrice, newWholesalePrice, selectedDeliveryOption, selectedPaymentOption, gcashName, gcashNumber, mayaName, mayaNumber, voucherCode, voucherAmount);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void fetchPriceRangeForDialog(TextView priceRangeTextView) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

        // Use the product name from the main view
        String productName = nameTextView.getText().toString(); // Assuming nameTextView contains the product name

        productsRef.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String minPrice = snapshot.child("price").child("min").getValue(String.class);
                        String maxPrice = snapshot.child("price").child("max").getValue(String.class);

                        if (minPrice != null && maxPrice != null) {
                            String priceRangeText = "Price: " + "(min - max): " + minPrice + " - " + maxPrice;
                            priceRangeTextView.setText(priceRangeText);
                        }
                    }
                } else {
                    priceRangeTextView.setText("Price range not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_edit_product.this, "Failed to load price range", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductDetailsForEditing(EditText descriptionEditText, EditText stockEditText) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String stock = dataSnapshot.child("stock").getValue(String.class);

                    descriptionEditText.setText(description);
                    stockEditText.setText(stock);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_edit_product.this, "Failed to load product details for editing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct(String newDescription, String newStock, String newPrice, String newWholesalePrice,
                               String newDeliveryOption, String newPaymentOption, String newGcashName, String newGcashNumber,
                               String newMayaName, String newMayaNumber, String newVoucherCode, String newVoucherAmount) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String type = dataSnapshot.child("type").getValue(String.class);
                    String category = dataSnapshot.child("category").getValue(String.class);

                    // Fetch product price for validation
                    DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
                    productsRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                boolean validPrice = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String minPrice = snapshot.child("price").child("min").getValue(String.class);
                                    String maxPrice = snapshot.child("price").child("max").getValue(String.class);

                                    if (minPrice != null && maxPrice != null) {
                                        double min = Double.parseDouble(minPrice);
                                        double max = Double.parseDouble(maxPrice);
                                        double price = Double.parseDouble(newPrice);

                                        if (price < min) {
                                            Toast.makeText(vendor_edit_product.this, "Price is too low", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else if (price > max) {
                                            Toast.makeText(vendor_edit_product.this, "Price is too high", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            validPrice = true;
                                            break;
                                        }
                                    }
                                }

                                if (validPrice) {
                                    // Update product details in Firebase
                                    productRef.child("description").setValue(newDescription);
                                    productRef.child("stock").setValue(newStock);
                                    productRef.child("price").setValue(newPrice);
                                    productRef.child("stock").setValue(newStock);
                                    productRef.child("price").setValue(newPrice);
                                    productRef.child("deliveryOption").setValue(newDeliveryOption);
                                    productRef.child("paymentOption").setValue(newPaymentOption);
                                    productRef.child("voucherCode").setValue(newVoucherCode);
                                    productRef.child("voucherAmount").setValue(newVoucherAmount);

                                    // Update payment options if available
                                    if ("Available".equals(newPaymentOption)) {
                                        productRef.child("gcashName").setValue(newGcashName);
                                        productRef.child("gcashNumber").setValue(newGcashNumber);
                                        productRef.child("mayaName").setValue(newMayaName);
                                        productRef.child("mayaNumber").setValue(newMayaNumber);
                                    }

                                    // Check product type and update sale field if applicable
                                    if ("Wholesale".equals(type) && !newWholesalePrice.isEmpty()) {
                                        productRef.child("sale").setValue(newWholesalePrice);
                                    }

                                    // Check product type and update sale field if applicable
                                    if ("Wholesale".equals(type) && !newWholesalePrice.isEmpty()) {
                                        productRef.child("sale").setValue(newWholesalePrice);
                                    }

                                    Toast.makeText(vendor_edit_product.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(vendor_edit_product.this, "Price is out of range", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(vendor_edit_product.this, "Product not found in products table", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(vendor_edit_product.this, "Failed to validate product price", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_edit_product.this, "Failed to load product details for update", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateViewAllButton() {
        if (showAllRatings) {
            viewAllButton.setText("See Less ↑");
        } else {
            viewAllButton.setText("View All ↓");
        }
    }

    private void fetchRatings() {
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("ratings").child(productId);
        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratingList.clear();
                int[] ratingCounts = new int[5]; // Array to store counts for 1 to 5 stars

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rating rating = snapshot.getValue(Rating.class);
                    if (rating != null) {
                        ratingList.add(rating);
                        if (rating.getRating() >= 1 && rating.getRating() <= 5) {
                            ratingCounts[rating.getRating() - 1]++;
                        }
                    }
                }

                if (!showAllRatings && ratingList.size() > 5) {
                    ratingList = ratingList.subList(0, 5);
                }

                ratingAdapter = new RatingAdapter(ratingList, showAllRatings ? Integer.MAX_VALUE : MAX_VISIBLE_RATINGS);
                ratingRecyclerView.setAdapter(ratingAdapter);
                ratingAdapter.notifyDataSetChanged();
                updateViewAllButton();

                // Update the count TextViews
                updateRatingCounts(ratingCounts);
            }

            private void updateRatingCounts(int[] ratingCounts) {
                TextView rate1TextView = findViewById(R.id.rate1);
                TextView rate2TextView = findViewById(R.id.rate2);
                TextView rate3TextView = findViewById(R.id.rate3);
                TextView rate4TextView = findViewById(R.id.rate4);
                TextView rate5TextView = findViewById(R.id.rate5);

                rate1TextView.setText("1 (" + ratingCounts[0] + ")");
                rate2TextView.setText("2 (" + ratingCounts[1] + ")");
                rate3TextView.setText("3 (" + ratingCounts[2] + ")");
                rate4TextView.setText("4 (" + ratingCounts[3] + ")");
                rate5TextView.setText("5 (" + ratingCounts[4] + ")");
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(vendor_edit_product.this, "Failed to load ratings", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(vendor_edit_product.this, vendor_homepage.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
