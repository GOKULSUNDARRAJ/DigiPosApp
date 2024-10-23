package com.app.digiposfinalapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomDialogClassfoeadd extends Dialog {

    private Context context;



    public CustomDialogClassfoeadd(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logout);



        // Clear dialog action
        ImageView clear = findViewById(R.id.claer);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the dialog
            }
        });

        // Update button action
        TextView updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Example of moving to another activity after an update
                dismiss();
            }
        });

        // Edit text input action
        Button editTextDialogInput = findViewById(R.id.editTextDialogInput);
        editTextDialogInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement action for this button
                Intent intent = new Intent(context, ProductReduceAdd.class); // Replace with your actual activity
                context.startActivity(intent);
                dismiss(); // Close the dialog
            }
        });
    }
}
