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

public class FetchBrandData extends AsyncTask<Void, Void, List<BrandSpinner>> {

    private static final String TAG = "FetchBrandData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String ipAddress, portNumber, databaseName, username, password;

    // Constructor to pass context and spinner reference
    public FetchBrandData(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
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

        BrandSpinnerAdapter adapter = new BrandSpinnerAdapter(context, brandList);
        spinner.setAdapter(adapter);
    }
}
