package com.example.catering_app.models;

public class FavoriteItem {
    private int id;
    private String name;
    private String cuisine;
    private double rating;
    private String reviews;
    private String distance;
    private String deliveryTime;
    private String price;
    private int imageResId;

    public FavoriteItem(int id, String name, String cuisine, double rating,
                        String reviews, String distance, String deliveryTime,
                        String price, int imageResId) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.reviews = reviews;
        this.distance = distance;
        this.deliveryTime = deliveryTime;
        this.price = price;
        this.imageResId = imageResId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public double getRating() { return rating; }
    public String getReviews() { return reviews; }
    public String getDistance() { return distance; }
    public String getDeliveryTime() { return deliveryTime; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}