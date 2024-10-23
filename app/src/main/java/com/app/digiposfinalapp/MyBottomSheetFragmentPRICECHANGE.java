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

public class MyBottomSheetFragmentPRICECHANGE extends BottomSheetDialogFragment {


    String plu1,barcode1, detail1,  shop1, price1,  date1, capacity1;
    int qty1, id1;
    Connection connection;

    EditText idedt,pluEditText, barcodeEditText, detailEditText, shopEditText, priceEditText, dateEditText, capacityEditText, qtyEditText;

    ImageView imageView;
    private String ipAddress, portNumber, databaseName, username, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_brand_edit_price, container, false);

        // Initialize SharedPreferences and get database connection details
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;



        idedt=view.findViewById(R.id.idedt);
        pluEditText = view.findViewById(R.id.autocompleteTextView2);
        barcodeEditText = view.findViewById(R.id.autocompleteTextView24);
        detailEditText = view.findViewById(R.id.autocompleteTextView246);
        shopEditText = view.findViewById(R.id.autocompleteTextView24676);
        priceEditText = view.findViewById(R.id.autocompleteTextView624676);
        dateEditText = view.findViewById(R.id.dayedt);
        capacityEditText = view.findViewById(R.id.doneedt);
        qtyEditText = view.findViewById(R.id.autocompleteTextView);


        pluEditText.setText(plu1);
        barcodeEditText.setText(barcode1);
        detailEditText.setText(detail1);
        shopEditText.setText(shop1);
        priceEditText.setText(price1);
        dateEditText.setText(date1);
        capacityEditText.setText(capacity1);
        qtyEditText.setText(String.valueOf(qty1));
        idedt.setText(String.valueOf(id1));

        imageView=view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button Add=view.findViewById(R.id.Add);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataInDatabase(pluEditText.getText().toString(), barcodeEditText.getText().toString(), detailEditText.getText().toString(), shopEditText.getText().toString(), priceEditText.getText().toString(), dateEditText.getText().toString(), capacityEditText.getText().toString(), qtyEditText.getText().toString());

            }
        });


        return view;
    }

    public void setDetails(Context context, String plu, String barcode, String detail, String shop, String price, String date, String capacity, int qty,int id) {
        if (context != null) {
            plu1=plu;
            barcode1=barcode;
            detail1=detail;
            shop1=shop;
            price1=price;
            date1=date;
            capacity1=capacity;
            qty1=qty;
            id1=id;

        } else {
            Log.e("MyBottomSheetFragment", "Context is null");
        }
    }

    public void updateDataInDatabase(String plu, String barcode, String detail, String shop, String price, String date, String capacity, String qty) {
        new Thread(() -> {
            Connection conn = null;
            PreparedStatement preparedStatement = null;
            try {
                if (connection == null) {
                    // Establish database connection
                    try {
                        Class.forName("net.sourceforge.jtds.jdbc.Driver");
                        String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                        conn = DriverManager.getConnection(connectURL);
                    } catch (ClassNotFoundException | SQLException e) {
                        Log.e("MyBottomSheetFragmentPRICECHANGE", "Failed to establish database connection: " + e.getMessage());
                        return;
                    }
                } else {
                    conn = connection;
                }

                if (conn != null) {
                    // Prepare SQL update statement
                    String updateStatement = "UPDATE tblBarcode SET PLU = ?, Barcode = ?, Detail = ?, Shop = ?, Price = ?, dtDate = ?, Capacity = ?, Qty = ? WHERE ID = ?";
                    preparedStatement = conn.prepareStatement(updateStatement);

                    // Set parameters for the prepared statement
                    preparedStatement.setString(1, plu);
                    preparedStatement.setString(2, barcode);
                    preparedStatement.setString(3, detail);
                    preparedStatement.setString(4, shop);
                    preparedStatement.setString(5, price);
                    preparedStatement.setString(6, date);
                    preparedStatement.setString(7, capacity);
                    preparedStatement.setString(8, qty);
                    preparedStatement.setInt(9, id1);

                    // Execute the update statement
                    int rowsUpdated = preparedStatement.executeUpdate();

                    // Provide feedback to the user
                    if (rowsUpdated > 0) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show());
                    } else {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "No data updated", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("MyBottomSheetFragmentPRICECHANGE", "Connection object is null");
                }
            } catch (Exception e) {
                Log.e("MyBottomSheetFragmentPRICECHANGE", e.getMessage());
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    Log.e("MyBottomSheetFragmentPRICECHANGE", "Failed to close resources: " + e.getMessage());
                }
            }
        }).start();
    }



}
