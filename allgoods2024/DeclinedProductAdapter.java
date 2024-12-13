package com.example.allgoods2024;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DeclinedProductAdapter extends RecyclerView.Adapter<DeclinedProductAdapter.DeclinedProductViewHolder> {

    private List<DeclinedProduct> declinedProducts;
    private String currentUserId; // Add this to handle the current user's ID

    public DeclinedProductAdapter(List<DeclinedProduct> declinedProducts, String currentUserId) {
        this.declinedProducts = declinedProducts;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public DeclinedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_declined_product, parent, false);
        return new DeclinedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeclinedProductViewHolder holder, int position) {
        DeclinedProduct product = declinedProducts.get(position);
        holder.declineReasonText.setText(product.getReason());
        holder.name.setText(product.getName());

        holder.remove.setOnClickListener(v -> showConfirmationDialog(holder.itemView, product.getUserId(), product.getReason()));
    }

    @Override
    public int getItemCount() {
        return declinedProducts.size();
    }

    private void showConfirmationDialog(View itemView, String userId, String reason) {
        new AlertDialog.Builder(itemView.getContext())
                .setTitle("Delete Notification")
                .setMessage("Are you sure you want to delete this notification?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem(userId, reason))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteItem(String userId, String reason) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("declined_products");
        databaseReference.orderByChild("userId").equalTo(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    DeclinedProduct product = snapshot.getValue(DeclinedProduct.class);
                    if (product != null && product.getReason().equals(reason)) {
                        snapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                            // Successfully removed
                        }).addOnFailureListener(e -> {
                            // Failed to remove
                        });
                        break; // Assuming `reason` is unique, break after removing
                    }
                }
            }
        });
    }

    public static class DeclinedProductViewHolder extends RecyclerView.ViewHolder {
        TextView declineReasonText, name;
        ImageView remove;

        public DeclinedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            declineReasonText = itemView.findViewById(R.id.declineReasonText);
            name = itemView.findViewById(R.id.name);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
