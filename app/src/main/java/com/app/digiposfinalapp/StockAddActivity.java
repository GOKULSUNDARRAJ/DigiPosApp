package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockAddActivity extends AppCompatActivity {

    private EditText barcodeEditText, pluEditText, dateEditText, timeEditText, currentQtyEditText, updateQtyEditText, doneByEditText;
    private Button addButton;
    private Connection connection;

    private String ipAddress, portNumber, databaseName, username, password;
    private static final String TAG = "StockAddActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_add);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize views
        barcodeEditText = findViewById(R.id.autocompleteTextView2);
        pluEditText = findViewById(R.id.autocompleteTextView24);
        dateEditText = findViewById(R.id.autocompleteTextView246);
        timeEditText = findViewById(R.id.autocompleteTextView24676);
        currentQtyEditText = findViewById(R.id.autocompleteTextView624676);
        updateQtyEditText = findViewById(R.id.dayedt);
        doneByEditText = findViewById(R.id.doneedt);
        addButton = findViewById(R.id.Add);

        // Check if the addButton is properly initialized
        if (addButton == null) {
            Log.e(TAG, "Button with ID 'Add' not found in layout.");
            Toast.makeText(this, "Button 'Add' not found", Toast.LENGTH_LONG).show();
            return; // Exit on initialization failure
        }

        // Set OnClickListener for the "Add" button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAnyFieldEmpty()) {
                    Log.w(TAG, "Field cannot be empty.");
                    Toast.makeText(StockAddActivity.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Inserting data into database.");
                    new DatabaseTask(
                            barcodeEditText.getText().toString(),
                            Integer.parseInt(pluEditText.getText().toString()),
                            dateEditText.getText().toString(),
                            timeEditText.getText().toString(),
                            Integer.parseInt(currentQtyEditText.getText().toString()),
                            Integer.parseInt(updateQtyEditText.getText().toString()),
                            Integer.parseInt(doneByEditText.getText().toString())
                    ).execute();
                }
            }
        });
    }

    // Method to check if any EditText is empty
    private boolean isAnyFieldEmpty() {
        return barcodeEditText.getText().toString().isEmpty() ||
                pluEditText.getText().toString().isEmpty() ||
                dateEditText.getText().toString().isEmpty() ||
                timeEditText.getText().toString().isEmpty() ||
                currentQtyEditText.getText().toString().isEmpty() ||
                updateQtyEditText.getText().toString().isEmpty() ||
                doneByEditText.getText().toString().isEmpty();
    }

    // AsyncTask to perform database operations in the background
    private class DatabaseTask extends AsyncTask<Void, Void, Boolean> {

        private String barcode;
        private int plu;
        private String date;
        private String time;
        private int currentQty;
        private int updateQty;
        private int doneBy;

        DatabaseTask(String barcode, int plu, String date, String time, int currentQty, int updateQty, int doneBy) {
            this.barcode = barcode;
            this.plu = plu;
            this.date = date;
            this.time = time;
            this.currentQty = currentQty;
            this.updateQty = updateQty;
            this.doneBy = doneBy;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (connection == null) {
                    // Establish database connection
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                        connection = DriverManager.getConnection(connectURL);
                        Log.i(TAG, "Database connection established.");
                    } catch (ClassNotFoundException | SQLException e) {
                        Log.e(TAG, "Failed to establish database connection", e);
                        return false;
                    }
                }

                if (connection != null) {
                    // Create SQL insert statement
                    String insertStatement = "INSERT INTO tbl_Stock_Update (barcode, plu, date, time, Current_qty, Update_Qty, Done_By) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertStatement)) {
                        // Set values for placeholders
                        preparedStatement.setString(1, barcode);
                        preparedStatement.setInt(2, plu);
                        preparedStatement.setString(3, date);
                        preparedStatement.setString(4, time);
                        preparedStatement.setInt(5, currentQty);
                        preparedStatement.setInt(6, updateQty);
                        preparedStatement.setInt(7, doneBy);

                        // Execute the insert statement
                        int rowsInserted = preparedStatement.executeUpdate();
                        Log.i(TAG, "Rows inserted: " + rowsInserted);
                        return rowsInserted > 0;
                    } catch (SQLException e) {
                        Log.e(TAG, "Failed to execute insert statement", e);
                        return false;
                    } finally {
                        // Close the connection
                        try {
                            if (connection != null && !connection.isClosed()) {
                                connection.close();
                                Log.i(TAG, "Database connection closed.");
                            }
                        } catch (SQLException e) {
                            Log.e(TAG, "Failed to close database connection", e);
                        }
                    }
                } else {
                    Log.w(TAG, "Database connection is null");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error occurred", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(StockAddActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(StockAddActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void gobackadd(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
