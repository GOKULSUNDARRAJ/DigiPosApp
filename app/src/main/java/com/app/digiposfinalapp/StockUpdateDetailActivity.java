package com.app.digiposfinalapp;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class StockUpdateDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_update_detail);



        // Retrieve data from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("id");
            String barcode = extras.getString("barcode");
            String plu = extras.getString("plu");
            String date = extras.getString("date");
            String time = extras.getString("time");
            String currentQty = extras.getString("currentQty");
            String updateQty = extras.getString("updateQty");
            String doneBy = extras.getString("doneBy");

            // Set the retrieved data to TextViews
            TextView idTextView = findViewById(R.id.textViewId);
            idTextView.setText(":"+"\t"+id);


            TextView barcodeTextView = findViewById(R.id.textViewBarcode);
            barcodeTextView.setText(":"+"\t"+barcode);

            TextView pluTextView = findViewById(R.id.textViewPLU);
            pluTextView.setText(":"+"\t"+plu);

            TextView dateTextView = findViewById(R.id.date4);
            dateTextView.setText(":"+"\t"+date);

            TextView timeTextView = findViewById(R.id.time);
            timeTextView.setText(":"+"\t"+time);

            TextView currentQtyTextView = findViewById(R.id.textViewCurrentQty);
            currentQtyTextView.setText(":"+"\t"+currentQty);

            TextView updateQtyTextView = findViewById(R.id.textViewUpdateQty);
            updateQtyTextView.setText(":"+"\t"+updateQty);

            TextView doneByTextView = findViewById(R.id.textViewDoneBy);
            doneByTextView.setText(":"+"\t"+doneBy);
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
