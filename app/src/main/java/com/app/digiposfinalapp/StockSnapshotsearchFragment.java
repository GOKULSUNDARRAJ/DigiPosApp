package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockSnapshotsearchFragment extends Fragment {

    private ImageView cameraImg;
    private EditText barcodeedt;
    private Button findbarcodeedt;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private String barcodeValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_snapshotsearch, container, false);

        // Home click
        ImageView home = view.findViewById(R.id.home);
        home.setOnClickListener(v -> {
            HomeFragment bottomBarFragment = new HomeFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // SharedPreferences for DB connection
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;
        dbPassword1 = Constants.PASSWORD;

        // Camera click
        cameraImg = view.findViewById(R.id.Camera);
        cameraImg.setOnClickListener(v -> {
            BarCodeScanStockSnapFragment productManagementFragment = new BarCodeScanStockSnapFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Back image click
        ImageView backimage = view.findViewById(R.id.imageView);
        backimage.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Save Stock Snapshot")
                    .setMessage("Do you want to save as Draft or Complete?")
                    .setPositiveButton("Complete", (dialog, which) -> {
                        new Thread(() -> {
                            String lastBatchId = getLastBatchId();
                            updateStatusToComplete(lastBatchId);
                            navigateToStockSubFragment();
                        }).start();
                    })
                    .setNegativeButton("Draft", (dialog, which) -> {
                        // Navigate back to StockSubFragment for Draft as well
                        navigateToStockSubFragment();
                    })
                    .setCancelable(true)
                    .show();
        });

        // Barcode input
        barcodeedt = view.findViewById(R.id.barcodeedt);
        findbarcodeedt = view.findViewById(R.id.findbarcodeedt);
        findbarcodeedt.setOnClickListener(v -> {
            barcodeValue = barcodeedt.getText().toString();
            new DatabaseTask().execute();
        });

        // Scan receiver
        BroadcastReceiver scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("nlscan.action.SCANNER_RESULT".equals(intent.getAction())) {
                    String scanResult = intent.getStringExtra("SCAN_BARCODE1");
                    if (scanResult != null) {
                        barcodeedt.setText(scanResult);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        ContextCompat.registerReceiver(requireContext(), scanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);

        barcodeedt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                barcodeValue = barcodeedt.getText().toString();
                new DatabaseTask().execute();
                v.clearFocus();
                return true;
            }
            return false;
        });

        return view;
    }

    // ------------------ Database Task ------------------
    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department,id,
                saleWithVAT, discount, costPerCase, price, vat, margin, ageLimit,itemcode,Brand,UnitPerCase,currentstock,minStock,reorderleve,
                CostPerCase,Price,sellingprice,Margin,outerbarcode,costprice,addbarcode,startDate,enddate,dd_price,ddpoints,manageStock,weight,capatitys;

        @Override
        protected Void doInBackground(Void... voids) {
            if (barcodeValue == null || barcodeValue.isEmpty()) return null;

            String query = "SELECT * FROM tbl_Products WHERE Barcode = ?";
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;

            try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, barcodeValue);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Fetch all required columns
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

                        // Pass data to fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("plu", plu);
                        bundle.putString("description", description);
                        bundle.putString("barcode", barcode);
                        bundle.putString("subDepartment", subDepartment);
                        bundle.putString("supplier", supplier);
                        bundle.putString("buyPrice", buyPrice);
                        bundle.putString("quantity", quantity);
                        bundle.putString("department", department);
                        bundle.putString("saleWithVAT", saleWithVAT);
                        bundle.putString("discount", discount);
                        bundle.putString("costPerCase", costPerCase);
                        bundle.putString("price", price);
                        bundle.putString("vat", vat);
                        bundle.putString("margin", margin);
                        bundle.putString("ageLimit", ageLimit);
                        bundle.putString("Itemcode", itemcode);
                        bundle.putString("Brand", Brand);
                        bundle.putString("UnitPerCase", UnitPerCase);
                        bundle.putString("CostPerCase", CostPerCase);
                        bundle.putString("Price", Price);
                        bundle.putString("SS_PRICE", sellingprice);
                        bundle.putString("Margin", Margin);
                        bundle.putString("OuterBarcode", outerbarcode);
                        bundle.putString("Price", costprice);
                        bundle.putString("AdditionalBarcode1", addbarcode);

                        bundle.putString("ID", id);
                        bundle.putString("StartDate", startDate);
                        bundle.putString("EndDate", enddate);
                        bundle.putString("DD_Price", dd_price);
                        bundle.putString("SS_POINTS", ddpoints);
                        bundle.putString("ManageStock", manageStock);
                        bundle.putString("Weight", weight);
                        bundle.putString("Capacity", capatitys);
                        bundle.putString("CurrentStock", currentstock);
                        bundle.putString("MinStock", minStock);
                        bundle.putString("Reorderleve", reorderleve);
                        bundle.putString("Discount", discount);

                        StockSnapshotFragment productManagementFragment = new StockSnapshotFragment();
                        productManagementFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "No product found", Toast.LENGTH_SHORT).show();

                            new GetLastPLUTask().execute();

                        });
                    }
                }

            } catch (SQLException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show());
                Log.e(TAG, "SQL Exception: " + e.getMessage());
            }
            return null;
        }
    }

    // ------------------ Get Last BatchID ------------------
    private String getLastBatchId() {
        String lastBatchId = null;
        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;
        try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
            String sql = "SELECT TOP 1 BatchID FROM [STAR_RETAIL].[dbo].[StockSnapShot] ORDER BY ID DESC";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    lastBatchId = resultSet.getString("BatchID");
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
        }
        return lastBatchId;
    }

    // ------------------ Update Last Row Status ------------------
    // ------------------ Update All Rows Status ------------------
    private void updateStatusToComplete(String batchId) {
        if (batchId == null) return;
        String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;
        try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
            String sql = "UPDATE [STAR_RETAIL].[dbo].[StockSnapShot] SET Status = 1 WHERE BatchID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, batchId);
                int rowsUpdated = statement.executeUpdate();
                Log.d(TAG, "Rows updated: " + rowsUpdated);
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
        }
    }


    // ------------------ Handle Back Press ------------------
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Save Stock Snapshot")
                            .setMessage("Do you want to save as Draft or Complete?")
                            .setPositiveButton("Complete", (dialog, which) -> {
                                new Thread(() -> {
                                    String lastBatchId = getLastBatchId();
                                    updateStatusToComplete(lastBatchId);
                                    navigateToStockSubFragment();
                                }).start();
                            })
                            .setNegativeButton("Draft", (dialog, which) -> {
                                // Navigate back to StockSubFragment for Draft as well
                                navigateToStockSubFragment();
                            })
                            .setCancelable(true)
                            .show();
                }
            });
        }
    }

    // Helper method to navigate to StockSubFragment
    private void navigateToStockSubFragment() {
        requireActivity().runOnUiThread(() -> {
            StockSubFragment productManagementFragment = new StockSubFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
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
                bundle.putString("StockSnapshotsearchFragment", "StockSnapshotsearchFragment");

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



}
