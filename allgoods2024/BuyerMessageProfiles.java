package com.example.allgoods2024;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BuyerMessageProfiles extends RecyclerView.Adapter<BuyerMessageProfiles.VendorMessageViewHolder> {
    private List<Vendor> vendorList;
    private String senderId;
    private Context context;

    public BuyerMessageProfiles(Context context, List<Vendor> vendorList, String senderId) {
        this.context = context;
        this.vendorList = vendorList;
        this.senderId = senderId;
    }

    @Override
    public VendorMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_message_profiles, parent, false);
        return new VendorMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VendorMessageViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        holder.storeName.setText(vendor.storeName);
        Glide.with(context).load(vendor.profileImageUrl).into(holder.profileImage);

        // Show unread message count
        if (vendor.getUnreadMessageCount() > 0) {
            holder.number.setText(String.valueOf(vendor.getUnreadMessageCount()));
            holder.number.setVisibility(View.VISIBLE);
        } else if(vendor.getUnreadMessageCount() > 99) {
            holder.number.setText("99+");
            holder.number.setVisibility(View.VISIBLE);
        } else {
            holder.number.setVisibility(View.GONE);
        }

        // Set up click listener for the entire item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the vendor_product_chatbox activity
                Intent intent = new Intent(context, buyer_product_chatbox.class);
                // Pass the buyerId and senderId to the intent
                intent.putExtra("storeName", vendor.storeName);
                intent.putExtra("profileImageUrl", vendor.profileImageUrl);
                intent.putExtra("receiverId", vendor.getUserId());
                intent.putExtra("senderId", senderId);
                context.startActivity(intent);

                // Apply transition effect if context is an Activity
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public class VendorMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView storeName, number;

        public VendorMessageViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            storeName = itemView.findViewById(R.id.storeName);
            number = itemView.findViewById(R.id.number);
        }
    }
}
