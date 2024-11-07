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

public class FetchVatEditData extends AsyncTask<Void, Void, List<VatType>> {

    private static final String TAG = "FetchVatEditData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String selectedVat; // Optional: To filter by selected VAT value

    // Constructor to pass context, spinner reference, and optional selected VAT
    public FetchVatEditData(Context context, Spinner spinner, String selectedVat) {
        this.context = context;
        this.spinner = spinner;
        this.selectedVat = selectedVat; // Get the selected VAT value from arguments
    }

    @Override
    protected List<VatType> doInBackground(Void... voids) {
        List<VatType> vatList = new ArrayList<>();
        Log.d(TAG, "Fetching VAT data...");

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

            // SQL query to fetch data from VatType
            String query = "SELECT TOP (1000) [ID], [vat], [done] FROM [STAR_RETAIL].[dbo].[VatType]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                VatType vatType = new VatType();
                vatType.setId(resultSet.getInt("ID"));
                vatType.setVat(resultSet.getString("vat"));
                vatType.setDone(resultSet.getInt("done"));

                vatList.add(vatType);
                Log.d(TAG, "VAT added: " + vatType.getVat());
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

        return vatList;
    }

    @Override
    protected void onPostExecute(List<VatType> vatList) {
        super.onPostExecute(vatList);

        // Log the number of VAT types fetched
        Log.d(TAG, "VAT types fetched: " + vatList.size());

        if (vatList.isEmpty()) {
            Log.d(TAG, "No VAT data found. Adapter will not be set.");
        } else {
            VatTypeAdapter adapter = new VatTypeAdapter(context, vatList);
            spinner.setAdapter(adapter);
            Log.d(TAG, "Adapter set successfully with " + vatList.size() + " items.");

            // Optionally, set the spinner's selection to the selected VAT if provided
            if (selectedVat != null) {
                for (int i = 0; i < vatList.size(); i++) {
                    if (vatList.get(i).getVat().equals(selectedVat)) {
                        spinner.setSelection(i);
                        Log.d(TAG, "Selected VAT set to: " + selectedVat);
                        break;
                    }
                }
            }
        }
    }
}
