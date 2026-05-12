package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddFoodActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ImageView ivBack;

    private MaterialCardView cardImageUpload;
    private ImageView ivFoodImage;
    private TextView tvUploadText;

    private TextInputLayout tilFoodName, tilDescription, tilPrice, tilOfferPrice, tilPrepTime;
    private TextInputEditText etFoodName, etDescription, etPrice, etOfferPrice, etPrepTime;

    private Spinner spinnerCategory;
    private String selectedCategory = "Main Course";

    private ChipGroup chipGroupFoodType;
    private Chip chipVeg, chipNonVeg;
    private String selectedFoodType = "Veg";  // Changed default to Veg

    private SwitchMaterial switchAvailable, switchPopular, switchBestSeller;

    private MaterialButton btnSave, btnDelete;
    private LinearLayout llDeleteButton;

    private SharedPreferences sharedPreferences;
    private boolean isEditMode = false;
    private String foodItemId = null;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        selectedImageUri = imageUri;
                        ivFoodImage.setImageURI(imageUri);
                        ivFoodImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        tvUploadText.setVisibility(View.GONE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        sharedPreferences = getSharedPreferences("FOOD_DATA", MODE_PRIVATE);

        foodItemId = getIntent().getStringExtra("food_item_id");
        isEditMode = (foodItemId != null && !foodItemId.isEmpty());

        initViews();
        setupToolbar();
        setupImageUpload();
        setupSpinner();
        setupFoodTypeChips();

        if (isEditMode) {
            loadFoodItemData();
        }

        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        ivBack = findViewById(R.id.ivBack);
        cardImageUpload = findViewById(R.id.cardImageUpload);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        tvUploadText = findViewById(R.id.tvUploadText);

        tilFoodName = findViewById(R.id.tilFoodName);
        etFoodName = findViewById(R.id.etFoodName);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
        tilPrice = findViewById(R.id.tilPrice);
        etPrice = findViewById(R.id.etPrice);
        tilOfferPrice = findViewById(R.id.tilOfferPrice);
        etOfferPrice = findViewById(R.id.etOfferPrice);
        tilPrepTime = findViewById(R.id.tilPrepTime);
        etPrepTime = findViewById(R.id.etPrepTime);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        chipGroupFoodType = findViewById(R.id.chipGroupFoodType);
        chipVeg = findViewById(R.id.chipVeg);
        chipNonVeg = findViewById(R.id.chipNonVeg);

        switchAvailable = findViewById(R.id.switchAvailable);
        switchPopular = findViewById(R.id.switchPopular);
        switchBestSeller = findViewById(R.id.switchBestSeller);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        llDeleteButton = findViewById(R.id.llDeleteButton);

        if (isEditMode) {
            tvToolbarTitle.setText("Edit Food Item");
            llDeleteButton.setVisibility(View.VISIBLE);
        } else {
            tvToolbarTitle.setText("Add New Food Item");
            llDeleteButton.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupImageUpload() {
        cardImageUpload.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void setupSpinner() {
        String[] categories = {"Main Course", "Appetizer", "Dessert", "Beverage", "Bread", "Rice", "Soup", "Salad"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Main Course";
            }
        });
    }

    private void setupFoodTypeChips() {
        chipGroupFoodType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipVeg)) {
                selectedFoodType = "Veg";
            } else if (checkedIds.contains(R.id.chipNonVeg)) {
                selectedFoodType = "NonVeg";
            }
        });
        chipVeg.setChecked(true);  // Changed default to Veg
    }

    private void loadFoodItemData() {
        List<FoodItem> allItems = getAllFoodItems();
        for (FoodItem item : allItems) {
            if (item.getId().equals(foodItemId)) {
                etFoodName.setText(item.getName());
                etDescription.setText(item.getDescription());
                etPrice.setText(String.valueOf((int) item.getPrice()));
                if (item.getOfferPrice() > 0) {
                    etOfferPrice.setText(String.valueOf((int) item.getOfferPrice()));
                }
                etPrepTime.setText(String.valueOf(item.getPrepTime()));
                selectedCategory = item.getCategory();
                selectedFoodType = item.getType();
                switchAvailable.setChecked(item.isAvailable());
                switchPopular.setChecked(item.isPopular());
                switchBestSeller.setChecked(item.isBestSeller());

                String[] categories = {"Main Course", "Appetizer", "Dessert", "Beverage", "Bread", "Rice", "Soup", "Salad"};
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(selectedCategory)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }

                if (selectedFoodType.equals("Veg")) {
                    chipVeg.setChecked(true);
                } else {
                    chipNonVeg.setChecked(true);
                }
                break;
            }
        }
    }

    private List<FoodItem> getAllFoodItems() {
        String foodItemsJson = sharedPreferences.getString("food_items_list", "");
        if (foodItemsJson.isEmpty()) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FoodItem>>() {}.getType();
        List<FoodItem> items = gson.fromJson(foodItemsJson, type);
        return items != null ? items : new ArrayList<>();
    }

    private void saveAllFoodItems(List<FoodItem> items) {
        Gson gson = new Gson();
        String json = gson.toJson(items);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("food_items_list", json);
        boolean saved = editor.commit();  // Use commit to check if saved
        if (saved) {
            Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save data!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveFoodItem());
        btnDelete.setOnClickListener(v -> deleteFoodItem());
    }

    private void saveFoodItem() {
        String foodName = etFoodName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String offerPriceStr = etOfferPrice.getText().toString().trim();
        String prepTimeStr = etPrepTime.getText().toString().trim();

        if (foodName.isEmpty()) {
            tilFoodName.setError("Food name is required");
            etFoodName.requestFocus();
            return;
        }
        if (priceStr.isEmpty()) {
            tilPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        double price = Double.parseDouble(priceStr);
        double offerPrice = offerPriceStr.isEmpty() ? 0 : Double.parseDouble(offerPriceStr);
        int prepTime = prepTimeStr.isEmpty() ? 30 : Integer.parseInt(prepTimeStr);

        // ========== FIX: SAVE IMAGE FIRST ==========
        String imagePath = "";
        if (selectedImageUri != null) {
            imagePath = saveImageToStorage();
            if (!imagePath.isEmpty()) {
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
            }
        }
        // ==========================================

        List<FoodItem> allItems = getAllFoodItems();

        if (isEditMode) {
            // Update existing item
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i).getId().equals(foodItemId)) {
                    allItems.get(i).setName(foodName);
                    allItems.get(i).setDescription(description);
                    allItems.get(i).setPrice(price);
                    allItems.get(i).setOfferPrice(offerPrice);
                    allItems.get(i).setType(selectedFoodType);
                    allItems.get(i).setCategory(selectedCategory);
                    allItems.get(i).setAvailable(switchAvailable.isChecked());
                    allItems.get(i).setPopular(switchPopular.isChecked());
                    allItems.get(i).setBestSeller(switchBestSeller.isChecked());
                    allItems.get(i).setPrepTime(prepTime);
                    if (!imagePath.isEmpty()) {
                        allItems.get(i).setImagePath(imagePath);
                    }
                    break;
                }
            }
            Toast.makeText(this, "Food item updated!", Toast.LENGTH_SHORT).show();
        } else {
            // Add new item - NOW WITH IMAGE PATH
            String newId = UUID.randomUUID().toString();
            FoodItem newItem = new FoodItem(
                    newId, foodName, description, price, offerPrice, selectedFoodType,
                    selectedCategory, switchAvailable.isChecked(), 0,
                    switchPopular.isChecked(), switchBestSeller.isChecked(), prepTime, imagePath  // ← FIXED: Pass imagePath
            );
            allItems.add(newItem);
            Toast.makeText(this, "Food item added with image!", Toast.LENGTH_LONG).show();
        }

        saveAllFoodItems(allItems);

        // Wait a moment then go back
        new android.os.Handler().postDelayed(() -> {
            finish();
        }, 500);
    }

    // ADD THIS METHOD TO SAVE IMAGE (if not already present)
    private String saveImageToStorage() {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);

            String fileName = "food_" + System.currentTimeMillis() + ".jpg";
            java.io.File directory = getFilesDir();
            java.io.File file = new java.io.File(directory, fileName);

            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void deleteFoodItem() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this food item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    List<FoodItem> allItems = getAllFoodItems();
                    allItems.removeIf(item -> item.getId().equals(foodItemId));
                    saveAllFoodItems(allItems);
                    Toast.makeText(this, "Food item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // FoodItem Model Class
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
}