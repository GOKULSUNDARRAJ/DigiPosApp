package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Date;
import java.util.Locale;

public class AddOrderFragment2 extends Fragment {

    private String description, barcode, SupplierName, subDepartment, department, vat, ageLimit, Itemcode, Brand, UnitPerCase, CostPerCase, Price, sellingprice, Margin, plu, outerBarcode, price, addbarcode, endDate, startDate, dd_Price, ddpoint, manageStock, weight, capacitys, currentStock1, qty, minStock, reorderleve, discount, supplierNameorder, orderID;

    Button savebtn;
    EditText qtyedt1;
    TextView qtyonhandedt1;
    String ipAddress, portNumber, databaseName, username, password;

    private static final String TAG = "AddOrderFragment2";
    private String savedSupplier; // To store the supplier selected from OrdersupplierFragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_order2, container, false);

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

        // Retrieve the saved supplier from SharedPreferences
        SharedPreferences appPrefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        savedSupplier = appPrefs.getString("selected_supplier", null);
        Log.d("AddOrderFragment2", "Saved supplier: " + savedSupplier);

        qtyedt1 = view.findViewById(R.id.qtyedt);
        qtyonhandedt1 = view.findViewById(R.id.qtyonhandedt);
        savebtn = view.findViewById(R.id.savebtn);

        savebtn.setOnClickListener(v -> {
            String qtyEntered = qtyedt1.getText().toString();
            if (qtyEntered.isEmpty() || !qtyEntered.matches("\\d+")) {
                Toast.makeText(getContext(), "Please enter valid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for supplier mismatch before proceeding
            checkSupplierMismatchAndProceed(qtyEntered);
        });

        // Ensure barcode is fetched after arguments are set
        if (getArguments() != null) {
            barcode = getArguments().getString("barcode");
            fetchQuantityByBarcode(barcode);
        }

        // Back navigation
        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanOrderCreateSerachFragment productManagementFragment = new BarCodeScanOrderCreateSerachFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            description = getArguments().getString("description");
            barcode = getArguments().getString("barcode");
            subDepartment = getArguments().getString("subDepartment");
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

            Log.d("AddOrderFragment2", "OrderID: " + orderID);
            Log.d("AddOrderFragment2", "Item supplier: " + supplierNameorder);

            // DEBUG: Check if we have both suppliers
            SharedPreferences appPrefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            savedSupplier = appPrefs.getString("selected_supplier", null);
            Log.d("AddOrderFragment2", "DEBUG - Saved supplier: " + savedSupplier);
            Log.d("AddOrderFragment2", "DEBUG - Item supplier: " + supplierNameorder);
            Log.d("AddOrderFragment2", "DEBUG - Are they different? " +
                    (savedSupplier != null && !savedSupplier.equals(supplierNameorder)));
        }
    }

    private void checkSupplierMismatchAndProceed(String quantity) {
        // DEBUG: Log the current state
        Log.d("AddOrderFragment2", "checkSupplierMismatchAndProceed called");
        Log.d("AddOrderFragment2", "savedSupplier: " + savedSupplier);
        Log.d("AddOrderFragment2", "supplierNameorder: " + supplierNameorder);

        // If no saved supplier, use the item's supplier
        if (savedSupplier == null || savedSupplier.isEmpty()) {
            Log.d("AddOrderFragment2", "No saved supplier, using item supplier");
            SupplierName = supplierNameorder;
            insertOrder(barcode, SupplierName, quantity, "1");
            return;
        }

        // If suppliers match, proceed normally
        if (savedSupplier.equals(supplierNameorder)) {
            Log.d("AddOrderFragment2", "Suppliers match, proceeding normally");
            SupplierName = savedSupplier;
            insertOrder(barcode, SupplierName, quantity, "1");
            return;
        }

        // Show dialog if there's a mismatch
        Log.d("AddOrderFragment2", "Suppliers don't match, showing dialog");
        showSupplierMismatchDialog(quantity);
    }

