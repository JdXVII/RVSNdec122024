package com.example.allgoods2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Product> productList;
    private boolean showCheckboxes = false;

    public EventAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    // Add this method to update the list
    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vendor_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set text fields
        holder.tvProductName.setText(product.getProductName());
        holder.tvCategory.setText("Category: " + product.getCategory());
        holder.tvType.setText("Type: " + product.getType());
        holder.tvPrice.setText("Price: ₱" + product.getPrice());
        holder.tvSale.setText("Sale: " + product.getSale());
        holder.tvUnit.setText("Unit: " + product.getUnit());
        holder.tvStock.setText("Stock: " + product.getStock());

        holder.checkBox.setVisibility(showCheckboxes ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(product.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked); // Update the selected state
        });

        // Load the first image from imageUrls using Glide
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrls().get(0)) // Get the first image URL
                    .placeholder(R.drawable.add) // Placeholder image
                    .error(R.drawable.add) // Error image
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.add);
        }

        // Check if upload_products is true
        if (product.getEvent()) {
            // Query the database for the event
            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
            eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        String eventCategory = eventSnapshot.child("event_category").getValue(String.class);

                        if ("Christmas".equals(eventCategory)) {
                            holder.container.setBackgroundResource(R.drawable.christmas_border);
                        } else if ("New Year".equals(eventCategory)) {
                            holder.container.setBackgroundResource(R.drawable.new_years_border);
                        } else if ("Special Sales".equals(eventCategory)) {
                            holder.container.setBackgroundResource(R.drawable.sales_border);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database errors if needed
                }
            });
        } else {
            // Keep the default background if upload_products is false
            holder.container.setBackgroundResource(R.color.dw1);
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setShowCheckboxes(boolean showCheckboxes) {
        this.showCheckboxes = showCheckboxes;
        notifyDataSetChanged(); // Refresh all items
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvCategory, tvType, tvPrice, tvSale, tvUnit, tvStock;
        CheckBox checkBox;
        ImageView productImage;
        LinearLayout container;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvType = itemView.findViewById(R.id.tvType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSale = itemView.findViewById(R.id.tvSale);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvStock = itemView.findViewById(R.id.tvStock);
            checkBox = itemView.findViewById(R.id.checkBox);
            productImage = itemView.findViewById(R.id.product_image);
            container = itemView.findViewById(R.id.container);
        }
    }
}

