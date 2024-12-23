package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FetchSupplierData extends AsyncTask<Void, Void, List<SupplierSpinner>> {

    private static final String TAG = "FetchSupplierData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String ipAddress, portNumber, databaseName, username, password;

    // Constructor to pass context and spinner reference
    public FetchSupplierData(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected List<SupplierSpinner> doInBackground(Void... voids) {
        List<SupplierSpinner> supplierList = new ArrayList<>();
        Log.d(TAG, "Fetching supplier data...");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Connection connection = null; // Initialize connection to null
        try {
            // jTDS connection string
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            // SQL query to fetch data from tbl_Supplier
            String query = "SELECT [ID], [SupplierName], [Address1], [ContactName] FROM [dbo].[tbl_Supplier]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SupplierSpinner supplier = new SupplierSpinner();
                supplier.setId(resultSet.getInt("ID"));
                supplier.setSupplier(resultSet.getString("SupplierName"));
                supplier.setAddress(resultSet.getString("Address1"));
                supplier.setContact(resultSet.getString("ContactName"));


                supplierList.add(supplier);
                Log.d(TAG, "Supplier added: " + supplier.getSupplier());
            }

            resultSet.close();
            statement.close();
            Log.d(TAG, "Data fetch complete.");

        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage());
        } finally {
            // Close the connection in the finally block to avoid resource leaks
            if (connection != null) {
                try {
                    connection.close();
                    Log.d(TAG, "Connection closed.");
                } catch (SQLException e) {
                    Log.e(TAG, "Failed to close connection: " + e.getMessage());
                }
            }
        }

        return supplierList;
    }

    @Override
    protected void onPostExecute(List<SupplierSpinner> supplierList) {
        super.onPostExecute(supplierList);

        if (supplierList != null && !supplierList.isEmpty()) {
            SupplierSpinnerAdapter adapter = new SupplierSpinnerAdapter(context, supplierList);
            spinner.setAdapter(adapter);
            Log.d(TAG, "Adapter set with " + supplierList.size() + " suppliers.");
        } else {
            Log.e(TAG, "No suppliers fetched or list is empty.");
        }
    }
}
