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

public class FetchChillerData2 extends AsyncTask<Void, Void, List<ChillerSpinner>> {

    private static final String TAG = "FetchChillerData";
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    private String ipAddress, portNumber, databaseName, username, password;

    public FetchChillerData2(Context context, AutoCompleteTextView autoCompleteTextView) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    protected List<ChillerSpinner> doInBackground(Void... voids) {
        List<ChillerSpinner> chillerList = new ArrayList<>();
        Log.d(TAG, "Fetching chiller data...");

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
                     "SELECT [ID], [Chiller_Name] FROM [STAR_RETAIL].[dbo].[tbl_Chiller]"
             );
             ResultSet resultSet = statement.executeQuery()) {

            Log.d(TAG, "Connection successful!");

            while (resultSet.next()) {
                ChillerSpinner chiller = new ChillerSpinner();
                chiller.setId(resultSet.getInt("ID"));
                chiller.setChillerName(resultSet.getString("Chiller_Name"));

                chillerList.add(chiller);
                Log.d(TAG, "Chiller added: " + chiller.getChillerName());
            }

            Log.d(TAG, "Data fetch complete.");

        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage());
        }

        return chillerList;
    }

    @Override
    protected void onPostExecute(List<ChillerSpinner> chillerList) {
        super.onPostExecute(chillerList);

        if (chillerList != null && !chillerList.isEmpty()) {
            Log.d(TAG, "Setting adapter with chiller data.");
            ChillerSpinnerAdapter2 adapter = new ChillerSpinnerAdapter2(context, chillerList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setThreshold(1); // Show suggestions after typing 1 character

            // Set the first item as selected by default
            if (chillerList.size() > 0) {
                autoCompleteTextView.setText(chillerList.get(0).getChillerName(), false);
            }
        } else {
            Log.e(TAG, "No chiller data found to populate the AutoCompleteTextView.");
        }
    }
}