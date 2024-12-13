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

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.ViewHolder> {

    private List<Product> topProductList;
    private OnItemClickListener listener;
    private String formatSoldNumber(String sold) {
        try {
            int soldInt = Integer.parseInt(sold);
            if (soldInt >= 1000) {
                double formattedValue = soldInt / 1000.0;
                return String.format("%.1fk", formattedValue);
            } else {
                return sold;
            }
        } catch (NumberFormatException e) {
            // Handle the case where parsing fails
            return sold;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public TopProductAdapter(List<Product> topProductList, OnItemClickListener listener) {
        this.topProductList = topProductList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.best_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = topProductList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return topProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView productImage;
        public TextView productName;
        public TextView storeName;
        public TextView productCategory;
        public TextView productInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            storeName = itemView.findViewById(R.id.store_name);
            productCategory = itemView.findViewById(R.id.product_category);
            productInfo = itemView.findViewById(R.id.product_info);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            productName.setText(product.getName());
            storeName.setText("Store: " + product.getStoreName());
            productCategory.setText(product.getCategory());
            productInfo.setText(String.format("%.2f", product.getAverageRating()) + " (" + product.getUserRatingCount() + ") " + formatSoldNumber(product.getSold()) + " sold");
            Glide.with(itemView.getContext()).load(product.getImageUrls().get(0)).into(productImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product);
                }
            });
        }
    }
}
