package com.example.my_ecommerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    private Toolbar toolbar;
    private FloatingActionButton addProductFab;
    
    private RecyclerView productsRecyclerView;
    private SwipeRefreshLayout productsSwipeRefresh;
    private TextView emptyProductsView;
    private ProgressBar productsLoadingIndicator;
    private TextView welcomeText;
    
    private ProductAdapter productAdapter;
    
    private ApiService apiService;
    private String authToken;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Get token and user info
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        isAdmin = prefs.getBoolean("is_admin", false);

        if (token == null) {
            // Redirect to login if token missing
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        // Format token for API calls
        authToken = "Bearer " + token;

        // Initialize API service
        apiService = ApiClient.getRetrofit().create(ApiService.class);

        // Initialize UI components
        initializeViews();
        setupListeners();
        setupAdapter();
        
        // Load initial data
        loadProducts();
        
        // Set welcome message
        String userName = prefs.getString("user_name", "User");
        welcomeText.setText("Welcome, " + userName + "!");
        
        // Show/hide admin controls
        if (isAdmin) {
            addProductFab.setVisibility(View.VISIBLE);
        } else {
            addProductFab.setVisibility(View.GONE);
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        addProductFab = findViewById(R.id.cart_fab); // Reusing the FAB for adding products
        addProductFab.setImageResource(android.R.drawable.ic_input_add);
        
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        productsSwipeRefresh = findViewById(R.id.swipe_refresh_products);
        emptyProductsView = findViewById(R.id.empty_products_view);
        productsLoadingIndicator = findViewById(R.id.products_loading_indicator);
        welcomeText = findViewById(R.id.welcome_text);
    }

    private void setupListeners() {
        // Swipe refresh listener
        productsSwipeRefresh.setOnRefreshListener(this::loadProducts);
        
        // Add product FAB listener (for admins)
        addProductFab.setOnClickListener(v -> {
            if (isAdmin) {
                openAddProductActivity();
            }
        });
    }

    private void setupAdapter() {
        // Setup products recycler view
        productAdapter = new ProductAdapter(this, isAdmin, this);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        productsLoadingIndicator.setVisibility(View.VISIBLE);
        emptyProductsView.setVisibility(View.GONE);
        
        apiService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                productsLoadingIndicator.setVisibility(View.GONE);
                productsSwipeRefresh.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    
                    if (products.isEmpty()) {
                        emptyProductsView.setVisibility(View.VISIBLE);
                    } else {
                        productAdapter.setProducts(products);
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, 
                            "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                productsLoadingIndicator.setVisibility(View.GONE);
                productsSwipeRefresh.setRefreshing(false);
                Toast.makeText(DashboardActivity.this, 
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddProductActivity() {
        Intent intent = new Intent(this, ProductFormActivity.class);
        startActivity(intent);
    }

    private void openEditProductActivity(Product product) {
        Intent intent = new Intent(this, ProductFormActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        intent.putExtra("PRODUCT_NAME", product.getName());
        intent.putExtra("PRODUCT_DESCRIPTION", product.getDescription());
        intent.putExtra("PRODUCT_PRICE", product.getPrice());
        intent.putExtra("PRODUCT_CATEGORY", product.getCategory());
        intent.putExtra("PRODUCT_QUANTITY", product.getQuantity());
        startActivity(intent);
    }

    private void deleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    productsLoadingIndicator.setVisibility(View.VISIBLE);
                    
                    apiService.deleteProduct(authToken, product.getId()).enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            productsLoadingIndicator.setVisibility(View.GONE);
                            
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(DashboardActivity.this, 
                                        response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                loadProducts(); // Refresh the list
                            } else {
                                Toast.makeText(DashboardActivity.this, 
                                        "Failed to delete product", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            productsLoadingIndicator.setVisibility(View.GONE);
                            Toast.makeText(DashboardActivity.this, 
                                    "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Clear token and user data
        SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
        editor.remove("auth_token");
        editor.remove("user_email");
        editor.remove("user_name");
        editor.remove("user_id");
        editor.remove("is_admin");
        editor.apply();
        
        // Redirect to login
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // ProductAdapter.ProductClickListener implementation
    @Override
    public void onAddToCartClick(Product product) {
        // For regular users - add to cart functionality
        Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Product product) {
        // For admin users - edit product
        if (isAdmin) {
            openEditProductActivity(product);
        }
    }

    @Override
    public void onDeleteClick(Product product) {
        // For admin users - delete product
        if (isAdmin) {
            deleteProduct(product);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh products when returning to this activity
        loadProducts();
    }
}
