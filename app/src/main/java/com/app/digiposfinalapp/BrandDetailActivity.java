package com.app.digiposfinalapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class BrandDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_brand_detail);

        Intent intent = getIntent();

        // Find TextViews by their IDs
        TextView brandIdTextView = findViewById(R.id.id);
        TextView brandNameTextView = findViewById(R.id.barcode);
        TextView brandDoneTextView = findViewById(R.id.textViewPLU);

        // Retrieve the data passed through the intent
        String brandId = intent.getStringExtra("brand_id");
        String brandName = intent.getStringExtra("brand_name");
        String brandDone = intent.getStringExtra("brand_done");

        // Set the retrieved data to the TextViews
        brandIdTextView.setText(":"+"\t"+brandId);
        brandNameTextView.setText(":"+"\t"+brandName);
        brandDoneTextView.setText(":"+"\t"+brandDone);


    }


    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
