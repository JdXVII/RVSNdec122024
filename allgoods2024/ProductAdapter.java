package com.example.allgoods2024;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;
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
        void onItemClick(String productId);
    }

    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.productList = productList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product details
        holder.productName.setText(product.getName());
        holder.productStock.setText("Stock: " + product.getStock());
        holder.productCategory.setText(product.getCategory());
        holder.type_product.setText("(" + product.getProductType() + ")");
        holder.productStatus.setText(String.format("%.2f", product.getAverageRating())
                + " (" + product.getUserRatingCount() + ") " + formatSoldNumber(product.getSold()));

        // Load product image
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(context).load(product.getImageUrls().get(0)).into(holder.productImage);
        }

        // Check if the product has an event
        if (product.getEvent()) {
            fetchActiveEventDiscount(new EventDiscountCallback() {
                @Override
                public void onEventDiscountRetrieved(String discount) {
                    if (discount != null) {
                        try {
                            // Calculate discounted price
                            double originalPrice = Double.parseDouble(product.getPrice());
                            double discountValue = Double.parseDouble(discount);
                            double discountedPrice = originalPrice * (1 - discountValue / 100);

                            // Format and display prices
                            holder.productPrice.setText(String.format("₱%.2f", originalPrice));
                            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            holder.productSale.setText(String.format("₱%.2f -%s%%", discountedPrice, discount));

                            // Show sale tag
                            holder.saleTag.setVisibility(View.VISIBLE);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // If no discount is available, show original price without modifications
                        holder.productPrice.setText("₱" + product.getPrice());
                        holder.productSale.setText("");
                        holder.saleTag.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            // No event, display regular price
            holder.productPrice.setText("₱" + product.getPrice());
            holder.productSale.setText("");
            holder.saleTag.setVisibility(View.GONE);
        }

        if (product.getEvent()) {
            holder.saleTag.setVisibility(View.VISIBLE);
            holder.productSale.setVisibility(View.VISIBLE);
        } else {
            holder.saleTag.setVisibility(View.GONE);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(product.getProductId());
            }
        });
    }

    private void fetchActiveEventDiscount(final EventDiscountCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("events").orderByChild("isActive").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                                String discount = eventSnapshot.child("discount").getValue(String.class);
                                if (discount != null) {
                                    callback.onEventDiscountRetrieved(discount);
                                    return; // Exit after finding the first active event
                                }
                            }
                        }
                        callback.onEventDiscountRetrieved(null); // No active event
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onEventDiscountRetrieved(null);
                    }
                });
    }

    public interface EventDiscountCallback {
        void onEventDiscountRetrieved(String discount);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage, saleTag;
        TextView productName;
        TextView productPrice;
        TextView productStock;
        TextView productCategory;
        TextView productStatus, type_product, productSale;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productStock = itemView.findViewById(R.id.product_stock);
            productCategory = itemView.findViewById(R.id.product_category);
            productStatus = itemView.findViewById(R.id.product_status);
            type_product = itemView.findViewById(R.id.type_product);
            saleTag = itemView.findViewById(R.id.sale_tag);
            productSale = itemView.findViewById(R.id.product_sale);
        }
    }
}
