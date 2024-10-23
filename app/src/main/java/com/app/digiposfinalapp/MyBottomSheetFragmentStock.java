package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyBottomSheetFragmentStock extends BottomSheetDialogFragment {

    private static final String TAG = "MyBottomSheetFragmentStock";

    // Declare variables
    String barcode1, date1, time1;
    int id1, currentQty1, updateQty1, doneBy1, pul1;

    // Declare EditText and Button
    EditText idedt, barcodeEdt, puledt, dateEdt, timeEdt, currentQtyEdt, updateQtyEdt, doneByEdt;
    Button button;
    Connection connection;

    private String ipAddress, portNumber, databaseName, username, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_edit_stock, container, false);

        // Retrieve SharedPreferences from the activity
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize EditText and Button
        idedt = view.findViewById(R.id.autocompleteTextView);
        barcodeEdt = view.findViewById(R.id.autocompleteTextView2);
        puledt = view.findViewById(R.id.autocompleteTextView24);
        dateEdt = view.findViewById(R.id.autocompleteTextView246);
        timeEdt = view.findViewById(R.id.autocompleteTextView24676);
        currentQtyEdt = view.findViewById(R.id.autocompleteTextView624676);
        updateQtyEdt = view.findViewById(R.id.dayedt);
        doneByEdt = view.findViewById(R.id.doneedt);
        button = view.findViewById(R.id.update);

        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the BottomSheetDialogFragment
            }
        });

        // Set EditText fields with existing data
        idedt.setText(String.valueOf(id1));
        barcodeEdt.setText(barcode1);
        puledt.setText(String.valueOf(pul1));
        dateEdt.setText(date1);
        timeEdt.setText(time1);
        currentQtyEdt.setText(String.valueOf(currentQty1));
        updateQtyEdt.setText(String.valueOf(updateQty1));
        doneByEdt.setText(String.valueOf(doneBy1));

        // Set onClickListener for the update button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to update data in the database
                updateDataInDatabase(
                        barcodeEdt.getText().toString(),
                        Integer.parseInt(puledt.getText().toString()),
                        dateEdt.getText().toString(),
                        timeEdt.getText().toString(),
                        Integer.parseInt(currentQtyEdt.getText().toString()),
                        Integer.parseInt(updateQtyEdt.getText().toString()),
                        Integer.parseInt(doneByEdt.getText().toString())
                );
            }
        });

        return view;
    }

    // Method to update data in the database
    public void updateDataInDatabase(String barcode, int pul, String date, String time, int currentQty, int updateQty, int doneBy) {
        new Thread(() -> {
            try {
                if (connection == null) {
                    // Establish database connection
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                        connection = DriverManager.getConnection(connectURL);
                    } catch (ClassNotFoundException | SQLException e) {
                        Log.e(TAG, "Failed to establish database connection: " + e.getMessage(), e);
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to connect to database", Toast.LENGTH_SHORT).show());
                        return;
                    }
                }

                if (connection != null) {
                    // Create SQL update statement
                    String updateStatement = "UPDATE tbl_Stock_Update SET Barcode = ?, PLU = ?, Date = ?, Time = ?, Current_qty = ?, Update_Qty = ?, Done_By = ? WHERE ID = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateStatement)) {
                        preparedStatement.setString(1, barcode);
                        preparedStatement.setInt(2, pul);
                        preparedStatement.setString(3, date);
                        preparedStatement.setString(4, time);
                        preparedStatement.setInt(5, currentQty);
                        preparedStatement.setInt(6, updateQty);
                        preparedStatement.setInt(7, doneBy);
                        preparedStatement.setInt(8, id1);

                        // Execute the update statement
                        int rowsUpdated = preparedStatement.executeUpdate();

                        getActivity().runOnUiThread(() -> {
                            if (rowsUpdated > 0) {
                                // Update successful
                                Log.d(TAG, "Data updated successfully");
                                Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                // No rows were updated
                                Log.d(TAG, "No rows were updated");
                                Toast.makeText(getContext(), "No rows updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (SQLException e) {
                        Log.e(TAG, "Failed to execute update statement: " + e.getMessage(), e);
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show());
                    } finally {
                        // Close the connection
                        try {
                            if (connection != null && !connection.isClosed()) {
                                connection.close();
                                Log.d(TAG, "Database connection closed.");
                            }
                        } catch (SQLException e) {
                            Log.e(TAG, "Failed to close database connection: " + e.getMessage(), e);
                        }
                    }
                } else {
                    Log.e(TAG, "Database connection is null");
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Database connection is null", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error occurred: " + e.getMessage(), e);
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void setDetails(Context context, int id, String barcode, int pul, String date, String time, int currentQty, int updateQty, int doneBy) {
        if (context != null) {
            id1 = id;
            barcode1 = barcode;
            pul1 = pul;
            date1 = date;
            time1 = time;
            currentQty1 = currentQty;
            updateQty1 = updateQty;
            doneBy1 = doneBy;
        } else {
            Log.e(TAG, "Context is null");
        }
    }
}
