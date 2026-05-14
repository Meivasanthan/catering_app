package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuManagementActivity extends AppCompatActivity {

    // Toolbar
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ImageView ivBack;

    // Filter Chips
    private ChipGroup chipGroup;
    private Chip chipAll, chipVeg, chipNonVeg, chipPopular;

    // RecyclerView
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private List<FoodItem> foodItemsList;

    // Add Buttons
    private MaterialButton btnAddItemTop;
    private LinearLayout bottomAddButton;
    private MaterialButton btnAddItemBottom;

    // Empty State
    private LinearLayout llEmptyState;
    private TextView tvEmptyMessage;
    private MaterialButton btnAddFromEmpty;

    private SharedPreferences sharedPreferences;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);
        // Initialize Bottom Navigation
        View bottomNav = findViewById(R.id.bottomNavigation);

        LinearLayout btnNavDashboard = bottomNav.findViewById(R.id.btnNavDashboard);
        LinearLayout btnNavMenu = bottomNav.findViewById(R.id.btnNavMenu);
        LinearLayout btnNavOrders = bottomNav.findViewById(R.id.btnNavOrders);
        LinearLayout btnNavEarnings = bottomNav.findViewById(R.id.btnNavEarnings);
        LinearLayout btnNavProfile = bottomNav.findViewById(R.id.btnNavProfile);

