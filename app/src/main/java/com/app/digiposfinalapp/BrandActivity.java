package com.app.digiposfinalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BrandActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BrandActivity"; // Define a common log tag


    RecyclerView recyclerView;
    BrandAdapter adapter;
    List<BrandModel> brandList;
    Connection connection;
    String ipAddress, portNumber, databaseName, username, password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_brand);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");

        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = findViewById(R.id.recyclerView);
        brandList = new ArrayList<>();
        adapter = new BrandAdapter(brandList);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Execute database operations in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                fetchBrandData();
            }
        }).start();
    }

    private void fetchBrandData() {
        Connection localConnection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish database connection
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                    ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
            localConnection = DriverManager.getConnection(connectURL);

            // Query to fetch data from the tbl_Brand table
            String sqlStatement = "SELECT ID, Brand, Done FROM tbl_Brand";
            statement = localConnection.createStatement();
            resultSet = statement.executeQuery(sqlStatement);

            // Clear existing data
            brandList.clear();

            // Populate brandList with data from ResultSet
            while (resultSet.next()) {
                BrandModel brand = new BrandModel();
                brand.setId(resultSet.getInt("ID"));
                brand.setBrandName(resultSet.getString("Brand"));
                brand.setDone(resultSet.getInt("Done"));
                // Add brand to the list
                brandList.add(brand);
            }

            // Notify adapter of data change on the main thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged(); // Notify adapter of data change
                }
            });

        } catch (ClassNotFoundException | SQLException e) {
            Log.e(LOG_TAG, "Error fetching brand data: " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BrandActivity.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (localConnection != null) localConnection.close();
            } catch (SQLException e) {
                Log.e(LOG_TAG, "Error closing resources: " + e.getMessage());
            }
        }
    }

    public void gotoAddbrand(View view) {
        Intent intent = new Intent(BrandActivity.this, BrandAddActivity.class);
        startActivity(intent); // Start BrandAddActivity with the intent
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gotoback(View view) {
        onBackPressed();
    }

}
