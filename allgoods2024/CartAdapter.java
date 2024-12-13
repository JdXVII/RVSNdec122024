package com.example.allgoods2024;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;

    public CartAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // Bind data to views
        holder.storeNameTextView.setText(cartItem.getStoreName());
        holder.productNameTextView.setText(cartItem.getProductName());
        holder.priceTextView.setText("₱" + cartItem.getPrice());
        Glide.with(context).load(cartItem.getProductImageUrl()).into(holder.productImageView);

        holder.buyNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, buyer_addtocart_buynow_product.class);
            intent.putExtra("storeName", cartItem.getStoreName());
            intent.putExtra("productName", cartItem.getProductName());
            intent.putExtra("price", String.valueOf(cartItem.getPrice()));
            intent.putExtra("type", cartItem.getType());
            intent.putExtra("quantity", cartItem.getQuantity());
            intent.putExtra("deliveryMethod", cartItem.getDeliveryMethod());
            intent.putExtra("paymentMethod", cartItem.getPaymentMethod());
            intent.putExtra("totalPrice", String.valueOf(cartItem.getTotalPrice()));
            intent.putExtra("productId", cartItem.getProductId());
            intent.putExtra("userId", cartItem.getUserId());
            intent.putExtra("productImageUrl", cartItem.getProductImageUrl());
            intent.putExtra("category", cartItem.getCategory());
            intent.putExtra("productType", cartItem.getProductType());
            intent.putExtra("cartId", cartItem.getCartId()); // Pass the cartId to remove it later
            context.startActivity(intent);
        });

        holder.removeButton.setOnClickListener(v -> {
            confirmRemoveCartItem(cartItem.getCartId());
        });

        holder.seeAll.setOnClickListener(v -> showPurchaseDetails(cartItem)); // Handle "See all" click

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, buyer_buy_product.class);
            intent.putExtra("storeName", cartItem.getStoreName());
            intent.putExtra("productName", cartItem.getProductName());
            intent.putExtra("price", String.valueOf(cartItem.getPrice()));
            intent.putExtra("type", cartItem.getType());
            intent.putExtra("quantity", cartItem.getQuantity());
            intent.putExtra("deliveryMethod", cartItem.getDeliveryMethod());
            intent.putExtra("paymentMethod", cartItem.getPaymentMethod());
            intent.putExtra("totalPrice", String.valueOf(cartItem.getTotalPrice()));
            intent.putExtra("productId", cartItem.getProductId());
            intent.putExtra("userId", cartItem.getUserId());
            intent.putExtra("productImageUrl", cartItem.getProductImageUrl());
            intent.putExtra("category", cartItem.getCategory());
            intent.putExtra("productType", cartItem.getProductType());
            intent.putExtra("cartId", cartItem.getCartId());
            context.startActivity(intent);
        });
    }



    private void showPurchaseDetails(CartItem cartItem) {
        // Inflate the custom layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cart_details, null);

        // Initialize the dialog's views
        TextView storeName = dialogView.findViewById(R.id.dialog_store_name);
        TextView productName = dialogView.findViewById(R.id.dialog_product_name);
        TextView quantity = dialogView.findViewById(R.id.dialog_quantity);
        TextView deliveryMethod = dialogView.findViewById(R.id.delivery_method_detail);
        TextView paymentMethod = dialogView.findViewById(R.id.payment_method_detail);
        TextView totalPrice = dialogView.findViewById(R.id.dialog_total_price);
        TextView price = dialogView.findViewById(R.id.price);
        TextView type = dialogView.findViewById(R.id.type_detail);

        // Set the data for the dialog's views
        storeName.setText("Store Name: " + cartItem.getStoreName());
        productName.setText("Product Name: " + cartItem.getProductName());
        quantity.setText("Quantity: " + cartItem.getQuantity());
        deliveryMethod.setText("Delivery Method: " + cartItem.getDeliveryMethod());
        paymentMethod.setText("Payment: " + cartItem.getPaymentMethod());
        totalPrice.setText("Total Price: " + cartItem.getTotalPrice());
        price.setText("Price: ₱" + cartItem.getPrice());
        type.setText(cartItem.getType());

        // Create and show the dialog
        new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
                .setView(dialogView)
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView storeNameTextView, productNameTextView, priceTextView;
        ImageView productImageView, removeButton;
        Button buyNowButton, editButton;
        TextView seeAll;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            storeNameTextView = itemView.findViewById(R.id.store_name);
            productNameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.price);
            productImageView = itemView.findViewById(R.id.product_image);
            buyNowButton = itemView.findViewById(R.id.buy_now);
            removeButton = itemView.findViewById(R.id.remove);
            seeAll = itemView.findViewById(R.id.see_all);
            editButton = itemView.findViewById(R.id.edit);
        }
    }


    private void confirmRemoveCartItem(String cartId) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item from your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    removeCartItem(cartId);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void removeCartItem(String cartId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("addtocart").child(cartId);
        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show());
    }

}
