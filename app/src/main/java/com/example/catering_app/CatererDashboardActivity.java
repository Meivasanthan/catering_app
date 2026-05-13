package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CatererDashboardActivity extends AppCompatActivity {

    // Header Views
    private TextView tvGreeting, tvBusinessName;
    private ImageView ivNotification, ivProfile;

    // Stats Cards
    private TextView tvTodayOrders, tvTodaySales, tvTodayRating;
    private CardView cardOrders, cardSales, cardRating;

    // New Orders Section
    private TextView tvOrderId, tvCustomerName, tvItemCount, tvTotalAmount;
    private MaterialButton btnAccept, btnDecline;
    private TextView tvSeeAll;

    // Low Stock Alert
    private CardView llLowStockAlert;
    private TextView tvLowStockItems;

    // Weekly Chart
    private LinearLayout llChartBars;
    private TextView tvReports;

    // Quick Actions
    private MaterialButton btnAddItem, btnAllOrders;

    // Bottom Navigation
    private LinearLayout btnNavDashboard, btnNavMenu, btnNavOrders, btnNavEarnings, btnNavProfile;
    private ImageView ivNavDashboard, ivNavMenu, ivNavOrders, ivNavEarnings, ivNavProfile;
    private TextView tvNavDashboard, tvNavMenu, tvNavOrders, tvNavEarnings, tvNavProfile;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caterer_dashboard);

        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        initViews();
        setupGreeting();
        loadRealData();
        setupClickListeners();
        loadWeeklyChart();
    }

    private void initViews() {
        // Header
        tvGreeting = findViewById(R.id.tvGreeting);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        ivNotification = findViewById(R.id.ivNotification);
        ivProfile = findViewById(R.id.ivProfile);

        // Stats Cards
        tvTodayOrders = findViewById(R.id.tvTodayOrders);
        tvTodaySales = findViewById(R.id.tvTodaySales);
        tvTodayRating = findViewById(R.id.tvTodayRating);
        cardOrders = findViewById(R.id.cardOrders);
        cardSales = findViewById(R.id.cardSales);
        cardRating = findViewById(R.id.cardRating);

        // New Orders Section - Individual views (not RecyclerView)
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvItemCount = findViewById(R.id.tvItemCount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);
        tvSeeAll = findViewById(R.id.tvSeeAll);

        // Low Stock Alert
        llLowStockAlert = findViewById(R.id.llLowStockAlert);
        tvLowStockItems = findViewById(R.id.tvLowStockItems);

        // Chart
        llChartBars = findViewById(R.id.llChartBars);
        tvReports = findViewById(R.id.tvReports);

        // Quick Actions
        btnAddItem = findViewById(R.id.btnAddItem);
        btnAllOrders = findViewById(R.id.btnAllOrders);

        // Bottom Navigation
        btnNavDashboard = findViewById(R.id.btnNavDashboard);
        btnNavMenu = findViewById(R.id.btnNavMenu);
        btnNavOrders = findViewById(R.id.btnNavOrders);
        btnNavEarnings = findViewById(R.id.btnNavEarnings);
        btnNavProfile = findViewById(R.id.btnNavProfile);

        ivNavDashboard = findViewById(R.id.ivNavDashboard);
        ivNavMenu = findViewById(R.id.ivNavMenu);
        ivNavOrders = findViewById(R.id.ivNavOrders);
        ivNavEarnings = findViewById(R.id.ivNavEarnings);
        ivNavProfile = findViewById(R.id.ivNavProfile);

        tvNavDashboard = findViewById(R.id.tvNavDashboard);
        tvNavMenu = findViewById(R.id.tvNavMenu);
        tvNavOrders = findViewById(R.id.tvNavOrders);
        tvNavEarnings = findViewById(R.id.tvNavEarnings);
        tvNavProfile = findViewById(R.id.tvNavProfile);

        // Set business name
        String businessName = sharedPreferences.getString("business_name", "Thambikalayan Caters");
        tvBusinessName.setText(businessName);
    }

    private void setupGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
        } else {
            greeting = "Good Evening";
        }

        String ownerName = sharedPreferences.getString("current_user_name", "Owner");
        tvGreeting.setText(greeting + ",");
    }

    private void loadRealData() {
        // Load data from SharedPreferences
        int todayOrders = sharedPreferences.getInt("today_orders", 12);
        float todaySales = sharedPreferences.getFloat("today_sales", 8500f);
        float avgRating = sharedPreferences.getFloat("avg_rating", 4.8f);

        tvTodayOrders.setText(String.valueOf(todayOrders));
        tvTodaySales.setText("₹" + formatNumber(todaySales));
        tvTodayRating.setText(String.format(Locale.US, "%.1f", avgRating));

        // Load low stock items
        loadLowStockAlert();
    }

    private String formatNumber(float number) {
        if (number >= 1000) {
            return (number / 1000) + "K";
        }
        return String.valueOf((int) number);
    }

    private void loadLowStockAlert() {
        List<String> lowStockItems = getLowStockItems();
        if (!lowStockItems.isEmpty()) {
            llLowStockAlert.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lowStockItems.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(lowStockItems.get(i));
            }
            tvLowStockItems.setText(" Low Stock Alert: " + sb.toString());
        } else {
            llLowStockAlert.setVisibility(View.GONE);
        }
    }

    private List<String> getLowStockItems() {
        List<String> items = new ArrayList<>();
        String lowStock = sharedPreferences.getString("low_stock_items", "");
        if (!lowStock.isEmpty()) {
            String[] parts = lowStock.split(",");
            for (String part : parts) {
                items.add(part.trim());
            }
        } else {
            // Sample data for demo
            items.add("Biryani (only 3 left)");
            items.add("Paneer Tikka (2 left)");
        }
        return items;
    }

    private void loadWeeklyChart() {
        // Get REAL weekly earnings data from SharedPreferences
        int[] weeklyData = getRealWeeklyData();

        int maxValue = getMaxValue(weeklyData);
        if (maxValue == 0) maxValue = 10000; // Default if no data

        llChartBars.removeAllViews();

        // Get colors
        int barColor = getColor(R.color.primary);
        int lowBarColor = getColor(R.color.primary_light);

        for (int i = 0; i < weeklyData.length; i++) {
            LinearLayout barContainer = new LinearLayout(this);
            barContainer.setOrientation(LinearLayout.VERTICAL);
            barContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            barContainer.setGravity(android.view.Gravity.BOTTOM);

            // Calculate bar height based on REAL value
            int height = (int) ((weeklyData[i] * 150.0) / maxValue);
            if (height < 15) height = 15;

            View bar = new View(this);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(28), height);
            barParams.bottomMargin = dpToPx(8);
            bar.setLayoutParams(barParams);

            // Different color for highest value
            if (weeklyData[i] == getMaxValue(weeklyData) && weeklyData[i] > 0) {
                bar.setBackgroundColor(barColor);
            } else {
                bar.setBackgroundColor(lowBarColor);
            }

            // Animation
            bar.setScaleY(0);
            bar.animate().scaleY(1).setDuration(500).setStartDelay(i * 100);

            // Add value text on top of bar (optional)
            TextView valueText = new TextView(this);
            valueText.setText(formatPrice(weeklyData[i]));
            valueText.setTextSize(9);
            valueText.setTextColor(getColor(R.color.text_gray));
            valueText.setGravity(android.view.Gravity.CENTER);

            barContainer.addView(bar);
            barContainer.addView(valueText);
            llChartBars.addView(barContainer);
        }
    }

    // Get REAL weekly data from SharedPreferences
    private int[] getRealWeeklyData() {
        int[] weeklyData = new int[7];

        // Get saved earnings for each day
        // Day order: Monday=0, Tuesday=1, ... Sunday=6
        weeklyData[0] = sharedPreferences.getInt("earnings_mon", 0);
        weeklyData[1] = sharedPreferences.getInt("earnings_tue", 0);
        weeklyData[2] = sharedPreferences.getInt("earnings_wed", 0);
        weeklyData[3] = sharedPreferences.getInt("earnings_thu", 0);
        weeklyData[4] = sharedPreferences.getInt("earnings_fri", 0);
        weeklyData[5] = sharedPreferences.getInt("earnings_sat", 0);
        weeklyData[6] = sharedPreferences.getInt("earnings_sun", 0);

        // If all are zero, load sample data for demo
        boolean allZero = true;
        for (int val : weeklyData) {
            if (val > 0) {
                allZero = false;
                break;
            }
        }

        if (allZero) {
            // Sample data for first time users
            weeklyData[0] = 1200;  // Monday
            weeklyData[1] = 3400;  // Tuesday
            weeklyData[2] = 2800;  // Wednesday
            weeklyData[3] = 4500;  // Thursday
            weeklyData[4] = 6200;  // Friday
            weeklyData[5] = 8900;  // Saturday
            weeklyData[6] = 5400;  // Sunday
        }

        return weeklyData;
    }

    // Get maximum value from array
    private int getMaxValue(int[] array) {
        int max = 0;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    // Format price for display
    private String formatPrice(int price) {
        if (price >= 1000) {
            return "₹" + (price / 1000) + "K";
        }
        return "₹" + price;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setupClickListeners() {
        // Header clicks
        ivNotification.setOnClickListener(v ->
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());
        ivProfile.setOnClickListener(v ->
                navigateToProfile());

        // Stats card clicks
        cardOrders.setOnClickListener(v ->
                navigateToOrders());
        cardSales.setOnClickListener(v ->
                navigateToEarnings());  // Changed to open Earnings page
        cardRating.setOnClickListener(v ->
                Toast.makeText(this, "Rating Details", Toast.LENGTH_SHORT).show());

        // New Orders buttons
        btnAccept.setOnClickListener(v -> {
            Toast.makeText(this, "Order Accepted", Toast.LENGTH_SHORT).show();
            // Hide the order card after accept
            findViewById(R.id.llLowStockAlert).requestLayout();
        });

        btnDecline.setOnClickListener(v -> {
            Toast.makeText(this, "Order Declined", Toast.LENGTH_SHORT).show();
        });

        tvSeeAll.setOnClickListener(v ->
                navigateToOrders());

        // Reports link
        tvReports.setOnClickListener(v ->
                navigateToEarnings());  // Changed to open Earnings page

        // Quick Actions
        btnAddItem.setOnClickListener(v ->
                navigateToMenuManagement());
        btnAllOrders.setOnClickListener(v ->
                navigateToOrders());

        // Bottom Navigation
        btnNavDashboard.setOnClickListener(v -> {
            setActiveNav(0);
            // Already on dashboard
        });

        btnNavMenu.setOnClickListener(v -> {
            setActiveNav(1);
            navigateToMenuManagement();
        });

        btnNavOrders.setOnClickListener(v -> {
            setActiveNav(2);
            navigateToOrders();
        });

        btnNavEarnings.setOnClickListener(v -> {
            setActiveNav(3);
            navigateToEarnings();  // Added navigation to Earnings page
        });

        btnNavProfile.setOnClickListener(v -> {
            setActiveNav(4);
            navigateToProfile();
        });
    }

    // ========== ADD THIS MISSING METHOD ==========
    private void navigateToEarnings() {
        Intent intent = new Intent(this, EarningsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    // ============================================

    private void setActiveNav(int position) {
        // Reset all to gray
        resetNavIcons();

        int primaryColor = getColor(R.color.primary);
        int grayColor = getColor(R.color.text_gray);

        switch (position) {
            case 0:
                ivNavDashboard.setColorFilter(primaryColor);
                tvNavDashboard.setTextColor(primaryColor);
                break;
            case 1:
                ivNavMenu.setColorFilter(primaryColor);
                tvNavMenu.setTextColor(primaryColor);
                break;
            case 2:
                ivNavOrders.setColorFilter(primaryColor);
                tvNavOrders.setTextColor(primaryColor);
                break;
            case 3:
                ivNavEarnings.setColorFilter(primaryColor);
                tvNavEarnings.setTextColor(primaryColor);
                break;
            case 4:
                ivNavProfile.setColorFilter(primaryColor);
                tvNavProfile.setTextColor(primaryColor);
                break;
        }
    }

    private void resetNavIcons() {
        int gray = getColor(R.color.text_gray);

        ivNavDashboard.setColorFilter(gray);
        ivNavMenu.setColorFilter(gray);
        ivNavOrders.setColorFilter(gray);
        ivNavEarnings.setColorFilter(gray);
        ivNavProfile.setColorFilter(gray);

        tvNavDashboard.setTextColor(gray);
        tvNavMenu.setTextColor(gray);
        tvNavOrders.setTextColor(gray);
        tvNavEarnings.setTextColor(gray);
        tvNavProfile.setTextColor(gray);
    }

    private void navigateToMenuManagement() {
        Intent intent = new Intent(this, MenuManagementActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToOrders() {
        Intent intent = new Intent(this, OwnerOrdersActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, CatererProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}