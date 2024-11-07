package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import android.util.Log;

public class PriceCheckMainFragment extends Fragment {
    private static final String TAG = "PriceCheckMainFragment";
    String ipAddress, portNumber, databaseName, username, password;
    String barcode1,price1,productDescription;
    EditText priceedt1;
    Button savepricecheckbtn;
    String newPrice;
    String barcodeCode;
    TextView Productnametv1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_check_main, container, false);

        Log.d(TAG, "onCreateView: Initializing SharedPreferences and retrieving database credentials.");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;


        Log.d(TAG, "onCreateView: Retrieved IP: " + ipAddress + ", Port: " + portNumber);
        priceedt1=view.findViewById(R.id.priceedt);


        priceedt1.setText(price1);

        // Example usage of UpdatePriceTask
        barcodeCode = barcode1; // Example item code


        savepricecheckbtn=view.findViewById(R.id.savepricecheckbtn);
        savepricecheckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });


        Productnametv1=view.findViewById(R.id.Productnametv);

        Productnametv1.setText(productDescription);

        return view;
    }

    private class UpdatePriceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "UpdatePriceTask: Starting AsyncTask for updating price.");
        }

        @Override
        protected String doInBackground(String... params) {
            String itemCode = params[0];
            String newPrice = params[1];

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Log.d(TAG, "doInBackground: Attempting to connect to the database.");
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                if (connection != null) {
                    Log.d(TAG, "doInBackground: Database connection successful.");

                    String sql = "UPDATE tbl_Products SET Price = ? WHERE Barcode = ?";
                    statement = connection.prepareStatement(sql);

                    statement.setString(1, newPrice);
                    statement.setString(2, itemCode);

                    int rowsUpdated = statement.executeUpdate();
                    Log.d(TAG, "doInBackground: Number of rows updated: " + rowsUpdated);

                    return (rowsUpdated > 0) ? "Price Update Successful" : "Price Update Failed";
                } else {
                    Log.e(TAG, "doInBackground: Database connection failed.");
                    return "Connection Failed";
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                Log.e(TAG, "doInBackground: Integrity constraint violation", e);
                return "SQL Error: Duplicate entry for a unique field.";
            } catch (SQLException e) {
                Log.e(TAG, "doInBackground: SQL error occurred", e);
                return "SQL Error: " + e.getMessage();
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                    Log.d(TAG, "doInBackground: Database resources closed.");
                } catch (SQLException e) {
                    Log.e(TAG, "doInBackground: Error closing database resources", e);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Update result - " + result);
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            price1 = getArguments().getString("Price");
            productDescription = getArguments().getString("description");

            Log.d(TAG, "Received Barcode: " + barcode1);
        }
    }

    private void showCustomDialog() {
        // Create a custom dialog instance
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_custom_layout_pricecheck);

        Button dialogCancelButton = dialog.findViewById(R.id.dialogCancelButton);
        Button dialogOkButton = dialog.findViewById(R.id.dialogOkButton);

        dialogCancelButton.setOnClickListener(v -> dialog.dismiss());

        dialogOkButton.setOnClickListener(v -> {
            newPrice = priceedt1.getText().toString(); // New price to set
            new UpdatePriceTask().execute(barcodeCode, newPrice);
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }





}
