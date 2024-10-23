package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class PriceChangeAddActivity extends AppCompatActivity {

    // Declare EditText fields
    EditText pluEditText;
    EditText barcodeEditText;
    EditText detailEditText;
    EditText shopEditText;
    EditText priceEditText;
    EditText dateEditText;
    EditText capacityEditText;
    EditText qtyEditText;

    // Declare Connection object
    Connection connection;

    String ipAddress, portNumber, databaseName, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_price_change_add);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize EditText fields
        pluEditText = findViewById(R.id.autocompleteTextView2);
        barcodeEditText = findViewById(R.id.autocompleteTextView24);
        detailEditText = findViewById(R.id.autocompleteTextView246);
        shopEditText = findViewById(R.id.autocompleteTextView24676);
        priceEditText = findViewById(R.id.autocompleteTextView624676);
        dateEditText = findViewById(R.id.dayedt);
        capacityEditText = findViewById(R.id.doneedt);
        qtyEditText = findViewById(R.id.autocompleteTextView);

        Button addButton = findViewById(R.id.Add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pluEditText.getText().toString().isEmpty() ||
                        barcodeEditText.getText().toString().isEmpty() ||
                        detailEditText.getText().toString().isEmpty() ||
                        shopEditText.getText().toString().isEmpty() ||
                        priceEditText.getText().toString().isEmpty() ||
                        dateEditText.getText().toString().isEmpty() ||
                        capacityEditText.getText().toString().isEmpty() ||
                        qtyEditText.getText().toString().isEmpty()) {
                    Toast.makeText(PriceChangeAddActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    addNewEntry();
                }
            }
        });
    }

    private void addNewEntry() {
        // Retrieve data from EditText fields
        String plu = pluEditText.getText().toString();
        String barcode = barcodeEditText.getText().toString();
        String detail = detailEditText.getText().toString();
        String shop = shopEditText.getText().toString();
        String price = priceEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String capacity = capacityEditText.getText().toString();
        String qty = qtyEditText.getText().toString();

        // Execute the AsyncTask
        new AddEntryTask(plu, barcode, detail, shop, price, date, capacity, qty).execute();
    }

    private class AddEntryTask extends AsyncTask<Void, Void, Boolean> {
        private String plu, barcode, detail, shop, price, date, capacity, qty;

        public AddEntryTask(String plu, String barcode, String detail, String shop, String price, String date, String capacity, String qty) {
            this.plu = plu;
            this.barcode = barcode;
            this.detail = detail;
            this.shop = shop;
            this.price = price;
            this.date = date;
            this.capacity = capacity;
            this.qty = qty;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = null;
            try {
                // Establish database connection
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectURL);

                // Create SQL insert statement
                String insertStatement = "INSERT INTO tblBarcode (PLU, Barcode, Detail, Shop, Price, dtDate, Capacity, Qty) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);

                // Set values for placeholders
                preparedStatement.setString(1, plu);
                preparedStatement.setString(2, barcode);
                preparedStatement.setString(3, detail);
                preparedStatement.setString(4, shop);
                preparedStatement.setString(5, price);
                preparedStatement.setString(6, date);
                preparedStatement.setString(7, capacity);
                preparedStatement.setString(8, qty);

                // Execute the insert statement
                int rowsInserted = preparedStatement.executeUpdate();

                // Return true if rows were inserted, false otherwise
                return rowsInserted > 0;
            } catch (ClassNotFoundException | SQLException e) {
                Log.e("Error", "SQL Exception during insert operation.", e);
                return false;
            } finally {
                // Close the database connection
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e("Error", "Failed to close database connection.", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(PriceChangeAddActivity.this, "Entry added successfully.", Toast.LENGTH_SHORT).show();
                // Optionally, you can redirect to another activity or clear the fields here
            } else {
                Toast.makeText(PriceChangeAddActivity.this, "Failed to add entry.", Toast.LENGTH_SHORT).show();
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
