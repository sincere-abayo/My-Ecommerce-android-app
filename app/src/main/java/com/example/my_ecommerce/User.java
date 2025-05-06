package com.example.my_ecommerce;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String name;
    private String email;

    @SerializedName("isAdmin")
    private int adminValue; // Store the raw value (0 or 1)
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
 // Convert the numeric value to boolean
 public boolean isAdmin() {
    return adminValue == 1;
}

}
