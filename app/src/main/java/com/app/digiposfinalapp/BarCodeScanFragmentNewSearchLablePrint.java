package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.registerReceiver;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.digiposfinalapp.util.SettingsHelper;
import com.app.digiposfinalapp.util.UIHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.device.ZebraIllegalArgumentException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BarCodeScanFragmentNewSearchLablePrint extends Fragment {

    private String barcodeValue;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private boolean isBarcodeDetected = false;
    private BroadcastReceiver scanReceiver;
    EditText searchbarcode;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_bar_code_scan_new_search_lableprint, container, false);





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
                LablePrintFragment productManagementFragment = new LablePrintFragment();
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
                HomeFragment productManagementFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

         searchbarcode=view.findViewById(R.id.barcodeedt);

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
                CaseUnit,VatValue;

        @SuppressLint("WrongThread")
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
                        bundle.putString("Markup", Markup);
                        bundle.putString("CasePrice", CasePrice);
                        bundle.putString("CaseUnit", CaseUnit);
                        bundle.putString("VatValue", VatValue);
                        bundle.putString("Expiry_date", Expiry_date);

                        new InsertBarcodeTask(plu, barcode, description, "NULL", price, capatitys, quantity).execute();

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

                    //    Toast.makeText(getContext(), "Product details updated", Toast.LENGTH_SHORT).show();
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
                bundle.putString("fromLablePrintFragment", "fromLablePrintFragment");

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


    private class InsertBarcodeTask extends AsyncTask<Void, Void, Boolean> {
        private String plu;
        private String barcode;
        private String detail;
        private String shop;
        private String price;
        private String capacity;
        private String quantity;
        private String currentDate;

        public InsertBarcodeTask(String plu, String barcode, String detail, String shop,
                                 String price, String capacity, String quantity) {
            this.plu = plu;
            this.barcode = barcode;
            this.detail = detail;
            this.shop = shop;
            this.price = price;
            this.capacity = capacity;
            this.quantity = quantity;

            // Format date as dd/MM/yyyy (e.g., 01/04/2025)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            this.currentDate = sdf.format(new Date());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;

            try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
                String sql = "INSERT INTO tblBarcode (PLU, Barcode, Detail, Shop, Price, dtDate, Capacity, Qty) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, plu);
                    statement.setString(2, barcode);
                    statement.setString(3, detail);
                    statement.setString(4, shop);
                    statement.setString(5, "£"+price);
                    statement.setString(6, currentDate); // Pass the formatted date string
                    statement.setString(7, capacity);
                    statement.setString(8, quantity);

                    int rowsAffected = statement.executeUpdate();
                    return rowsAffected > 0;
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception while inserting barcode: " + e.getMessage(), e);
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            requireActivity().runOnUiThread(() -> {
                if (success) {
                    searchbarcode.setText("");
                    Toast.makeText(getContext(), "Barcode saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to save barcode", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}