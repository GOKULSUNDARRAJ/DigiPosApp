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

public class FetchSubDepartmentEdit extends AsyncTask<Void, Void, List<SubDepartmentspinner>> {

    private static final String TAG = "FetchSubDepartmentEdit";
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    private String subDepartmentID;  // To select current sub-department (as String)
    private int departmentId;        // To filter by department ID (as INT)
    private String ipAddress, portNumber, databaseName, username, password;

    // Modified constructor to accept department ID as int
    public FetchSubDepartmentEdit(Context context, AutoCompleteTextView autoCompleteTextView,
                                  String subDepartmentID, int departmentId) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
        this.subDepartmentID = subDepartmentID;
        this.departmentId = departmentId;
    }

    @Override
    protected List<SubDepartmentspinner> doInBackground(Void... voids) {
        List<SubDepartmentspinner> subDepartmentList = new ArrayList<>();
        Log.d(TAG, "Fetching sub-department data for department ID: " + departmentId);

        // Retrieve connection details from SharedPreferences
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

            // SQL query to fetch sub-departments filtered by department ID (INT)
            String query = "SELECT [AutoID], [ID], [Department], [Sub_Departments], [done] " +
                    "FROM [dbo].[tblSub_Departments] " +
                    "WHERE [Department] = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, departmentId); // Set the INT parameter
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                SubDepartmentspinner subDepartment = new SubDepartmentspinner();
                subDepartment.setId(resultSet.getInt("ID"));
                subDepartment.setAutoID(resultSet.getInt("AutoID"));
                subDepartment.setDepartment(resultSet.getString("Department"));
                subDepartment.setSubDepartment(resultSet.getString("Sub_Departments"));
                subDepartment.setDone(resultSet.getBoolean("done"));

                subDepartmentList.add(subDepartment);
                Log.d(TAG, "Sub-department added: " + subDepartment.getSubDepartment() +
                        " for department ID: " + departmentId);
            }

            resultSet.close();
            statement.close();
            Log.d(TAG, "Data fetch complete. Found " + subDepartmentList.size() + " sub-departments.");

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

        return subDepartmentList;
    }

    @Override
    protected void onPostExecute(List<SubDepartmentspinner> subDepartmentList) {
        super.onPostExecute(subDepartmentList);

        if (subDepartmentList == null || subDepartmentList.isEmpty()) {
            Log.e(TAG, "No sub-departments found for department ID: " + departmentId);
            Toast.makeText(context, "No sub-departments found for selected department", Toast.LENGTH_SHORT).show();
            return;
        }

        SubDepartmentspinner selectedSubDepartment = null;

        // Find the selected sub-department based on the subDepartmentID
        for (int i = 0; i < subDepartmentList.size(); i++) {
            SubDepartmentspinner subDepartment = subDepartmentList.get(i);
            if (String.valueOf(subDepartment.getId()).equals(subDepartmentID)) {
                selectedSubDepartment = subDepartmentList.remove(i);
                break;
            }
        }

        // Add the selected sub-department at the top if it was found
        if (selectedSubDepartment != null) {
            subDepartmentList.add(0, selectedSubDepartment);
            Log.d(TAG, "Selected sub-department: " + selectedSubDepartment.getSubDepartment());

            // Save selected sub-department to SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("SubDepartmentPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedSubDepartmentID", String.valueOf(selectedSubDepartment.getId()));
            editor.putString("selectedSubDepartmentName", selectedSubDepartment.getSubDepartment());
            editor.apply();
        }

        // Set the adapter for AutoCompleteTextView
        SubDepartmentAutoCompleteAdapter adapter = new SubDepartmentAutoCompleteAdapter(context, subDepartmentList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(selectedSubDepartment != null ? selectedSubDepartment.getSubDepartment() : "");
    }
}