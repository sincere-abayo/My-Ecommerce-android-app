package com.example.my_ecommerce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://ecommerce-api-node-js-yvxk.onrender.com/api/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            // Create a custom Gson instance with our deserializers
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ProductResponse.class, new ProductResponseDeserializer())
                    .registerTypeAdapter(MessageResponse.class, new MessageResponseDeserializer())
                    .create();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
