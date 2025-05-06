package com.example.my_ecommerce;

public class LoginResponse {
    private String message;
    private String token;
    private User user;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
    
    // Helper method to determine if login was successful
    public boolean isSuccess() {
        return token != null && !token.isEmpty();
    }
}
