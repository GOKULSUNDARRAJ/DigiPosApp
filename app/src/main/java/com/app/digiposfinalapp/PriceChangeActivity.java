package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PriceChangeActivity extends AppCompatActivity {

    private static final String TAG = "PriceChangeActivity";

    RecyclerView recyclerView;
    BarcodeAdapter adapter;
    List<BarcodeDataModel> dataList;
    Connection connection;

    String ipAddress, portNumber, databaseName, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_price);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new BarcodeAdapter(dataList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Start database operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                establishConnectionAndFetchData();
            }
        }).start();
    }

    private void establishConnectionAndFetchData() {
        if (connection == null) {
            // Establish database connection
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                Log.d(TAG, "Connecting to database with URL: " + connectURL);
                connection = DriverManager.getConnection(connectURL);
                Log.d(TAG, "Database connection established.");
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Database driver class not found: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(PriceChangeActivity.this, "Database driver not found.", Toast.LENGTH_SHORT).show());
                return;
            } catch (SQLException e) {
                Log.e(TAG, "Failed to establish database connection: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(PriceChangeActivity.this, "Failed to connect to database.", Toast.LENGTH_SHORT).show());
                return;
            }
        }
        try {
            String sqlStatement = "SELECT ID, PLU, Barcode, Detail, Shop, Price, dtDate, Capacity, Qty FROM tblBarcode";
            Log.d(TAG, "Executing SQL statement: " + sqlStatement);
            Statement smt = connection.createStatement();
            ResultSet set = smt.executeQuery(sqlStatement);

            // Clear existing data
            runOnUiThread(() -> {
                dataList.clear();
                adapter.notifyDataSetChanged();
            });

            // Populate dataList with data from ResultSet
            while (set.next()) {
                BarcodeDataModel data = new BarcodeDataModel();
                try {
                    data.setDepartmentId(set.getInt("ID")); // Ensure this column is an integer
                } catch (SQLException e) {
                    Log.e(TAG, "Error converting ID to integer: " + e.getMessage());
                    data.setDepartmentId(-1); // Set a default value or handle appropriately
                }
                data.setPLU(set.getString("PLU"));
                data.setBarcode(set.getString("Barcode"));
                data.setDetail(set.getString("Detail"));
                data.setShop(set.getString("Shop"));
                data.setPrice(set.getString("Price"));
                data.setDtDate(set.getString("dtDate"));
                data.setCapacity(set.getString("Capacity"));
                try {
                    data.setQty(set.getInt("Qty")); // Ensure this column is an integer
                } catch (SQLException e) {
                    Log.e(TAG, "Error converting Qty to integer: " + e.getMessage());
                    data.setQty(0); // Set a default value or handle appropriately
                }
                // Set other data accordingly
                Log.d(TAG, "Fetched data: " + data.toString());
                runOnUiThread(() -> {
                    dataList.add(data);
                    adapter.notifyDataSetChanged();
                });
            }

            connection.close();
            Log.d(TAG, "Database connection closed.");

        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception during query execution: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(PriceChangeActivity.this, "Error querying database.", Toast.LENGTH_SHORT).show());
        }
    }

    public void gotoAddprice(View view) {
        Log.d(TAG, "Navigating to PriceChangeAddActivity.");
        Intent intent = new Intent(PriceChangeActivity.this, PriceChangeAddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed.");
        super.onBackPressed();
    }

    public void goback(View view) {
        Log.d(TAG, "Back button clicked.");
        super.onBackPressed();
    }
}
