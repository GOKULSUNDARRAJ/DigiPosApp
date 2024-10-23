package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockInAddActivity extends AppCompatActivity {

    private static final String TAG = "StockInAddActivity"; // Added TAG for logging

    private EditText barcodeEditText, pluEditText, descriptionEditText, dateEditText, timeEditText,
            typeEditText, reasonEditText, currentStockEditText, stockInEditText, stockOutEditText,
            scaleTypeEditText, currentPriceEditText, tillNoEditText, usernameedt, logIdEditText, shortDescriptionEditText;
    private Button addButton;
    private Connection connection;

    private String ipAddress, portNumber, databaseName, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_in_add);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        initializeViews();

        addButton.setOnClickListener(view -> {
            if (validateFields()) {
                insertRowIntoStockInOutTable();
            } else {
                Toast.makeText(StockInAddActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        barcodeEditText = findViewById(R.id.autocompleteTextView2);
        pluEditText = findViewById(R.id.autocompleteTextView24);
        descriptionEditText = findViewById(R.id.desedt);
        dateEditText = findViewById(R.id.autocompleteTextView246);
        timeEditText = findViewById(R.id.autocompleteTextView24676);
        typeEditText = findViewById(R.id.autocompleteTextView624676);
        reasonEditText = findViewById(R.id.dayedt);
        currentStockEditText = findViewById(R.id.doneedt);
        stockInEditText = findViewById(R.id.stockinedt);
        stockOutEditText = findViewById(R.id.scaltypeedt);
        scaleTypeEditText = findViewById(R.id.currentpriceedt);
        currentPriceEditText = findViewById(R.id.tillnoedt);
        tillNoEditText = findViewById(R.id.logidedtn);
        usernameedt = findViewById(R.id.usernameedt);
        logIdEditText = findViewById(R.id.tillnoedt);
        shortDescriptionEditText = findViewById(R.id.shortdescriptionedt);
        addButton = findViewById(R.id.Add);
    }

    private boolean validateFields() {
        return !barcodeEditText.getText().toString().isEmpty() &&
                !pluEditText.getText().toString().isEmpty() &&
                !descriptionEditText.getText().toString().isEmpty() &&
                !dateEditText.getText().toString().isEmpty() &&
                !timeEditText.getText().toString().isEmpty() &&
                !typeEditText.getText().toString().isEmpty() &&
                !reasonEditText.getText().toString().isEmpty() &&
                !currentStockEditText.getText().toString().isEmpty() &&
                !stockInEditText.getText().toString().isEmpty() &&
                !stockOutEditText.getText().toString().isEmpty() &&
                !scaleTypeEditText.getText().toString().isEmpty() &&
                !currentPriceEditText.getText().toString().isEmpty() &&
                !tillNoEditText.getText().toString().isEmpty() &&
                !usernameedt.getText().toString().isEmpty() &&
                !logIdEditText.getText().toString().isEmpty() &&
                !shortDescriptionEditText.getText().toString().isEmpty();
    }

    private void insertRowIntoStockInOutTable() {
        new Thread(() -> {
            try {
                // Establish database connection
                if (connection == null) {
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                        connection = DriverManager.getConnection(connectURL);
                    } catch (ClassNotFoundException | SQLException e) {
                        Log.e(TAG, "Failed to establish database connection: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(StockInAddActivity.this, "Database connection failed", Toast.LENGTH_SHORT).show());
                        return;
                    }
                }

                if (connection != null) {
                    // Create SQL insert statement
                    String insertStatement = "INSERT INTO [STAR_RETAIL].[dbo].[tbl_Stock_in_out] " +
                            "(Barcode, PLU, Description, D_date, D_time, Type, Reason, Current_stock, " +
                            "Stock_in, Stock_out, Scale_type, Current_price, Till_no, User_name, Logid, SHORTCUT_DESCRIPTION) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertStatement)) {
                        // Set values for placeholders
                        preparedStatement.setString(1, barcodeEditText.getText().toString());
                        preparedStatement.setInt(2, Integer.parseInt(pluEditText.getText().toString()));
                        preparedStatement.setString(3, descriptionEditText.getText().toString());
                        preparedStatement.setString(4, dateEditText.getText().toString());
                        preparedStatement.setString(5, timeEditText.getText().toString());
                        preparedStatement.setString(6, typeEditText.getText().toString());
                        preparedStatement.setString(7, reasonEditText.getText().toString());
                        preparedStatement.setInt(8, Integer.parseInt(currentStockEditText.getText().toString()));
                        preparedStatement.setInt(9, Integer.parseInt(stockInEditText.getText().toString()));
                        preparedStatement.setInt(10, Integer.parseInt(stockOutEditText.getText().toString()));
                        preparedStatement.setString(11, scaleTypeEditText.getText().toString());
                        preparedStatement.setDouble(12, Double.parseDouble(currentPriceEditText.getText().toString()));
                        preparedStatement.setInt(13, Integer.parseInt(tillNoEditText.getText().toString()));
                        preparedStatement.setString(14, usernameedt.getText().toString());
                        preparedStatement.setInt(15, Integer.parseInt(logIdEditText.getText().toString()));
                        preparedStatement.setString(16, shortDescriptionEditText.getText().toString());

                        // Execute the insert statement
                        int rowsInserted = preparedStatement.executeUpdate();

                        runOnUiThread(() -> {
                            if (rowsInserted > 0) {
                                Toast.makeText(StockInAddActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                finish(); // Close activity and return to previous one
                            } else {
                                Toast.makeText(StockInAddActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception during insert operation: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(StockInAddActivity.this, "Error occurred", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Failed to close database connection: " + e.getMessage());
                }
            }
        }).start();
    }

    public void gobackadd(View view) {
       onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
