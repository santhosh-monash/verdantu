package com.example.verdantu.modelinterfaces;

import com.example.verdantu.models.Food;
import com.example.verdantu.models.RecipeConsumption;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostService {


    @Headers("Content-Type: application/json")
    @POST("api/addEmission")
    Call<List<Food>> addConsumption(@Body RequestBody consumption);

    @Headers("Content-Type: application/json")
    @POST("api/add_recipe_emission")
    Call<List<RecipeConsumption>> addRecipeConsumption(@Body RequestBody recipeConsumption);
}
