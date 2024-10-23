package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyBottomSheetFragmentsupplier extends BottomSheetDialogFragment {

    private static final String LOG_TAG = "MyBottomSheetFragment";
    private EditText Nameedt, idedt, doneedt;
    private Button updateButton;

    private String supplierName1, done1, id1;

    private String ipAddress, portNumber, databaseName, username, password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_supplier_edit, container, false);

        // Retrieve SharedPreferences from the activity
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Nameedt = view.findViewById(R.id.name);
        doneedt = view.findViewById(R.id.done);
        idedt = view.findViewById(R.id.id);


        idedt.setText(id1);
        Nameedt.setText(supplierName1);
        doneedt.setText(done1);

        updateButton = view.findViewById(R.id.update);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve and parse input values
                String supplierName = Nameedt.getText().toString();
                int id = Integer.parseInt(idedt.getText().toString());
                int done = Integer.parseInt(doneedt.getText().toString());

                // Call AsyncTask to update the data
                new UpdateDataTask().execute(supplierName, id, done);
            }
        });

        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the BottomSheetDialogFragment
            }
        });

        return view;
    }

    public void setDetails(Context context, String supplierName, String done, String id) {
        if (context != null) {
            supplierName1 = supplierName;
            done1 = done;
            id1 = id;
            Toast.makeText(context, supplierName + id, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(LOG_TAG, "Context is null");
        }
    }

    private class UpdateDataTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            String supplierName = (String) params[0];
            int id = (int) params[1];
            int done = (int) params[2];

            Connection connection = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectURL);

                String updateStatement = "UPDATE M_Supplier SET M_Supplier = ?, done = ? WHERE ID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateStatement)) {
                    preparedStatement.setString(1, supplierName);
                    preparedStatement.setInt(2, done);
                    preparedStatement.setInt(3, id);

                    int rowsUpdated = preparedStatement.executeUpdate();
                    return rowsUpdated > 0;
                }
            } catch (ClassNotFoundException | SQLException e) {
                Log.e(LOG_TAG, "Error updating supplier: " + e.getMessage());
                return false;
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(LOG_TAG, "Error closing connection: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
