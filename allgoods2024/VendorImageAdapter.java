package com.example.allgoods2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VendorImageAdapter extends RecyclerView.Adapter<VendorImageAdapter.VendorImageViewHolder> {

    private Context context;
    private List<String> imageUrls;

    public VendorImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public VendorImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vendor_image_item, parent, false);
        return new VendorImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context).load(imageUrl).into(holder.vendorImage);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class VendorImageViewHolder extends RecyclerView.ViewHolder {
        ImageView vendorImage;

        public VendorImageViewHolder(@NonNull View itemView) {
            super(itemView);
            vendorImage = itemView.findViewById(R.id.vendor_image);
        }
    }
}
