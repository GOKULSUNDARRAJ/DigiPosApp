package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class StockUpdateActivity extends AppCompatActivity {

    private static final String TAG = "StockUpdateActivity";

    RecyclerView recyclerView;
    StockUpdateAdapter adapter;
    List<StockUpdateModel> dataList;

    String ipAddress, portNumber, databaseName, username, password;
    Connection connection;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_update);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new StockUpdateAdapter(dataList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Execute the database task in a background thread
        new DatabaseTask().execute();
    }

    private class DatabaseTask extends AsyncTask<Void, Void, List<StockUpdateModel>> {

        @Override
        protected List<StockUpdateModel> doInBackground(Void... voids) {
            List<StockUpdateModel> resultList = new ArrayList<>();
            Connection conn = null;
            Statement smt = null;
            ResultSet set = null;

            try {
                // Establish database connection
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                conn = DriverManager.getConnection(connectURL);

                // Execute SQL query
                String sqlStatement = "SELECT ID, Barcode, PLU, Date, Time, Current_qty, Update_Qty, Done_By FROM tbl_Stock_Update";
                smt = conn.createStatement();
                set = smt.executeQuery(sqlStatement);

                // Populate resultList with data from ResultSet
                while (set.next()) {
                    StockUpdateModel data = new StockUpdateModel(
                            set.getInt("ID"),
                            set.getString("Barcode"),
                            set.getInt("PLU"),
                            set.getString("Date"),
                            set.getString("Time"),
                            set.getInt("Current_qty"),
                            set.getInt("Update_Qty"),
                            set.getInt("Done_By")
                    );
                    resultList.add(data);
                }
            } catch (ClassNotFoundException | SQLException e) {
                Log.e(TAG, "Database error: " + e.getMessage());
            } finally {
                // Close resources
                try {
                    if (set != null) set.close();
                    if (smt != null) smt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    Log.e(TAG, "Failed to close resources: " + e.getMessage());
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<StockUpdateModel> resultList) {
            dataList.clear();
            dataList.addAll(resultList);
            adapter.notifyDataSetChanged(); // Notify adapter of data change
        }
    }

    public void gotoAddStockUpdate(View view) {
        Intent intent = new Intent(StockUpdateActivity.this, StockAddActivity.class);
        startActivity(intent); // Start StockAddActivity
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gotoback(View view) {
        onBackPressed();
    }
}