    private void showSupplierMismatchDialog(String quantity) {
        Log.d("AddOrderFragment2", "Showing supplier mismatch dialog");

        new AlertDialog.Builder(requireContext())
                .setTitle("Supplier Mismatch")
                .setMessage("You selected '" + savedSupplier + "' but this item belongs to '" +
                        supplierNameorder + "'. Do you want to proceed with '" + savedSupplier + "'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Use the saved supplier
                    Log.d("AddOrderFragment2", "User chose to use saved supplier: " + savedSupplier);
                    SupplierName = savedSupplier;
                    insertOrder(barcode, SupplierName, quantity, "1");
                    dialog.dismiss();
                })
                .setCancelable(false)
                .setOnDismissListener(dialog -> {
                    Log.d("AddOrderFragment2", "Dialog dismissed");
                })
                .show();
    }


    // Fetch quantity based on barcode
    private void fetchQuantityByBarcode(String barcode) {
        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                try {
                    Log.d("AddOrderFragment2", "Database connection URL: " + connectionUrl);

                    connection = DriverManager.getConnection(connectionUrl, username, password);

                    String query = "SELECT Quantity FROM tbl_SoldItems WHERE Barcode = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, barcode);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        int quantity = resultSet.getInt("Quantity");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                qtyonhandedt1.setText(String.valueOf(quantity));
                                Log.d("AddOrderFragment2", "Quantity for barcode " + barcode + ": " + quantity);
                            }
                        });
                    } else {
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

    public void insertOrder(String barcode, String supplier, String quantity, String done) {
        new InsertOrderTask().execute(barcode, supplier, quantity, done);
    }

    private class InsertOrderTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String barcode = params[0];
            String supplier = params[1];
            String quantity = params[2];
            String done = params[3];

            // Generate current date and time as yyyyMMddHHmmss string
            Date now = new Date();
            String formattedDateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault()).format(now);


            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                // Check if record exists (regardless of done status)
                if (recordExists(connection, barcode, supplier)) {
                    Log.d(TAG, "Record exists, updating quantity and DateTime");
                    return updateExistingRecord(connection, barcode, supplier, quantity, formattedDateTime);
                } else {
                    Log.d(TAG, "Record doesn't exist, inserting new record with DateTime");
                    return insertNewRecord(connection, barcode, supplier, quantity, done, formattedDateTime);
                }
            } catch (SQLException e) {
                Log.e(TAG, "Database error: " + e.getMessage());
                return false;
            }
        }

        private boolean recordExists(Connection connection, String barcode, String supplier) throws SQLException {
            String query = "SELECT 1 FROM HHDOrderList WHERE Barcode = ? AND Supplier = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, barcode);
                stmt.setString(2, supplier);
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean exists = rs.next();
                    Log.d(TAG, "Record exists check: " + exists);
                    return exists;
                }
            }
        }

        private boolean insertNewRecord(Connection connection, String barcode, String supplier, String quantity, String done, String formattedDateTime) throws SQLException {
            String query = "INSERT INTO HHDOrderList (Barcode, Supplier, Quantity, done, DateTime) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, barcode);
                stmt.setString(2, supplier);
                stmt.setString(3, quantity);
                stmt.setString(4, done);
                stmt.setString(5, formattedDateTime); // store date + time
                boolean success = stmt.executeUpdate() > 0;
                Log.d(TAG, "Insert successful: " + success + " with DateTime: " + formattedDateTime);
                return success;
            }
        }

        private boolean updateExistingRecord(Connection connection, String barcode, String supplier, String quantity, String formattedDateTime) throws SQLException {
            String query = "UPDATE HHDOrderList " +
                    "SET Quantity = Quantity + ?, done = 0, DateTime = ? " +
                    "WHERE Barcode = ? AND Supplier = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(quantity));
                stmt.setString(2, formattedDateTime); // store date + time
                stmt.setString(3, barcode);
                stmt.setString(4, supplier);
                int rowsUpdated = stmt.executeUpdate();
                Log.d(TAG, "Rows updated: " + rowsUpdated + " with DateTime: " + formattedDateTime);
                return rowsUpdated > 0;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                BarCodeScanOrderCreateSerachFragment productManagementFragment = new BarCodeScanOrderCreateSerachFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(getContext(), "Order saved successfully!", Toast.LENGTH_SHORT).show();
                qtyedt1.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to save order", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            // Disable back press
            ((AppCompatActivity) context).getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Do nothing to prevent back press
                }
            });
        }
    }
}