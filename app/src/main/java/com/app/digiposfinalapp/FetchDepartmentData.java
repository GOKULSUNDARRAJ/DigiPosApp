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

public class FetchDepartmentData extends AsyncTask<Void, Void, List<Departmentspinner>> {

    private static final String TAG = "FetchDepartmentData"; // Tag for logging
    private Context context;
    private Spinner spinner;  // Spinner reference to populate
    private String ipAddress, portNumber, databaseName, username, password;

    // Constructor to pass context and spinner reference
    public FetchDepartmentData(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
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

        Connection connection = null; // Initialize connection to null
        try {
            // jTDS connection string
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            String query = "SELECT TOP (1000) [ID], [Age], [Department], [Num], [Noshop], [Points], [done], [image], [VAT] FROM [dbo].[tbl_Departments]";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Departmentspinner department = new Departmentspinner();
                department.setId(resultSet.getInt("ID"));

                // Get Age as a String
                String ageString = resultSet.getString("Age");
                try {
                    // Try to parse Age to Integer
                    int age = Integer.parseInt(ageString);
                    department.setAge(age);
                } catch (NumberFormatException e) {
                    // Handle invalid age value (e.g., log or set a default value)
                    Log.e(TAG, "Invalid Age value: " + ageString + ". Defaulting to 0.");
                    department.setAge(0); // Default value or you can choose to skip this entry
                }

                department.setDepartment(resultSet.getString("Department"));
                department.setNum(resultSet.getString("Num"));
                department.setNoshop(resultSet.getString("Noshop"));
                department.setPoints(resultSet.getInt("Points"));
                department.setDone(resultSet.getBoolean("done"));
                department.setImage(resultSet.getString("image"));
                department.setVat(resultSet.getDouble("VAT"));

                departmentList.add(department);
                Log.d(TAG, "Department added: " + department.getDepartment());
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

        return departmentList;
    }


    @Override
    protected void onPostExecute(List<Departmentspinner> departmentList) {
        super.onPostExecute(departmentList);

        DepartmentSpinnerAdapter adapter = new DepartmentSpinnerAdapter(context, departmentList);
        spinner.setAdapter(adapter);
    }

}
