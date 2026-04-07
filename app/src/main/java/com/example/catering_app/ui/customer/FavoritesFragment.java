package com.example.catering_app.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.catering_app.R;
import com.example.catering_app.adapters.FavoriteAdapter;
import com.example.catering_app.models.FavoriteItem;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoriteAdapter.OnFavoriteRemoveListener {

    private RecyclerView rvFavorites;
    private LinearLayout emptyState;
    private TextView tvFavoriteCount;
    private FavoriteAdapter adapter;
    private List<FavoriteItem> favoriteList;

    // Category chips
    private TextView chipAll, chipItalian, chipBiryani, chipChinese, chipContinental;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initViews(view);
        setupRecyclerView();
        setupCategoryChips();
        loadSampleData();
        updateFavoriteCount();

        return view;
    }

    private void initViews(View view) {
        rvFavorites = view.findViewById(R.id.rvFavorites);
        emptyState = view.findViewById(R.id.emptyState);
        tvFavoriteCount = view.findViewById(R.id.tvFavoriteCount);

        chipAll = view.findViewById(R.id.chipAll);
        chipItalian = view.findViewById(R.id.chipItalian);
        chipBiryani = view.findViewById(R.id.chipBiryani);
        chipChinese = view.findViewById(R.id.chipChinese);
        chipContinental = view.findViewById(R.id.chipContinental);

        Button btnExplore = view.findViewById(R.id.btnExplore);
        btnExplore.setOnClickListener(v -> {
            // Navigate to home or search
        });
    }

    private void setupRecyclerView() {
        favoriteList = new ArrayList<>();
        adapter = new FavoriteAdapter(favoriteList, this);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavorites.setAdapter(adapter);
    }

    private void setupCategoryChips() {
        chipAll.setOnClickListener(v -> {
            updateChipSelection(chipAll);
            adapter.filterByCategory("All");
            updateFavoriteCount();
        });

        chipItalian.setOnClickListener(v -> {
            updateChipSelection(chipItalian);
            adapter.filterByCategory("Italian");
            updateFavoriteCount();
        });

        chipBiryani.setOnClickListener(v -> {
            updateChipSelection(chipBiryani);
            adapter.filterByCategory("Biryani");
            updateFavoriteCount();
        });

        chipChinese.setOnClickListener(v -> {
            updateChipSelection(chipChinese);
            adapter.filterByCategory("Chinese");
            updateFavoriteCount();
        });

        chipContinental.setOnClickListener(v -> {
            updateChipSelection(chipContinental);
            adapter.filterByCategory("Continental");
            updateFavoriteCount();
        });
    }

    private void updateChipSelection(TextView selectedChip) {
        // Reset all chips
        TextView[] chips = {chipAll, chipItalian, chipBiryani, chipChinese, chipContinental};
        for (TextView chip : chips) {
            chip.setBackgroundResource(R.drawable.chip_unselected);
            chip.setTextColor(getResources().getColor(R.color.navy));
        }
        // Highlight selected chip
        selectedChip.setBackgroundResource(R.drawable.chip_selected);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void loadSampleData() {
        favoriteList.add(new FavoriteItem(1, "Grand Kitchen", "Italian • Continental",
                4.8, "1.2k", "2.5 km", "30-45 min", "₹500/plate", R.drawable.caterer_hero_1));
        favoriteList.add(new FavoriteItem(2, "Royal Biryani House", "Biryani • Mughlai",
                4.9, "2.5k", "1.2 km", "35-50 min", "₹600/plate", R.drawable.caterer_hero_2));
        favoriteList.add(new FavoriteItem(3, "Spice Garden", "Indian • North Indian",
                4.7, "800", "3.0 km", "20-35 min", "₹400/plate", R.drawable.caterer_hero_1));

        adapter.notifyDataSetChanged();
    }

    private void updateFavoriteCount() {
        int count = adapter.getItemCount();
        tvFavoriteCount.setText(count + " items");

        if (count == 0) {
            rvFavorites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFavoriteRemoved(int position) {
        updateFavoriteCount();
    }
}