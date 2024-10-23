package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
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

public class MyBottomSheetFragmentbrand extends BottomSheetDialogFragment {

    private Connection connection;
    private EditText nameEdt, idEdt, pluEdt;
    private Button updateButton;

    private String brandName1, plu1, id1;

    private String ipAddress, portNumber, databaseName, username, password;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_brand_edit, null);

        // Initialize SharedPreferences and get database connection details
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize UI components
        nameEdt = view.findViewById(R.id.name);
        pluEdt = view.findViewById(R.id.done);
        idEdt = view.findViewById(R.id.idedt);



        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(v -> dismiss()); // Close the BottomSheetDialogFragment

        // Set initial values
        idEdt.setText(id1);
        nameEdt.setText(brandName1);
        pluEdt.setText(plu1);

        updateButton = view.findViewById(R.id.add);
        updateButton.setOnClickListener(v -> {
            String brandName = nameEdt.getText().toString();
            String plu = pluEdt.getText().toString();
            String id = idEdt.getText().toString();

            if (!brandName.isEmpty() && !plu.isEmpty() && !id.isEmpty()) {
                // Execute database update operation in a background thread
                new Thread(() -> updateDataInDatabase(brandName, Integer.parseInt(id), Integer.parseInt(plu))).start();
            } else {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void setDetails(Context context, String brandName, String plu, String id) {
        if (context != null) {
            brandName1 = brandName;
            plu1 = plu;
            id1 = id;
        } else {
            Log.e("MyBottomSheetFragment", "Context is null");
        }
    }

    private void updateDataInDatabase(String brandName, int id, int plu) {
        try {
            if (connection == null) {
                // Establish database connection
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                    connection = DriverManager.getConnection(connectURL);
                } catch (ClassNotFoundException | SQLException e) {
                    Log.e("Error", "Failed to establish database connection: " + e.getMessage());
                    return;
                }
            }

            if (connection != null) {
                // Create SQL update statement
                String updateStatement = "UPDATE tbl_Brand SET Brand = ?, Done = ? WHERE ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateStatement);

                preparedStatement.setString(1, brandName);
                preparedStatement.setInt(2, plu);
                preparedStatement.setInt(3, id);

                // Execute the update statement
                int rowsUpdated = preparedStatement.executeUpdate();

                getActivity().runOnUiThread(() -> {
                    if (rowsUpdated > 0) {
                        Log.d("Update", "Data updated successfully");
                        Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Update", "No rows were updated");
                        Toast.makeText(getContext(), "No rows updated", Toast.LENGTH_SHORT).show();
                    }
                });

                preparedStatement.close();
            } else {
                Log.e("Error", "Connection object is null");
            }
        } catch (SQLException e) {
            Log.e("Error", "SQLException: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                Log.e("Error", "SQLException: " + e.getMessage());
            }
        }
    }
}
