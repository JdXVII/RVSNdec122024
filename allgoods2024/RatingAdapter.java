package com.example.allgoods2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private List<Rating> ratingList;
    private int maxVisibleRatings; // Maximum number of ratings to display

    public RatingAdapter(List<Rating> ratingList, int maxVisibleRatings) {
        this.ratingList = ratingList;
        this.maxVisibleRatings = maxVisibleRatings;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_rating_card_item, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        setStars(holder, rating.getRating());
        holder.buyerNameTextView.setText(rating.getBuyerFirstName());
    }

    @Override
    public int getItemCount() {
        return Math.min(ratingList.size(), maxVisibleRatings);
    }

    private void setStars(RatingViewHolder holder, int rating) {
        int[] starIds = {R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
        for (int i = 0; i < 5; i++) {
            ImageView star = holder.itemView.findViewById(starIds[i]);
            if (i < rating) {
                star.setImageResource(R.drawable.star_filled);
            } else {
                star.setImageResource(R.drawable.star_empty);
            }
        }
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        public TextView buyerNameTextView;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            buyerNameTextView = itemView.findViewById(R.id.buyerName);
        }
    }
}
