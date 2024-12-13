package com.example.allgoods2024;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BuyerPurchaseDoneAdapter extends RecyclerView.Adapter<BuyerPurchaseDoneAdapter.ViewHolder> {

    private List<Purchase> purchaseList;
    private OnSeeAllClickListener onSeeAllClickListener;
    private OnReviewClickListener onReviewClickListener;

    public interface OnReviewClickListener {
        void onReviewClick(Purchase purchase);
    }

    public interface OnSeeAllClickListener {
        void onSeeAllClick(Purchase purchase);
    }

    public BuyerPurchaseDoneAdapter(List<Purchase> purchaseList, OnSeeAllClickListener onSeeAllClickListener, OnReviewClickListener onReviewClickListener) {
        this.purchaseList = purchaseList;
        this.onSeeAllClickListener = onSeeAllClickListener;
        this.onReviewClickListener = onReviewClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_purchases_done_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.date.setText("Date: " + purchase.getPurchaseDate());
        holder.storeName.setText("Store Name: " + purchase.getStoreName());
        holder.productName.setText("Product Name: " + purchase.getProductName());
        holder.quantity.setText("Quantity: " + purchase.getQuantity());
        holder.totalPrice.setText("₱" + purchase.getTotal());

        Glide.with(holder.itemView.getContext())
                .load(purchase.getproductImageUrl())
                .into(holder.productImage);

        holder.seeAll.setOnClickListener(v -> onSeeAllClickListener.onSeeAllClick(purchase));
        holder.review.setOnClickListener(v -> onReviewClickListener.onReviewClick(purchase));

        holder.buyAgain.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), buyer_buy_product.class);
            intent.putExtra("productId", purchase.getProductId());
            intent.putExtra("userId", purchase.getUserId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName, productName, quantity, totalPrice, seeAll, date;
        ImageView productImage;
        Button buyAgain, review;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.store_name);
            productName = itemView.findViewById(R.id.product_name);
            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            seeAll = itemView.findViewById(R.id.see_all);
            productImage = itemView.findViewById(R.id.product_image);
            date = itemView.findViewById(R.id.date);
            buyAgain = itemView.findViewById(R.id.buy_again);
            review = itemView.findViewById(R.id.review);
        }
    }
}
