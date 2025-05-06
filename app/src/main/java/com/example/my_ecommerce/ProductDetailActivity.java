package com.example.my_ecommerce;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName;
    private TextView productPrice;
    private TextView productDescription;
    private TextView productCategory;
    private TextView productQuantity;
    private Button addToCartButton;
    private ProgressBar loadingIndicator;
    
    private ApiService apiService;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        // Get product ID from intent
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize API service
        apiService = ApiClient.getRetrofit().create(ApiService.class);
        
        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Details");
        
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);
        productCategory = findViewById(R.id.product_category);
        productQuantity = findViewById(R.id.product_quantity);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        
        // Load product details
        loadProductDetails();
        
        // Setup add to cart button
        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProductDetails() {
        loadingIndicator.setVisibility(View.VISIBLE);
        
        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                loadingIndicator.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    displayProductDetails(product);
                } else {
                    Toast.makeText(ProductDetailActivity.this, 
                            "Failed to load product details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(ProductDetailActivity.this, 
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayProductDetails(Product product) {
        productName.setText(product.getName());
        productPrice.setText(String.format("$%.2f", product.getPrice()));
        productDescription.setText(product.getDescription());
        productCategory.setText("Category: " + product.getCategory());
        productQuantity.setText("In Stock: " + product.getQuantity());
        
                // Disable add to cart button if out of stock
                if (product.getQuantity() <= 0) {
                    addToCartButton.setEnabled(false);
                    addToCartButton.setText("Out of Stock");
                } else {
                    addToCartButton.setEnabled(true);
                    addToCartButton.setText("Add to Cart");
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
        