package com.example.verdantu.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.verdantu.R;
import com.example.verdantu.models.Consumption;
import com.example.verdantu.rest.RestClient;

public class AddFoodItems extends AppCompatActivity {
    String foodName;
    String foodCarbonFootPrints;
    TextView foodItemName;
    TextView foodItemCarbonEmission;
    TextView totalFoodEmissions;
    EditText foodQuantity;
    String foodQty;
    Button showCarbonFootPrint;
    String deviceId;
    String foodCategory;
    int rgCategorySelected;
    Button addFood;
    String finalTotalEmissions;
    String userId = "10";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_items);
        deviceId = getDeviceId(getApplicationContext());
        System.out.println("Device ID  : " + deviceId);
        Intent intent = getIntent();
        foodName = intent.getStringExtra("foodItems");
        foodCarbonFootPrints = intent.getStringExtra("carbonEmissions");
        foodCategory = intent.getStringExtra("checkedFoodCategory");
        System.out.println("Category ID  : " + foodCategory);
        foodItemName = findViewById(R.id.textView_FoodName);
        foodItemName.setText("Selected Food : " + foodName);
        foodItemCarbonEmission = findViewById(R.id.textView_FoodEmissions);
        foodItemCarbonEmission.setText("Emission : " + foodCarbonFootPrints + " Kg/Co2");
        foodQuantity = findViewById(R.id.editTextView_Qty);
        showCarbonFootPrint = findViewById(R.id.btn_ShowCarbonFootPrint);
        addFood = findViewById(R.id.btn_Add);
        showCarbonFootPrint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                foodQty = foodQuantity.getText().toString();
                float qty = Float.parseFloat(foodQty);
                float convertKgToGram = qty/1000;
                float totalCarbonEmissions = convertKgToGram * (Float.parseFloat(foodCarbonFootPrints));
                totalFoodEmissions = findViewById(R.id.textView_TotalFoodEmissions);
                final float roundDecimalTotalFoodEmission = (float) ((double) Math.round(totalCarbonEmissions * 100000d) / 100000d);
                finalTotalEmissions = String.valueOf(roundDecimalTotalFoodEmission);
                totalFoodEmissions.setText("Total Emissions : " + finalTotalEmissions +  " Kg/Co2");
                System.out.println("Emission : " + foodName);
                System.out.println("Name : " + foodName + "Emission" + finalTotalEmissions + "Category"+ foodCategory);
                addFood.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ConsumptionAsyncTask foodConsumed = new ConsumptionAsyncTask();
                        //foodConsumed.execute(userId,foodItemName.getText().toString(),finalTotalEmissions,foodCategory);
                        foodConsumed.execute(foodName);
                    }
                });
            }
        });
    }

    public class ConsumptionAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //Consumption addConsumed =new Consumption(deviceId,foodItemName.getText().toString(),Float.parseFloat(finalTotalEmissions),foodCategory);
            RestClient.testData(params[0]);
            return "Consumption Added";
        }

        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
}