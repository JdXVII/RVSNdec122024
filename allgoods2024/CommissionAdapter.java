package com.example.allgoods2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.CommissionViewHolder> {

    private List<Purchase> purchaseList;

    public CommissionAdapter(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public CommissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commission, parent, false);
        return new CommissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommissionViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);

        holder.productNameTextView.setText(purchase.getProductName());
        holder.categoryTextView.setText(purchase.getCategory());
        holder.quantityTextView.setText(String.valueOf(purchase.getQuantity()));
        holder.priceTextView.setText(purchase.getprice());
        holder.totalPriceTextView.setText(String.format("₱%.2f", Double.parseDouble(purchase.getTotalPrice())));
        holder.feeTextView.setText(String.format("₱%.2f", purchase.getFee()));
        holder.purchaseDateTextView.setText(purchase.getPurchaseDate());
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public static class CommissionViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, categoryTextView, quantityTextView, priceTextView, totalPriceTextView, feeTextView, purchaseDateTextView;

        public CommissionViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name);
            categoryTextView = itemView.findViewById(R.id.category);
            quantityTextView = itemView.findViewById(R.id.quantity);
            priceTextView = itemView.findViewById(R.id.price);
            totalPriceTextView = itemView.findViewById(R.id.total_price);
            feeTextView = itemView.findViewById(R.id.fee);
            purchaseDateTextView = itemView.findViewById(R.id.purchase_date);
        }
    }
}
