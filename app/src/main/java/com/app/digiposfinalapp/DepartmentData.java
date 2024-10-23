package com.app.digiposfinalapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DepartmentData {

    private static final String TAG = "DepartmentData";
    private static Connection connection;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void getDepartmentList(String ipAddress, String portNumber, String databaseName, String username, String password, OnDepartmentDataFetchListener listener) {
        executorService.execute(() -> {
            List<Department> departmentList = new ArrayList<>();
            if (connection == null) {
                // Establish database connection
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                    connection = DriverManager.getConnection(connectURL);
                } catch (ClassNotFoundException | SQLException e) {
                    Log.e(TAG, "Failed to establish database connection: " + e.getMessage());
                    listener.onFetchFailed(e);
                    return;
                }
            }
            // Query to fetch department data
            String query = "SELECT Age, Department, Num, Noshop, Points, Done, Image, VAT FROM tbl_Departments"; // Modify as needed
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                // Loop through the result set and populate the department list
                while (resultSet.next()) {
                    Department department = new Department();
                    department.setAge(resultSet.getString("Age"));
                    department.setName(resultSet.getString("Department"));
                    department.setNum(resultSet.getInt("Num"));
                    department.setNoshop(resultSet.getInt("Noshop"));
                    department.setPoints(resultSet.getInt("Points"));
                    department.setDone(resultSet.getInt("Done"));
                    department.setImage(resultSet.getString("Image")); // Assuming Image is stored as a URL or file path
                    department.setVat(resultSet.getString("VAT")); // VAT as a String
                    departmentList.add(department);
                }
                // Notify the listener with the fetched department list
                listener.onFetchSuccess(departmentList);

            } catch (SQLException e) {
                Log.e(TAG, "Failed to fetch departments: " + e.getMessage());
                listener.onFetchFailed(e);
            } finally {
                // Close the connection if necessary
                try {
                    if (connection != null) {
                        connection.close();
                        connection = null; // Clear connection reference after closing
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Failed to close database connection: " + e.getMessage());
                }
            }
        });

    }

    public interface OnDepartmentDataFetchListener {
        void onFetchSuccess(List<Department> departmentList);

        void onFetchFailed(Exception e);
    }
}
