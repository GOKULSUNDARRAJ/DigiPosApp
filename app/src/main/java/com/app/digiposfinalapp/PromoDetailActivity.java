package com.app.digiposfinalapp;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PromoDetailActivity extends AppCompatActivity {

    TextView promotionIdTextView, promoIdTextView, descriptionTextView, receiptTextView, ruleNoTextView, ruleValueTextView, typeTextView, typeValueTextView, startTextView, endTextView, itemCountTextView, pluTextView, doneTextView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promo_detail);
        Intent intent = getIntent();


        String promotionId = intent.getStringExtra("promotion_id" );
        String promoId = intent.getStringExtra("promo_id" );
        String description = intent.getStringExtra("description");
        String receipt = intent.getStringExtra("Recipt");
        String ruleNo = intent.getStringExtra("Ruleno");
        String ruleValue = intent.getStringExtra("RuleValue" );
        String type = intent.getStringExtra("Type");
        String typeValue = intent.getStringExtra("Typevale" );
        String start = intent.getStringExtra("Start");
        String end = intent.getStringExtra("End");
        String itemCount = intent.getStringExtra("itemvalue" );
        String plu = intent.getStringExtra("Plue");
        String done = intent.getStringExtra("done" );

        // Set the retrieved data into TextViews
        promotionIdTextView = findViewById(R.id.promotionIdTextView);
        promoIdTextView = findViewById(R.id.promoIdTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        receiptTextView = findViewById(R.id.receiptTextView);
        ruleNoTextView = findViewById(R.id.ruleNoTextView);
        ruleValueTextView = findViewById(R.id.txt);
        typeTextView = findViewById(R.id.typeTextView);
        typeValueTextView = findViewById(R.id.typeValueTextView);
        startTextView = findViewById(R.id.startTextView);
        endTextView = findViewById(R.id.endTextView);
        itemCountTextView = findViewById(R.id.itemCountTextView);
        pluTextView = findViewById(R.id.pluTextView);
        doneTextView = findViewById(R.id.doneTextView);

        promotionIdTextView.setText(":"+"\t"+String.valueOf(promotionId));
        promoIdTextView.setText(":"+"\t"+String.valueOf(promoId));
        descriptionTextView.setText(":"+"\t"+description);
        receiptTextView.setText(":"+"\t"+receipt);
        ruleNoTextView.setText(":"+"\t"+ruleNo);
        ruleValueTextView.setText(":"+"\t"+String.valueOf(ruleValue));
        typeTextView.setText(":"+"\t"+type);
        typeValueTextView.setText(":"+"\t"+String.valueOf(typeValue));
        startTextView.setText(":"+"\t"+start);
        endTextView.setText(":"+"\t"+end);
        itemCountTextView.setText(":"+"\t"+String.valueOf(itemCount));
        pluTextView.setText(":"+"\t"+plu);
        doneTextView.setText(":"+"\t"+String.valueOf(done));
    }

    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}