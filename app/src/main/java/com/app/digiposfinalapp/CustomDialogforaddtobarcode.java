package com.app.digiposfinalapp;


import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CustomDialogforaddtobarcode extends Dialog {
    private static final String TAG = "Addtobarcodedialog";
    private Context context;
    String ipAddress, portNumber, databaseName, username, password;
    String barcode;
    String plu;
    String detail;
    String shop;
    String price;
    Date date;
    int capacity;
    int qty;

    public CustomDialogforaddtobarcode(@NonNull Context context, Context context1, String barcode, String detail, String plu, String shop, String price, Date date, int capacity, int qty) {
        super(context);
        this.context = context1;
        this.barcode = barcode;
        this.detail = detail;
        this.plu = plu;
        this.shop = shop;
        this.price = price;
        this.date = date;
        this.capacity = capacity;
        this.qty = qty;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_addtobarcode);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;


        ImageView clear = findViewById(R.id.claer);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        TextView updateButton=findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });


        TextView save=findViewById(R.id.editTextDialogInput);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InsertBarcodeTask(barcode, plu, detail, shop, price, date, capacity, qty).execute();
            }
        });




    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {

    }


    private class InsertBarcodeTask extends AsyncTask<Void, Void, Boolean> {
        private String barcode;
        private String plu;
        private String detail;
        private String shop;
        private String price;
        private Date date;
        private int capacity;
        private int qty;
        private boolean barcodeExists = false; // To track if barcode exists

        // Constructor to initialize values
        public InsertBarcodeTask(String barcode, String plu, String detail, String shop, String price, Date date, int capacity, int qty) {
            this.barcode = barcode;
            this.plu = plu;
            this.detail = detail;
            this.shop = shop;
            this.price = price;
            this.date = date;
            this.capacity = capacity;
            this.qty = qty;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connection connection = null;
            PreparedStatement checkStatement = null;
            PreparedStatement insertStatement = null;

            try {
                // Create connection string
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                // Check if barcode already exists
                String checkQuery = "SELECT COUNT(*) FROM [STAR_RETAIL].[dbo].[tblBarcode] WHERE Barcode = ?";
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, barcode);
                java.sql.ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    barcodeExists = true; // Barcode already exists
                    return false;
                }

                // Format the date to "dd/MM/yyyy"
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(date);

                // If barcode does not exist, insert it into the table
                String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[tblBarcode] ([PLU], [Barcode], [Detail], [Shop], [Price], [dtDate], [Capacity], [Qty]) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, plu); // PLU
                insertStatement.setString(2, barcode); // Barcode
                insertStatement.setString(3, detail); // Detail
                insertStatement.setString(4, shop); // Shop
                insertStatement.setString(5, price); // Price with £ symbol
                insertStatement.setString(6, formattedDate); // Formatted Date in "dd/MM/yyyy"
                insertStatement.setInt(7, capacity); // Capacity
                insertStatement.setInt(8, qty); // Qty

                // Execute the insert statement
                insertStatement.executeUpdate();
                Log.d(TAG, "Barcode inserted successfully");

                dismiss();

                return true;

            } catch (SQLException e) {
                Log.e(TAG, "Database connection error: " + e.getMessage());
            } finally {
                // Close resources
                try {
                    if (checkStatement != null) {
                        checkStatement.close();
                    }
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Error closing resources: " + e.getMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (barcodeExists) {
                Toast.makeText(getContext(), "Barcode already exists", Toast.LENGTH_SHORT).show();
            } else if (isSuccess) {
                BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
                Toast.makeText(getContext(), "Barcode inserted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to insert barcode", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
