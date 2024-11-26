package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddOrderFragment2 extends Fragment {

    private String description, barcode, subDepartment, supplier, department, vat, ageLimit, Itemcode, Brand, UnitPerCase, CostPerCase, Price, sellingprice, Margin, plu, outerBarcode, price, addbarcode, endDate, startDate, dd_Price, ddpoint, manageStock, weight, capacitys, currentStock1, qty, minStock, reorderleve, discount, supplierNameorder, orderID;

    Button savebtn;
    EditText qtyedt1;
    TextView qtyonhandedt1;
    String ipAddress, portNumber, databaseName, username, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_order2, container, false);

        // Log fragment creation
        Log.d("AddOrderFragment2", "onCreateView called");

        // Retrieve database connection details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Log.d("AddOrderFragment2", "IP: " + ipAddress + ", Port: " + portNumber);

        qtyedt1 = view.findViewById(R.id.qtyedt);
        qtyonhandedt1 = view.findViewById(R.id.qtyonhandedt);

        savebtn = view.findViewById(R.id.savebtn);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qtyedt1.getText().toString().isEmpty()) {
                    Log.d("AddOrderFragment2", "Qty entered is empty");
                    Toast.makeText(v.getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Get the values to update in the database
                    String qtyEntered = qtyedt1.getText().toString();

                    // Attempt to update the order in the database
                    updateOrderInDatabase(qtyEntered, orderID);
                }
            }
        });


        fetchQuantityByBarcode(barcode);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Log received arguments
        Log.d("AddOrderFragment2", "onCreate called, Arguments received: " + getArguments().toString());

        if (getArguments() != null) {
            description = getArguments().getString("description");
            barcode = getArguments().getString("barcode");
            subDepartment = getArguments().getString("subDepartment");
            supplier = getArguments().getString("supplier");
            department = getArguments().getString("department");
            vat = getArguments().getString("vat");
            ageLimit = getArguments().getString("ageLimit");
            Itemcode = getArguments().getString("Itemcode");
            Brand = getArguments().getString("Brand");
            UnitPerCase = getArguments().getString("UnitPerCase");
            CostPerCase = getArguments().getString("CostPerCase");
            Price = getArguments().getString("Price");
            sellingprice = getArguments().getString("SS_PRICE");
            Margin = getArguments().getString("Margin");
            plu = getArguments().getString("plu");
            outerBarcode = getArguments().getString("OuterBarcode");
            price = getArguments().getString("Price");
            addbarcode = getArguments().getString("AdditionalBarcode1");
            startDate = getArguments().getString("StartDate");
            endDate = getArguments().getString("EndDate");
            dd_Price = getArguments().getString("DD_Price");
            ddpoint = getArguments().getString("SS_POINTS");
            manageStock = getArguments().getString("ManageStock");
            weight = getArguments().getString("Weight");
            capacitys = getArguments().getString("Capacity");
            currentStock1 = getArguments().getString("CurrentStock");
            qty = getArguments().getString("quantity");
            minStock = getArguments().getString("MinStock");
            reorderleve = getArguments().getString("Reorderleve");
            discount = getArguments().getString("Discount");
            supplierNameorder = getArguments().getString("SupplierName");
            orderID = getArguments().getString("orderID");

            Log.d("AddOrderFragment2", "OrderID: " + orderID); // Log order ID
        }
    }

    private void updateOrderInDatabase(String qtyEntered, String orderID) {
        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                try {
                    // Log the connection string for debugging
                    Log.d("AddOrderFragment2", "Database connection URL: " + connectionUrl);

                    // Open connection
                    connection = DriverManager.getConnection(connectionUrl, username, password);


                    double Cost = Long.parseLong(CostPerCase) * Double.parseDouble(qtyEntered);
                    double Unit = Long.parseLong(UnitPerCase) * Double.parseDouble(qtyEntered);

                    // Define the SQL update query

                    String updateQuery = "UPDATE tbl_Order SET Quantity = ?, [Total_Cost (Inc VAT)] = ?,PLU =?,Barcode=?,Description=?,Supplier=?,CostPerCase=?,UnitPerCase=?,done=?,Cost=?,Units=? WHERE OrderID = ?";

                    // Prepare the statement
                    preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, qtyEntered);  // Use the actual quantity entered
                    preparedStatement.setDouble(2, 0);   // Use calculated total cost
                    preparedStatement.setString(3, orderID);     // Use the actual orderID
                    preparedStatement.setString(3, plu);  // Set the PLU
                    preparedStatement.setString(4, barcode);  // Set the barcode
                    preparedStatement.setString(5, description);  // Set the description
                    preparedStatement.setString(6, supplierNameorder);  // Set the supplier
                    preparedStatement.setDouble(7, Double.parseDouble(UnitPerCase));  // Set CostPerCase
                    preparedStatement.setDouble(8, Double.parseDouble(CostPerCase));  // Set CostPerCase
                    preparedStatement.setInt(9, 0);
                    preparedStatement.setDouble(10, Unit);  // Set Cost
                    preparedStatement.setString(11, String.valueOf(Cost));  // Set Units
                    preparedStatement.setString(12, orderID);  // Set OrderID


                    // Log the parameters before executing
                    Log.d("AddOrderFragment2", "Executing update with Quantity: " + qtyEntered + ", OrderID: " + orderID);

                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        // Successfully updated
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Order updated successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // No rows affected (order not found or update failed)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Failed to update order. Order not found.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (SQLException | NumberFormatException e) {
                    e.printStackTrace();
                    // Log the error and handle the exception
                    Log.e("AddOrderFragment2", "Database error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    try {
                        if (preparedStatement != null) {
                            preparedStatement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void fetchQuantityByBarcode(String barcode) {
        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                try {
                    // Log the connection string for debugging
                    Log.d("AddOrderFragment2", "Database connection URL: " + connectionUrl);

                    // Open connection
                    connection = DriverManager.getConnection(connectionUrl, username, password);

                    // Define the SQL query
                    String query = "SELECT Quantity FROM tbl_SoldItems WHERE Barcode = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, barcode);

                    // Execute the query
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        // Retrieve the quantity from the result
                        int quantity = resultSet.getInt("Quantity");

                        // Update the UI on the main thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                qtyonhandedt1.setText(String.valueOf(quantity));
                                Log.d("AddOrderFragment2", "Quantity for barcode " + barcode + ": " + quantity);
                            }
                        });
                    } else {
                        // No record found for the given barcode
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                qtyonhandedt1.setText("0");
                                Toast.makeText(getContext(), "No record found for the barcode.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("AddOrderFragment2", "Database error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    try {
                        if (preparedStatement != null) {
                            preparedStatement.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


}
