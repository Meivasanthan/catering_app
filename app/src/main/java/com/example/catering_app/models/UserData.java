package com.example.catering_app.models;

public class UserData {
    // Common fields (Both Customer & Owner)
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String role;  // "Customer" or "Caterer"
    private boolean isLoggedIn;
    private long signupTimestamp;

    // Owner/Caterer specific fields (Only for role = "Caterer")
    private String businessName;
    private String gstNumber;
    private String businessAddress;
    private String cuisineType;
    private String businessLogo;
    private double minimumOrderAmount;
    private int deliveryRadius;

    // Constructor for Customer (No owner fields)
    public UserData(String fullName, String email, String phone, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.isLoggedIn = false;
        this.signupTimestamp = System.currentTimeMillis();

        // Initialize owner fields as empty
        this.businessName = "";
        this.gstNumber = "";
        this.businessAddress = "";
        this.cuisineType = "";
        this.businessLogo = "";
        this.minimumOrderAmount = 0;
        this.deliveryRadius = 0;
    }

    // Constructor for Owner/Caterer (With owner fields)
    public UserData(String fullName, String email, String phone, String password, String role,
                    String businessName, String gstNumber, String businessAddress, String cuisineType) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.businessName = businessName;
        this.gstNumber = gstNumber;
        this.businessAddress = businessAddress;
        this.cuisineType = cuisineType;
        this.isLoggedIn = false;
        this.signupTimestamp = System.currentTimeMillis();
        this.businessLogo = "";
        this.minimumOrderAmount = 0;
        this.deliveryRadius = 0;
    }

    // ========== GETTERS ==========
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return isLoggedIn; }
    public long getSignupTimestamp() { return signupTimestamp; }

    // Owner Getters
    public String getBusinessName() { return businessName; }
    public String getGstNumber() { return gstNumber; }
    public String getBusinessAddress() { return businessAddress; }
    public String getCuisineType() { return cuisineType; }
    public String getBusinessLogo() { return businessLogo; }
    public double getMinimumOrderAmount() { return minimumOrderAmount; }
    public int getDeliveryRadius() { return deliveryRadius; }

    // ========== SETTERS ==========
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }
    public void setSignupTimestamp(long signupTimestamp) { this.signupTimestamp = signupTimestamp; }

    // Owner Setters
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public void setBusinessLogo(String businessLogo) { this.businessLogo = businessLogo; }
    public void setMinimumOrderAmount(double minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; }
    public void setDeliveryRadius(int deliveryRadius) { this.deliveryRadius = deliveryRadius; }

    // Helper method to check if user is Owner
    public boolean isOwner() {
        return role != null && role.equals("Caterer");
    }

    // Helper method to check if user is Customer
    public boolean isCustomer() {
        return role != null && role.equals("Customer");
    }
}