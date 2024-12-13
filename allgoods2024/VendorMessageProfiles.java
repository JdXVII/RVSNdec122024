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

public class VendorMessageProfiles extends RecyclerView.Adapter<VendorMessageProfiles.VendorMessageViewHolder> {
    private List<Buyer> buyerList;
    private String senderId;
    private Context context;

    public VendorMessageProfiles(Context context, List<Buyer> buyerList, String senderId) {
        this.context = context;
        this.buyerList = buyerList;
        this.senderId = senderId;
    }

    @Override
    public VendorMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_message_profiles, parent, false);
        return new VendorMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VendorMessageViewHolder holder, int position) {
        Buyer buyer = buyerList.get(position);
        holder.firstName.setText(buyer.firstName);
        holder.lastName.setText(buyer.lastName);
        Glide.with(context).load(buyer.profileImageUrl).into(holder.profileImage);

        // Show unread message count
        if (buyer.getUnreadMessageCount() > 0) {
            holder.number.setText(String.valueOf(buyer.getUnreadMessageCount()));
            holder.number.setVisibility(View.VISIBLE);
        } else if(buyer.getUnreadMessageCount() > 99) {
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
                Intent intent = new Intent(context, vendor_product_chatbox.class);
                // Pass the buyerId and senderId to the intent
                intent.putExtra("firstName", buyer.firstName);
                intent.putExtra("lastName", buyer.lastName);
                intent.putExtra("profileImageUrl", buyer.profileImageUrl);
                intent.putExtra("senderId", buyer.getUserId());
                intent.putExtra("receiverId", senderId);
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
        return buyerList.size();
    }

    public class VendorMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView firstName, lastName, number;

        public VendorMessageViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            firstName = itemView.findViewById(R.id.firstName);
            lastName = itemView.findViewById(R.id.lastName);
            number = itemView.findViewById(R.id.number);
        }
    }
}
