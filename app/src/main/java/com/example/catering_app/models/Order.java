package com.example.catering_app.models;

public class Order {
    private String orderId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private int itemCount;
    private double totalAmount;
    private String orderTime;
    private String status;
    private String catererId;

    public Order() {
        // Default constructor
    }

    public Order(String orderId, String customerName, int itemCount, double totalAmount, String orderTime) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.itemCount = itemCount;
        this.totalAmount = totalAmount;
        this.orderTime = orderTime;
        this.status = "pending";
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public int getItemCount() { return itemCount; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderTime() { return orderTime; }
    public String getStatus() { return status; }
    public String getCatererId() { return catererId; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }
    public void setStatus(String status) { this.status = status; }
    public void setCatererId(String catererId) { this.catererId = catererId; }
}