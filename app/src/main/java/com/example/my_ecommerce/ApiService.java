package com.example.my_ecommerce;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    
    @GET("products")
    Call<List<Product>> getAllProducts();
    
    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int productId);
    
    @POST("products")
    Call<ProductResponse> createProduct(@Header("Authorization") String token, @Body Product product);
    
    @PUT("products/{id}")
    Call<ProductResponse> updateProduct(@Header("Authorization") String token, @Path("id") int productId, @Body Product product);
    
    @DELETE("products/{id}")
    Call<MessageResponse> deleteProduct(@Header("Authorization") String token, @Path("id") int productId);
}
