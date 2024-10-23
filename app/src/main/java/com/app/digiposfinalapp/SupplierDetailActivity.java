package com.app.digiposfinalapp;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SupplierDetailActivity extends AppCompatActivity {

    TextView idTextView, nameTextView, doneTextView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_detail);
        Intent intent = getIntent();


        String supplierId = intent.getStringExtra("supplier_id");
        String supplierName = intent.getStringExtra("supplier_name");
        String supplierDone = intent.getStringExtra("supplier_done");

        // Find TextViews
        idTextView = findViewById(R.id.itemText3410);
        nameTextView = findViewById(R.id.supplierTextView);
        doneTextView = findViewById(R.id.itemText342980);

        // Set data to TextViews
        idTextView.setText(":"+"\t"+supplierId);
        nameTextView.setText(":"+"\t"+supplierName);
        doneTextView.setText(":"+"\t"+supplierDone);
    }

    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}