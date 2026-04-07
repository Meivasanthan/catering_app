package com.example.catering_app.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catering_app.R;
import com.example.catering_app.models.FavoriteItem;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<FavoriteItem> favoriteList;
    private List<FavoriteItem> originalList;
    private OnFavoriteRemoveListener removeListener;

    public interface OnFavoriteRemoveListener {
        void onFavoriteRemoved(int position);
    }

    public FavoriteAdapter(List<FavoriteItem> favoriteList, OnFavoriteRemoveListener listener) {
        this.favoriteList = new ArrayList<>(favoriteList);
        this.originalList = new ArrayList<>(favoriteList);
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem item = favoriteList.get(position);

        holder.tvCatererName.setText(item.getName());
        holder.tvCuisine.setText(item.getCuisine());
        holder.tvRating.setText(String.valueOf(item.getRating()));
        holder.tvReviews.setText("(" + item.getReviews() + ")");
        holder.tvDistance.setText(item.getDistance());
        holder.tvDeliveryTime.setText(item.getDeliveryTime());
        holder.tvPrice.setText(item.getPrice());

        // Set image (using placeholder for now)
        holder.ivCatererImage.setImageResource(item.getImageResId());

        // Favorite button click with animation
        holder.ivFavorite.setOnClickListener(v -> {
            animateHeart(holder.ivFavorite);
            removeFavorite(position);
        });

        // View Menu button
        holder.btnViewMenu.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(),
                    "Viewing menu for " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        // Book Now button
        holder.btnBookNow.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(),
                    "Booking " + item.getName(), Toast.LENGTH_LONG).show();
        });

        // Card click
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(),
                    "Opening " + item.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void animateHeart(ImageView heart) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(heart, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(heart, "scaleY", 1f, 1.3f, 1f);
        scaleUpX.setDuration(300);
        scaleUpY.setDuration(300);
        scaleUpX.start();
        scaleUpY.start();
    }

    private void removeFavorite(int position) {
        favoriteList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, favoriteList.size());
        if (removeListener != null) {
            removeListener.onFavoriteRemoved(position);
        }
    }

    public void filterByCategory(String category) {
        if (category.equals("All")) {
            favoriteList.clear();
            favoriteList.addAll(originalList);
        } else {
            favoriteList.clear();
            for (FavoriteItem item : originalList) {
                if (item.getCuisine().contains(category)) {
                    favoriteList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return favoriteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCatererImage, ivFavorite;
        TextView tvCatererName, tvCuisine, tvRating, tvReviews;
        TextView tvDistance, tvDeliveryTime, tvPrice;
        Button btnViewMenu, btnBookNow;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCatererImage = itemView.findViewById(R.id.ivCatererImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvCatererName = itemView.findViewById(R.id.tvCatererName);
            tvCuisine = itemView.findViewById(R.id.tvCuisine);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviews = itemView.findViewById(R.id.tvReviews);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDeliveryTime = itemView.findViewById(R.id.tvDeliveryTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnViewMenu = itemView.findViewById(R.id.btnViewMenu);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}