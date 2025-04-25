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

public class FetchBrandDataEdit extends AsyncTask<Void, Void, List<BrandSpinner>> {

    private static final String TAG = "FetchBrandDataEdit"; // Tag for logging
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;  // AutoCompleteTextView reference to populate
    private String selectedBrandName; // Use brand name instead of ID

    // Constructor to pass context, AutoCompleteTextView reference, and selected brand name
    public FetchBrandDataEdit(Context context, AutoCompleteTextView autoCompleteTextView, String selectedBrandName) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
        this.selectedBrandName = selectedBrandName; // Get the selected brand name from arguments
    }

    @Override
    protected List<BrandSpinner> doInBackground(Void... voids) {
        List<BrandSpinner> brandList = new ArrayList<>();
        Log.d(TAG, "Fetching brand data...");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        String databaseName = Constants.DATABASE_NAME;
        String username = Constants.USERNAME;
        String password = Constants.PASSWORD;

        Connection connection = null; // Initialize connection to null
        try {
            // jTDS connection string
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            // SQL query to fetch data from tbl_Brand
            String query = "SELECT [ID], [Brand], [Done] FROM [dbo].[tbl_Brand]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                BrandSpinner brand = new BrandSpinner();
                brand.setId(resultSet.getInt("ID"));
                brand.setBrand(resultSet.getString("Brand"));
                brand.setDone(resultSet.getInt("Done"));

                brandList.add(brand);
                Log.d(TAG, "Brand added: " + brand.getBrand());
            }

            resultSet.close();
            statement.close();
            Log.d(TAG, "Data fetch complete.");

        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    Log.d(TAG, "Connection closed.");
                } catch (SQLException e) {
                    Log.e(TAG, "Failed to close connection: " + e.getMessage());
                }
            }
        }

        return brandList;
    }

    @Override
    protected void onPostExecute(List<BrandSpinner> brandList) {
        super.onPostExecute(brandList);

        if (brandList == null || brandList.isEmpty()) {
            Log.e(TAG, "No brands fetched or list is empty.");
            return;
        }

        BrandSpinner selectedBrand = null;

        // Find the selected brand based on the selectedBrandName
        for (int i = 0; i < brandList.size(); i++) {
            BrandSpinner brand = brandList.get(i);
            if (brand.getBrand().equalsIgnoreCase(selectedBrandName)) {
                selectedBrand = brandList.remove(i);
                break;
            }
        }

        // Add the selected brand at the top if it was found
        if (selectedBrand != null) {
            brandList.add(0, selectedBrand);

            // Save selected brand to SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("BrandPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedBrandID", String.valueOf(selectedBrand.getId()));
            editor.putString("selectedBrandName", selectedBrand.getBrand());
            editor.apply();
        }

        Log.d(TAG, "Setting adapter with brands:");
        for (BrandSpinner brand : brandList) {
            Log.d(TAG, "Brand: " + brand.getBrand());
        }

        // Set the adapter for AutoCompleteTextView
        BrandAutoCompleteAdapter adapter = new BrandAutoCompleteAdapter(context, brandList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(selectedBrand != null ? selectedBrand.getBrand() : "");

        // Show a toast with the selected brand's details
        if (selectedBrand != null) {
            String toastMessage = "Selected Brand: " + selectedBrand.getBrand() +
                    "\nID: " + selectedBrand.getId() +
                    "\nDone: " + selectedBrand.getDone();
           // Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(context, "No brand selected or found.", Toast.LENGTH_SHORT).show();
        }
    }
}