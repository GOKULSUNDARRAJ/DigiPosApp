package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupplierActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SupplierActivity"; // Common log tag

    private RecyclerView recyclerView;
    private SupplierAdapter adapter;
    private List<SupplierModel> supplierList;
    private Connection connection;

    private String ipAddress, portNumber, databaseName, username, password;
    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = findViewById(R.id.recyclerView);
        supplierList = new ArrayList<>();
        adapter = new SupplierAdapter(supplierList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        executorService = Executors.newSingleThreadExecutor();

        loadDataFromDatabase();

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {

        executorService.submit(() -> {
            try {
                // Establish database connection
                if (connection == null) {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                    connection = DriverManager.getConnection(connectURL);
                }

                if (connection != null) {
                    // Query to fetch data from the supplier table
                    String sqlStatement = "SELECT ID, M_Supplier, done FROM M_Supplier";
                    try (Statement smt = connection.createStatement();
                         ResultSet set = smt.executeQuery(sqlStatement)) {
                        // Clear existing data
                        supplierList.clear();

                        // Populate supplierList with data from ResultSet
                        while (set.next()) {
                            SupplierModel supplier = new SupplierModel();
                            supplier.setId(set.getInt("ID"));
                            supplier.setSupplier(set.getString("M_Supplier"));
                            supplier.setDone(set.getInt("done"));

                            // Add supplier to the list
                            supplierList.add(supplier);
                        }

                    } catch (SQLException e) {
                        Log.e(LOG_TAG, "Failed to execute query: " + e.getMessage());
                    } finally {
                        try {
                            if (connection != null && !connection.isClosed()) {
                                connection.close();
                            }
                        } catch (SQLException e) {
                            Log.e(LOG_TAG, "Failed to close database connection: " + e.getMessage());
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Connection object is null");
                }
            } catch (ClassNotFoundException e) {
                Log.e(LOG_TAG, "JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                Log.e(LOG_TAG, "Failed to establish database connection: " + e.getMessage());
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });

    }

    public void gotoAddsupplier(View view) {
        // Implement navigation to add supplier activity
        Intent intent = new Intent(this, SupplierAddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gotoback(View view) {
        onBackPressed();
    }

}
