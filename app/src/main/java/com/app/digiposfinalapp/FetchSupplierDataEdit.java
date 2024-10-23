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

public class FetchSupplierDataEdit extends AsyncTask<Void, Void, List<SupplierSpinner>> {

    private static final String TAG = "FetchSupplierDataTask"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String supplierName;  // To filter by supplier name
    private String ipAddress, portNumber, databaseName, username, password;

    // Modified constructor to accept the supplier name string
    public FetchSupplierDataEdit(Context context, Spinner spinner, String supplierName) {
        this.context = context;
        this.spinner = spinner;
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
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            // SQL query to fetch data from tbl_Supplier
            String query = "SELECT [ID], [Supplier], [Address], [Contact], [done] FROM [dbo].[tbl_Supplier]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SupplierSpinner supplier = new SupplierSpinner();
                supplier.setId(resultSet.getInt("ID"));
                supplier.setSupplier(resultSet.getString("Supplier"));
                supplier.setAddress(resultSet.getString("Address"));
                supplier.setContact(resultSet.getString("Contact"));
                supplier.setDone(resultSet.getInt("done"));

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

        SupplierSpinner selectedSupplier = null;

        // Rearrange the list to put the supplier with the matching name at the top
        for (int i = 0; i < supplierList.size(); i++) {
            SupplierSpinner supplier = supplierList.get(i);
            if (supplier.getSupplier().equalsIgnoreCase(supplierName)) {
                selectedSupplier = supplierList.remove(i); // Remove the matching supplier from its original position
                break;
            }
        }

        // Add the selected supplier at the top if it was found
        if (selectedSupplier != null) {
            supplierList.add(0, selectedSupplier);  // Add it to the top of the list
        }

        // Create and set the adapter for the spinner
        SupplierSpinnerAdapter adapter = new SupplierSpinnerAdapter(context, supplierList);
        spinner.setAdapter(adapter);

        // Set the spinner's selection to the first item (which is now the selected supplier)
        spinner.setSelection(0);  // The selected supplier is now at index 0
    }
}
