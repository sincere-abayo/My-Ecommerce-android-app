package com.example.my_ecommerce;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Optionally add getters if needed by Retrofit
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
