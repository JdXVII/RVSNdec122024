package com.example.allgoods2024;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private List<Purchase> purchaseList;
    private Context context;
    private String[] defaultStatusOptions;
    private String[] pickupStatusOptions;
    private String VendorId;

    private List<Purchase> selectedPurchases = new ArrayList<>();
    private boolean isSelectionMode = false;

    public void toggleSelectionMode(boolean enabled) {
        isSelectionMode = enabled;
        if (!enabled) {
            selectedPurchases.clear();
        }
        notifyDataSetChanged();
    }

    public void selectAll(boolean isSelected) {
        selectedPurchases.clear();
        if (isSelected) {
            selectedPurchases.addAll(purchaseList);
        }
        notifyDataSetChanged();
    }

    public boolean areAllSelected() {
        return selectedPurchases.size() == purchaseList.size();
    }

    public List<Purchase> getSelectedPurchases() {
        return selectedPurchases;
    }

    public PurchaseAdapter(List<Purchase> purchaseList, Context context, String VendorId) {
        this.purchaseList = purchaseList;
        this.context = context;
        this.defaultStatusOptions = context.getResources().getStringArray(R.array.order_status);
        this.pickupStatusOptions = context.getResources().getStringArray(R.array.order_status_pickup);
        this.VendorId = VendorId;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_order_item, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);

        holder.productName.setText("Product Name: " + purchase.getProductName());
        holder.buyerName.setText("Name: " + purchase.getFirstName() + " " + purchase.getLastName());
        holder.quantity.setText("Quantity: " + purchase.getQuantity());
        holder.totalPrice.setText(purchase.getTotalPrice());
        holder.type.setText("(" + purchase.getProductType() + ")");

        // Load product image using Glide
        Glide.with(context)
                .load(purchase.getproductImageUrl())
                .placeholder(R.drawable.add) // default image
                .into(holder.productImage);

        // Handle checkbox visibility and state
        if (isSelectionMode) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(selectedPurchases.contains(purchase));
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.checkbox.setChecked(false);
        }

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedPurchases.contains(purchase)) {
                    selectedPurchases.add(purchase);
                }
            } else {
                selectedPurchases.remove(purchase);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                holder.checkbox.setChecked(!holder.checkbox.isChecked());
            } else {
                showPurchaseDetails(purchase);
            }
        });

        // Determine which status options to use based on payment and delivery methods
        String[] statusOptions;
        if ("Online Payment".equals(purchase.getPaymentMethod())) {
            if ("Pickup".equals(purchase.getDeliveryMethod())) {
                statusOptions = context.getResources().getStringArray(R.array.order_status_pickup_payment);
            } else if ("Deliver".equals(purchase.getDeliveryMethod())) {
                statusOptions = context.getResources().getStringArray(R.array.order_status_payment);
            } else {
                statusOptions = pickupStatusOptions; // Fallback if delivery method doesn't match
            }
        } else {
            // Default options if payment method is not "Online Payment"
            if ("Not Available".equals(purchase.getDeliveryMethod()) || "Pickup".equals(purchase.getDeliveryMethod())) {
                statusOptions = pickupStatusOptions;
            } else {
                statusOptions = defaultStatusOptions;
            }
        }

        // Set up the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.custom_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(adapter);

        // Set the spinner to the current status
        int spinnerPosition = adapter.getPosition(purchase.getStatus());
        holder.statusSpinner.setSelection(spinnerPosition);

        // Handle button click to update the status
        holder.updateButton.setOnClickListener(v -> {
            // Check Internet Connection
            if (!isInternetAvailable(context)) {
                Toast.makeText(context, "No internet connection. Please check your connection and try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedStatus = holder.statusSpinner.getSelectedItem().toString();
            if (!selectedStatus.equals(purchase.getStatus())) {
                // Show confirmation dialog
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Update")
                        .setMessage("Are you sure you want to update the status to " + selectedStatus + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Show ProgressDialog
                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Updating status...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            // Update status in Firebase
                            DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchases").child(purchase.getPurchaseId());
                            purchaseRef.child("status").setValue(selectedStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss(); // Dismiss ProgressDialog on success
                                        Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();

                                        // If status is "Completed", handle additional actions
                                        if (selectedStatus.equals("Completed")) {
                                            // Delete the timestamp field
                                            purchaseRef.child("timestamp").removeValue()
                                                    .addOnSuccessListener(aVoid1 -> {
                                                        Toast.makeText(context, "Timestamp removed", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(context, "Failed to remove timestamp", Toast.LENGTH_SHORT).show();
                                                    });

                                            // Create the sales report
                                            createSalesReport(
                                                    purchase.getProductId(),
                                                    purchase.getCategory(),
                                                    Double.parseDouble(purchase.getTotalPrice()),
                                                    purchase.getQuantity()
                                            );
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss(); // Dismiss ProgressDialog on failure
                                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // User canceled, do nothing
                            dialog.dismiss();
                        })
                        .show();
            } else {
                Toast.makeText(context, "No change in status", Toast.LENGTH_SHORT).show();
            }
        });

        holder.chatButton.setOnClickListener(v -> {
            // Get buyer's userId from the purchase object
            String buyerId = purchase.getUserId();

            // Reference to the buyer's details in Firebase
            DatabaseReference buyerRef = FirebaseDatabase.getInstance().getReference("buyers").child(buyerId);

            buyerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve buyer details to display in the chat
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        Intent intent = new Intent(context, vendor_product_chatbox.class);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("profileImageUrl", profileImageUrl);
                        intent.putExtra("receiverId", VendorId);
                        intent.putExtra("senderId", buyerId);

                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Buyer details not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, "Failed to fetch buyer details", Toast.LENGTH_SHORT).show();
                }
            });
        });


        holder.cancelButton.setOnClickListener(v -> {
            // Check Internet Connection
            if (!isInternetAvailable(context)) {
                Toast.makeText(context, "No internet connection. Please check your connection and try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show ProgressDialog
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Canceling your order...");
            progressDialog.setCancelable(false); // Prevent user from dismissing the dialog
            progressDialog.show();

            new AlertDialog.Builder(context)
                    .setTitle("Cancel Purchase")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference purchaseRef = FirebaseDatabase.getInstance().getReference("purchases").child(purchase.getPurchaseId());

                        // Fetch current product stock
                        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(purchase.getProductId());
                        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    if (snapshot.exists()) {
                                        int currentStock = Integer.parseInt(snapshot.child("stock").getValue(String.class));
                                        int canceledQuantity = purchase.getQuantity();
                                        int updatedStock = currentStock + canceledQuantity;

                                        // Assuming you have a field "sold" in upload_products
                                        int soldQuantity = Integer.parseInt(snapshot.child("sold").getValue(String.class));
                                        int updatedSold = soldQuantity - canceledQuantity;

                                        // Update product stock and sold quantity
                                        productRef.child("stock").setValue(String.valueOf(updatedStock))
                                                .addOnSuccessListener(aVoid -> {
                                                    productRef.child("sold").setValue(String.valueOf(updatedSold))
                                                            .addOnSuccessListener(aVoid1 -> {
                                                                // Remove purchase entry
                                                                purchaseRef.removeValue().addOnCompleteListener(task -> {
                                                                    if (task.isSuccessful()) {
                                                                        purchaseList.remove(holder.getAdapterPosition());
                                                                        notifyItemRemoved(holder.getAdapterPosition());
                                                                        notifyItemRangeChanged(holder.getAdapterPosition(), purchaseList.size());
                                                                        Toast.makeText(context, "Order canceled", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(context, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    // Dismiss ProgressDialog
                                                                    progressDialog.dismiss();
                                                                });
                                                            }).addOnFailureListener(e -> {
                                                                Toast.makeText(context, "Failed to update sold quantity", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            });
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Failed to update product stock", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Product details not found.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(context, "Error processing product data", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> progressDialog.dismiss())
                    .show();
        });

        // Handle "See all" click to show details
        holder.seeAll.setOnClickListener(v -> showPurchaseDetails(purchase));
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
                                Toast.makeText(context, "Failed to update sales report", Toast.LENGTH_SHORT).show();
                            } else if (committed) {
                                Toast.makeText(context, "Sales report updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch product data", Toast.LENGTH_SHORT).show();
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

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    private void showPurchaseDetails(Purchase purchase) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_purchase_details, null);

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

        new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }


    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {

        public TextView productName, buyerName, quantity, totalPrice, type;
        public ImageView productImage;
        public Spinner statusSpinner;
        public Button updateButton;
        public TextView seeAll;
        Button cancelButton, chatButton;
        public CheckBox checkbox;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            buyerName = itemView.findViewById(R.id.buyer_name);
            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            productImage = itemView.findViewById(R.id.product_image);
            statusSpinner = itemView.findViewById(R.id.status);
            updateButton = itemView.findViewById(R.id.update_button);
            seeAll = itemView.findViewById(R.id.see_all);
            cancelButton = itemView.findViewById(R.id.cancel_button);
            chatButton = itemView.findViewById(R.id.chat);
            checkbox = itemView.findViewById(R.id.checkbox);
            type = itemView.findViewById(R.id.type);
        }
    }
}
