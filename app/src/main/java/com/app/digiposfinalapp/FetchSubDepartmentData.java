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

public class FetchSubDepartmentData extends AsyncTask<Void, Void, List<SubDepartmentspinner>> {

    private static final String TAG = "FetchSubDepartmentData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String ipAddress, portNumber, databaseName, username, password;

    // Constructor to pass context and spinner reference
    public FetchSubDepartmentData(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
    }

    @Override
    protected List<SubDepartmentspinner> doInBackground(Void... voids) {
        List<SubDepartmentspinner> subDepartmentList = new ArrayList<>();
        Log.d(TAG, "Fetching sub-department data...");

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

            // Updated SQL query for tblSub_Departments
            String query = "SELECT TOP (1000) [AutoID], [ID], [Department], [Sub_Departments], [done] FROM [STAR_RETAIL1].[dbo].[tblSub_Departments]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SubDepartmentspinner subDepartment = new SubDepartmentspinner(); // Changed class name here
                subDepartment.setId(resultSet.getInt("ID"));
                subDepartment.setAutoID(resultSet.getInt("AutoID")); // Assuming you want to keep AutoID
                subDepartment.setDepartment(resultSet.getString("Department"));
                subDepartment.setSubDepartment(resultSet.getString("Sub_Departments")); // New field
                subDepartment.setDone(resultSet.getBoolean("done")); // New field


                subDepartmentList.add(subDepartment);
                Log.d(TAG, "Sub-department added: " + subDepartment.getDepartment());
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

        return subDepartmentList; // Changed the return type
    }

    @Override
    protected void onPostExecute(List<SubDepartmentspinner> subDepartmentList) { // Changed the type here
        super.onPostExecute(subDepartmentList);

        SubDepartmentSpinnerAdapter adapter = new SubDepartmentSpinnerAdapter(context, subDepartmentList);
        spinner.setAdapter(adapter);
    }
}
