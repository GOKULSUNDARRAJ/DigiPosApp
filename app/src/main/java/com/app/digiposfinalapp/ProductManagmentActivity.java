package com.app.digiposfinalapp;

import android.os.Bundle;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ProductManagmentActivity extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener{
    private CustomSpinner spinner_fruits;

    private FruitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_managment);
        spinner_fruits = findViewById(R.id.spinner_fruits);

        spinner_fruits.setSpinnerEventsListener(this);

        adapter = new FruitAdapter(ProductManagmentActivity.this, Data.getFruitList());
        spinner_fruits.setAdapter(adapter);
    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinner_fruits.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        spinner_fruits.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit));
    }
}