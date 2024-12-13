package com.example.allgoods2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ViewHolder> {
    private List<ArchivedPermit> archivedPermits;
    private Context context;

    public ArchiveAdapter(List<ArchivedPermit> archivedPermits, Context context) {
        this.archivedPermits = archivedPermits;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archived_permit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArchivedPermit permit = archivedPermits.get(position);
        holder.expirationDateTextView.setText("Expiration Date: " + permit.getOldExpirationDate());
        Glide.with(context).load(permit.getOldPermitImageUrl()).into(holder.permitImageView);
    }

    @Override
    public int getItemCount() {
        return archivedPermits.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView permitImageView;
        TextView expirationDateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            permitImageView = itemView.findViewById(R.id.permitImageView);
            expirationDateTextView = itemView.findViewById(R.id.expirationDateTextView);
        }
    }
}
