package com.example.my_ecommerce;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("userId")
    private int userId;

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }
}
