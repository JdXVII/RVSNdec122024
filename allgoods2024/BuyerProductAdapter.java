package com.example.allgoods2024;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class BuyerProductAdapter extends RecyclerView.Adapter<BuyerProductAdapter.ProductViewHolder> {

    private List<Product> productList;
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
            return sold;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public BuyerProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, sale_tag;
        TextView productType, productName, productPrice, productSale, productStock, productCategory, productDesc;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productSale = itemView.findViewById(R.id.product_sale);
            productStock = itemView.findViewById(R.id.product_stock);
            productCategory = itemView.findViewById(R.id.product_category);
            productDesc = itemView.findViewById(R.id.product_desc);
            sale_tag = itemView.findViewById(R.id.sale_tag);
            productType = itemView.findViewById(R.id.product_type);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                String firstImageUrl = product.getImageUrls().get(0);
                Glide.with(itemView.getContext()).load(firstImageUrl).into(productImage);
            }

            productName.setText(product.getName());
            productDesc.setText(product.getDescription());
            productCategory.setText(product.getCategory() + " (" + product.getType() + ")");
            productType.setText("(" + product.getProductType() + ")");

            double price = Double.parseDouble(product.getPrice());

            if (product.getEvent()) {
                // Only apply discount if the product has event set to true
                fetchActiveEventDiscount(new EventDiscountCallback() {
                    @Override
                    public void onEventDiscountRetrieved(String discount) {
                        if (discount != null) {
                            double discountValue = Double.parseDouble(discount); // Convert discount to percentage
                            double discountedPrice = price * (1 - discountValue / 100);  // Calculate discounted price

                            // Format the discounted price and original price
                            String formattedDiscountedPrice = String.format("₱%.2f", discountedPrice);
                            String formattedOriginalPrice = String.format("₱%.2f", price);

                            // Display the original price with a strikethrough and the discounted price
                            productPrice.setText(formattedOriginalPrice);
                            productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            productSale.setText(formattedDiscountedPrice + " -" + discount + "%");
                        } else {
                            // No active event, display the original price
                            productPrice.setText("₱" + product.getPrice());
                            productSale.setText("");
                        }
                    }
                });
            } else {
                // If no event, display the regular price
                productPrice.setText("₱" + product.getPrice());
                productSale.setText("");
            }

            productStock.setText(String.format("%.2f", product.getAverageRating()) + " (" + product.getUserRatingCount() + ") " + formatSoldNumber(product.getSold()) + " sold");

            if (product.getEvent()) {
                sale_tag.setVisibility(View.VISIBLE);
                productSale.setVisibility(View.VISIBLE);
            } else {
                sale_tag.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product);
                }
            });
        }
    }

    // Fetch the active event discount from Firebase Realtime Database (no need for eventId)
    public void fetchActiveEventDiscount(final EventDiscountCallback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("events").orderByChild("isActive").equalTo(true)  // Fetch only active events
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming only one active event or you can decide how to handle multiple active events
                            for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                String discount = eventSnapshot.child("discount").getValue(String.class);
                                if (discount != null) {
                                    callback.onEventDiscountRetrieved(discount);
                                    return; // Exit after applying the first found active event discount
                                }
                            }
                        } else {
                            callback.onEventDiscountRetrieved(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onEventDiscountRetrieved(null);
                    }
                });
    }

    public interface EventDiscountCallback {
        void onEventDiscountRetrieved(String discount);
    }
}


