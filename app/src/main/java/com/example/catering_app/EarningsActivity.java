package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class EarningsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivBack;
    private TextView tvToolbarTitle;

    // Period Filter
    private ChipGroup chipGroupPeriod;
    private Chip chipToday, chipWeek, chipMonth, chipYear;
    private MaterialButton btnDownloadReport;

    // Total Earnings
    private TextView tvTotalEarnings, tvEarningsChange;

    // Stats Cards
    private TextView tvTotalOrders, tvAvgRating, tvTopItemsCount, tvTotalRevenue;
    private CardView cardOrders, cardRating, cardTopItems, cardRevenue;

    // Sales Breakdown
    private LinearLayout llSalesBreakdown;
    private MaterialCardView cardSalesBreakdown;

    // Weekly Chart
    private LinearLayout llChartBars;
    private TextView tvWeeklyTotal;
    private MaterialCardView cardWeeklyChart;

    // Payout Section
    private TextView tvNextPayout, tvPayoutDate, tvPaymentMethod;
    private MaterialButton btnRequestPayout;

    private SharedPreferences sharedPreferences;
    private String currentPeriod = "Week";
    private List<SalesItem> topSellingItems;
    private Animation bounceAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);

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
            Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show();
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

        sharedPreferences = getSharedPreferences("EARNINGS_DATA", MODE_PRIVATE);
        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        initViews();
        setupToolbar();
        setupPeriodFilter();
        loadEarningsData();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        chipGroupPeriod = findViewById(R.id.chipGroupPeriod);
        chipToday = findViewById(R.id.chipToday);
        chipWeek = findViewById(R.id.chipWeek);
        chipMonth = findViewById(R.id.chipMonth);
        chipYear = findViewById(R.id.chipYear);
        btnDownloadReport = findViewById(R.id.btnDownloadReport);

        tvTotalEarnings = findViewById(R.id.tvTotalEarnings);
        tvEarningsChange = findViewById(R.id.tvEarningsChange);

        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvAvgRating = findViewById(R.id.tvAvgRating);
        tvTopItemsCount = findViewById(R.id.tvTopItemsCount);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        cardOrders = findViewById(R.id.cardOrders);
        cardRating = findViewById(R.id.cardRating);
        cardTopItems = findViewById(R.id.cardTopItems);
        cardRevenue = findViewById(R.id.cardRevenue);

        llSalesBreakdown = findViewById(R.id.llSalesBreakdown);
        cardSalesBreakdown = findViewById(R.id.cardSalesBreakdown);

        llChartBars = findViewById(R.id.llChartBars);
        tvWeeklyTotal = findViewById(R.id.tvWeeklyTotal);
        cardWeeklyChart = findViewById(R.id.cardWeeklyChart);

        tvNextPayout = findViewById(R.id.tvNextPayout);
        tvPayoutDate = findViewById(R.id.tvPayoutDate);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        btnRequestPayout = findViewById(R.id.btnRequestPayout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        tvToolbarTitle.setText("Earnings & Reports");
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupPeriodFilter() {
        chipWeek.setChecked(true);

        chipGroupPeriod.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipToday)) {
                currentPeriod = "Today";
            } else if (checkedIds.contains(R.id.chipWeek)) {
                currentPeriod = "Week";
            } else if (checkedIds.contains(R.id.chipMonth)) {
                currentPeriod = "Month";
            } else if (checkedIds.contains(R.id.chipYear)) {
                currentPeriod = "Year";
            }
            loadEarningsData();
        });
    }

    private void loadEarningsData() {
        loadTotalEarnings();
        loadStatsCards();
        loadSalesBreakdown();
        loadMarketStyleGraph(); // ← CHANGED: Now using market-style graph
        loadPayoutInfo();
    }

    private void loadTotalEarnings() {
        double earnings = getEarningsForPeriod(currentPeriod);
        tvTotalEarnings.setText("₹" + formatNumber(earnings));

        double previousEarnings = getPreviousPeriodEarnings(currentPeriod);
        double change = ((earnings - previousEarnings) / previousEarnings) * 100;

        if (change > 0) {
            tvEarningsChange.setText("↑ +" + String.format("%.1f", change) + "% from last " + currentPeriod.toLowerCase());
            tvEarningsChange.setTextColor(getColor(R.color.success));
        } else if (change < 0) {
            tvEarningsChange.setText("↓ " + String.format("%.1f", change) + "% from last " + currentPeriod.toLowerCase());
            tvEarningsChange.setTextColor(getColor(R.color.warning));
        } else {
            tvEarningsChange.setText("No change from last " + currentPeriod.toLowerCase());
            tvEarningsChange.setTextColor(getColor(R.color.text_gray));
        }
    }

    private void loadStatsCards() {
        int totalOrders = getTotalOrdersForPeriod(currentPeriod);
        double avgRating = getAverageRating();
        int topItems = 12;
        double totalRevenue = getEarningsForPeriod(currentPeriod);

        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvAvgRating.setText(String.format("%.1f", avgRating));
        tvTopItemsCount.setText(String.valueOf(topItems));
        tvTotalRevenue.setText("₹" + formatNumber(totalRevenue));
    }

    private void loadSalesBreakdown() {
        llSalesBreakdown.removeAllViews();
        topSellingItems = getTopSellingItems();

        int maxAmount = 0;
        for (SalesItem item : topSellingItems) {
            if (item.getAmount() > maxAmount) {
                maxAmount = item.getAmount();
            }
        }

        for (SalesItem item : topSellingItems) {
            View itemView = getLayoutInflater().inflate(R.layout.item_sales_breakdown, null);

            TextView tvItemName = itemView.findViewById(R.id.tvItemName);
            TextView tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
            TextView tvPercentage = itemView.findViewById(R.id.tvPercentage);
            View progressBar = itemView.findViewById(R.id.progressBar);

            tvItemName.setText(item.getName());
            tvItemAmount.setText("₹" + formatNumber(item.getAmount()));

            int progressPercent = (int) ((double) item.getAmount() / maxAmount * 100);
            tvPercentage.setText(progressPercent + "%");

            int screenWidth = getResources().getDisplayMetrics().widthPixels - dpToPx(32);
            int progressWidth = (screenWidth * progressPercent) / 100;

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) progressBar.getLayoutParams();
            params.width = progressWidth;
            progressBar.setLayoutParams(params);

            final int finalProgressPercent = progressPercent;
            final String finalItemName = item.getName();
            final int finalAmount = item.getAmount();
            itemView.setOnClickListener(v -> {
                v.startAnimation(bounceAnimation);
                Toast.makeText(this, finalItemName + ": ₹" + formatNumber(finalAmount) + " (" + finalProgressPercent + "% of total)", Toast.LENGTH_SHORT).show();
            });

            llSalesBreakdown.addView(itemView);
        }
    }

    // ========== MARKET STYLE INTERACTIVE GRAPH ==========
    private void loadMarketStyleGraph() {
        int[] weeklyData = getWeeklyEarnings();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] fullDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int maxValue = getMaxValue(weeklyData);
        int minValue = getMinValue(weeklyData);

        llChartBars.removeAllViews();

        // Create main chart container
        LinearLayout chartContainer = new LinearLayout(this);
        chartContainer.setOrientation(LinearLayout.VERTICAL);
        chartContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Graph area
        FrameLayout graphArea = new FrameLayout(this);
        graphArea.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(220)
        ));

        // Create and add graph view
        View graphView = createGraphView(weeklyData, maxValue, minValue);
        graphArea.addView(graphView);

        // Add interactive dots
        addInteractiveDots(graphArea, weeklyData, days, fullDays, maxValue, minValue);

        chartContainer.addView(graphArea);

        // Add X-axis labels
        LinearLayout xAxisLabels = new LinearLayout(this);
        xAxisLabels.setOrientation(LinearLayout.HORIZONTAL);
        xAxisLabels.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        xAxisLabels.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), 0);

        for (String day : days) {
            TextView label = new TextView(this);
            label.setText(day);
            label.setTextSize(11);
            label.setTextColor(getColor(R.color.text_gray));
            label.setGravity(android.view.Gravity.CENTER);
            label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            xAxisLabels.addView(label);
        }
        chartContainer.addView(xAxisLabels);

        // Calculate total
        int total = 0;
        for (int val : weeklyData) total += val;
        tvWeeklyTotal.setText("₹" + formatNumber(total));

        llChartBars.addView(chartContainer);
    }

    private View createGraphView(int[] data, int maxValue, int minValue) {
        final int[] graphData = data;
        final int max = maxValue;
        final int min = minValue;

        View graphView = new View(this);
        graphView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        graphView.setBackground(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                int width = getBounds().width();
                int height = getBounds().height();
                int padding = dpToPx(40);
                int graphHeight = height - padding;
                int graphWidth = width - dpToPx(16);
                int stepX = graphWidth / (graphData.length - 1);

                Paint paint = new Paint();
                paint.setAntiAlias(true);

                // Draw horizontal grid lines
                paint.setColor(ContextCompat.getColor(EarningsActivity.this, R.color.divider));
                paint.setStrokeWidth(1);
                for (int i = 0; i <= 4; i++) {
                    int y = padding + (graphHeight * i / 4);
                    canvas.drawLine(dpToPx(8), y, width - dpToPx(8), y, paint);

                    // Y-axis labels
                    int value = max - ((max - min) * i / 4);
                    Paint textPaint = new Paint();
                    textPaint.setColor(ContextCompat.getColor(EarningsActivity.this, R.color.text_gray));
                    textPaint.setTextSize(dpToPx(10));
                    String label = "₹" + (value / 1000) + "K";
                    canvas.drawText(label, dpToPx(4), y - dpToPx(4), textPaint);
                }

                // Draw line graph
                Path path = new Path();
                paint.setColor(ContextCompat.getColor(EarningsActivity.this, R.color.primary));
                paint.setStrokeWidth(3);
                paint.setStyle(Paint.Style.STROKE);

                float[] points = new float[graphData.length];
                for (int i = 0; i < graphData.length; i++) {
                    float x = dpToPx(8) + (i * stepX);
                    float y = padding + graphHeight - ((graphData[i] - min) * graphHeight / (max - min));
                    points[i] = y;
                    if (i == 0) {
                        path.moveTo(x, y);
                    } else {
                        path.lineTo(x, y);
                    }
                }
                canvas.drawPath(path, paint);

                // Fill area under graph
                paint.setColor(ContextCompat.getColor(EarningsActivity.this, R.color.primary_light));
                paint.setAlpha(80);
                paint.setStyle(Paint.Style.FILL);
                Path fillPath = new Path();
                fillPath.moveTo(dpToPx(8), height - padding);
                for (int i = 0; i < graphData.length; i++) {
                    float x = dpToPx(8) + (i * stepX);
                    fillPath.lineTo(x, points[i]);
                }
                fillPath.lineTo(dpToPx(8) + ((graphData.length - 1) * stepX), height - padding);
                fillPath.close();
                canvas.drawPath(fillPath, paint);
            }

            @Override
            public void setAlpha(int alpha) {}
            @Override
            public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
            @Override
            public int getOpacity() { return android.graphics.PixelFormat.UNKNOWN; }
        });

        return graphView;
    }

    private void addInteractiveDots(FrameLayout container, int[] data, String[] shortDays,
                                    String[] fullDays, int maxValue, int minValue) {
        container.post(() -> {
            int width = container.getWidth();
            int height = container.getHeight();
            int padding = dpToPx(40);
            int graphHeight = height - padding;
            int graphWidth = width - dpToPx(16);
            int stepX = graphWidth / (data.length - 1);

            for (int i = 0; i < data.length; i++) {
                final int index = i;
                final int amount = data[i];

                float x = dpToPx(8) + (i * stepX);
                float y = padding + graphHeight - ((amount - minValue) * graphHeight / (maxValue - minValue));

                // Create dot
                View dot = new View(this);
                FrameLayout.LayoutParams dotParams = new FrameLayout.LayoutParams(dpToPx(14), dpToPx(14));
                dotParams.leftMargin = (int) x - dpToPx(7);
                dotParams.topMargin = (int) y - dpToPx(7);
                dot.setLayoutParams(dotParams);
                dot.setBackgroundResource(R.drawable.graph_dot);
                dot.setClickable(true);

                final String day = fullDays[i];
                final String shortDay = shortDays[i];

                dot.setOnClickListener(v -> {
                    showGraphTooltip(day, shortDay, amount);
                    v.startAnimation(bounceAnimation);
                });

                container.addView(dot);
            }
        });
    }

    private void showGraphTooltip(String fullDay, String shortDay, int amount) {
        View tooltipView = getLayoutInflater().inflate(R.layout.view_graph_tooltip, null);

        TextView tvDay = tooltipView.findViewById(R.id.tvTooltipDay);
        TextView tvEarnings = tooltipView.findViewById(R.id.tvTooltipEarnings);
        TextView tvOrders = tooltipView.findViewById(R.id.tvTooltipOrders);
        TextView tvRating = tooltipView.findViewById(R.id.tvTooltipRating);
        TextView tvInsight = tooltipView.findViewById(R.id.tvTooltipInsight);

        tvDay.setText("📍 " + fullDay);
        tvEarnings.setText("💰 Earnings: ₹" + formatNumber(amount));

        int orders = amount / 250;
        tvOrders.setText("📦 Orders: " + orders);

        double rating = 4.5 + (Math.random() * 0.5);
        tvRating.setText("⭐ Rating: " + String.format("%.1f", rating));

        String insight;
        if (amount > 20000) {
            insight = "🚀 Best day of the week!";
        } else if (amount < 15000) {
            insight = "📉 Could improve with promotions";
        } else {
            insight = "👍 Steady performance";
        }
        tvInsight.setText(insight);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(tooltipView)
                .setPositiveButton("OK", null)
                .show();
    }
    // ==========================================================

    private void loadPayoutInfo() {
        double nextPayout = 8500;
        String payoutDate = "15th May 2024";
        String paymentMethod = "Bank Transfer (XXXXXXXX1234)";

        tvNextPayout.setText("₹" + formatNumber(nextPayout));
        tvPayoutDate.setText(payoutDate);
        tvPaymentMethod.setText(paymentMethod);
    }

    private void setupClickListeners() {
        btnDownloadReport.setOnClickListener(v -> downloadReport());
        btnRequestPayout.setOnClickListener(v -> requestPayout());

        cardOrders.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Total Orders: " + tvTotalOrders.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        cardRating.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Average Rating: " + tvAvgRating.getText().toString() + " ⭐", Toast.LENGTH_SHORT).show();
        });

        cardTopItems.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Top 12 selling items this period", Toast.LENGTH_SHORT).show();
        });

        cardRevenue.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Total Revenue: " + tvTotalRevenue.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        cardSalesBreakdown.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            Toast.makeText(this, "Sales breakdown by item", Toast.LENGTH_SHORT).show();
        });

        cardWeeklyChart.setOnClickListener(v -> {
            v.startAnimation(bounceAnimation);
            int[] weeklyData = getWeeklyEarnings();
            int total = 0;
            for (int val : weeklyData) total += val;
            Toast.makeText(this, "Weekly Total: ₹" + formatNumber(total), Toast.LENGTH_SHORT).show();
        });
    }

    private void downloadReport() {
        Toast.makeText(this, "Downloading " + currentPeriod + " report...", Toast.LENGTH_SHORT).show();
    }

    private void requestPayout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Request Payout")
                .setMessage("Are you sure you want to request payout of " + tvNextPayout.getText() + "?")
                .setPositiveButton("Yes, Request", (dialog, which) -> {
                    Toast.makeText(this, "Payout request submitted successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private double getEarningsForPeriod(String period) {
        switch (period) {
            case "Today": return 8500;
            case "Week": return 45280;
            case "Month": return 185000;
            case "Year": return 1250000;
            default: return 45280;
        }
    }

    private double getPreviousPeriodEarnings(String period) {
        switch (period) {
            case "Today": return 7200;
            case "Week": return 40500;
            case "Month": return 168000;
            case "Year": return 1100000;
            default: return 40500;
        }
    }

    private int getTotalOrdersForPeriod(String period) {
        switch (period) {
            case "Today": return 12;
            case "Week": return 342;
            case "Month": return 1250;
            case "Year": return 8500;
            default: return 342;
        }
    }

    private double getAverageRating() {
        return 4.8;
    }

    private List<SalesItem> getTopSellingItems() {
        List<SalesItem> items = new ArrayList<>();
        items.add(new SalesItem("Butter Chicken", 12000));
        items.add(new SalesItem("Chicken Biryani", 8500));
        items.add(new SalesItem("Paneer Butter Masala", 7200));
        items.add(new SalesItem("Garlic Naan", 4500));
        items.add(new SalesItem("Gulab Jamun", 3200));
        items.add(new SalesItem("Masala Dosa", 2800));
        items.add(new SalesItem("Mango Lassi", 2100));
        items.add(new SalesItem("Tandoori Chicken", 1900));
        return items;
    }

    private int[] getWeeklyEarnings() {
        return new int[]{12500, 14800, 13200, 16500, 19200, 22800, 17500};
    }

    private int getMaxValue(int[] array) {
        int max = array[0];
        for (int val : array) {
            if (val > max) max = val;
        }
        return max;
    }

    private int getMinValue(int[] array) {
        int min = array[0];
        for (int val : array) {
            if (val < min) min = val;
        }
        return min;
    }

    private String formatNumber(double number) {
        if (number >= 100000) {
            return (number / 100000) + "L";
        } else if (number >= 1000) {
            return (number / 1000) + "K";
        }
        return String.valueOf((int) number);
    }

    private String formatPrice(int price) {
        if (price >= 1000) {
            return "₹" + (price / 1000) + "K";
        }
        return "₹" + price;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public static class SalesItem {
        private String name;
        private int amount;

        public SalesItem(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() { return name; }
        public int getAmount() { return amount; }
    }
}