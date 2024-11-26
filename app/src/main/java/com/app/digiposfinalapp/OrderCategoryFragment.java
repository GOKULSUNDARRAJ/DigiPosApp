package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OrderCategoryFragment extends Fragment {
    CardView cardView1;
    String ipAddress, portNumber, databaseName, username, password;

    // Order data fields to insert
    String orderDate;
    String orderId;
    String plu;
    String barcode;
    String description;
    int quantity;
    String supplier;
    double costPerCase;
    int unitPerCase;
    int done;
    double cost;
    int units;
    double totalCost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_category, container, false);
        cardView1 = view.findViewById(R.id.card1);

        // Retrieve database connection details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Log the retrieved values (avoid logging sensitive info like passwords)
        Log.d("OrderCategoryFragment", "Database details: IP=" + ipAddress + ", Port=" + portNumber + ", DB=" + databaseName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        // Order data fields to insert (example)
        orderDate = dateFormat.format(date); // Example date
        plu = "";           // Example PLU
        barcode = "";    // Example Barcode
        description = ""; // Example Description
        quantity = 0;               // Example Quantity
        supplier = "";   // Example Supplier
        costPerCase = 0;      // Example Cost per Case
        unitPerCase = 0;           // Example Units per Case
        done = 0;                   // Example Done status
        cost = 0;            // Example Total Cost
        units = 0;                // Example Units
        totalCost = 0;       // Example Total Cost (Inc VAT)

        // Set the onClickListener for the card view
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrderCategoryFragment", "Card clicked, fetching last OrderID");
                // Insert data into the database
                getNextOrderIdFromDatabase();
            }
        });

        return view;
    }

    private void getNextOrderIdFromDatabase() {
        // Define the SQL query to get all OrderIDs
        String query = "SELECT OrderID FROM tbl_Order ORDER BY OrderID ASC";
        Log.d("OrderCategoryFragment", "Executing query: " + query);

        // Database connection parameters
        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
        Log.d("OrderCategoryFragment", "Database connection URL: " + connectionUrl);

        // Execute the query in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DriverManager.getConnection(connectionUrl, username, password);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    // Execute the query
                    ResultSet resultSet = preparedStatement.executeQuery();
                    Log.d("OrderCategoryFragment", "Query executed, processing result");

                    List<Integer> orderIds = new ArrayList<>();

                    // Retrieve all OrderIDs and extract the numeric part
                    while (resultSet.next()) {
                        String orderId = resultSet.getString("OrderID");
                        if (orderId != null && !orderId.isEmpty()) {
                            String numericPart = orderId.replaceAll("[^0-9]", "");
                            if (!numericPart.isEmpty()) {
                                orderIds.add(Integer.parseInt(numericPart));
                            }
                        }
                    }

                    if (!orderIds.isEmpty()) {
                        // Sort the OrderIDs
                        Collections.sort(orderIds);

                        // Find the next available OrderID
                        int nextOrderId = orderIds.get(orderIds.size() - 1) + 1; // Increment the largest OrderID
                        String newOrderId = "ORD" + String.format("%04d", nextOrderId); // Format the new OrderID with leading zeros

                        Log.d("OrderCategoryFragment", "New Order ID: " + newOrderId);

                        // Check for gaps in the sequence
                        for (int i = 0; i < orderIds.size() - 1; i++) {
                            int currentId = orderIds.get(i);
                            int nextId = orderIds.get(i + 1);

                            if (nextId != currentId + 1) {
                                // Gap found, use the missing ID
                                newOrderId = "ORD" + String.format("%04d", currentId + 1);
                                break;
                            }
                        }

                        // Use the final newOrderId for the next order
                        insertOrderIntoDatabase(newOrderId);
                    } else {
                        // If no orders exist, start from ORD5001
                        String defaultOrderId = "ORD5001";
                        Log.d("OrderCategoryFragment", "No orders found, assigning default Order ID: " + defaultOrderId);
                        insertOrderIntoDatabase(defaultOrderId);
                    }

                } catch (SQLException e) {
                    Log.e("OrderCategoryFragment", "Error retrieving OrderIDs: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Error fetching Order IDs", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start(); // Run the database operation in a background thread
    }




    // Method to insert order into database
    private void insertOrderIntoDatabase(String orderid) {
        String insertQuery = "INSERT INTO tbl_Order (OrderDate, OrderID, PLU, Barcode, Description, Quantity, Supplier, CostPerCase, UnitPerCase, done, Cost, Units, [Total_Cost (Inc VAT)]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Log.d("OrderCategoryFragment", "Executing insert query: " + insertQuery);

        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
        Log.d("OrderCategoryFragment", "Database connection URL for insertion: " + connectionUrl);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try (Connection connection = DriverManager.getConnection(connectionUrl, username, password);
                     PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                    // Set parameters
                    preparedStatement.setString(1, orderDate);
                    preparedStatement.setString(2, orderid);
                    preparedStatement.setString(3, plu);
                    preparedStatement.setString(4, barcode);
                    preparedStatement.setString(5, description);
                    preparedStatement.setInt(6, quantity);
                    preparedStatement.setString(7, supplier);
                    preparedStatement.setDouble(8, costPerCase);
                    preparedStatement.setInt(9, unitPerCase);
                    preparedStatement.setInt(10, done);
                    preparedStatement.setDouble(11, cost);
                    preparedStatement.setInt(12, units);
                    preparedStatement.setDouble(13, totalCost);

                    // Execute the query
                    int rowsAffected = preparedStatement.executeUpdate();
                    Log.d("OrderCategoryFragment", "Rows inserted: " + rowsAffected);

                    AddOrderFragment addOrderFragment = new AddOrderFragment();

                    // Create a Bundle and add the OrderID
                    Bundle bundle = new Bundle();
                    bundle.putString("orderID", orderid); // Pass the OrderID
                    addOrderFragment.setArguments(bundle); // Set the bundle as arguments

                    // Navigate to AddOrderFragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, addOrderFragment); // Update the layout
                    fragmentTransaction.addToBackStack(null); // Add to back stack for back navigation
                    fragmentTransaction.commit();


                    // Update UI in the main thread if necessary
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Optional: Provide user feedback, like a Toast
                            Toast.makeText(getContext(), "Order inserted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (SQLException e) {
                    Log.e("OrderCategoryFragment", "Database insertion error: " + e.getMessage());
                }
            }
        }).start();
    }
}
