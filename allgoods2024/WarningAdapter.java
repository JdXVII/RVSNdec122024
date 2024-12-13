package com.example.allgoods2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.WarningViewHolder> {
    private List<Warning> warnings;

    public WarningAdapter(List<Warning> warnings) {
        this.warnings = warnings;
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        Warning warning = warnings.get(position);
        holder.nameTextView.setText(warning.getName());
        holder.reasonTextView.setText(warning.getReason());
    }

    @Override
    public int getItemCount() {
        return warnings.size();
    }

    public static class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView reasonTextView;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.warningName);
            reasonTextView = itemView.findViewById(R.id.warningReason);
        }
    }
}
