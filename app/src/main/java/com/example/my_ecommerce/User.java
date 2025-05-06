package com.example.my_ecommerce;

public class User {
    private int id;
    private String name;
    private String email;
    private int isAdmin;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin == 1;
    }
}
