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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BuyerPurchaseAdapter extends RecyclerView.Adapter<BuyerPurchaseAdapter.ViewHolder> {
    private Context context;
    private List<Purchase> purchaseList;
    private String currentUserId;

    public BuyerPurchaseAdapter(Context context, List<Purchase> purchaseList, String currentUserId) {
        this.context = context;
        this.purchaseList = purchaseList;
        this.currentUserId = currentUserId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.buyer_purchases_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);

        holder.storeName.setText("Store Name: " + purchase.getStoreName());
        holder.productName.setText("Product Name: " + purchase.getProductName());
        holder.quantity.setText("Quantity: " + purchase.getQuantity());
        holder.totalPrice.setText(String.format("₱%.2f", purchase.getTotal()));
        holder.status.setText("Status: " + purchase.getStatus());
        holder.productType.setText("(" + purchase.getProductType() + ")");

        Glide.with(context).load(purchase.getproductImageUrl()).into(holder.productImage);

        if (purchase.getStatus().equals("Pending")) {
            holder.cancelButton.setEnabled(true);
            holder.cancelButton.setBackgroundColor(ContextCompat.getColor(context, R.color.green3)); // Set enabled button color
        } else {
            holder.cancelButton.setEnabled(false);
            holder.cancelButton.setBackgroundColor(ContextCompat.getColor(context, R.color.darkwhite)); // Set disabled button color
        }

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

        holder.seeAll.setOnClickListener(v -> {
            // Show popup with details
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
            LayoutInflater inflater = LayoutInflater.from(context);
            View detailView = inflater.inflate(R.layout.buyer_purchase_details, null);

            // Find views and set data
            TextView storeNameDetail = detailView.findViewById(R.id.store_name_detail);
            TextView productNameDetail = detailView.findViewById(R.id.product_name_detail);
            TextView quantityDetail = detailView.findViewById(R.id.quantity_detail);
            TextView totalPriceDetail = detailView.findViewById(R.id.total_price_detail);
            TextView overallTotalDetail = detailView.findViewById(R.id.overall_total_detail);
            TextView deliveryMethodDetail = detailView.findViewById(R.id.delivery_method_detail);
            TextView paymentMethodDetail = detailView.findViewById(R.id.payment_method_detail);
            TextView deliveryPaymentDetail = detailView.findViewById(R.id.delivery_payment_detail);
            TextView firstNameDetail = detailView.findViewById(R.id.first_name_detail);
            TextView lastNameDetail = detailView.findViewById(R.id.last_name_detail);
            TextView provinceDetail = detailView.findViewById(R.id.province_detail);
            TextView cityDetail = detailView.findViewById(R.id.city_detail);
            TextView barangayDetail = detailView.findViewById(R.id.barangay_detail);
            TextView zipCodeDetail = detailView.findViewById(R.id.zip_code_detail);
            TextView zoneDetail = detailView.findViewById(R.id.zone_detail);
            TextView dateDetail = detailView.findViewById(R.id.date_detail);

            // Set text values
            storeNameDetail.setText("Store Name: " + purchase.getStoreName());
            productNameDetail.setText("Product Name: " + purchase.getProductName());
            quantityDetail.setText("Quantity: " + purchase.getQuantity());
            totalPriceDetail.setText("Total Price: ₱" + purchase.getTotalPrice());
            overallTotalDetail.setText(String.format("Total Payment: ₱%.2f", purchase.getTotal()));
            deliveryMethodDetail.setText("Delivery: " + purchase.getDeliveryMethod());
            deliveryPaymentDetail.setText(String.format("Delivery Payment: ₱%.2f", + purchase.getDeliveryPayment()));
            paymentMethodDetail.setText("Payment: " + purchase.getPaymentMethod());
            firstNameDetail.setText("Firstname: " + purchase.getFirstName());
            lastNameDetail.setText("Lastname: " + purchase.getLastName());
            provinceDetail.setText("Province: " + purchase.getProvince());
            cityDetail.setText("City/Municipality: " + purchase.getCity());
            barangayDetail.setText("Brgy: " + purchase.getBarangay());
            zipCodeDetail.setText("Zip code: " + purchase.getZipCode());
            zoneDetail.setText("Zone: " + purchase.getZone());
            dateDetail.setText("Date: " + purchase.getPurchaseDate());

            // Show the dialog with the detail view
            builder.setPositiveButton("Close", null);
            builder.setView(detailView)
                    .show();
        });

        holder.chatButton.setOnClickListener(v -> {
            // Get product ID from the current purchase
            String productId = purchase.getProductId();

            // Reference to the 'upload_products' node to fetch product details
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("upload_products").child(productId);

            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the vendor's userId (receiverId)
                        String receiverId = snapshot.child("userId").getValue(String.class);
                        String storeName = snapshot.child("storeName").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        // Now that we have receiverId and other details, start the chat activity
                        Intent intent = new Intent(context, buyer_product_chatbox.class);
                        intent.putExtra("storeName", storeName);
                        intent.putExtra("profileImageUrl", profileImageUrl);
                        intent.putExtra("receiverId", receiverId);
                        intent.putExtra("senderId", currentUserId);

                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Product details not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, "Failed to fetch product details", Toast.LENGTH_SHORT).show();
                }
            });
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName, productName, quantity, totalPrice, status, seeAll, productType;
        ImageView productImage;
        Button cancelButton, chatButton;  // Add chatButton here

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.store_name);
            productName = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            status = itemView.findViewById(R.id.status);
            productImage = itemView.findViewById(R.id.product_image);
            seeAll = itemView.findViewById(R.id.see_all);
            cancelButton = itemView.findViewById(R.id.cancel_button);
            chatButton = itemView.findViewById(R.id.chat);
            productType = itemView.findViewById(R.id.productType);
        }
    }
}
