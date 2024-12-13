package com.example.allgoods2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    private final List<Receipt> receiptList;

    public ReceiptAdapter(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_item, parent, false);
        return new ReceiptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        Receipt receipt = receiptList.get(position);
        holder.dateTextView.setText(receipt.getDate());
        holder.totalSalesTextView.setText(String.format("Total Sales: %.2f", receipt.getTotalSales()));
        holder.totalCommissionTextView.setText(String.format("Total Percentage: %.2f", receipt.getTotalCommission()));
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }

    static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, totalSalesTextView, totalCommissionTextView;

        ReceiptViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            totalSalesTextView = itemView.findViewById(R.id.totalSalesTextView);
            totalCommissionTextView = itemView.findViewById(R.id.totalCommissionTextView);
        }
    }
}
