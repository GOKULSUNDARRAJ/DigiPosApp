package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockSnapshotFragment extends Fragment {
    private static final String TAG = "StockSnapshotFragment";

    private String barcode1, description, OuterBarcode;
    private String ipAddress, portNumber, databaseName, username, password;

    private TextView currentqtytxt;
    private EditText qtyedt;
    private Button savestocktakebtn;
    int currectqty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_snapshot, container, false);

        ImageView home = view.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");

        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        currentqtytxt = view.findViewById(R.id.currentqtytxt);
        qtyedt = view.findViewById(R.id.qtyedt);
        savestocktakebtn = view.findViewById(R.id.savestocktakebtn);

        TextView descriptiontxt = view.findViewById(R.id.description);
        descriptiontxt.setText(description);

        // Fetch system quantity
        new CheckBarcodeExistsTask().execute(barcode1);

        // Save button click
        savestocktakebtn.setOnClickListener(v -> {
            String qtyStr = qtyedt.getText().toString().trim();
            if (qtyStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter physical quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int physicalQty = Integer.parseInt(qtyStr);
            new InsertStockSnapshotTask().execute(physicalQty);
        });

        ImageView backimage = view.findViewById(R.id.imageView);
        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockSnapshotsearchFragment productManagementFragment = new StockSnapshotsearchFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private class CheckBarcodeExistsTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String barcode = params[0];
            Connection connection = null;
            int quantity = -1;

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionString, username, password);

                String query = "SELECT Quantity FROM tbl_SoldItems WHERE Barcode = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, barcode);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    quantity = resultSet.getInt("Quantity");
                }

                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                Log.e(TAG, "Database Error", e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "Failed to close connection", e);
                    }
                }
            }
            return quantity;
        }

        @Override
        protected void onPostExecute(Integer quantity) {
            if (quantity >= 0) {
                currentqtytxt.setText("THE CURRENT STOCK IS " + quantity);
                currectqty = quantity;
            } else {
                Toast.makeText(getContext(), "Barcode does not exist in the database.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class InsertStockSnapshotTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int physicalQty = params[0];
            Connection connection = null;
            boolean success = false;

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionString, username, password);

                // 1. Determine BatchID based on last row
                int nextBatchID = 3001; // Default starting value
                String lastRowQuery = "SELECT TOP 1 BatchID, Status FROM StockSnapShot ORDER BY ID DESC";
                PreparedStatement lastRowStmt = connection.prepareStatement(lastRowQuery);
                ResultSet rs = lastRowStmt.executeQuery();
                if (rs.next()) {
                    int lastBatchID = rs.getInt("BatchID");
                    int lastStatus = rs.getInt("Status");
                    if (lastStatus == 0) {
                        nextBatchID = lastBatchID; // Continue with same batch
                    } else {
                        nextBatchID = lastBatchID + 1; // Increment batch
                    }
                }
                rs.close();
                lastRowStmt.close();

                // 2. Check if row already exists for same BatchID and Barcode
                String checkQuery = "SELECT ID FROM StockSnapShot WHERE BatchID = ? AND Barcode = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                checkStmt.setInt(1, nextBatchID);
                checkStmt.setString(2, barcode1);
                ResultSet checkRs = checkStmt.executeQuery();

                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                java.sql.Time currentTime = new java.sql.Time(System.currentTimeMillis());

                if (checkRs.next()) {
                    // Row exists → update it
                    int existingId = checkRs.getInt("ID");
                    String updateQuery = "UPDATE StockSnapShot SET OuterBarcode = ?, PhysicalQuantity = ?, SystemQuantity = ?, Date = ?, Time = ?, Done = ?, Status = ? WHERE ID = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setString(1, OuterBarcode);
                    updateStmt.setInt(2, physicalQty);
                    updateStmt.setInt(3, currectqty);
                    updateStmt.setDate(4, currentDate);
                    updateStmt.setTime(5, currentTime);
                    updateStmt.setInt(6, 0);
                    updateStmt.setInt(7, 0);
                    updateStmt.setInt(8, existingId);

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) success = true;
                    updateStmt.close();

                } else {
                    // Row does not exist → insert new
                    String insertQuery = "INSERT INTO StockSnapShot " +
                            "(BatchID, Barcode, OuterBarcode, PhysicalQuantity, SystemQuantity, Date, Time, Done, Status) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                    insertStmt.setInt(1, nextBatchID);
                    insertStmt.setString(2, barcode1);
                    insertStmt.setString(3, OuterBarcode);
                    insertStmt.setInt(4, physicalQty);
                    insertStmt.setInt(5, currectqty);
                    insertStmt.setDate(6, currentDate);
                    insertStmt.setTime(7, currentTime);
                    insertStmt.setInt(8, 0);
                    insertStmt.setInt(9, 0);

                    int rows = insertStmt.executeUpdate();
                    if (rows > 0) success = true;
                    insertStmt.close();
                }

                checkRs.close();
                checkStmt.close();

            } catch (SQLException e) {
                Log.e(TAG, "Insert/Update Error", e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "Failed to close connection", e);
                    }
                }
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getContext(), "Stock Snapshot saved successfully!", Toast.LENGTH_SHORT).show();

                StockSnapshotsearchFragment productManagementFragment = new StockSnapshotsearchFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } else {
                Toast.makeText(getContext(), "Failed to save Stock Snapshot.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            description = getArguments().getString("description");
            OuterBarcode = getArguments().getString("OuterBarcode");

            Log.d(TAG, "Received Barcode: " + barcode1);
        }
    }
}