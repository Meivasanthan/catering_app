package com.example.catering_app.models;

import java.util.ArrayList;
import java.util.List;

public class FoodItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private double offerPrice;
    private String type;  // "Veg" or "NonVeg"
    private String category;  // "Main Course", "Dessert", etc.
    private boolean isAvailable;
    private int ordersCount;
    private boolean isPopular;
    private boolean isBestSeller;
    private int prepTime;
    private String imagePath;
    private String catererId;
    private List<String> eventTypes;  // NEW: ["Birthday", "Wedding", "Corporate", "Party", "House Party"]

    public FoodItem() {
        this.eventTypes = new ArrayList<>();
    }

    public FoodItem(String id, String name, String description, double price,
                    double offerPrice, String type, String category,
                    boolean isAvailable, int ordersCount, boolean isPopular,
                    boolean isBestSeller, int prepTime, String imagePath,
                    List<String> eventTypes) {
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
        this.eventTypes = eventTypes != null ? eventTypes : new ArrayList<>();
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
    public String getCatererId() { return catererId; }
    public List<String> getEventTypes() { return eventTypes; }

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
    public void setCatererId(String catererId) { this.catererId = catererId; }
    public void setEventTypes(List<String> eventTypes) { this.eventTypes = eventTypes; }

    // Helper method to check if food is suitable for an event
    public boolean isSuitableForEvent(String eventType) {
        return eventTypes != null && eventTypes.contains(eventType);
    }
}