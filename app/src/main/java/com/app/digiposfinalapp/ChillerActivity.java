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

public class ChillerActivity extends AppCompatActivity {

    private static final String TAG = "ChillerActivity";
    RecyclerView recyclerView;
    ChillerReportAdapter adapter;
    List<ChillerReportItem> dataList;

    Connection connection;
    String ipAddress, portNumber, databaseName, username, password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chiller);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Log.d(TAG, "IP Address: " + ipAddress);
        Log.d(TAG, "Port Number: " + portNumber);
        Log.d(TAG, "Database Name: " + databaseName);
        Log.d(TAG, "Username: " + username);

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new ChillerReportAdapter(dataList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Execute the database operation in an AsyncTask
        new DatabaseTask().execute();
    }

    private class DatabaseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectURL);
                Log.d(TAG, "Database connection established successfully");

                String sqlStatement = "SELECT ID, Chiller_Name, Temperature, Date, Time, Changeid, Day FROM Chiller_Report";
                Statement smt = connection.createStatement();
                ResultSet set = smt.executeQuery(sqlStatement);

                // Clear existing data
                dataList.clear();

                // Populate dataList with data from ResultSet
                while (set.next()) {
                    ChillerReportItem item = new ChillerReportItem();
                    item.setId(Integer.parseInt(set.getString("ID")));
                    item.setChillerName(set.getString("Chiller_Name"));
                    item.setTemperature(Double.parseDouble(set.getString("Temperature")));
                    item.setDate(set.getString("Date"));
                    item.setTime(set.getString("Time"));
                    item.setChangeId(Integer.parseInt(set.getString("Changeid")));
                    item.setDay(set.getString("Day"));

                    // Add item to the list
                    dataList.add(item);

                    Log.d(TAG, "Added item: ID=" + item.getId() + ", Chiller Name=" + item.getChillerName());
                }
                return null;
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "JDBC Driver not found: " + e.toString());
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.toString());
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "SQL Exception while closing connection: " + e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged(); // Notify adapter of data change
            Log.d(TAG, "Adapter notified of data change");
        }
    }

    public void gotoAdddeptchiller(View view) {
        Intent intent = new Intent(ChillerActivity.this, ChillerAddActivity.class);
        startActivity(intent); // Start ChillerAddActivity with the intent
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gotoback(View view) {
        onBackPressed();
    }
}
