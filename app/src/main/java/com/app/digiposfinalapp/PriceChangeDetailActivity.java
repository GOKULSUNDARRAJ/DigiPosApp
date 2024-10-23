package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceChangeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_price_change_detail);
        Intent intent = getIntent();


        // Get the extra data from the intent
        String departmentId = intent.getStringExtra("DEPARTMENTID");
        String plu = intent.getStringExtra("PLU");
        String barcode = intent.getStringExtra("BARCODE");
        String detail = intent.getStringExtra("DETAIL");
        String shop = intent.getStringExtra("SHOP");
        String price = intent.getStringExtra("PRICE");
        String date = intent.getStringExtra("DATE");
        String capacity = intent.getStringExtra("CAPACITY");
        String qty = intent.getStringExtra("QTY");

        // Find TextViews in the layout
        TextView departmentIdTextView = findViewById(R.id.itemText34109);
        TextView pluTextView = findViewById(R.id.itemText3410);
        TextView barcodeTextView = findViewById(R.id.itemText3420);
        TextView detailTextView = findViewById(R.id.itemText342980);
        TextView shopTextView = findViewById(R.id.itemText342900);
        TextView priceTextView = findViewById(R.id.itemText3429000);
        TextView dateTextView = findViewById(R.id.itemText345629000);
        TextView capacityTextView = findViewById(R.id.itemText342569000);
        TextView qtyTextView = findViewById(R.id.itemText346529000);

        // Set the retrieved data in TextViews
        departmentIdTextView.setText(String.valueOf(departmentId));
        pluTextView.setText(plu);
        barcodeTextView.setText(barcode);
        detailTextView.setText(detail);
        shopTextView.setText(shop);
        priceTextView.setText(price);
        dateTextView.setText(date);
        capacityTextView.setText(capacity);
        qtyTextView.setText(String.valueOf(qty));



    }

    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}