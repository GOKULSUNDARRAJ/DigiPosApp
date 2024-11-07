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

    private static final String TAG = "FetchDepartmentDataEdit";
    private Context context;
    private Spinner spinner;
    private String departmentID;
    private String ipAddress, portNumber, databaseName, username, password;

    public FetchDepartmentDataEdit(Context context, Spinner spinner, String departmentID) {
        this.context = context;
        this.spinner = spinner;
        this.departmentID = departmentID;
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
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";user=" + username + ";password=" + password;
            Log.d(TAG, "Connecting to database...");
            connection = DriverManager.getConnection(url);
            Log.d(TAG, "Connection successful!");

            String query = "SELECT [ID], [Age], [Department], [Num], [Noshop], [Points], [done], [image], [VAT] FROM [dbo].[tbl_Departments]";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Departmentspinner departmentSpinner = new Departmentspinner();
                departmentSpinner.setId(resultSet.getInt("ID"));

                // Handle age value
                String ageString = resultSet.getString("Age");
                try {
                    if (ageString.matches("\\d+")) { // Check if it's numeric
                        int age = Integer.parseInt(ageString);
                        departmentSpinner.setAge(age);
                    } else {
                        Log.e(TAG, "Invalid Age value: " + ageString + ". Defaulting to 0.");
                        departmentSpinner.setAge(0);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing age: " + ageString + ". Defaulting to 0.");
                    departmentSpinner.setAge(0);
                }

                departmentSpinner.setDepartment(resultSet.getString("Department"));
                departmentSpinner.setNum(resultSet.getString("Num"));
                departmentSpinner.setNoshop(resultSet.getString("Noshop"));
                departmentSpinner.setPoints(resultSet.getInt("Points"));
                departmentSpinner.setDone(resultSet.getBoolean("done"));
                departmentSpinner.setImage(resultSet.getString("image"));

                // Handle VAT value
                String vatString = resultSet.getString("VAT");
                try {
                    if (vatString.endsWith("%")) { // Strip '%' if it's there
                        vatString = vatString.substring(0, vatString.length() - 1);
                    }
                    double vat = Double.parseDouble(vatString);
                    departmentSpinner.setVat(vat);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing VAT value: " + vatString + ". Defaulting to 0.0.");
                    departmentSpinner.setVat(0.0);
                }

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

        if (departmentList == null || departmentList.isEmpty()) {
            Log.e(TAG, "No departments fetched or list is empty.");
            return; // Early exit if no data
        }

        Departmentspinner selectedDepartment = null;

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

        Log.d(TAG, "Setting adapter with departments:");
        for (Departmentspinner dept : departmentList) {
            Log.d(TAG, "Department: " + dept.getDepartment());
        }

        DepartmentSpinnerAdapter adapter = new DepartmentSpinnerAdapter(context, departmentList);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);  // The selected department is now at index 0
    }
}
