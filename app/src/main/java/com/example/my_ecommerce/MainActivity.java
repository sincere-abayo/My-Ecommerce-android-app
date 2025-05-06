package com.example.my_ecommerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout loginForm, registerForm;
    private TextView loginLink, registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginForm = findViewById(R.id.login_form);
        registerForm = findViewById(R.id.register_form);

        loginLink = findViewById(R.id.login_link);
        registerLink = findViewById(R.id.register_link);


        loginLink.setOnClickListener(view -> showLogin());
        registerLink.setOnClickListener(view -> showRegister());
        Button loginBtn = findViewById(R.id.login_button);
        EditText emailField = findViewById(R.id.login_email);
        EditText passwordField = findViewById(R.id.login_password);

        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            LoginRequest request = new LoginRequest(email, password);
            ApiService service = ApiClient.getRetrofit().create(ApiService.class);
            service.login(request).enqueue(new Callback<LoginResponse>() {


// Inside the loginBtn.setOnClickListener method, update the onResponse method:

@Override
public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
    if (response.isSuccessful() && response.body() != null) {
        LoginResponse loginResponse = response.body();
        
        if (loginResponse.isSuccess()) {
            // Login successful
            Toast.makeText(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
            
            // Save token
            saveToken(loginResponse.getToken());
            
            // Save user information
            User user = loginResponse.getUser();
            if (user != null) {
                SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
                editor.putString("user_email", user.getEmail());
                editor.putString("user_name", user.getName());
                editor.putInt("user_id", user.getId());
                editor.putBoolean("is_admin", user.isAdmin());
                editor.apply();
            } else {
                // If user object is null, at least save the email
                SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
                editor.putString("user_email", email);
                editor.apply();
            }
            
            // Go to dashboard with a slight delay to ensure preferences are saved
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Prevent back to login
            }, 300);
        } else {
            // Login failed with a message from the server
            Toast.makeText(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }
    } else {
        // HTTP error or parsing error
        try {
            // Try to parse error message from response body
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            Toast.makeText(MainActivity.this, "Login failed: " + errorBody, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
        }
    }
}


                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        Button registerBtn = findViewById(R.id.register_button);
        EditText nameField = findViewById(R.id.register_name);
        EditText regEmailField = findViewById(R.id.register_email);
        EditText regPasswordField = findViewById(R.id.register_password);

        registerBtn.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String email = regEmailField.getText().toString();
            String password = regPasswordField.getText().toString();

            RegisterRequest request = new RegisterRequest(name, email, password);
            ApiService service = ApiClient.getRetrofit().create(ApiService.class);

            service.register(request).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String msg = response.body().getMessage();
                        Toast.makeText(MainActivity.this, "Registration successful: " + msg, Toast.LENGTH_SHORT).show();
                        goToDashboard();
                    } else {
                        Toast.makeText(MainActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

    private void showLogin() {
        loginForm.setVisibility(View.VISIBLE);
        registerForm.setVisibility(View.GONE);

        loginLink.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        loginLink.setTypeface(null, android.graphics.Typeface.BOLD);

        registerLink.setTextColor(getResources().getColor(android.R.color.darker_gray));
        registerLink.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void showRegister() {
        loginForm.setVisibility(View.GONE);
        registerForm.setVisibility(View.VISIBLE);

        registerLink.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        registerLink.setTypeface(null, android.graphics.Typeface.BOLD);

        loginLink.setTextColor(getResources().getColor(android.R.color.darker_gray));
        loginLink.setTypeface(null, android.graphics.Typeface.NORMAL);
    }
    private void saveToken(String token) {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putString("auth_token", token)
                .apply();
    }
    private void goToDashboard() {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // Prevent back to login
    }

}