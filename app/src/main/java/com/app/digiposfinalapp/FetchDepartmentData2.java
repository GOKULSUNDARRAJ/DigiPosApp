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

public class FetchDepartmentData2 extends AsyncTask<Void, Void, List<Departmentspinner>> {

    private static final String TAG = "FetchDepartmentData";
    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    private String ipAddress, portNumber, databaseName, username, password;

    public FetchDepartmentData2(Context context, AutoCompleteTextView autoCompleteTextView) {
        this.context = context;
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    protected List<Departmentspinner> doInBackground(Void... voids) {
        List<Departmentspinner> departmentList = new ArrayList<>();
        Log.d(TAG, "Fetching department data...");

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
                     "SELECT [ID], [Department], [Age],[VAT] FROM [dbo].[tbl_Departments]"
             );

             ResultSet resultSet = statement.executeQuery()) {

            Log.d(TAG, "Connection successful!");

            while (resultSet.next()) {
                Departmentspinner department = new Departmentspinner();
                department.setId(resultSet.getInt("ID"));
                department.setDepartment(resultSet.getString("Department"));
                department.setAgestring(resultSet.getString("Age"));
                department.setVatstring(resultSet.getString("VAT"));// get as String instead of getInt

                departmentList.add(department);
                Log.d(TAG, "Department added: " + department.getDepartment() + " with age: " + department.getAge());
            }

            Log.d(TAG, "Data fetch complete.");

        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage());
        }

        return departmentList;
    }

    @Override
    protected void onPostExecute(List<Departmentspinner> departmentList) {
        super.onPostExecute(departmentList);

        if (departmentList != null && !departmentList.isEmpty()) {
            // Create a list of department names
            List<String> departmentNames = new ArrayList<>();
            for (Departmentspinner department : departmentList) {
                departmentNames.add(department.getDepartment());
            }

            Log.d(TAG, "Setting adapter with department data.");
            DepartmentSpinnerAdapter2 adapter = new DepartmentSpinnerAdapter2(context, departmentList);
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setThreshold(1); // Show suggestions after typing 1 character

            // Set the first item as selected by default
            if (departmentList.size() > 0) {
                autoCompleteTextView.setText(departmentList.get(0).getDepartment(), false);
                // If you need to store the ID or other data of the selected item
                // You might want to use a tag or other mechanism to store the selected department
            }
        }
    }

}
