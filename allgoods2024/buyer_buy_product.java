package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.recyclerview.widget.LinearSnapHelper;
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

public class buyer_buy_product extends AppCompatActivity {

    private TextView unit, type_product, storeNameTextView, productNameTextView, productDescriptionTextView, productTypeTextView, productSaleTextView, productPriceTextView, productPriceTextViewNew, totalPriceTextView, status, productStockTextView, category;
    private EditText quantityEditText;
    private ImageView upImageView, downImageView, cart, chatboxImageView, btn_back;
    private Spinner spinnerDelivery, spinnerPayment;
    private String productId, userId;
    private Button addToCartButton;
    private int quantity = 1; // Initial quantity
    private double unitPrice = 0.0; // Unit price of the product
    private double saleAmount = 0.0; // Amount to subtract from total price if quantity >= 30
    private Button buyNowButton;
    private  double totalPrice;
    private String deliveryOption; // Variable to store delivery option
    private String paymentOption;
    private String productImageUrl; // Variable to store product image URL
    private RecyclerView ratingRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<Rating> ratingList = new ArrayList<>();
    private TextView viewAllButton;
    private boolean showAllRatings = false;
    private static final int MAX_VISIBLE_RATINGS = 5;

    private double discountedTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_buy_product);

        // Set status bar color to black for Android 7.0 (Nougat) and above
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));

        storeNameTextView = findViewById(R.id.store_name);
        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);
        productTypeTextView = findViewById(R.id.product_type);
        productSaleTextView = findViewById(R.id.product_sale);
        productPriceTextView = findViewById(R.id.product_price);
        productPriceTextViewNew = findViewById(R.id.product_price_new);
        totalPriceTextView = findViewById(R.id.total_price);
        status = findViewById(R.id.status);
        quantityEditText = findViewById(R.id.quantity);
        upImageView = findViewById(R.id.up);
        downImageView = findViewById(R.id.down);
        spinnerDelivery = findViewById(R.id.spinnerDelivery);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        category = findViewById(R.id.category);
        productStockTextView = findViewById(R.id.product_stock);
        type_product = findViewById(R.id.type_product);
        unit = findViewById(R.id.unit);

        // Add a click listener to the "Enter Voucher" TextView
        TextView enterVoucherTextView = findViewById(R.id.enter_voucher);
        enterVoucherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoucherDialog();
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btn_back = new Intent(buyer_buy_product.this, buyer_homepage.class);
                startActivity(btn_back);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        chatboxImageView = findViewById(R.id.chatboxImageView);
        chatboxImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null) {
                            String sellerUserId = product.getUserId();
                            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            Intent intent = new Intent(buyer_buy_product.this, buyer_product_chatbox.class);
                            intent.putExtra("senderId", currentUserId);
                            intent.putExtra("receiverId", sellerUserId);
                            intent.putExtra("productDescription", productDescriptionTextView.getText().toString());
                            intent.putExtra("productImageUrl", productImageUrl);
                            intent.putExtra("productName", productNameTextView.getText().toString());
                            intent.putExtra("storeName", storeNameTextView.getText().toString());
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(buyer_buy_product.this, "Product not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(buyer_buy_product.this, "Failed to get seller details", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(buyer_buy_product.this, buyer_addtocart.class);
                startActivity(cart);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        addToCartButton = findViewById(R.id.add_cart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }

            private void addToCart() {
                // Reference to the user's cart items
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("addtocart");

                cartRef.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean itemExists = false;
                                String existingCartId = null;

                                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                                    CartItem existingItem = cartSnapshot.getValue(CartItem.class);
                                    if (existingItem != null && existingItem.getProductId().equals(productId)) {
                                        itemExists = true;
                                        existingCartId = existingItem.getCartId();
                                        break;
                                    }
                                }

                                if (itemExists && existingCartId != null) {
                                    // Update existing cart item
                                    updateCartItem(existingCartId);
                                } else {
                                    // Add new cart item
                                    createNewCartItem();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(buyer_buy_product.this, "Failed to check cart items", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            private void updateCartItem(String cartId) {
                DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference("addtocart").child(cartId);

                cartItemRef.child("quantity").setValue(Integer.parseInt(quantityEditText.getText().toString()));
                cartItemRef.child("totalPrice").setValue(totalPriceTextView.getText().toString().replace("₱", ""));
                cartItemRef.child("deliveryMethod").setValue(spinnerDelivery.getSelectedItem().toString());
                cartItemRef.child("paymentMethod").setValue(spinnerPayment.getSelectedItem().toString());

                cartItemRef.setValue(new CartItem(
                                cartId,
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                productId,
                                productImageUrl,
                                category.getText().toString(),
                                storeNameTextView.getText().toString(),
                                productNameTextView.getText().toString(),
                                productPriceTextView.getText().toString().replace("₱", ""),
                                productTypeTextView.getText().toString(),
                                Integer.parseInt(quantityEditText.getText().toString()),
                                spinnerDelivery.getSelectedItem().toString(),
                                spinnerPayment.getSelectedItem().toString(),
                                totalPriceTextView.getText().toString().replace("₱", ""),
                                type_product.getText().toString()
                        )).addOnSuccessListener(aVoid -> Toast.makeText(buyer_buy_product.this, "Cart updated", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(buyer_buy_product.this, "Failed to update cart", Toast.LENGTH_SHORT).show());
            }

            private void createNewCartItem() {
                // Create a reference to a new cart item
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("addtocart").push();
                String cartId = cartRef.getKey(); // Generate unique cart ID

                // Create a new CartItem object with all fields
                CartItem cartItem = new CartItem(
                        cartId,
                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        productId,
                        productImageUrl,
                        category.getText().toString(),
                        storeNameTextView.getText().toString(),
                        productNameTextView.getText().toString(),
                        productPriceTextView.getText().toString().replace("₱", ""),
                        productTypeTextView.getText().toString(),
                        Integer.parseInt(quantityEditText.getText().toString()),
                        spinnerDelivery.getSelectedItem().toString(),
                        spinnerPayment.getSelectedItem().toString(),
                        totalPriceTextView.getText().toString().replace("₱", ""),
                        type_product.getText().toString()
                );

                // Save the cartItem to the database
                cartRef.setValue(cartItem)
                        .addOnSuccessListener(aVoid -> Toast.makeText(buyer_buy_product.this, "Added to cart", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(buyer_buy_product.this, "Failed to add to cart", Toast.LENGTH_SHORT).show());
            }
        });

        productId = getIntent().getStringExtra("productId");

        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid(); // Get user ID of the currently logged-in user
        } else {
            // Handle case where user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close activity or redirect to login
            return;
        }

        fetchProductDetails();
        setupSpinners();

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

        buyNowButton = findViewById(R.id.buy_now);
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }

            private void validateFields() {
                if (quantity < 1) {
                    Toast.makeText(buyer_buy_product.this, "Quantity must be at least 1", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check stock availability
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null) {
                            int availableStock = Integer.parseInt(product.getStock());
                            if (quantity > availableStock) {
                                Toast.makeText(buyer_buy_product.this, "Not enough stock available", Toast.LENGTH_SHORT).show();
                            } else {
                                proceedToBuyNow(); // Proceed if validation is successful
                            }
                        } else {
                            Toast.makeText(buyer_buy_product.this, "Product not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(buyer_buy_product.this, "Failed to check stock", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void proceedToBuyNow() {
                Intent intent = new Intent(buyer_buy_product.this, buyer_buynow_product.class);
                intent.putExtra("storeName", storeNameTextView.getText().toString());
                intent.putExtra("productName", productNameTextView.getText().toString());
                intent.putExtra("price", productPriceTextView.getText().toString());
                intent.putExtra("type", productTypeTextView.getText().toString());
                intent.putExtra("quantity", quantity);
                intent.putExtra("deliveryMethod", spinnerDelivery.getSelectedItem().toString());
                intent.putExtra("paymentMethod", spinnerPayment.getSelectedItem().toString());
                intent.putExtra("totalPrice", String.format("%.2f", discountedTotalPrice)); // Pass the discounted price
                intent.putExtra("productId", productId);
                intent.putExtra("userId", userId); // Pass the correct user ID
                intent.putExtra("productImageUrl", productImageUrl);
                intent.putExtra("category", category.getText().toString());
                intent.putExtra("productType", type_product.getText().toString());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Set click listeners for quantity buttons
        upImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        downImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        // Set TextWatcher for quantity EditText
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int inputQuantity = Integer.parseInt(s.toString());
                    if (inputQuantity >= 1) {
                        quantity = inputQuantity;
                        updateTotalPrice();
                    } else {
                        quantity = 1; // Reset to 1 if the input is invalid
                        updateQuantityDisplay();
                    }
                } catch (NumberFormatException e) {
                    quantity = 1; // Default to 1 if the input is invalid
                    updateQuantityDisplay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
                Toast.makeText(buyer_buy_product.this, "Failed to load ratings", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupSpinners() {
        ArrayAdapter<CharSequence> deliveryAdapter = ArrayAdapter.createFromResource(this,
                R.array.delivery_options, R.layout.custom_spinner_item);
        deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDelivery.setAdapter(deliveryAdapter);

        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                R.array.payment_options, R.layout.custom_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(paymentAdapter);
    }

    // Declare a global variable to store the discount value
    private double activeDiscount = 0.0; // Default is no discount

    private void fetchProductDetails() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    storeNameTextView.setText(product.getStoreName());
                    productNameTextView.setText(product.getName());
                    status.setText(String.format("%.2f", product.getAverageRating()) + " (" + formatuserRatingCountNumber(product.getUserRatingCount()) + ") " + "| " + formatSoldNumber(product.getSold())  + " sold");
                    productDescriptionTextView.setText(product.getDescription());
                    productTypeTextView.setText("Sale type: " + product.getType());
                    productSaleTextView.setText("Discount: -" + product.getSale());
                    unitPrice = Double.parseDouble(product.getPrice());
                    category.setText(product.getCategory());
                    unit.setText("(Sold per " + product.getUnit() + ")");
                    type_product.setText(product.getProductType());

                    // Disable addToCartButton if product type is Pre Order
                    if ("Pre Order".equals(product.getProductType())) {
                        buyNowButton.setText("Pre-Order Now");
                        addToCartButton.setVisibility(View.GONE);
                    }

                    // Convert sale string to double
                    try {
                        saleAmount = Double.parseDouble(product.getSale());
                    } catch (NumberFormatException e) {
                        saleAmount = 0.0; // Default to 0 if conversion fails
                    }

                    productPriceTextView.setText("₱" + product.getPrice());
                    productStockTextView.setText("Stock: " + product.getStock()); // Set stock quantity
                    // Fetch image URLs and set the first image URL
                    List<String> imageUrls = product.getImageUrls();
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        productImageUrl = imageUrls.get(0); // Set the first image URL
                    }
                    ProductImagesAdapter adapter = new ProductImagesAdapter(buyer_buy_product.this, imageUrls);

                    RecyclerView recyclerView = findViewById(R.id.product_image_recycler_view);
                    recyclerView.setLayoutManager(new LinearLayoutManager(buyer_buy_product.this, LinearLayoutManager.HORIZONTAL, false));
                    recyclerView.setAdapter(adapter);

                    // Add the LinearSnapHelper to snap each item to the center
                    LinearSnapHelper snapHelper = new LinearSnapHelper();
                    snapHelper.attachToRecyclerView(recyclerView);

                    deliveryOption = product.getDeliveryOption(); // Fetch the delivery option
                    paymentOption = product.getPaymentOption(); // Fetch the payment option
                    updateDeliverySpinner(); // Update the spinner based on delivery option
                    updatePaymentSpinner();// Update the spinner based on delivery option

                    // Check if the product has an event and is active
                    if (product.getEvent()) {
                        fetchActiveEventDiscount(new EventDiscountCallback() {
                            @Override
                            public void onEventDiscountRetrieved(String discount) {
                                if (discount != null) {
                                    activeDiscount = Double.parseDouble(discount); // Store the active event discount value
                                    updateProductPriceAndTotalPrice(); // Recalculate price with discount
                                } else {
                                    activeDiscount = 0.0; // No active discount
                                    updateProductPriceAndTotalPrice(); // Recalculate price without discount
                                }
                            }
                        });
                    } else {
                        activeDiscount = 0.0; // No discount if event is not active
                        updateProductPriceAndTotalPrice(); // Recalculate price without discount
                    }

                    updateTotalPrice(); // Update the total price initially
                }
            }

            private void updateProductPriceAndTotalPrice() {
                // Apply discount to product price if event is active
                double discountedPrice = unitPrice;
                if (activeDiscount > 0) {
                    discountedPrice = unitPrice * (1 - activeDiscount / 100); // Apply discount
                }

                // Format and display the prices
                String formattedDiscountedPrice = String.format("₱%.2f", discountedPrice);
                String formattedOriginalPrice = String.format("₱%.2f", unitPrice);

                // Format the discount percentage without decimal if it's a whole number
                String formattedDiscountPercentage = (activeDiscount % 1 == 0)
                        ? String.format("%.0f", activeDiscount) // Remove decimal if it's a whole number
                        : String.format("%.2f", activeDiscount); // Keep two decimals if not a whole number

                // If the event is active, show the original price with a strikethrough and the discounted price
                if (activeDiscount > 0) {
                    productPriceTextView.setText(formattedOriginalPrice);
                    productPriceTextView.setPaintFlags(productPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productPriceTextViewNew.setText(formattedDiscountedPrice + " -" + formattedDiscountPercentage + "%");
                    productPriceTextViewNew.setVisibility(View.VISIBLE);
                } else {
                    // If no event, just show the original price without a strikethrough
                    productPriceTextView.setText(formattedOriginalPrice);
                    productPriceTextView.setPaintFlags(productPriceTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Remove strikethrough
                }

                // Update the total price with any applicable discount (both event and quantity-based discounts)
                updateTotalPrice(); // Ensure total price includes discount
            }


            private String formatuserRatingCountNumber(int userRatingCount) {
                if (userRatingCount >= 1000) {
                    double formattedValue = userRatingCount / 1000.0;
                    return String.format("%.1fk", formattedValue);
                } else {
                    return String.valueOf(userRatingCount);
                }
            }

            private String formatSoldNumber(String sold) {
                try {
                    int soldInt = Integer.parseInt(sold);
                    if (soldInt >= 1000) {
                        double formattedValue = soldInt / 1000.0;
                        return String.format("%.1fk", formattedValue);
                    } else {
                        return sold;
                    }
                } catch (NumberFormatException e) {
                    return sold;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_buy_product.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch the active event discount from Firebase Realtime Database (no need for eventId)
    public void fetchActiveEventDiscount(final EventDiscountCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("events").orderByChild("isActive").equalTo(true)  // Fetch only active events
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming only one active event or you can decide how to handle multiple active events
                            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                String discount = eventSnapshot.child("discount").getValue(String.class);
                                if (discount != null) {
                                    callback.onEventDiscountRetrieved(discount);
                                    return; // Exit after applying the first found active event discount
                                }
                            }
                        } else {
                            callback.onEventDiscountRetrieved(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onEventDiscountRetrieved(null);
                    }
                });
    }

    private void updateTotalPrice() {
        totalPrice = unitPrice * quantity;

        // Apply quantity-based discount (for quantities 12 or more)
        if (quantity >= 12) {
            double discountPerUnit = saleAmount; // Use saleAmount as the discount per unit
            totalPrice = (unitPrice - discountPerUnit) * quantity;
        }

        // Apply event-based discount if active
        if (activeDiscount > 0) {
            totalPrice = totalPrice * (1 - activeDiscount / 100); // Apply the percentage discount
        }

        // Ensure the total price does not go below zero
        if (totalPrice < 0) {
            totalPrice = 0;
        }

        discountedTotalPrice = totalPrice; // Update discountedTotalPrice
        totalPriceTextView.setText("₱" + String.format("%.2f", totalPrice)); // Display the final price
    }


    public interface EventDiscountCallback {
        void onEventDiscountRetrieved(String discount);
    }


    private void increaseQuantity() {
        quantity++;
        updateQuantityDisplay();
    }

    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            updateQuantityDisplay();
        } else {
            Toast.makeText(this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuantityDisplay() {
        quantityEditText.setText(String.valueOf(quantity));
        updateTotalPrice(); // Update total price when quantity changes
    }



    private void showVoucherDialog() {
        // Create an AlertDialog for voucher input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Voucher Code");

        // Create an EditText for voucher input
        final EditText voucherInput = new EditText(this);
        voucherInput.setHint("Enter voucher code");
        builder.setView(voucherInput);

        // Set up the buttons for Submit and Cancel
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredVoucherCode = voucherInput.getText().toString().trim();
                if (!enteredVoucherCode.isEmpty()) {
                    checkVoucherCode(enteredVoucherCode);
                } else {
                    Toast.makeText(buyer_buy_product.this, "Please enter a voucher code", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // Close the dialog without doing anything
            }
        });

        builder.show();  // Show the dialog
    }

    private void checkVoucherCode(final String enteredVoucherCode) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String voucherCodeFromDb = dataSnapshot.child("voucherCode").getValue(String.class);
                    String voucherAmountFromDb = dataSnapshot.child("voucherAmount").getValue(String.class);

                    if (voucherCodeFromDb != null && voucherCodeFromDb.equals(enteredVoucherCode)) {
                        try {
                            // Get the voucher discount percentage
                            double voucherAmount = Double.parseDouble(voucherAmountFromDb);

                            // First, apply any existing discounts (event and quantity discounts) to the total price
                            double priceAfterDiscounts = unitPrice * quantity;

                            // Apply quantity-based discount (if any, such as for quantities 12 or more)
                            if (quantity >= 12) {
                                double discountPerUnit = saleAmount; // Use saleAmount as the discount per unit
                                priceAfterDiscounts = (unitPrice - discountPerUnit) * quantity;
                            }

                            // Apply event-based discount (if any)
                            if (activeDiscount > 0) {
                                priceAfterDiscounts = priceAfterDiscounts * (1 - activeDiscount / 100);
                            }

                            // Now apply the voucher discount to the already discounted price
                            double voucherDiscount = (voucherAmount / 100) * priceAfterDiscounts;

                            // Apply the voucher discount to the price
                            discountedTotalPrice = priceAfterDiscounts - voucherDiscount;

                            // Ensure the total price does not go below zero
                            if (discountedTotalPrice < 0) {
                                discountedTotalPrice = 0;
                            }

                            // Update the UI with the new total price after voucher discount
                            TextView newTotalTextView = findViewById(R.id.new_total);
                            newTotalTextView.setText("₱" + String.format("%.2f", discountedTotalPrice));

                            // Update the total price in the totalPriceTextView
                            totalPriceTextView.setText("₱" + String.format("%.2f", discountedTotalPrice));

                            // Update the voucher_discount TextView with the applied discount percentage
                            TextView voucherDiscountTextView = findViewById(R.id.voucher_discount);
                            voucherDiscountTextView.setText(String.format("%.0f%%", voucherAmount));

                            Toast.makeText(buyer_buy_product.this, "Voucher applied successfully!", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(buyer_buy_product.this, "Error applying voucher", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(buyer_buy_product.this, "Invalid voucher code!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(buyer_buy_product.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(buyer_buy_product.this, "Failed to verify voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateDeliverySpinner() {
        if ("Not Available".equalsIgnoreCase(deliveryOption)) {
            spinnerDelivery.setEnabled(false);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, new String[]{"Not Available"});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDelivery.setAdapter(adapter);
        } else {
            spinnerDelivery.setEnabled(true);
            // Assuming the delivery options array contains "Pickup" and "Deliver"
            ArrayAdapter<CharSequence> deliveryAdapter = ArrayAdapter.createFromResource(this,
                    R.array.delivery_options, R.layout.custom_spinner_item);
            deliveryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDelivery.setAdapter(deliveryAdapter);
        }
    }

    private void updatePaymentSpinner() {
        if ("Not Available".equalsIgnoreCase(deliveryOption) && "Not Available".equalsIgnoreCase(paymentOption)) {
            // Both deliveryOption and paymentOption are Not Available
            spinnerPayment.setEnabled(true);
            ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                    R.array.payment_options_four, R.layout.custom_spinner_item);
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPayment.setAdapter(paymentAdapter);
        } else if ("Not Available".equalsIgnoreCase(deliveryOption)) {
            // deliveryOption is Not Available, but paymentOption is Available
            spinnerPayment.setEnabled(true);
            ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                    R.array.payment_options_two, R.layout.custom_spinner_item);
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPayment.setAdapter(paymentAdapter);
        } else if ("Not Available".equalsIgnoreCase(paymentOption)) {
            // deliveryOption is Available, but paymentOption is Not Available
            spinnerPayment.setEnabled(true);
            ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                    R.array.payment_options_three, R.layout.custom_spinner_item);
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPayment.setAdapter(paymentAdapter);
        } else {
            // Both deliveryOption and paymentOption are Available
            spinnerPayment.setEnabled(true);
            ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(this,
                    R.array.payment_options, R.layout.custom_spinner_item);
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPayment.setAdapter(paymentAdapter);
        }
    }
}
