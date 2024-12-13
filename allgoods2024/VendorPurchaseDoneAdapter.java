package com.example.allgoods2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VendorPurchaseDoneAdapter extends RecyclerView.Adapter<VendorPurchaseDoneAdapter.ViewHolder> {

    private List<Purchase> purchaseList;
    private OnSeeAllClickListener onSeeAllClickListener;

    public interface OnSeeAllClickListener {
        void onSeeAllClick(Purchase purchase);
    }

    public VendorPurchaseDoneAdapter(List<Purchase> purchaseList, OnSeeAllClickListener onSeeAllClickListener) {
        this.purchaseList = purchaseList;
        this.onSeeAllClickListener = onSeeAllClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_purchases_done_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.storeName.setText("Store Name: " +purchase.getStoreName());
        holder.productName.setText("Product Name: " +purchase.getProductName());
        holder.quantity.setText("Quantity: " + purchase.getQuantity());
        holder.totalPrice.setText("Price: " + purchase.getTotalPrice());

        Glide.with(holder.itemView.getContext())
                .load(purchase.getproductImageUrl())
                .into(holder.productImage);

        holder.seeAll.setOnClickListener(v -> onSeeAllClickListener.onSeeAllClick(purchase));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName, productName, quantity, totalPrice, seeAll;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.store_name);
            productName = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            seeAll = itemView.findViewById(R.id.see_all);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
