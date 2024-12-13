package com.example.allgoods2024;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class VendorMessagesAdapter extends RecyclerView.Adapter<VendorMessagesAdapter.MessageViewHolder> {

    private List<Message> messagesList;
    private String currentUserId;
    private Context context;
    private DatabaseReference buyersRef;

    public VendorMessagesAdapter(Context context, List<Message> messagesList, String currentUserId) {
        this.context = context;
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
        buyersRef = FirebaseDatabase.getInstance().getReference("buyers");
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messagesList.get(position);

        // Align message container and image based on message category
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        if ("buyer".equals(message.getCategory())) {
            params.gravity = android.view.Gravity.START;
            holder.messageImageView.setLayoutParams(params);
            holder.messageTimestamp.setLayoutParams(params);
            holder.profileImageView.setVisibility(View.VISIBLE);
            fetchBuyerProfileImage(message.getSenderId(), holder.profileImageView);
        } else if ("vendor".equals(message.getCategory())) {
            params.gravity = android.view.Gravity.END;
            holder.messageImageView.setLayoutParams(params);
            holder.messageTimestamp.setLayoutParams(params);
            holder.profileImageView.setVisibility(View.GONE);
        }
        holder.messageContainer.setLayoutParams(params);

        // Set background color based on message category
        if ("buyer".equals(message.getCategory())) {
            holder.messageTextView.setBackgroundResource(R.drawable.message_bubble_grey); // Define this drawable for buyer
            holder.messageTextView.setTextColor(android.graphics.Color.WHITE);
        } else if ("vendor".equals(message.getCategory())) {
            holder.messageTextView.setBackgroundResource(R.drawable.message_bubble_green); // Define this drawable for vendor
            holder.messageTextView.setTextColor(android.graphics.Color.DKGRAY); // Grey color
        }

        // Show or hide text view and image view based on message content
        if (message.getText() != null && !message.getText().isEmpty()) {
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageTextView.setText(message.getText());
            holder.messageImageView.setVisibility(View.GONE); // Hide image view if text is present
        } else {
            holder.messageTextView.setVisibility(View.GONE);
            if (message.getImage() != null) {
                holder.messageImageView.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext()).load(message.getImage()).into(holder.messageImageView);

                // Apply fixed width and height
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                        200,
                        400  // Height in dp
                );
                imageParams.setMargins(10, 8, 0, 0); // Set top margin to 8dp
                imageParams.gravity = params.gravity; // Align image according to the category
                holder.messageImageView.setLayoutParams(imageParams);
                // Set rounded corners background
                holder.messageImageView.setBackgroundResource(R.drawable.round_corners);

                // Set click listener for image view to show preview
                holder.messageImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showImagePreview(message.getImage());
                    }
                });
            } else {
                holder.messageImageView.setVisibility(View.GONE);
            }
        }

        // Show or hide the timestamp
        holder.messageTimestamp.setText(message.getTimestamp());
        holder.messageTimestamp.setVisibility(View.GONE); // Initially hide the timestamp

        // Set click listener to toggle timestamp visibility
        holder.messageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.messageTimestamp.getVisibility() == View.GONE) {
                    holder.messageTimestamp.setVisibility(View.VISIBLE);
                } else {
                    holder.messageTimestamp.setVisibility(View.GONE);
                }
            }
        });
    }

    private void fetchBuyerProfileImage(String buyerId, final ImageView profileImageView) {
        buyersRef.child(buyerId).child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileImageUrl = dataSnapshot.getValue(String.class);
                if (profileImageUrl != null) {
                    Glide.with(context).load(profileImageUrl).into(profileImageView);
                } else {
                    // Handle case where profileImageUrl is null
                    profileImageView.setImageResource(R.drawable.user); // Set a default image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                String errorMessage = "Error fetching profile image: " + databaseError.getMessage();

                // Set a default image in case of an error
                profileImageView.setImageResource(R.drawable.user);
            }
        });
    }


    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private void showImagePreview(String imageUrl) {
        Intent intent = new Intent(context, image_preview.class);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);

        // Apply the transition effect
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout messageContainer;
        public TextView messageTextView;
        public PhotoView messageImageView;
        public TextView messageTimestamp;
        public ImageView profileImageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
