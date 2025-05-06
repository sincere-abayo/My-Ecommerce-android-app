package com.example.my_ecommerce;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Product> products = new ArrayList<>();
    private Context context;
    private boolean isAdmin;
    private ProductClickListener listener;
    
    public interface ProductClickListener {
        void onAddToCartClick(Product product);
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }
    
    public ProductAdapter(Context context, boolean isAdmin, ProductClickListener listener) {
        this.context = context;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.productDescription.setText(product.getDescription());
        
        // Show admin controls if user is admin
        if (isAdmin) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.addToCartButton.setVisibility(View.GONE);
            
            holder.editButton.setOnClickListener(v -> listener.onEditClick(product));
            holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(product));
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.addToCartButton.setVisibility(View.VISIBLE);
            
            holder.addToCartButton.setOnClickListener(v -> listener.onAddToCartClick(product));
        }
        
        // Set click listener for the whole item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return products.size();
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }
    
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;
        Button addToCartButton;
        Button editButton;
        Button deleteButton;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDescription = itemView.findViewById(R.id.product_description);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
