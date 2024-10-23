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

public class FetchBrandDataEdit extends AsyncTask<Void, Void, List<BrandSpinner>> {

    private static final String TAG = "FetchBrandData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String selectedBrandID; // Optional: To filter by selected brand ID

    // Constructor to pass context, spinner reference, and optional selected brand ID
    public FetchBrandDataEdit(Context context, Spinner spinner, String selectedBrandID) {
        this.context = context;
        this.spinner = spinner;
        this.selectedBrandID = selectedBrandID; // Get the selected brand ID from arguments
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
            // Optional: Handle the error to inform the user, e.g., showing a Toast
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

        BrandSpinner selectedBrand = null;

        // Optional: Rearrange the list to put the selected brand at the top
        if (selectedBrandID != null) {
            for (int i = 0; i < brandList.size(); i++) {
                BrandSpinner brand = brandList.get(i);
                if (String.valueOf(brand.getId()).equals(selectedBrandID)) {
                    selectedBrand = brandList.remove(i); // Remove the matching brand from its original position
                    break;
                }
            }

            // Add the selected brand at the top if it was found
            if (selectedBrand != null) {
                brandList.add(0, selectedBrand);  // Add it to the top of the list
            }
        }

        // Create and set the adapter for the spinner
        BrandSpinnerAdapter adapter = new BrandSpinnerAdapter(context, brandList);
        spinner.setAdapter(adapter);

        // Optionally, set the spinner's selection to the first item (which is now the selected brand if applicable)
        spinner.setSelection(0);  // The selected brand is now at index 0
    }
}
