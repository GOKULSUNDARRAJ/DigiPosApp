package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

public class ActiveCountProductFragment extends Fragment {
    private String barcodeValue;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private boolean isBarcodeDetected = false;
    private BroadcastReceiver scanReceiver;
    TextView statusView;

    private String description, barcode, subDepartment, supplier, department, vat, ageLimit,
            Itemcode, Brand, UnitPerCase, CostPerCase, Price, sellingprice, Margin, plu, outerBarcode, price,
            addbarcode, endDate, startDate, dd_Price, ddpoint, manageStock, weight, capacitys, currentStock1,
            qty, minStock, reorderleve, Markup, discount, expiry_date, buyPrice, CasePrice, CaseUnit, VatValue1;


    String TAG="ActiveCountProductFragment";
    EditText searchbarcode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_active_count_product, container, false);




                ImageView home=view.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });


        statusView = view.findViewById(R.id.product_status);

        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password


        ImageView camera=view.findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentactive productManagementFragment = new BarCodeScanFragmentactive();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });


        ImageView back=view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductmagementfullFragment productManagementFragment = new ProductmagementfullFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        searchbarcode=view.findViewById(R.id.barcodeedt);

        searchbarcode.setText(barcode);

        Button searchbutton=view.findViewById(R.id.findbarcodeedt);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barcodeValue=searchbarcode.getText().toString();
                requireActivity().runOnUiThread(() -> {
                    new DatabaseTask().execute(); // Call DatabaseTask with barcode value
                });

            }
        });


        searchbarcode.requestFocus();


        scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("nlscan.action.SCANNER_RESULT".equals(intent.getAction())) {
                    String scanResult = intent.getStringExtra("SCAN_BARCODE1"); // Get scanned text
                    if (scanResult != null) {
                        searchbarcode.setText(scanResult); // Set scanned QR code text in EditText
                    }
                }
            }
        };

        // Register the receiver using requireContext()
        IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        registerReceiver(requireContext(), scanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);



        searchbarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                barcodeValue=searchbarcode.getText().toString();
                requireActivity().runOnUiThread(() -> {
                    new DatabaseTask().execute(); // Call DatabaseTask with barcode value
                });
                v.clearFocus(); // Hide keyboard
                return true;
            }
            return false;
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scanReceiver != null) {
            requireContext().unregisterReceiver(scanReceiver); // Unregister receiver in Fragment
        }
    }

    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department,id,Expiry_date,
                saleWithVAT, Markup,discount, costPerCase, price, vat, margin, ageLimit,itemcode,Brand,UnitPerCase,currentstock,minStock,reorderleve,
                CostPerCase,Price,sellingprice,Margin,outerbarcode,costprice,addbarcode,startDate,enddate,dd_price,ddpoints,manageStock,weight,capatitys,CasePrice,
                CaseUnit,VatValue, activeProduct;

        @Override
        protected Void doInBackground(Void... voids) {
            String searchQuery = barcodeValue; // Get the search query from the barcode
            String query = ""; // Initialize query
            String filterValue = ""; // Initialize filter value

            if (!searchQuery.isEmpty()) {
                // If the search query is not empty, search by the query
                query = "SELECT * FROM tbl_Products WHERE Barcode = ?";
                filterValue = searchQuery;
            } else {
                return null; // Exit early if both search query and barcode are empty
            }

            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;

            try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, filterValue); // Set search query as the filter
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getString("PLU");
                        plu = resultSet.getString("PLU");
                        description = resultSet.getString("Description");
                        barcode = resultSet.getString("Barcode");
                        subDepartment = resultSet.getString("Sub_Department");
                        supplier = resultSet.getString("Supplier");
                        buyPrice = resultSet.getString("Buy_Price");
                        quantity = resultSet.getString("Quantity");
                        department = resultSet.getString("Department");
                        saleWithVAT = resultSet.getString("SaleWithVAT");
                        discount = resultSet.getString("Discount");
                        costPerCase = resultSet.getString("CostPerCase");
                        price = resultSet.getString("Price");
                        vat = resultSet.getString("VAT");
                        margin = resultSet.getString("Margin");
                        ageLimit = resultSet.getString("Age_Limit");
                        itemcode=resultSet.getString("Item_code");
                        Brand=resultSet.getString("Brand");
                        UnitPerCase=resultSet.getString("UnitPerCase");
                        CostPerCase=resultSet.getString("CostPerCase");
                        Price=resultSet.getString("Price");
                        Markup=resultSet.getString("Markup");
                        sellingprice=resultSet.getString("SS_PRICE");
                        Margin=resultSet.getString("Margin");
                        outerbarcode=resultSet.getString("OuterBarcode");
                        costprice=resultSet.getString("Price");
                        addbarcode=resultSet.getString("AdditionalBarcode1");
                        startDate=resultSet.getString("StartDate");
                        enddate=resultSet.getString("EndDate");
                        dd_price=resultSet.getString("DD_Price");
                        ddpoints=resultSet.getString("SS_POINTS");
                        manageStock=resultSet.getString("ManageStock");
                        weight=resultSet.getString("Weight");
                        capatitys=resultSet.getString("Capacity");
                        currentstock=resultSet.getString("CurrentStock");
                        minStock=resultSet.getString("MinStock");
                        reorderleve=resultSet.getString("ReorderLevel");
                        Expiry_date=resultSet.getString("Expiry_date");
                        CasePrice=resultSet.getString("CasePrice");
                        CaseUnit=resultSet.getString("CaseUnit");
                        VatValue=resultSet.getString("VatValue");
                        activeProduct = resultSet.getString("ActiveProduct");

                        // Update ActiveProduct to 1 if the product is found
                        updateActiveProductStatus(connection, plu);

                        insertActiveProductLog(connection,barcode);

                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "No product found", Toast.LENGTH_SHORT).show();
                            new GetLastPLUTask().execute();
                        });
                    }
                }

            } catch (SQLException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "SQL Exception: " + e.getMessage());
            }

            return null;
        }

        // Helper method to update ActiveProduct status
        private void updateActiveProductStatus(Connection connection, String plu) throws SQLException {
            String updateQuery = "UPDATE tbl_Products SET ActiveProduct = 1 WHERE PLU = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, plu);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    Log.d(TAG, "Successfully updated ActiveProduct to 1 for PLU: " + plu);
                } else {
                    Log.w(TAG, "No rows affected when updating ActiveProduct for PLU: " + plu);
                }
            }
        }

        // Helper method to insert log entry with date and time explicitly
        private void insertActiveProductLog(Connection connection, String barcode) throws SQLException {
            String insertLogQuery = "INSERT INTO ActiveProductsLogs (Barcode, Date, Time) VALUES (?, ?, ?)";
            try (PreparedStatement logStatement = connection.prepareStatement(insertLogQuery)) {
                logStatement.setString(1, barcode); // Set barcode
                // Get current date and time
                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                java.sql.Time currentTime = new java.sql.Time(System.currentTimeMillis());

                logStatement.setDate(2, currentDate); // Set date
                logStatement.setTime(3, currentTime); // Set time

                int rows = logStatement.executeUpdate(); // Execute the insert
                if (rows > 0) {
                    Log.d(TAG, "Log inserted successfully for barcode: " + barcode);
                } else {
                    Log.w(TAG, "No log inserted for barcode: " + barcode);
                }
            }
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (plu != null) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Product activated successfully!", Toast.LENGTH_SHORT).show();
                    searchbarcode.setText(""); // Clear the EditText
                    searchbarcode.requestFocus(); // Set focus back to the EditText
                });
                isBarcodeDetected = false; // Reset detection for the next barcode scan
            }
        }
    }

    private class GetLastPLUTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String maxPLU = null;

            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;
                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
                    // SQL query to get the maximum PLU value
                    String sql = "SELECT MAX(CAST(PLU AS BIGINT)) AS MaxPLU FROM tbl_Products WHERE ISNUMERIC(PLU) = 1";
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            maxPLU = resultSet.getString("MaxPLU");
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
            }
            return maxPLU;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Create a new Bundle to pass the barcode value
                Bundle bundle = new Bundle();
                bundle.putString("barcode", barcodeValue);
                bundle.putString("fromActiveCountProductFragment", "fromActiveCountProductFragment");

                // Handle PLU value safely
                int pluValue;
                if (result != null && !result.trim().isEmpty()) {
                    try {
                        pluValue = Integer.parseInt(result.trim()) + 1;
                    } catch (NumberFormatException e) {
                        pluValue = 3001; // Default value if parsing fails
                    }
                } else {
                    pluValue = 3001; // Default value if result is null or empty
                }
                bundle.putString("PLU", String.valueOf(pluValue));

                // Create and show the fragment
                ProductManagmentAddFragment productManagementFragment = new ProductManagmentAddFragment();
                productManagementFragment.setArguments(bundle);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } catch (Exception e) {
                Log.e(TAG, "Error in onPostExecute: ", e);
                Toast.makeText(requireActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Markup = getArguments().getString("Markup");
            expiry_date = getArguments().getString("Expiry_date");
            buyPrice = getArguments().getString("buyPrice");
            CasePrice = getArguments().getString("CasePrice");
            CaseUnit = getArguments().getString("CaseUnit");
            VatValue1 = getArguments().getString("VatValue");
        }
    }

}