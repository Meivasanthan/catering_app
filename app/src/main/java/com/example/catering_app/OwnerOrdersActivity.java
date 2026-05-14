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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OwnerOrdersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView ivBack;
    private TextView tvToolbarTitle;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TextView tvNoOrders;
    private LinearLayout llEmptyState;

    private OrdersAdapter ordersAdapter;
    private List<Order> ordersList;
    private SharedPreferences sharedPreferences;
    private String currentStatus = "New";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_orders);

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
            // Already on Menu page, do nothing or refresh
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

        sharedPreferences = getSharedPreferences("ORDER_DATA", MODE_PRIVATE);

        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();

        // Check if orders exist, if not add sample orders
        String ordersJson = sharedPreferences.getString("orders_list", "");
        if (ordersJson.isEmpty()) {
            addSampleOrders();  // Add demo orders only first time
        }

        loadOrders();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.ivBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        llEmptyState = findViewById(R.id.llEmptyState);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        tvToolbarTitle.setText("Order Management");
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("🆕 New"));
        tabLayout.addTab(tabLayout.newTab().setText("⏳ Preparing"));
        tabLayout.addTab(tabLayout.newTab().setText("🚚 Out for Delivery"));
        tabLayout.addTab(tabLayout.newTab().setText("✅ Completed"));
        tabLayout.addTab(tabLayout.newTab().setText("❌ Cancelled"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String[] statuses = {"New", "Preparing", "Out for Delivery", "Completed", "Cancelled"};
                currentStatus = statuses[tab.getPosition()];
                loadOrdersByStatus();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        ordersList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, ordersList, new OrdersAdapter.OnOrderActionListener() {
            @Override
            public void onViewOrder(Order order) {
                openOrderDetail(order);
            }

            @Override
            public void onAcceptOrder(Order order) {
                updateOrderStatus(order, "Preparing");
            }

            @Override
            public void onDeclineOrder(Order order) {
                updateOrderStatus(order, "Cancelled");
            }

            @Override
            public void onMarkPreparing(Order order) {
                updateOrderStatus(order, "Preparing");
            }

            @Override
            public void onMarkOutForDelivery(Order order) {
                updateOrderStatus(order, "Out for Delivery");
            }

            @Override
            public void onMarkDelivered(Order order) {
                updateOrderStatus(order, "Completed");
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(ordersAdapter);
    }

    private void loadOrders() {
        String ordersJson = sharedPreferences.getString("orders_list", "");
        if (!ordersJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Order>>() {}.getType();
            List<Order> savedOrders = gson.fromJson(ordersJson, type);
            if (savedOrders != null) {
                ordersList.clear();
                ordersList.addAll(savedOrders);
            }
        }
        loadOrdersByStatus();
    }

    private void loadOrdersByStatus() {
        List<Order> filteredList = new ArrayList<>();
        for (Order order : ordersList) {
            if (order.getStatus().equals(currentStatus)) {
                filteredList.add(order);
            }
        }
        ordersAdapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
            tvNoOrders.setText("No " + currentStatus + " orders");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateOrderStatus(Order order, String newStatus) {
        for (Order o : ordersList) {
            if (o.getId().equals(order.getId())) {
                o.setStatus(newStatus);
                break;
            }
        }
        saveAllOrders();
        loadOrdersByStatus();
        Toast.makeText(this, "Order " + order.getId() + " marked as " + newStatus, Toast.LENGTH_SHORT).show();
    }

    private void saveAllOrders() {
        Gson gson = new Gson();
        String json = gson.toJson(ordersList);
        sharedPreferences.edit().putString("orders_list", json).apply();
    }

    private void openOrderDetail(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_id", order.getId());
        startActivity(intent);
    }

    // ========== ADD SAMPLE ORDERS FOR DEMO ==========
    private void addSampleOrders() {
        ordersList.clear();

        // 🆕 NEW ORDERS (3)
        ordersList.add(new Order(
                "ORD-1028", "Priya Sharma", "priya@email.com", "+91 98765 43210",
                "123 Main Street, New York, NY 10001", getCurrentTime(),
                3, 1856.0, "New",
                "• 2x Butter Chicken (₹700)\n• 1x Garlic Naan (₹50)\n• 1x Biryani (₹250)"
        ));

        ordersList.add(new Order(
                "ORD-1029", "Rajesh Kumar", "rajesh@email.com", "+91 98765 43211",
                "456 Park Avenue, New York, NY 10002", getCurrentTime(),
                5, 2850.0, "New",
                "• 3x Paneer Butter Masala (₹840)\n• 2x Garlic Naan (₹100)\n• 1x Gulab Jamun (₹120)"
        ));

        ordersList.add(new Order(
                "ORD-1030", "Meena Sharma", "meena@email.com", "+91 98765 43212",
                "789 Lake View, New York, NY 10003", getCurrentTime(),
                2, 950.0, "New",
                "• 1x Masala Dosa (₹140)\n• 1x Mango Lassi (₹90)"
        ));

        // ⏳ PREPARING ORDERS (2)
        ordersList.add(new Order(
                "ORD-1025", "Amit Singh", "amit@email.com", "+91 98765 43213",
                "321 River Road, New York, NY 10004", "11:30 AM, 12 May",
                4, 2100.0, "Preparing",
                "• 2x Chicken Biryani (₹500)\n• 1x Raita (₹40)\n• 1x Gulab Jamun (₹120)"
        ));

        ordersList.add(new Order(
                "ORD-1026", "Neha Gupta", "neha@email.com", "+91 98765 43214",
                "654 Hillside Ave, New York, NY 10005", "12:15 PM, 12 May",
                3, 1450.0, "Preparing",
                "• 1x Butter Chicken (₹350)\n• 2x Butter Naan (₹80)\n• 1x Jeera Rice (₹120)"
        ));

        // 🚚 OUT FOR DELIVERY ORDERS (2)
        ordersList.add(new Order(
                "ORD-1023", "Vikram Patel", "vikram@email.com", "+91 98765 43215",
                "987 Oak Street, New York, NY 10006", "10:45 AM, 12 May",
                6, 3200.0, "Out for Delivery",
                "• 3x Biryani (₹750)\n• 2x Chicken 65 (₹300)\n• 1x Naan (₹40)"
        ));

        ordersList.add(new Order(
                "ORD-1024", "Divya Reddy", "divya@email.com", "+91 98765 43216",
                "147 Pine Street, New York, NY 10007", "11:00 AM, 12 May",
                2, 750.0, "Out for Delivery",
                "• 1x Paneer Tikka (₹250)\n• 1x Garlic Naan (₹50)"
        ));

        // ✅ COMPLETED ORDERS (3)
        ordersList.add(new Order(
                "ORD-1020", "Suresh Nair", "suresh@email.com", "+91 98765 43217",
                "258 Cedar Lane, New York, NY 10008", "Yesterday, 7:30 PM",
                4, 1950.0, "Completed",
                "• 2x Paneer Butter Masala (₹560)\n• 2x Garlic Naan (₹100)"
        ));

        ordersList.add(new Order(
                "ORD-1021", "Anjali Menon", "anjali@email.com", "+91 98765 43218",
                "369 Birch Street, New York, NY 10009", "Yesterday, 8:15 PM",
                3, 1650.0, "Completed",
                "• 1x Chicken Curry (₹300)\n• 2x Rice (₹100)\n• 1x Salad (₹80)"
        ));

        ordersList.add(new Order(
                "ORD-1022", "Ravi Verma", "ravi@email.com", "+91 98765 43219",
                "741 Maple Ave, New York, NY 10010", "Yesterday, 9:00 PM",
                5, 2400.0, "Completed",
                "• 2x Biryani (₹500)\n• 3x Chicken 65 (₹450)"
        ));

        // ❌ CANCELLED ORDERS (2)
        ordersList.add(new Order(
                "ORD-1018", "Kavita Joshi", "kavita@email.com", "+91 98765 43220",
                "159 Walnut Street, New York, NY 10011", "2 days ago",
                2, 650.0, "Cancelled",
                "• 1x Veg Biryani (₹180)\n• 1x Raita (₹40)"
        ));

        ordersList.add(new Order(
                "ORD-1019", "Mohan Das", "mohan@email.com", "+91 98765 43221",
                "753 Cherry Lane, New York, NY 10012", "2 days ago",
                3, 1200.0, "Cancelled",
                "• 1x Butter Chicken (₹350)\n• 2x Naan (₹80)"
        ));

        saveAllOrders();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a, dd MMM", Locale.getDefault());
        return sdf.format(new Date());
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

        // Getters
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

        // Setters
        public void setStatus(String status) { this.status = status; }
    }

    // RecyclerView Adapter
    public static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

        private AppCompatActivity context;
        private List<Order> orders;
        private OnOrderActionListener listener;

        public interface OnOrderActionListener {
            void onViewOrder(Order order);
            void onAcceptOrder(Order order);
            void onDeclineOrder(Order order);
            void onMarkPreparing(Order order);
            void onMarkOutForDelivery(Order order);
            void onMarkDelivered(Order order);
        }

        public OrdersAdapter(AppCompatActivity context, List<Order> orders, OnOrderActionListener listener) {
            this.context = context;
            this.orders = orders;
            this.listener = listener;
        }

        public void updateList(List<Order> newOrders) {
            this.orders = newOrders;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Order order = orders.get(position);

            holder.tvOrderId.setText("Order #" + order.getId());
            holder.tvOrderTime.setText(order.getOrderTime());
            holder.tvCustomerName.setText(order.getCustomerName());
            holder.tvItemCount.setText(order.getItemCount() + " items");
            holder.tvTotalAmount.setText("₹" + (int) order.getTotalAmount());
            holder.tvDeliveryAddress.setText(order.getDeliveryAddress());

            setStatusBadge(holder.tvStatus, order.getStatus());

            holder.btnView.setOnClickListener(v -> listener.onViewOrder(order));
            holder.btnView.setVisibility(View.VISIBLE);

            // New orders - Show Accept/Decline
            if (order.getStatus().equals("New")) {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnDecline.setVisibility(View.VISIBLE);
                holder.btnMarkPreparing.setVisibility(View.GONE);
                holder.btnMarkOut.setVisibility(View.GONE);
                holder.btnMarkDelivered.setVisibility(View.GONE);

                holder.btnAccept.setOnClickListener(v -> listener.onAcceptOrder(order));
                holder.btnDecline.setOnClickListener(v -> listener.onDeclineOrder(order));
            }
            // Preparing orders - Show Mark Out for Delivery
            else if (order.getStatus().equals("Preparing")) {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDecline.setVisibility(View.GONE);
                holder.btnMarkPreparing.setVisibility(View.GONE);
                holder.btnMarkOut.setVisibility(View.VISIBLE);
                holder.btnMarkDelivered.setVisibility(View.GONE);

                holder.btnMarkOut.setOnClickListener(v -> listener.onMarkOutForDelivery(order));
            }
            // Out for Delivery - Show Mark Delivered
            else if (order.getStatus().equals("Out for Delivery")) {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDecline.setVisibility(View.GONE);
                holder.btnMarkPreparing.setVisibility(View.GONE);
                holder.btnMarkOut.setVisibility(View.GONE);
                holder.btnMarkDelivered.setVisibility(View.VISIBLE);

                holder.btnMarkDelivered.setOnClickListener(v -> listener.onMarkDelivered(order));
            }
            // Completed or Cancelled - No action buttons
            else {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnDecline.setVisibility(View.GONE);
                holder.btnMarkPreparing.setVisibility(View.GONE);
                holder.btnMarkOut.setVisibility(View.GONE);
                holder.btnMarkDelivered.setVisibility(View.GONE);
            }
        }

        private void setStatusBadge(TextView tvStatus, String status) {
            switch (status) {
                case "New":
                    tvStatus.setText("🟡 New");
                    tvStatus.setBackgroundColor(context.getColor(R.color.status_new_bg));
                    tvStatus.setTextColor(context.getColor(R.color.status_new_text));
                    break;
                case "Preparing":
                    tvStatus.setText("🟠 Preparing");
                    tvStatus.setBackgroundColor(context.getColor(R.color.status_preparing_bg));
                    tvStatus.setTextColor(context.getColor(R.color.status_preparing_text));
                    break;
                case "Out for Delivery":
                    tvStatus.setText("🔵 Out for Delivery");
                    tvStatus.setBackgroundColor(context.getColor(R.color.status_out_bg));
                    tvStatus.setTextColor(context.getColor(R.color.status_out_text));
                    break;
                case "Completed":
                    tvStatus.setText("✅ Completed");
                    tvStatus.setBackgroundColor(context.getColor(R.color.status_completed_bg));
                    tvStatus.setTextColor(context.getColor(R.color.status_completed_text));
                    break;
                case "Cancelled":
                    tvStatus.setText("❌ Cancelled");
                    tvStatus.setBackgroundColor(context.getColor(R.color.status_cancelled_bg));
                    tvStatus.setTextColor(context.getColor(R.color.status_cancelled_text));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderId, tvOrderTime, tvCustomerName, tvItemCount, tvTotalAmount, tvDeliveryAddress, tvStatus;
            MaterialButton btnView, btnAccept, btnDecline, btnMarkPreparing, btnMarkOut, btnMarkDelivered;

            ViewHolder(View itemView) {
                super(itemView);
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
                tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
                tvItemCount = itemView.findViewById(R.id.tvItemCount);
                tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
                tvDeliveryAddress = itemView.findViewById(R.id.tvDeliveryAddress);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnView = itemView.findViewById(R.id.btnView);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnDecline = itemView.findViewById(R.id.btnDecline);
                btnMarkPreparing = itemView.findViewById(R.id.btnMarkPreparing);
                btnMarkOut = itemView.findViewById(R.id.btnMarkOut);
                btnMarkDelivered = itemView.findViewById(R.id.btnMarkDelivered);
            }
        }
    }
}