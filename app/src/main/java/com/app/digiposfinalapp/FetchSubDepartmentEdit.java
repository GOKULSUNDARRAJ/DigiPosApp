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

public class FetchSubDepartmentEdit extends AsyncTask<Void, Void, List<SubDepartmentspinner>> {

    private static final String TAG = "FetchSubDepartmentTask"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String subDepartmentID;  // To filter by sub-department
    private String ipAddress, portNumber, databaseName, username, password;

    // Modified constructor to accept the sub-department ID string
    public FetchSubDepartmentEdit(Context context, Spinner spinner, String subDepartmentID) {
        this.context = context;
        this.spinner = spinner;
        this.subDepartmentID = subDepartmentID;  // Get the sub-department ID from arguments
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
            String query = "SELECT [AutoID], [ID], [Department], [Sub_Departments], [done] FROM [STAR_RETAIL1].[dbo].[tblSub_Departments]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SubDepartmentspinner subDepartment = new SubDepartmentspinner();
                subDepartment.setId(resultSet.getInt("ID"));
                subDepartment.setAutoID(resultSet.getInt("AutoID"));
                subDepartment.setDepartment(resultSet.getString("Department"));
                subDepartment.setSubDepartment(resultSet.getString("Sub_Departments"));
                subDepartment.setDone(resultSet.getBoolean("done"));

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

        return subDepartmentList; // Return the fetched list
    }

    @Override
    protected void onPostExecute(List<SubDepartmentspinner> subDepartmentList) {
        super.onPostExecute(subDepartmentList);

        SubDepartmentspinner selectedSubDepartment = null;

        // Rearrange the list to put the sub-department with the matching ID at the top
        for (int i = 0; i < subDepartmentList.size(); i++) {
            SubDepartmentspinner subDepartment = subDepartmentList.get(i);
            if (String.valueOf(subDepartment.getId()).equals(subDepartmentID)) {
                selectedSubDepartment = subDepartmentList.remove(i); // Remove the matching sub-department from its original position
                break;
            }
        }

        // Add the selected sub-department at the top if it was found
        if (selectedSubDepartment != null) {
            subDepartmentList.add(0, selectedSubDepartment);  // Add it to the top of the list
        }

        // Create and set the adapter for the spinner
        SubDepartmentSpinnerAdapter adapter = new SubDepartmentSpinnerAdapter(context, subDepartmentList);
        spinner.setAdapter(adapter);

        // Set the spinner's selection to the first item (which is now the selected sub-department)
        spinner.setSelection(0);  // The selected sub-department is now at index 0
    }
}
