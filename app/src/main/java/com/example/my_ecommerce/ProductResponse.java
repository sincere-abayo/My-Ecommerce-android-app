package com.example.my_ecommerce;

public class ProductResponse {
    private String message;
    private Product product;
    
    public ProductResponse(String message, Product product) {
        this.message = message;
        this.product = product;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Product getProduct() {
        return product;
    }
}
