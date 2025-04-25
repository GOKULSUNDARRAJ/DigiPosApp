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

public class FetchReasonEditData extends AsyncTask<Void, Void, List<ReasonType>> {

    private static final String TAG = "FetchReasonEditData";
    private Context context;
    private Spinner spinner;

    public FetchReasonEditData(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected List<ReasonType> doInBackground(Void... voids) {
        List<ReasonType> reasonList = new ArrayList<>();
        Log.d(TAG, "Fetching reason data...");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        String databaseName = Constants.DATABASE_NAME;
        String username = Constants.USERNAME;
        String password = Constants.PASSWORD;

        Connection connection = null;
        try {
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            String query = "SELECT TOP (1000) [ID], [reason], [section], [done] FROM [STAR_RETAIL].[dbo].[reasons]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ReasonType reasonType = new ReasonType();
                reasonType.setId(resultSet.getInt("ID"));
                reasonType.setReason(resultSet.getString("reason"));
                reasonType.setSection(resultSet.getString("section"));
                reasonType.setDone(resultSet.getInt("done"));

                reasonList.add(reasonType);
                Log.d(TAG, "Reason added: " + reasonType.getReason());
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

        return reasonList;
    }

    @Override
    protected void onPostExecute(List<ReasonType> reasonList) {
        super.onPostExecute(reasonList);

        Log.d(TAG, "Reason types fetched: " + reasonList.size());

        if (reasonList.isEmpty()) {
            Log.d(TAG, "No reason data found. Adapter will not be set.");
        } else {
            ReasonTypeAdapter adapter = new ReasonTypeAdapter(context, reasonList);
            spinner.setAdapter(adapter);
            Log.d(TAG, "Adapter set successfully with " + reasonList.size() + " items.");
        }
    }
}
