package com.app.digiposfinalapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class StockDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_details);
        Intent intent = getIntent();


        // Find TextViews
        TextView textViewId = findViewById(R.id.id);
        TextView textViewBarcode = findViewById(R.id.barcode1);
        TextView textViewPLU = findViewById(R.id.pluedt);
        TextView textViewDescription = findViewById(R.id.description);
        TextView textViewDate = findViewById(R.id.date);
        TextView textViewTime = findViewById(R.id.time);
        TextView textViewType = findViewById(R.id.typevalue);
        TextView textViewReason = findViewById(R.id.reason);
        TextView textViewCurrentStock = findViewById(R.id.currentstock);
        TextView textViewStockIn = findViewById(R.id.stockin);
        TextView textViewStockOut = findViewById(R.id.stockout);
        TextView textViewScaleType = findViewById(R.id.scaletype);
        TextView textViewCurrentPrice = findViewById(R.id.currenttype);
        TextView textViewTillNo = findViewById(R.id.tillno);
        TextView textViewUserName = findViewById(R.id.username);
        TextView textViewLogId = findViewById(R.id.loginid);
        TextView textViewShortDescription = findViewById(R.id.shortdescription);

        // Retrieve intent extras

        if (intent != null) {
            String id = intent.getStringExtra("ID");
            String barcode = intent.getStringExtra("BARCODE");
            String plu = intent.getStringExtra("PLU");
            String description = intent.getStringExtra("DESCRIPTION");
            String date = intent.getStringExtra("DATE");
            String time = intent.getStringExtra("TIME");
            String type = intent.getStringExtra("TYPE");
            String reason = intent.getStringExtra("REASON");
            String currentStock = intent.getStringExtra("CURRENT_STOCK");
            String stockIn = intent.getStringExtra("STOCK_IN");
            String stockOut = intent.getStringExtra("STOCK_OUT");
            String scaleType = intent.getStringExtra("SCALE_TYPE");
            String currentPrice = intent.getStringExtra("CURRENT_PRICE");
            String tillNo = intent.getStringExtra("TILL_NO");
            String userName = intent.getStringExtra("USER_NAME");
            String logId = intent.getStringExtra("LOG_ID");
            String shortDescription = intent.getStringExtra("SHORTCUT_DESCRIPTION");

            // Set text to TextViews
            textViewId.setText( id);
            textViewBarcode.setText(barcode);
            textViewPLU.setText(plu);
            textViewDescription.setText(description);
            textViewDate.setText(date);
            textViewTime.setText(time);
            textViewType.setText(type);
            textViewReason.setText(reason);
            textViewCurrentStock.setText(currentStock);
            textViewStockIn.setText(stockIn);
            textViewStockOut.setText(stockOut);
            textViewScaleType.setText(scaleType);
            textViewCurrentPrice.setText(currentPrice);
            textViewTillNo.setText(tillNo);
            textViewUserName.setText(userName);
            textViewLogId.setText(logId);
            textViewShortDescription.setText(shortDescription);
        }
    }

    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
