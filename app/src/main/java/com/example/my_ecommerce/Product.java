package com.example.my_ecommerce;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int quantity;
    
    // Constructor for creating new products
    public Product(String name, String description, double price, String category, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getCategory() {
        return category;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    // Setters for updates
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
