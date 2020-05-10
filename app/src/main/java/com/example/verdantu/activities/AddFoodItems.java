package com.example.verdantu.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.verdantu.R;
import com.example.verdantu.modelinterfaces.PostService;
import com.example.verdantu.models.Consumption;
import com.example.verdantu.models.Food;
import com.example.verdantu.rest.RetrofitClientInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFoodItems extends AppCompatActivity {
    private String foodName;
    private String foodCarbonFootPrints;
    private TextView foodItemName;
    private TextView foodItemCarbonEmission;
    private TextView totalFoodEmissions;
    private EditText foodQuantity;
    private String foodQty;
    private Button showCarbonFootPrint;
    private Button cancel;
    private String deviceId;
    private String foodCategory;
    private int rgCategorySelected;
    private Button addFood;
    private String finalTotalEmissions;
    private String userId = "10";
    private Boolean isFootPrintShown = false;
    private float qty;
    private float roundDecimalTotalFoodEmission;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_items);
        deviceId = DeviceData.getDeviceId(getApplicationContext());
        System.out.println("Device ID  : " + deviceId);
        Intent intent = getIntent();
        foodName = intent.getStringExtra("foodItems");
        foodCarbonFootPrints = intent.getStringExtra("carbonEmissions");
        foodCategory = intent.getStringExtra("checkedFoodCategory");
        System.out.println("Category ID  : " + foodCategory);
        foodItemName = findViewById(R.id.textView_FoodName);
        foodItemName.setText("Selected Food : " + foodName);
        foodItemCarbonEmission = findViewById(R.id.textView_FoodEmissions);
        foodItemCarbonEmission.setText("Emission : " + foodCarbonFootPrints + " KgCo2/100g");
        foodQuantity = findViewById(R.id.editTextView_Qty);
        showCarbonFootPrint = findViewById(R.id.btn_ShowCarbonFootPrint);
        addFood = findViewById(R.id.btn_Add);
        cancel = findViewById(R.id.btn_Cancel);
        // System.out.println(" Food Quantity : " + foodQty);
        showCarbonFootPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCarbonFootPrint();
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addFood();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                cancelFood();
            }
        });
    }

    public void showCarbonFootPrint()
    {
        foodQty = foodQuantity.getText().toString();
            if (!foodQty.equalsIgnoreCase("")) {
            qty = Float.parseFloat(foodQty);
            float convertKgToGram = qty/100;
            float totalCarbonEmissions = convertKgToGram * (Float.parseFloat(foodCarbonFootPrints));
            totalFoodEmissions = findViewById(R.id.textView_TotalFoodEmissions);
            roundDecimalTotalFoodEmission = (float) ((double) Math.round(totalCarbonEmissions * 100000d) / 100000d);
            finalTotalEmissions = String.valueOf(roundDecimalTotalFoodEmission);
            totalFoodEmissions.setText("Total Emissions : " + finalTotalEmissions + " KgCo2/100g");
            foodQuantity.setText("");
            System.out.println("Emission : " + foodName);
            System.out.println("Name : " + foodName + "Emission" + finalTotalEmissions + "Category" + foodCategory);
            isFootPrintShown = true;
        } else if (foodQty.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please enter the quantity", Toast.LENGTH_SHORT).show();
        }

    }

    public void addFood(){
        if (isFootPrintShown){
            PostService service = RetrofitClientInstance.getRetrofitInstance().create(PostService.class);
            Gson gson = new Gson();
            ArrayList<Consumption> consumedData = new ArrayList<>();
            System.out.println("Emission value from ad food itesm : " + roundDecimalTotalFoodEmission);
            Consumption addConsumed = new Consumption(deviceId, foodName, roundDecimalTotalFoodEmission, foodCategory,qty);
            consumedData.add(addConsumed);
            RequestBody postData = RequestBody.create(MediaType.parse("application/json"), gson.toJson(consumedData));
            service.addConsumption(postData).enqueue(new Callback<List<Food>>() {
                @Override
                public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                    System.out.println("Body response " + response.body());
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Toast.makeText(getApplicationContext(), "Consumption Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Food>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    System.out.println(" Throwable error : " + t);
                }
            });
        }
        else  {
            Toast.makeText(getApplicationContext(), "Please enter the quantity", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelFood() {

        AlertDialog alertDialog = new AlertDialog.Builder(AddFoodItems.this).setIcon(android.R.drawable.ic_dialog_map)
                .setTitle("Exit?").setMessage("Are you sure to exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(AddFoodItems.this,"Back to Add",Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }


}