// Set click listeners
        btnNavDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatererDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavMenu.setOnClickListener(v -> {
            // Already on Menu page, just refresh or show message
            loadFoodItemsFromStorage();  // Refresh the list
            Toast.makeText(this, "Menu refreshed", Toast.LENGTH_SHORT).show();
        });

        btnNavOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, OwnerOrdersActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavEarnings.setOnClickListener(v -> {
            Intent intent = new Intent(this, EarningsActivity.class);
            startActivity(intent);
            finish();
        });

        btnNavProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatererProfileActivity.class);
            startActivity(intent);
            finish();
        });

        sharedPreferences = getSharedPreferences("FOOD_DATA", MODE_PRIVATE);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupFilterChips();
        loadFoodItemsFromStorage();
        setupClickListeners();

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        ivBack = findViewById(R.id.ivBack);
        chipGroup = findViewById(R.id.chipGroup);
        chipAll = findViewById(R.id.chipAll);
        chipVeg = findViewById(R.id.chipVeg);
        chipNonVeg = findViewById(R.id.chipNonVeg);
        chipPopular = findViewById(R.id.chipPopular);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddItemTop = findViewById(R.id.btnAddItemTop);
        bottomAddButton = findViewById(R.id.bottomAddButton);
        btnAddItemBottom = findViewById(R.id.btnAddItemBottom);
        llEmptyState = findViewById(R.id.llEmptyState);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnAddFromEmpty = findViewById(R.id.btnAddFromEmpty);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        tvToolbarTitle.setText("Menu Management");
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        foodItemsList = new ArrayList<>();
        menuAdapter = new MenuAdapter(this, foodItemsList, new MenuAdapter.OnMenuItemActionListener() {
            @Override
            public void onEdit(FoodItem item) {
                openEditFoodItem(item);
            }

            @Override
            public void onDelete(FoodItem item) {
                deleteFoodItem(item);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(menuAdapter);
    }

    private void setupFilterChips() {
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentFilter = "All";
                filterFoodItems();
                return;
            }

            Chip selectedChip = findViewById(checkedIds.get(0));
            if (selectedChip != null) {
                String chipText = selectedChip.getText().toString();
                switch (chipText) {
                    case "All":
                        currentFilter = "All";
                        break;
                    case "Veg":
                        currentFilter = "Veg";
                        break;
                    case "Non-Veg":
                        currentFilter = "NonVeg";
                        break;
                    case "Popular":
                        currentFilter = "Popular";
                        break;
                }
                filterFoodItems();
            }
        });
    }

    private void loadFoodItemsFromStorage() {
        foodItemsList.clear();

        String foodItemsJson = sharedPreferences.getString("food_items_list", "");

        if (!foodItemsJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<FoodItem>>() {}.getType();
            List<FoodItem> savedItems = gson.fromJson(foodItemsJson, type);
            if (savedItems != null) {
                foodItemsList.addAll(savedItems);
            }
        }

        filterFoodItems();
    }

    private void filterFoodItems() {
        List<FoodItem> filteredList = new ArrayList<>();

        for (FoodItem item : foodItemsList) {
            switch (currentFilter) {
                case "All":
                    filteredList.add(item);
                    break;
                case "Veg":
                    if (item.getType().equals("Veg")) {
                        filteredList.add(item);
                    }
                    break;
                case "NonVeg":
                    if (item.getType().equals("NonVeg")) {
                        filteredList.add(item);
                    }
                    break;
                case "Popular":
                    if (item.isPopular()) {
                        filteredList.add(item);
                    }
                    break;
            }
        }

        menuAdapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
            if (currentFilter.equals("All")) {
                tvEmptyMessage.setText("No food items added yet.\nTap + Add Item to get started");
            } else {
                tvEmptyMessage.setText("No " + currentFilter + " items found");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
        }
    }

    private void openAddFoodItem() {
        Intent intent = new Intent(this, AddFoodActivity.class);
        startActivity(intent);
    }

    private void openEditFoodItem(FoodItem item) {
        Intent intent = new Intent(this, AddFoodActivity.class);
        intent.putExtra("food_item_id", item.getId());
        startActivity(intent);
    }

    private void deleteFoodItem(FoodItem item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    foodItemsList.remove(item);
                    saveAllFoodItemsToStorage();
                    filterFoodItems();
                    Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAllFoodItemsToStorage() {
        Gson gson = new Gson();
        String json = gson.toJson(foodItemsList);
        sharedPreferences.edit().putString("food_items_list", json).apply();
    }

    private void setupClickListeners() {
        btnAddItemTop.setOnClickListener(v -> openAddFoodItem());
        btnAddItemBottom.setOnClickListener(v -> openAddFoodItem());
        btnAddFromEmpty.setOnClickListener(v -> openAddFoodItem());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFoodItemsFromStorage();
    }


    // FoodItem Model
    public static class FoodItem {
        private String id;
        private String name;
        private String description;
        private double price;
        private double offerPrice;
        private String type;
        private String category;
        private boolean isAvailable;
        private int ordersCount;
        private boolean isPopular;
        private boolean isBestSeller;
        private int prepTime;
        private String imagePath;

        public FoodItem() {
            // Default constructor for Gson
        }

        public FoodItem(String id, String name, String description, double price,
                        double offerPrice, String type, String category,
                        boolean isAvailable, int ordersCount, boolean isPopular,
                        boolean isBestSeller, int prepTime, String imagePath) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.offerPrice = offerPrice;
            this.type = type;
            this.category = category;
            this.isAvailable = isAvailable;
            this.ordersCount = ordersCount;
            this.isPopular = isPopular;
            this.isBestSeller = isBestSeller;
            this.prepTime = prepTime;
            this.imagePath = imagePath;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public double getOfferPrice() { return offerPrice; }
        public String getType() { return type; }
        public String getCategory() { return category; }
        public boolean isAvailable() { return isAvailable; }
        public int getOrdersCount() { return ordersCount; }
        public boolean isPopular() { return isPopular; }
        public boolean isBestSeller() { return isBestSeller; }
        public int getPrepTime() { return prepTime; }
        public String getImagePath() { return imagePath; }
        public String getDisplayPrice() {
            if (offerPrice > 0 && offerPrice < price) {
                return "₹" + (int) offerPrice;
            }
            return "₹" + (int) price;
        }

        // Setters
        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setDescription(String description) { this.description = description; }
        public void setPrice(double price) { this.price = price; }
        public void setOfferPrice(double offerPrice) { this.offerPrice = offerPrice; }
        public void setType(String type) { this.type = type; }
        public void setCategory(String category) { this.category = category; }
        public void setAvailable(boolean available) { isAvailable = available; }
        public void setOrdersCount(int ordersCount) { this.ordersCount = ordersCount; }
        public void setPopular(boolean popular) { isPopular = popular; }
        public void setBestSeller(boolean bestSeller) { isBestSeller = bestSeller; }
        public void setPrepTime(int prepTime) { this.prepTime = prepTime; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    }

    // RecyclerView Adapter - FULLY UPDATED with all badges
    public static class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

        private AppCompatActivity context;
        private List<FoodItem> items;
        private OnMenuItemActionListener listener;

        public interface OnMenuItemActionListener {
            void onEdit(FoodItem item);
            void onDelete(FoodItem item);
        }

        public MenuAdapter(AppCompatActivity context, List<FoodItem> items, OnMenuItemActionListener listener) {
            this.context = context;
            this.items = items;
            this.listener = listener;
        }

        public void updateList(List<FoodItem> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_food, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            FoodItem item = items.get(position);

            // ========== LOAD FOOD IMAGE ==========
            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                File imgFile = new File(item.getImagePath());
                if (imgFile.exists()) {
                    Glide.with(context)
                            .load(imgFile)
                            .placeholder(R.drawable.ic_food_placeholder)
                            .error(R.drawable.ic_food_placeholder)
                            .into(holder.ivFoodImage);
                } else {
                    holder.ivFoodImage.setImageResource(R.drawable.ic_food_placeholder);
                }
            } else {
                holder.ivFoodImage.setImageResource(R.drawable.ic_food_placeholder);
            }
            // ====================================

            holder.tvFoodName.setText(item.getName());

            // Category and Type text
            String categoryType = item.getCategory() + " · " + item.getType();
            holder.tvCategoryType.setText(categoryType);

            // Orders count
            holder.tvOrdersCount.setText(item.getOrdersCount() + " orders this month");

            // Price
            holder.tvPrice.setText(item.getDisplayPrice());

            // ========== POPULAR BADGE (Orange Background) ==========
            if (item.isPopular()) {
                holder.tvPopular.setVisibility(View.VISIBLE);
            } else {
                holder.tvPopular.setVisibility(View.GONE);
            }

            // ========== BEST SELLER BADGE (Green Background) ==========
            if (item.isBestSeller()) {
                holder.tvBestSeller.setVisibility(View.VISIBLE);
            } else {
                holder.tvBestSeller.setVisibility(View.GONE);
            }

            // ========== AVAILABILITY STATUS ==========
            if (item.isAvailable()) {
                holder.tvAvailability.setText("🟢 Available");
                holder.tvAvailability.setTextColor(context.getColor(R.color.success));
            } else {
                holder.tvAvailability.setText("🔴 Out of Stock");
                holder.tvAvailability.setTextColor(context.getColor(R.color.warning));
            }

            // Edit button
            holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));

            // Delete button
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFoodImage;
            TextView tvFoodName, tvCategoryType, tvOrdersCount, tvPrice, tvPopular, tvBestSeller, tvAvailability;
            ImageView btnEdit, btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
                tvFoodName = itemView.findViewById(R.id.tvFoodName);
                tvCategoryType = itemView.findViewById(R.id.tvCategoryType);
                tvOrdersCount = itemView.findViewById(R.id.tvOrdersCount);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvPopular = itemView.findViewById(R.id.tvPopular);
                tvBestSeller = itemView.findViewById(R.id.tvBestSeller);
                tvAvailability = itemView.findViewById(R.id.tvAvailability);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}