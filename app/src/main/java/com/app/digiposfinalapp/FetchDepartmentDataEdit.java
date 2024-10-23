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

public class FetchDepartmentDataEdit extends AsyncTask<Void, Void, List<Departmentspinner>> {

    private static final String TAG = "FetchDepartmentDataEdit"; // Updated tag for logging
    private Context context;
    private Spinner spinner;
    private String departmentID;  // To filter by department
    private String ipAddress, portNumber, databaseName, username, password;

    // Modified constructor to accept the department string
    public FetchDepartmentDataEdit(Context context, Spinner spinner, String departmentID) {
        this.context = context;
        this.spinner = spinner;
        this.departmentID = departmentID;  // Get the department from arguments
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

        Connection connection = null;
        try {
            // jTDS connection string
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            // Modified query to fetch all departments
            String query = "SELECT [ID], [Age], [Department], [Num], [Noshop], [Points], [done], [image], [VAT] FROM [dbo].[tbl_Departments]";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Departmentspinner departmentSpinner = new Departmentspinner();
                departmentSpinner.setId(resultSet.getInt("ID"));

                String ageString = resultSet.getString("Age");
                try {
                    int age = Integer.parseInt(ageString);
                    departmentSpinner.setAge(age);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Invalid Age value: " + ageString + ". Defaulting to 0.");
                    departmentSpinner.setAge(0);
                }

                departmentSpinner.setDepartment(resultSet.getString("Department"));
                departmentSpinner.setNum(resultSet.getString("Num"));
                departmentSpinner.setNoshop(resultSet.getString("Noshop"));
                departmentSpinner.setPoints(resultSet.getInt("Points"));
                departmentSpinner.setDone(resultSet.getBoolean("done"));
                departmentSpinner.setImage(resultSet.getString("image"));
                departmentSpinner.setVat(resultSet.getDouble("VAT"));

                departmentList.add(departmentSpinner);
                Log.d(TAG, "Department added: " + departmentSpinner.getDepartment());
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

        return departmentList;
    }

    @Override
    protected void onPostExecute(List<Departmentspinner> departmentList) {
        super.onPostExecute(departmentList);

        Departmentspinner selectedDepartment = null;

        // Rearrange the list to put the department with the matching ID at the top
        for (int i = 0; i < departmentList.size(); i++) {
            Departmentspinner department = departmentList.get(i);
            if (String.valueOf(department.getId()).equals(departmentID)) {
                selectedDepartment = departmentList.remove(i); // Remove the matching department from its original position
                break;
            }
        }

        // Add the selected department at the top if it was found
        if (selectedDepartment != null) {
            departmentList.add(0, selectedDepartment);  // Add it to the top of the list
        }

        // Create and set the adapter for the spinner
        DepartmentSpinnerAdapter adapter = new DepartmentSpinnerAdapter(context, departmentList);
        spinner.setAdapter(adapter);

        // Set the spinner's selection to the first item (which is now the selected department)
        spinner.setSelection(0);  // The selected department is now at index 0
    }




}
