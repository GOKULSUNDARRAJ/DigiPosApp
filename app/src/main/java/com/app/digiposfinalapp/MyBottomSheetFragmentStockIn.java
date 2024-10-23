package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyBottomSheetFragmentStockIn extends BottomSheetDialogFragment {

    private int id1, plu1, currentStock1, stockIn1, stockOut1, tillNo1, logId1;
    private String description1, barcode1, date1, time1, type1, reason1, scaleType1, userName1, shortDescription1;
    private double currentPrice1;

    private EditText idedt, barcodeEditText, pluEditText, descriptionEditText, dateEditText, timeEditText,
            typeEditText, reasonEditText, currentStockEditText, stockInEditText, stockOutEditText,
            scaleTypeEditText, currentPriceEditText, tillNoEditText, usernameEditText, logIdEditText, shortDescriptionEditText;

    private Button addButton;

    String ipAddress, portNumber, databaseName, username, password;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_edit_stockin, container, false);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");

        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize EditText fields and Button

        idedt = view.findViewById(R.id.idedt);
        barcodeEditText = view.findViewById(R.id.autocompleteTextView2);
        pluEditText = view.findViewById(R.id.autocompleteTextView24);
        descriptionEditText = view.findViewById(R.id.desedt);
        dateEditText = view.findViewById(R.id.autocompleteTextView246);
        timeEditText = view.findViewById(R.id.timeedt);
        typeEditText = view.findViewById(R.id.autocompleteTextView24676);
        reasonEditText = view.findViewById(R.id.autocompleteTextView624676);
        currentStockEditText = view.findViewById(R.id.dayedt);
        stockInEditText = view.findViewById(R.id.doneedt);
        stockOutEditText = view.findViewById(R.id.stockinedt);
        scaleTypeEditText = view.findViewById(R.id.scaltypeedt);
        currentPriceEditText = view.findViewById(R.id.currentpriceedt);
        tillNoEditText = view.findViewById(R.id.tillnoedt);
        usernameEditText = view.findViewById(R.id.usernameedt);
        logIdEditText = view.findViewById(R.id.logidedtn);
        shortDescriptionEditText = view.findViewById(R.id.shortdescriptionedt);
        addButton = view.findViewById(R.id.Add);

        // Set data in EditText fields
        idedt.setText(String.valueOf(id1));
        barcodeEditText.setText(barcode1);
        pluEditText.setText(String.valueOf(plu1));
        descriptionEditText.setText(description1);
        dateEditText.setText(date1);
        timeEditText.setText(time1);
        typeEditText.setText(type1);
        reasonEditText.setText(reason1);
        currentStockEditText.setText(String.valueOf(currentStock1));
        stockInEditText.setText(String.valueOf(stockIn1));
        stockOutEditText.setText(String.valueOf(stockOut1));
        scaleTypeEditText.setText(scaleType1);
        currentPriceEditText.setText(String.valueOf(currentPrice1));
        tillNoEditText.setText(String.valueOf(tillNo1));
        usernameEditText.setText(userName1);
        logIdEditText.setText(String.valueOf(logId1));
        shortDescriptionEditText.setText(shortDescription1);

        // Close button logic
        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(v -> dismiss());

        // Add button logic to update stock in/out
        addButton.setOnClickListener(view1 -> {
            // Execute the AsyncTask to update stock in/out
            new UpdateStockTask().execute(
                    barcodeEditText.getText().toString(),
                    Integer.parseInt(pluEditText.getText().toString()),
                    descriptionEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    timeEditText.getText().toString(),
                    typeEditText.getText().toString(),
                    reasonEditText.getText().toString(),
                    Integer.parseInt(currentStockEditText.getText().toString()),
                    Integer.parseInt(stockInEditText.getText().toString()),
                    Integer.parseInt(stockOutEditText.getText().toString()),
                    scaleTypeEditText.getText().toString(),
                    Double.parseDouble(currentPriceEditText.getText().toString()),
                    Integer.parseInt(tillNoEditText.getText().toString()),
                    usernameEditText.getText().toString(),
                    Integer.parseInt(logIdEditText.getText().toString()),
                    shortDescriptionEditText.getText().toString()
            );
        });

        return view;
    }

    // Method to set details
    public void setDetails(Context context, int id, String barcode, int plu, String description, String date, String time, String type, String reason, int currentStock, int stockIn, int stockOut, String scaleType, double currentPrice, int tillNo, String userName, int logId, String shortDescription) {
        if (context != null) {
            id1 = id;
            barcode1 = barcode;
            plu1 = plu;
            description1 = description;
            date1 = date;
            time1 = time;
            type1 = type;
            reason1 = reason;
            currentStock1 = currentStock;
            stockIn1 = stockIn;
            stockOut1 = stockOut;
            scaleType1 = scaleType;
            currentPrice1 = currentPrice;
            tillNo1 = tillNo;
            userName1 = userName;
            logId1 = logId;
            shortDescription1 = shortDescription;
        } else {
            Log.e("MyBottomSheetFragment", "Context is null");
        }
    }

    // AsyncTask to perform the stock update operation in the background
    private class UpdateStockTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            Connection connection = null;
            try {
                // Database connection logic
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectURL);

                // Create SQL update statement
                String updateStatement = "UPDATE tbl_Stock_in_out SET Barcode = ?, PLU = ?, Description = ?, D_date = ?, D_time = ?, Type = ?, Reason = ?, " +
                        "Current_stock = ?, Stock_in = ?, Stock_out = ?, Scale_type = ?, Current_price = ?, Till_no = ?, User_name = ?, Logid = ?, " +
                        "SHORTCUT_DESCRIPTION = ? WHERE ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateStatement);
                preparedStatement.setString(1, (String) params[0]);
                preparedStatement.setInt(2, (int) params[1]);
                preparedStatement.setString(3, (String) params[2]);
                preparedStatement.setString(4, (String) params[3]);
                preparedStatement.setString(5, (String) params[4]);
                preparedStatement.setString(6, (String) params[5]);
                preparedStatement.setString(7, (String) params[6]);
                preparedStatement.setInt(8, (int) params[7]);
                preparedStatement.setInt(9, (int) params[8]);
                preparedStatement.setInt(10, (int) params[9]);
                preparedStatement.setString(11, (String) params[10]);
                preparedStatement.setDouble(12, (double) params[11]);
                preparedStatement.setInt(13, (int) params[12]);
                preparedStatement.setString(14, (String) params[13]);
                preparedStatement.setInt(15, (int) params[14]);
                preparedStatement.setString(16, (String) params[15]);
                preparedStatement.setInt(17, id1);

                // Execute the update
                int rowsUpdated = preparedStatement.executeUpdate();
                return rowsUpdated > 0;

            } catch (ClassNotFoundException | SQLException e) {
                Log.e("Database Error", "Error: " + e.getMessage());
                return false;
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e("Database Error", "Error closing connection: " + e.getMessage());
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(getContext(), "Stock updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update stock", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
