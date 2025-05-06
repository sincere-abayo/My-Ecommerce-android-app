package com.example.my_ecommerce;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFormActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private Spinner categorySpinner;
    private EditText quantityEditText;
    private Button submitButton;
    private ProgressBar loadingIndicator;
    
    private ApiService apiService;
    private String authToken;
    
    private int productId = -1; // -1 means new product
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);
        
        // Get token
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("auth_token", "");
        
        // Initialize API service
        apiService = ApiClient.getRetrofit().create(ApiService.class);
        
        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        nameEditText = findViewById(R.id.product_name_edit);
        descriptionEditText = findViewById(R.id.product_description_edit);
        priceEditText = findViewById(R.id.product_price_edit);
        categorySpinner = findViewById(R.id.product_category_spinner);
        quantityEditText = findViewById(R.id.product_quantity_edit);
        submitButton = findViewById(R.id.submit_button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        
        // Setup category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        
        // Check if we're editing an existing product
        if (getIntent().hasExtra("PRODUCT_ID")) {
            isEditMode = true;
            productId = getIntent().getIntExtra("PRODUCT_ID", -1);
            getSupportActionBar().setTitle("Edit Product");
            
            // Fill form with product data
            nameEditText.setText(getIntent().getStringExtra("PRODUCT_NAME"));
            descriptionEditText.setText(getIntent().getStringExtra("PRODUCT_DESCRIPTION"));
            priceEditText.setText(String.valueOf(getIntent().getDoubleExtra("PRODUCT_PRICE", 0)));
            
            String category = getIntent().getStringExtra("PRODUCT_CATEGORY");
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(category)) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }
            
            quantityEditText.setText(String.valueOf(getIntent().getIntExtra("PRODUCT_QUANTITY", 0)));
        } else {
            getSupportActionBar().setTitle("Add New Product");
        }
        
        // Setup submit button
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                submitProduct();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            valid = false;
        }
        
        String description = descriptionEditText.getText().toString().trim();
        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            valid = false;
        }
        
        String priceStr = priceEditText.getText().toString().trim();
        if (priceStr.isEmpty()) {
            priceEditText.setError("Price is required");
            valid = false;
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    priceEditText.setError("Price must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                priceEditText.setError("Invalid price format");
                valid = false;
            }
        }
        
        String quantityStr = quantityEditText.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            quantityEditText.setError("Quantity is required");
            valid = false;
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity < 0) {
                    quantityEditText.setError("Quantity cannot be negative");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                quantityEditText.setError("Invalid quantity format");
                valid = false;
            }
        }
        
        return valid;
    }

    private void submitProduct() {
        loadingIndicator.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        double price = Double.parseDouble(priceEditText.getText().toString().trim());
        String category = categorySpinner.getSelectedItem().toString();
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        
        Product product = new Product(name, description, price, category, quantity);
        
        if (isEditMode) {
            // Update existing product
            apiService.updateProduct(authToken, productId, product).enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    loadingIndicator.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ProductFormActivity.this, 
                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProductFormActivity.this, 
                                "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {
                    loadingIndicator.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(ProductFormActivity.this, 
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new product
            apiService.createProduct(authToken, product).enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    loadingIndicator.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ProductFormActivity.this, 
                                response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ProductFormActivity.this, 
                                "Failed to create product", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {
                    loadingIndicator.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(ProductFormActivity.this, 
                            "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}