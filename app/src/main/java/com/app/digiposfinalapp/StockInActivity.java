package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class StockInActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockEntryAdapter adapter;
    private List<StockEntry> dataList;
    private Connection connection;

    private String ipAddress, portNumber, databaseName, username, password;
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // Define ExecutorService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_in);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new StockEntryAdapter(this, dataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {
        executorService.execute(() -> {
            if (connection == null) {
                // Establish database connection
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                    connection = DriverManager.getConnection(connectURL);
                } catch (ClassNotFoundException | SQLException e) {
                    Log.e("Error", "Failed to establish database connection: " + e.getMessage());
                    return;
                }
            }

            try {
                String sqlStatement = "SELECT * FROM tbl_Stock_in_out";
                Statement smt = connection.createStatement();
                ResultSet resultSet = smt.executeQuery(sqlStatement);

                // Clear existing data
                List<StockEntry> newList = new ArrayList<>();

                // Populate newList with data from ResultSet
                while (resultSet.next()) {
                    StockEntry entry = new StockEntry(
                            resultSet.getInt("Id"),
                            resultSet.getString("Barcode"),
                            resultSet.getInt("PLU"),
                            resultSet.getString("Description"),
                            resultSet.getString("D_date"),
                            resultSet.getString("D_time"),
                            resultSet.getString("Type"),
                            resultSet.getString("Reason"),
                            resultSet.getInt("Current_stock"),
                            resultSet.getInt("Stock_in"),
                            resultSet.getInt("Stock_out"),
                            resultSet.getString("Scale_type"),
                            resultSet.getDouble("Current_price"),
                            resultSet.getInt("Till_no"),
                            resultSet.getString("User_name"),
                            resultSet.getInt("Logid"),
                            resultSet.getString("Shortcut_description")
                    );
                    newList.add(entry);
                }

                // Update RecyclerView on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    dataList.clear();
                    dataList.addAll(newList);
                    adapter.notifyDataSetChanged();
                });

                connection.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        });
    }

    public void gotoAddStockIn(View view) {
        Intent intent = new Intent(StockInActivity.this, StockInAddActivity.class);
        startActivity(intent); // Start StockInAddActivity with the intent
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gotoback(View view) {
        onBackPressed();
    }
}
