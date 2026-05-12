package com.example.catering_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivBack;
    private TextView tvToolbarTitle;

    // Order Header
    private TextView tvOrderId, tvOrderDate, tvOrderStatus;

    // Customer Details
    private TextView tvCustomerName, tvCustomerPhone, tvCustomerEmail, tvDeliveryAddress;

    // Order Items
    private TextView tvOrderItems;

    // Order Summary
    private TextView tvSubtotal, tvDeliveryFee, tvPackingCharge, tvTax, tvTotalAmount;

    // Status Selection Chips
    private ChipGroup chipGroupStatus;
    private Chip chipConfirmed, chipPreparing, chipOutForDelivery, chipDelivered, chipCancelled;

    // Timeline Views
    private FrameLayout step1Circle, step2Circle, step3Circle, step4Circle, step5Circle;
    private TextView step1Icon, step2Icon, step3Icon, step4Icon, step5Icon;
    private TextView step1Text, step2Text, step3Text, step4Text, step5Text;
    private TextView step1Time, step2Time, step3Time, step4Time, step5Time;

    // Action Buttons
    private MaterialButton btnUpdateStatus, btnCallCustomer, btnCancelOrder;
    private CardView cardCancelWarning;

    private SharedPreferences sharedPreferences;
    private String orderId;
    private Order currentOrder;
    private String currentStatus;
    private String selectedStatus = "";
    private Animation shakeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        sharedPreferences = getSharedPreferences("ORDER_DATA", MODE_PRIVATE);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        orderId = getIntent().getStringExtra("order_id");

        initViews();
        setupToolbar();
        loadOrderDetails();
        setupClickListeners();
        setupStatusChips();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        // Order Header
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);

        // Customer Details
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerEmail = findViewById(R.id.tvCustomerEmail);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);

        // Order Items
        tvOrderItems = findViewById(R.id.tvOrderItems);

        // Order Summary
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvPackingCharge = findViewById(R.id.tvPackingCharge);
        tvTax = findViewById(R.id.tvTax);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        // Status Chips
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        chipConfirmed = findViewById(R.id.chipConfirmed);
        chipPreparing = findViewById(R.id.chipPreparing);
        chipOutForDelivery = findViewById(R.id.chipOutForDelivery);
        chipDelivered = findViewById(R.id.chipDelivered);
        chipCancelled = findViewById(R.id.chipCancelled);

        // Timeline Views
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step4Circle = findViewById(R.id.step4Circle);
        step5Circle = findViewById(R.id.step5Circle);
        step1Icon = findViewById(R.id.step1Icon);
        step2Icon = findViewById(R.id.step2Icon);
        step3Icon = findViewById(R.id.step3Icon);
        step4Icon = findViewById(R.id.step4Icon);
        step5Icon = findViewById(R.id.step5Icon);
        step1Text = findViewById(R.id.step1Text);
        step2Text = findViewById(R.id.step2Text);
        step3Text = findViewById(R.id.step3Text);
        step4Text = findViewById(R.id.step4Text);
        step5Text = findViewById(R.id.step5Text);
        step1Time = findViewById(R.id.step1Time);
        step2Time = findViewById(R.id.step2Time);
        step3Time = findViewById(R.id.step3Time);
        step4Time = findViewById(R.id.step4Time);
        step5Time = findViewById(R.id.step5Time);

        // Buttons
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        btnCallCustomer = findViewById(R.id.btnCallCustomer);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        cardCancelWarning = findViewById(R.id.cardCancelWarning);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        tvToolbarTitle.setText("Order Details");
        ivBack.setOnClickListener(v -> finish());
    }

    private void loadOrderDetails() {
        String ordersJson = sharedPreferences.getString("orders_list", "");
        if (!ordersJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Order>>() {}.getType();
            List<Order> ordersList = gson.fromJson(ordersJson, type);

            if (ordersList != null) {
                for (Order order : ordersList) {
                    if (order.getId().equals(orderId)) {
                        currentOrder = order;
                        currentStatus = order.getStatus();
                        break;
                    }
                }
            }
        }

        if (currentOrder != null) {
            displayOrderDetails();
            updateUIForCurrentStatus();
            updateTimeline();
        } else {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayOrderDetails() {
        // Order Header
        tvOrderId.setText("Order #" + currentOrder.getId());
        tvOrderDate.setText(currentOrder.getOrderTime());
        setStatusBadge(tvOrderStatus, currentOrder.getStatus());

        // Customer Details
        tvCustomerName.setText(currentOrder.getCustomerName());
        tvCustomerPhone.setText(currentOrder.getCustomerPhone());
        tvCustomerEmail.setText(currentOrder.getCustomerEmail());
        tvDeliveryAddress.setText(currentOrder.getDeliveryAddress());

        // Order Items
        tvOrderItems.setText(currentOrder.getItemsList());

        // Order Summary
        double total = currentOrder.getTotalAmount();
        double deliveryFee = 100;
        double packingCharge = 50;
        double subtotal = total - deliveryFee - packingCharge;
        double tax = subtotal * 0.05;

        tvSubtotal.setText("₹" + (int) subtotal);
        tvDeliveryFee.setText("₹" + (int) deliveryFee);
        tvPackingCharge.setText("₹" + (int) packingCharge);
        tvTax.setText("₹" + (int) tax);
        tvTotalAmount.setText("₹" + (int) total);
    }

    private void setStatusBadge(TextView tvStatus, String status) {
        switch (status) {
            case "Confirmed":
                tvStatus.setText("✅ Confirmed");
                tvStatus.setBackgroundColor(getColor(R.color.status_confirmed_bg));
                tvStatus.setTextColor(getColor(R.color.status_confirmed_text));
                break;
            case "Preparing":
                tvStatus.setText("⏳ Preparing");
                tvStatus.setBackgroundColor(getColor(R.color.status_preparing_bg));
                tvStatus.setTextColor(getColor(R.color.status_preparing_text));
                break;
            case "Out for Delivery":
                tvStatus.setText("🚚 Out for Delivery");
                tvStatus.setBackgroundColor(getColor(R.color.status_out_bg));
                tvStatus.setTextColor(getColor(R.color.status_out_text));
                break;
            case "Delivered":
                tvStatus.setText("🎉 Delivered");
                tvStatus.setBackgroundColor(getColor(R.color.status_delivered_bg));
                tvStatus.setTextColor(getColor(R.color.status_delivered_text));
                break;
            case "Cancelled":
                tvStatus.setText("❌ Cancelled");
                tvStatus.setBackgroundColor(getColor(R.color.status_cancelled_bg));
                tvStatus.setTextColor(getColor(R.color.status_cancelled_text));
                break;
        }
    }

    private void updateUIForCurrentStatus() {
        // Clear all selections
        chipGroupStatus.clearCheck();

        // Highlight current status in chips
        switch (currentStatus) {
            case "Confirmed":
                chipConfirmed.setChecked(true);
                break;
            case "Preparing":
                chipPreparing.setChecked(true);
                break;
            case "Out for Delivery":
                chipOutForDelivery.setChecked(true);
                break;
            case "Delivered":
                chipDelivered.setChecked(true);
                btnUpdateStatus.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.GONE);
                cardCancelWarning.setVisibility(View.GONE);
                break;
            case "Cancelled":
                chipCancelled.setChecked(true);
                btnUpdateStatus.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.GONE);
                cardCancelWarning.setVisibility(View.GONE);
                break;
        }

        // Cancel button always visible for non-final stages
        if (!currentStatus.equals("Delivered") && !currentStatus.equals("Cancelled")) {
            btnCancelOrder.setVisibility(View.VISIBLE);
            cardCancelWarning.setVisibility(View.VISIBLE);
        }
    }

    // ========== CORRECTED updateTimeline() METHOD - NO CRASH ==========
    private void updateTimeline() {
        int currentStep = 0;
        boolean isCancelled = false;

        switch (currentStatus) {
            case "Confirmed":
                currentStep = 1;
                break;
            case "Preparing":
                currentStep = 2;
                break;
            case "Out for Delivery":
                currentStep = 3;
                break;
            case "Delivered":
                currentStep = 4;
                break;
            case "Cancelled":
                isCancelled = true;
                break;
        }

        if (isCancelled) {
            // Show cancelled state
            step5Circle.setBackgroundResource(R.drawable.step_circle_bg_cancelled);
            step5Icon.setTextColor(getColor(R.color.warning));
            step5Text.setTextColor(getColor(R.color.warning));

            // Set timestamps
            step1Time.setText("10:30 AM");
            step2Time.setText("11:00 AM");
            step3Time.setText("12:00 PM");
            step4Time.setText("01:00 PM");
            step5Time.setText("Cancelled");
        } else {
            // Update step colors and timestamps
            updateStep(1, currentStep >= 1);
            updateStep(2, currentStep >= 2);
            updateStep(3, currentStep >= 3);
            updateStep(4, currentStep >= 4);
            updateStep(5, false);

            // Set timestamps
            step1Time.setText("10:30 AM");
            step2Time.setText("11:00 AM");
            step3Time.setText("12:00 PM");
            step4Time.setText("01:00 PM");
            step5Time.setText("");
        }
    }
    // ===========================================================

    private void updateStep(int step, boolean isCompleted) {
        FrameLayout circle = null;
        TextView icon = null;
        TextView text = null;

        switch (step) {
            case 1:
                circle = step1Circle;
                icon = step1Icon;
                text = step1Text;
                break;
            case 2:
                circle = step2Circle;
                icon = step2Icon;
                text = step2Text;
                break;
            case 3:
                circle = step3Circle;
                icon = step3Icon;
                text = step3Text;
                break;
            case 4:
                circle = step4Circle;
                icon = step4Icon;
                text = step4Text;
                break;
            case 5:
                circle = step5Circle;
                icon = step5Icon;
                text = step5Text;
                break;
        }

        if (circle != null) {
            if (isCompleted) {
                circle.setBackgroundResource(R.drawable.step_circle_bg_completed);
                icon.setTextColor(getColor(R.color.success));
                text.setTextColor(getColor(R.color.success));
            } else {
                circle.setBackgroundResource(R.drawable.step_circle_bg);
                icon.setTextColor(getColor(R.color.text_gray));
                text.setTextColor(getColor(R.color.text_gray));
            }
        }
    }

    private void setupStatusChips() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int selectedId = checkedIds.get(0);
                if (selectedId == R.id.chipConfirmed) {
                    selectedStatus = "Confirmed";
                } else if (selectedId == R.id.chipPreparing) {
                    selectedStatus = "Preparing";
                } else if (selectedId == R.id.chipOutForDelivery) {
                    selectedStatus = "Out for Delivery";
                } else if (selectedId == R.id.chipDelivered) {
                    selectedStatus = "Delivered";
                } else if (selectedId == R.id.chipCancelled) {
                    selectedStatus = "Cancelled";
                }

                // Show update button when different status selected
                if (!selectedStatus.equals(currentStatus)) {
                    btnUpdateStatus.setVisibility(View.VISIBLE);
                    btnUpdateStatus.setText("UPDATE TO " + selectedStatus.toUpperCase());

                    // Change button color based on selected status
                    if (selectedStatus.equals("Cancelled")) {
                        btnUpdateStatus.setBackgroundTintList(getColorStateList(R.color.warning));
                        btnUpdateStatus.setText("CANCEL ORDER");
                    } else if (selectedStatus.equals("Delivered")) {
                        btnUpdateStatus.setBackgroundTintList(getColorStateList(R.color.success));
                    } else {
                        btnUpdateStatus.setBackgroundTintList(getColorStateList(R.color.primary));
                    }
                } else {
                    btnUpdateStatus.setVisibility(View.GONE);
                }
            } else {
                btnUpdateStatus.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        btnUpdateStatus.setOnClickListener(v -> updateOrderStatus());
        btnCallCustomer.setOnClickListener(v -> callCustomer());
        btnCancelOrder.setOnClickListener(v -> showCancelOrderDialog());
    }

    private void updateOrderStatus() {
        if (selectedStatus.isEmpty()) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            chipGroupStatus.startAnimation(shakeAnimation);
            return;
        }

        // Special handling for cancellation
        if (selectedStatus.equals("Cancelled")) {
            showCancelConfirmation();
            return;
        }

        // Special handling for delivered
        if (selectedStatus.equals("Delivered")) {
            showDeliveredConfirmation();
            return;
        }

        // Normal status update
        updateOrderInStorage(selectedStatus);
        currentStatus = selectedStatus;
        updateUIForCurrentStatus();
        setStatusBadge(tvOrderStatus, currentStatus);
        updateTimeline();
        Toast.makeText(this, "Order status updated to " + selectedStatus, Toast.LENGTH_SHORT).show();
        btnUpdateStatus.setVisibility(View.GONE);
    }

    private void showCancelConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("⚠️ Cancel Order")
                .setMessage("Are you sure you want to cancel this order?\n\n"
                        + "📋 Order #" + currentOrder.getId() + "\n"
                        + "💰 Amount: ₹" + (int) currentOrder.getTotalAmount() + "\n\n"
                        + "Refund will be processed within 5-7 business days.")
                .setPositiveButton("Yes, Cancel Order", (dialog, which) -> {
                    updateOrderInStorage("Cancelled");
                    currentStatus = "Cancelled";
                    updateUIForCurrentStatus();
                    setStatusBadge(tvOrderStatus, currentStatus);
                    updateTimeline();
                    Toast.makeText(this, "Order cancelled. Refund initiated.", Toast.LENGTH_LONG).show();
                    btnUpdateStatus.setVisibility(View.GONE);
                })
                .setNegativeButton("No, Go Back", null)
                .show();
    }

    private void showDeliveredConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("✅ Mark as Delivered")
                .setMessage("Confirm that this order has been delivered to the customer?")
                .setPositiveButton("Yes, Delivered", (dialog, which) -> {
                    updateOrderInStorage("Delivered");
                    currentStatus = "Delivered";
                    updateUIForCurrentStatus();
                    setStatusBadge(tvOrderStatus, currentStatus);
                    updateTimeline();
                    Toast.makeText(this, "Order marked as delivered! 🎉", Toast.LENGTH_LONG).show();
                    btnUpdateStatus.setVisibility(View.GONE);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void callCustomer() {
        String phoneNumber = currentOrder.getCustomerPhone();
        if (!phoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCancelOrderDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?\n\n"
                        + "📋 Order #" + currentOrder.getId() + "\n"
                        + "💰 Amount: ₹" + (int) currentOrder.getTotalAmount() + "\n\n"
                        + "Refund will be processed within 5-7 business days.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelOrder())
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelOrder() {
        updateOrderInStorage("Cancelled");
        currentStatus = "Cancelled";
        updateUIForCurrentStatus();
        setStatusBadge(tvOrderStatus, currentStatus);
        updateTimeline();
        Toast.makeText(this, "Order cancelled. Refund initiated.", Toast.LENGTH_LONG).show();
        btnCancelOrder.setVisibility(View.GONE);
        cardCancelWarning.setVisibility(View.GONE);
    }

    private void updateOrderInStorage(String newStatus) {
        String ordersJson = sharedPreferences.getString("orders_list", "");
        if (!ordersJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Order>>() {}.getType();
            List<Order> ordersList = gson.fromJson(ordersJson, type);

            if (ordersList != null) {
                for (Order order : ordersList) {
                    if (order.getId().equals(orderId)) {
                        order.setStatus(newStatus);
                        break;
                    }
                }
                String updatedJson = gson.toJson(ordersList);
                sharedPreferences.edit().putString("orders_list", updatedJson).apply();
            }
        }
    }

    // Order Model Class
    public static class Order {
        private String id;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String deliveryAddress;
        private String orderTime;
        private int itemCount;
        private double totalAmount;
        private String status;
        private String itemsList;

        public Order() {}

        public Order(String id, String customerName, String customerEmail, String customerPhone,
                     String deliveryAddress, String orderTime, int itemCount,
                     double totalAmount, String status, String itemsList) {
            this.id = id;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.customerPhone = customerPhone;
            this.deliveryAddress = deliveryAddress;
            this.orderTime = orderTime;
            this.itemCount = itemCount;
            this.totalAmount = totalAmount;
            this.status = status;
            this.itemsList = itemsList;
        }

        public String getId() { return id; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public String getCustomerPhone() { return customerPhone; }
        public String getDeliveryAddress() { return deliveryAddress; }
        public String getOrderTime() { return orderTime; }
        public int getItemCount() { return itemCount; }
        public double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getItemsList() { return itemsList; }
        public void setStatus(String status) { this.status = status; }
    }
}