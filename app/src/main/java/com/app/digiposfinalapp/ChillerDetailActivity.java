package com.app.digiposfinalapp;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChillerDetailActivity extends AppCompatActivity {

    TextView textViewId, textViewChillerName, textViewTemperature, textViewDate, textViewTime, textViewChangeId, textViewDay;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chiller_detail);
        Intent intent = getIntent();



        // Get data from the Intent
        String id = intent.getStringExtra("id"); // Replace 0 with a default value if needed
        String chillerName = intent.getStringExtra("chiller_name");
        String temperature = intent.getStringExtra("temperature"); // Replace 0.0 with a default value if needed
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String changeId = intent.getStringExtra("changeid"); // Replace 0 with a default value if needed
        String day = intent.getStringExtra("day");


        // Find TextViews in the layout
        textViewId = findViewById(R.id.ChillerID);
        textViewChillerName = findViewById(R.id.ChillerName);
        textViewTemperature = findViewById(R.id.Temperature);
        textViewDate = findViewById(R.id.date);
        textViewTime = findViewById(R.id.time);
        textViewChangeId = findViewById(R.id.chillerchangeid);
        textViewDay = findViewById(R.id.day);

        // Set data to TextViews
        textViewId.setText(":"+"\t"+String.valueOf(id));
        textViewChillerName.setText(":"+"\t"+chillerName);
        textViewTemperature.setText(":"+"\t"+String.valueOf(temperature));
        textViewDate.setText(":"+"\t"+date);
        textViewTime.setText(":"+"\t"+time);
        textViewChangeId.setText(":"+"\t"+String.valueOf(changeId));
        textViewDay.setText(":"+"\t"+day);
    }

    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}




