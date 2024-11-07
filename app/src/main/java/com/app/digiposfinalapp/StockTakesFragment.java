package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class StockTakesFragment extends Fragment {

    ImageView cameraImg;

    private String description, barcode, subDepartment, supplier, department, vat, ageLimit,Itemcode,
            Brand,UnitPerCase,CostPerCase,Price,sellingprice,Margin,plu,outerBarcode,price,addbarcode,
            endDate,startDate,dd_Price,ddpoint,manageStock,weight,capacitys,currentStock1,qty,minStock,reorderleve,discount,stockTake;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    EditText barcodeedt;
    Button findbarcodeedt;
    private boolean isBarcodeDetected = false;
    Bundle bundle;

    String laststocktakeid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_takes, container, false);

        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password



        cameraImg=view.findViewById(R.id.Camera);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanStockTakeFragment productManagementFragment = new BarCodeScanStockTakeFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        barcodeedt=view.findViewById(R.id.barcodeedt);
        barcodeedt.setText(barcode);

        findbarcodeedt = view.findViewById(R.id.findbarcodeedt);
        findbarcodeedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatabaseTask().execute(); // Call DatabaseTask with barcode value

            }
        });

        return view;
    }


    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department,id,
                saleWithVAT, discount, costPerCase, price, vat, margin, ageLimit,itemcode,Brand,UnitPerCase,currentstock,minStock,reorderleve,
                CostPerCase,Price,sellingprice,Margin,outerbarcode,costprice,addbarcode,startDate,enddate,dd_price,ddpoints,manageStock,weight,capatitys;

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            String searchQuery = barcodeedt.getText().toString(); // Get the search query from the barcode
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




                        bundle = new Bundle();
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
                        bundle.putString("StockTake", laststocktakeid);




                        new StockTakesFragment.GetLastPLUTask().execute();


                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "No product found", Toast.LENGTH_SHORT).show();
                            ProductManagmentAddFragment productManagementFragment = new ProductManagmentAddFragment();
                            FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                            fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                            fragmentTransaction.commit();

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



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Update UI with the retrieved data
            if (plu != null) {
                requireActivity().runOnUiThread(() -> {
                    // Update the TextView with product details

                    Toast.makeText(getContext(), "Product details updated", Toast.LENGTH_SHORT).show();
                });
                isBarcodeDetected = false; // Reset detection for the next barcode scan
            }
        }

    }

    private class GetLastPLUTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            laststocktakeid = null;

            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;
                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
                    // SQL query to get the last PLU value
                    String sql = "SELECT TOP 1 StockTakeID FROM StockTake ORDER BY ID DESC"; // Assuming ID is auto-incremented
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {

                        if (resultSet.next()) {
                            laststocktakeid = resultSet.getString("StockTakeID");
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
            }
            return laststocktakeid;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Do something with the last PLU, like updating the UI
                Toast.makeText(requireActivity(), "Stock Take Id" + result, Toast.LENGTH_LONG).show();


                // Create the ProductManagmentEditFragment and set arguments
                CountProductFragment productManagementFragment = new CountProductFragment();
                productManagementFragment.setArguments(bundle); // Set the bundle as arguments
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();


            } else {
                Toast.makeText(requireActivity(), "Empty", Toast.LENGTH_LONG).show();
                // Create the ProductManagementEditFragment and set arguments
                CountProductFragment productManagementFragment = new CountProductFragment();
                Bundle args = new Bundle();
                args.putString("StockTake", "3000"); // Use a key to identify the data
                args.putString("barcode", barcode); // Use a key to identify the data
                productManagementFragment.setArguments(args);
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();


            }
        }
    }

}