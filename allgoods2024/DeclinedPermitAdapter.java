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

public class DeclinedPermitAdapter extends RecyclerView.Adapter<DeclinedPermitAdapter.DeclinedPermitViewHolder> {

    private List<DeclinedPermit> declinedPermits;
    private String currentUserId; // Add this to handle the current user's ID

    public DeclinedPermitAdapter(List<DeclinedPermit> declinedPermits, String currentUserId) {
        this.declinedPermits = declinedPermits;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public DeclinedPermitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_declined_permit, parent, false);
        return new DeclinedPermitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeclinedPermitViewHolder holder, int position) {
        DeclinedPermit permit = declinedPermits.get(position);
        holder.declineReasonText.setText(permit.getReason());
        holder.name.setText(permit.getName());

        holder.remove.setOnClickListener(v -> showConfirmationDialog(holder.itemView, permit.getUserId(), permit.getReason()));
    }

    @Override
    public int getItemCount() {
        return declinedPermits.size();
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("declined_permits");
        databaseReference.orderByChild("userId").equalTo(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    DeclinedPermit permit = snapshot.getValue(DeclinedPermit.class);
                    if (permit != null && permit.getReason().equals(reason)) {
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

    public static class DeclinedPermitViewHolder extends RecyclerView.ViewHolder {
        TextView declineReasonText, name;
        ImageView remove;

        public DeclinedPermitViewHolder(@NonNull View itemView) {
            super(itemView);
            declineReasonText = itemView.findViewById(R.id.declineReasonText);
            name = itemView.findViewById(R.id.name);
            remove = itemView.findViewById(R.id.remove);
        }
    }
}
