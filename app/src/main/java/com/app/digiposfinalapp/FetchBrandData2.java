package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FetchBrandData2 extends AsyncTask<Void, Void, List<BrandSpinner>> {

    private static final String TAG = "FetchBrandData";
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    private String ipAddress, portNumber, databaseName, username, password;

    public FetchBrandData2(Context context, AutoCompleteTextView autoCompleteTextView) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    protected List<BrandSpinner> doInBackground(Void... voids) {
        List<BrandSpinner> brandList = new ArrayList<>();
        Log.d(TAG, "Fetching brand data...");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName
                + ";user=" + username + ";password=" + password;
        Log.d(TAG, "Connecting to database...");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT [ID], [Brand], [Done] FROM [dbo].[tbl_Brand]");
             ResultSet resultSet = statement.executeQuery()) {

            Log.d(TAG, "Connection successful!");

            while (resultSet.next()) {
                BrandSpinner brand = new BrandSpinner();
                brand.setId(resultSet.getInt("ID"));
                brand.setBrand(resultSet.getString("Brand"));
                brand.setDone(resultSet.getInt("Done"));

                brandList.add(brand);
                Log.d(TAG, "Brand added: " + brand.getBrand());
            }
            Log.d(TAG, "Data fetch complete.");

        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage());
        }

        return brandList;
    }

    @Override
    protected void onPostExecute(List<BrandSpinner> brandList) {
        super.onPostExecute(brandList);

        if (brandList != null && !brandList.isEmpty()) {
            // Create a list of brand names (optional, if needed elsewhere)
            List<String> brandNames = new ArrayList<>();
            for (BrandSpinner brand : brandList) {
                brandNames.add(brand.getBrand());
            }

            Log.d(TAG, "Setting adapter with brand data.");
            BrandSpinnerAdapter2 adapter = new BrandSpinnerAdapter2(context, brandList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setThreshold(1); // Show suggestions after typing 1 character

            // Removed the code that sets the first item as selected by default
            autoCompleteTextView.setText("", false); // Clear any text to force user selection
        } else {
            Log.e(TAG, "No brands available or list is empty.");
        }
    }
}