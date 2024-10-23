package com.app.digiposfinalapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DepartmentDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_department_details);

        Intent intent = getIntent();


        String department = intent.getStringExtra("DEPARTMENT");
        String age = intent.getStringExtra("AGE");
        String sale = intent.getStringExtra("SALE");
        String weight = intent.getStringExtra("WEIGHT");
        String points = intent.getStringExtra("POINTS");
        String id = intent.getStringExtra("ID");

        // Initialize TextViews to display the data
        TextView departmentTextView = findViewById(R.id.itemText3410);
        TextView ageTextView = findViewById(R.id.itemText3420);
        TextView saleTextView = findViewById(R.id.itemText342980);
        TextView weightTextView = findViewById(R.id.itemText342900);
        TextView pointsTextView = findViewById(R.id.itemText3429000);
        TextView idtxtview=findViewById(R.id.itemText34109);

        // Set the text of TextViews with the received data
        departmentTextView.setText(":"+"\t"+department);
        ageTextView.setText(":"+"\t"+age);
        saleTextView.setText(":"+"\t"+sale);
        weightTextView.setText(":"+"\t"+weight);
        pointsTextView.setText(":"+"\t"+points);
        idtxtview.setText(":"+"\t"+id);


    }

    public void goback(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}