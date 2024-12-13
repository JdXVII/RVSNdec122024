package com.example.allgoods2024;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.NameTextView.setText(category.getFirstName() + " " + category.getLastName());
        holder.StoreNameTextView.setText(category.getStoreName());

        // Set click listener for the card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, buyer_category_products.class);
            intent.putExtra("storeName", category.getStoreName());
            intent.putExtra("userId", category.getUserId()); // Pass userId
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView NameTextView;
        TextView StoreNameTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            NameTextView = itemView.findViewById(R.id.NameTextView);
            StoreNameTextView = itemView.findViewById(R.id.StoreNameTextView);
        }
    }
}

