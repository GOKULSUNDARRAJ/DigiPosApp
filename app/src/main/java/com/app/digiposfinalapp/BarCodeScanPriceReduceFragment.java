package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BarCodeScanPriceReduceFragment extends Fragment {

    private SurfaceView cameraPreview;
    private CameraSource cameraSource;
    private boolean isBarcodeDetected = false;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private String barcodeValue;

    ImageView back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_barcode_scanner, container, false);

        cameraPreview = view.findViewById(R.id.camera_preview);
        back=view.findViewById(R.id.imageView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment productManagementFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password

        // Check camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_PERMISSION_REQUEST_CODE
            );
        } else {
            startCameraPreview();
        }



        return view;

    }

    private void startCameraPreview() {

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024) // Adjust as per your preference
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (cameraSource != null) {
                    cameraSource.stop();
                }
            }

        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0 && !isBarcodeDetected) {
                    isBarcodeDetected = true;
                    Barcode barcode = barcodes.valueAt(0);
                    barcodeValue = barcode.displayValue;
                    requireActivity().runOnUiThread(() -> {
                        new DatabaseTask().execute(); // Call DatabaseTask with barcode value
                    });
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPreview();
            } else {
                Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop(); // Stop camera preview when the fragment is paused
        }
    }

    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department,id,
                saleWithVAT, discount, costPerCase, price, vat, margin, ageLimit,itemcode,Expiry_date,Brand,UnitPerCase,currentstock,minStock,reorderleve,
                CostPerCase,Price,sellingprice,Margin,outerbarcode,costprice,addbarcode,startDate,enddate,dd_price,ddpoints,manageStock,weight,capatitys;

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
                        Expiry_date=resultSet.getString("Expiry_date");
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
                        bundle.putString("Expiry_date", Expiry_date);

                        Log.d("PriceReduceFragment", "VAT Value: " + Expiry_date);

                        // Create the ProductManagmentEditFragment and set arguments
                        PriceReduceFragment PiceCheckFragment = new PriceReduceFragment();
                        PiceCheckFragment.setArguments(bundle); // Set the bundle as arguments
                        FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, PiceCheckFragment);
                        fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                        fragmentTransaction.commit();

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
            String lastPLU = null;

            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;
                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
                    // SQL query to get the last PLU value
                    String sql = "SELECT TOP 1 PLU FROM tbl_Products ORDER BY ID DESC"; // Assuming ID is auto-incremented
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {

                        if (resultSet.next()) {
                            lastPLU = resultSet.getString("PLU");
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
            }
            return lastPLU;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Do something with the last PLU, like updating the UI
                Toast.makeText(requireActivity(), "Last PLU: " + result, Toast.LENGTH_LONG).show();

                // Create a new Bundle to pass the barcode value
                Bundle bundle = new Bundle();
                bundle.putString("barcode", barcodeValue); // Send the barcode value
                bundle.putString("PLU", String.valueOf(Integer.parseInt(result)+1)); // Send the barcode value

                // Create the ProductManagmentAddFragment and set arguments
                ProductManagmentAddFragment productManagementFragment = new ProductManagmentAddFragment();
                productManagementFragment.setArguments(bundle); // Set the bundle with the barcode value

                // Perform fragment transaction to display ProductManagmentAddFragment
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            } else {
                Toast.makeText(requireActivity(), "No PLU found.", Toast.LENGTH_LONG).show();

                // Create a new Bundle to pass the barcode value
                Bundle bundle = new Bundle();
                bundle.putString("barcode", barcodeValue); // Send the barcode value
                bundle.putString("PLU", "3001"); // Send the barcode value

                // Create the ProductManagmentAddFragment and set arguments
                ProductManagmentAddFragment productManagementFragment = new ProductManagmentAddFragment();
                productManagementFragment.setArguments(bundle); // Set the bundle with the barcode value

                // Perform fragment transaction to display ProductManagmentAddFragment
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        }
    }
}
