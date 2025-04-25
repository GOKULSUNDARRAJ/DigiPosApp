package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FetchSupplierDataEdit extends AsyncTask<Void, Void, List<SupplierSpinner>> {

    private static final String TAG = "FetchSupplierDataEdit"; // Tag for logging
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;  // AutoCompleteTextView reference to populate
    private String supplierName;  // To filter by supplier name
    private String ipAddress, portNumber, databaseName, username, password;

    // Modified constructor to accept the AutoCompleteTextView and supplier name
    public FetchSupplierDataEdit(Context context, AutoCompleteTextView autoCompleteTextView, String supplierName) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
        this.supplierName = supplierName;  // Get the supplier name from arguments
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
            Log.d(TAG, "Connecting to database at " + ipAddress + ":" + portNumber + "/" + databaseName + "...");
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

        if (supplierList == null || supplierList.isEmpty()) {
            Log.e(TAG, "No suppliers fetched or list is empty.");
            return;
        }

        SupplierSpinner selectedSupplier = null;

        // Find the selected supplier based on the supplierName
//        for (int i = 0; i < supplierList.size(); i++) {
//            SupplierSpinner supplier = supplierList.get(i);
//            if (supplier.getSupplier().equalsIgnoreCase(supplierName)) {
//                selectedSupplier = supplierList.remove(i);
//                break;
//            }
//        }

        // Find the selected supplier based on the supplierName
        for (int i = 0; i < supplierList.size(); i++) {
            SupplierSpinner supplier = supplierList.get(i);
            if (supplierName != null && supplierName.equalsIgnoreCase(supplier.getSupplier())) {
                selectedSupplier = supplierList.remove(i);
                break;
            }
        }



        // Add the selected supplier at the top if it was found
        if (selectedSupplier != null) {
            supplierList.add(0, selectedSupplier);

            // Save selected supplier to SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedSupplierID", String.valueOf(selectedSupplier.getId()));
            editor.putString("selectedSupplierName", selectedSupplier.getSupplier());
            editor.apply();
        }

        Log.d(TAG, "Setting adapter with suppliers:");
        for (SupplierSpinner supplier : supplierList) {
            Log.d(TAG, "Supplier: " + supplier.getSupplier());
        }

        // Set the adapter for AutoCompleteTextView
        SupplierAutoCompleteAdapter adapter = new SupplierAutoCompleteAdapter(context, supplierList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(selectedSupplier != null ? selectedSupplier.getSupplier() : "");

        // Show a toast with the selected supplier's details
        if (selectedSupplier != null) {
            String toastMessage = "Selected Supplier: " + selectedSupplier.getSupplier() +
                    "\nID: " + selectedSupplier.getId() +
                    "\nContact: " + selectedSupplier.getContact();
         //   Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(context, "No supplier selected or found.", Toast.LENGTH_SHORT).show();
        }
    }
}