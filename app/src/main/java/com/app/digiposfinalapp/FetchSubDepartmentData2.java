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

public class FetchSubDepartmentData2 extends AsyncTask<Void, Void, List<SubDepartmentspinner>> {

    private static final String TAG = "FetchSubDepartmentData";
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    private String ipAddress, portNumber, databaseName, username, password;
    private int departmentId; // Add this field to store department ID

    public FetchSubDepartmentData2(Context context, AutoCompleteTextView autoCompleteTextView, int departmentId) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
        this.departmentId = departmentId; // Initialize department ID
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

        String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName
                + ";user=" + username + ";password=" + password;
        Log.d(TAG, "Connecting to database...");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT [AutoID], [ID], [Department], [Sub_Departments], [done] " +
                             "FROM [dbo].[tblSub_Departments] " +
                             "WHERE [Department] = ?")) {

            // Set the department ID parameter
            statement.setInt(1, departmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                Log.d(TAG, "Connection successful!");

                while (resultSet.next()) {
                    SubDepartmentspinner subDepartment = new SubDepartmentspinner();
                    subDepartment.setId(resultSet.getInt("ID"));
                    subDepartment.setAutoID(resultSet.getInt("AutoID"));
                    subDepartment.setDepartment(resultSet.getString("Department"));
                    subDepartment.setSubDepartment(resultSet.getString("Sub_Departments"));
                    subDepartment.setDone(resultSet.getBoolean("done"));

                    subDepartmentList.add(subDepartment);
                    Log.d(TAG, "Sub-department added: " + subDepartment.getSubDepartment());
                }
                Log.d(TAG, "Data fetch complete.");
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage());
        }

        return subDepartmentList;
    }

    @Override
    protected void onPostExecute(List<SubDepartmentspinner> subDepartmentList) {
        super.onPostExecute(subDepartmentList);

        if (subDepartmentList != null && !subDepartmentList.isEmpty()) {
            Log.d(TAG, "Setting adapter with sub-department data.");
            SubDepartmentSpinnerAdapter adapter = new SubDepartmentSpinnerAdapter(context, subDepartmentList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setThreshold(1); // Show suggestions after typing 1 character

            // Set the first item as selected by default
            if (subDepartmentList.size() > 0) {
                autoCompleteTextView.setText(subDepartmentList.get(0).getSubDepartment(), false);
            }
        }
    }
}