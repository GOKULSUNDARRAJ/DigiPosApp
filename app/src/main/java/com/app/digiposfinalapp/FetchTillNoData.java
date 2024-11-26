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

public class FetchTillNoData extends AsyncTask<Void, Void, List<TillNoType>> {

    private static final String TAG = "FetchTillNoData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String ipAddress, portNumber, databaseName, username, password;

    // Constructor to pass context and spinner reference
    public FetchTillNoData(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected List<TillNoType> doInBackground(Void... voids) {
        List<TillNoType> tillNoList = new ArrayList<>();
        Log.d(TAG, "Fetching TillNo data...");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Connection connection = null;
        try {
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            // SQL query to fetch data from TillNo
            String query = "SELECT TOP (1000) [ID], [TillNo], [Workshop] FROM [STAR_RETAIL].[dbo].[TillNo]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                TillNoType tillNoType = new TillNoType();
                tillNoType.setId(resultSet.getInt("ID"));
                tillNoType.setTillNo(resultSet.getString("TillNo"));
                tillNoType.setWorkshop(resultSet.getString("Workshop"));

                tillNoList.add(tillNoType);
                Log.d(TAG, "TillNo added: " + tillNoType.getTillNo());
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

        return tillNoList;
    }

    @Override
    protected void onPostExecute(List<TillNoType> tillNoList) {
        super.onPostExecute(tillNoList);

        Log.d(TAG, "TillNo types fetched: " + tillNoList.size());

        if (tillNoList.isEmpty()) {
            Log.d(TAG, "No TillNo data found. Adapter will not be set.");
        } else {
            TillNoTypeAdapter adapter = new TillNoTypeAdapter(context, tillNoList);
            spinner.setAdapter(adapter);
            Log.d(TAG, "Adapter set successfully with " + tillNoList.size() + " items.");
        }
    }
}
