package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class SaleReportFragment extends Fragment {

    private static final String TAG = "SaleReportFragment"; // Tag for Logcat
    private RecyclerView recyclerView;
    private SaleReportAdapter adapter;
    String ipAddress, portNumber, databaseName, username, password;

    Spinner departmentSpinner, brandSpinner,subdepartmentSpinner,supplierSpinner;
    int departmentId,subdepartmentId;
    int BrandDone;  // This stores the selected brand's ID
    String supplierName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sale, container, false);



        ImageView back=view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportSubCategoryFragment productManagementFragment = new ReportSubCategoryFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });
        // Retrieve database connection details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Log when the fragment is created
        Log.d(TAG, "onCreateView: Fragment view is created, initializing AsyncTask.");

        recyclerView = view.findViewById(R.id.rvSaleReport); // Assuming you have RecyclerView in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize spinner and fetch department and brand data
        departmentSpinner = view.findViewById(R.id.spinner_PRODUCTCATEGORY);
        subdepartmentSpinner = view.findViewById(R.id.spinner_SUBPRODUCTCATEGORY);
        brandSpinner = view.findViewById(R.id.spinner_brand);
        supplierSpinner=view.findViewById(R.id.spinner_PRODUCTSUPPLIER);

        new FetchDepartmentData(getContext(), departmentSpinner).execute();
        new FetchSubDepartmentData(getContext(), subdepartmentSpinner).execute();
        new FetchBrandData(getContext(), brandSpinner).execute();
        new FetchSupplierData(getContext(), supplierSpinner).execute();

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Departmentspinner selectedDepartment = (Departmentspinner) parent.getItemAtPosition(position);
                departmentId = selectedDepartment.getId(); // Get the ID of the selected department
                String departmentName = selectedDepartment.getDepartment(); // Get the name of the selected department

                // Display selected department information
                Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + departmentId + ")", Toast.LENGTH_SHORT).show();

                // Trigger AsyncTask to fetch data filtered by departmentId and BrandDone
                new FetchSaleReportData().execute(departmentId, BrandDone,subdepartmentId,supplierName);  // Pass both departmentId and BrandDone to the AsyncTask
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        subdepartmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubDepartmentspinner selectedDepartment = (SubDepartmentspinner) parent.getItemAtPosition(position);
                subdepartmentId = selectedDepartment.getId(); // Get the ID of the selected department
                String departmentName = selectedDepartment.getSubDepartment(); // Get the name of the selected department
                new FetchSaleReportData().execute(departmentId, BrandDone,subdepartmentId,supplierName);  // Pass both departmentId and BrandDone to the AsyncTask
                // Display selected department information
                Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + subdepartmentId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });


        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BrandSpinner selectedBrand = (BrandSpinner) parent.getItemAtPosition(position);
                BrandDone = selectedBrand.getId(); // Get the ID of the selected brand

                String brandName = selectedBrand.getBrand();
                // Display selected brand information
                Toast.makeText(view.getContext(), "Selected: " + brandName + " (ID: " + BrandDone + ")", Toast.LENGTH_SHORT).show();

                // Trigger AsyncTask to fetch data filtered by both departmentId and BrandDone
                new FetchSaleReportData().execute(departmentId, BrandDone,subdepartmentId,supplierName);  // Pass both departmentId and BrandDone to the AsyncTask
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
                Toast.makeText(parent.getContext(), "No brand selected", Toast.LENGTH_SHORT).show();
            }
        });

        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                int supplierId = selectedSupplier.getId(); // Get the ID of the selected supplier
                supplierName = selectedSupplier.getSupplier(); // Get the name of the selected supplier
                new FetchSaleReportData().execute(departmentId, BrandDone,subdepartmentId,supplierName);
                // Display selected supplier information
                Toast.makeText(view.getContext(), "Selected: " + supplierName + " (ID: " + supplierId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });


        EditText barcodeedt=view.findViewById(R.id.barcodeedt);

        LinearLayout search=view.findViewById(R.id.button3);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (barcodeedt.getText().toString().isEmpty()){
                    new FetchSaleReportData().execute(departmentId, BrandDone,subdepartmentId,supplierName);
                }else {
                    new FetchSaleReportDatasearch().execute(barcodeedt.getText().toString());
                }

            }
        });

        return view;

    }

    private class FetchSaleReportData extends AsyncTask<Object, Void, List<SaleReportItem>> {

        @Override
        protected List<SaleReportItem> doInBackground(Object... params) {
            List<SaleReportItem> saleReportItems = new ArrayList<>();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                // Log before connecting to the database
                Log.d(TAG, "doInBackground: Connecting to SQL Server database.");

                // Use the jTDS driver for SQL Server
                String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(url, username, password);

                // Get departmentId, BrandDone, subdepartmentId, and supplierName passed from the AsyncTask
                int departmentId = (Integer) params[0];
                int brandId = (Integer) params[1];
                int subDepartmentId = (Integer) params[2];
                String supplierName = (String) params[3]; // Retrieve supplierName

                // Modify the query to filter by departmentId, BrandDone, subDepartmentId, and supplierName
                String query = "SELECT * FROM tbl_Products WHERE Department = ? AND Brand = ? AND Sub_Department = ? AND Supplier = ?";
                preparedStatement = connection.prepareStatement(query);

                preparedStatement.setInt(1, departmentId);  // Set departmentId in the query
                preparedStatement.setInt(2, brandId);  // Set brandId in the query
                preparedStatement.setInt(3, subDepartmentId);  // Set subDepartmentId in the query
                preparedStatement.setString(4, supplierName);  // Set supplierName in the query

                resultSet = preparedStatement.executeQuery();

                // Log the query execution
                Log.d(TAG, "doInBackground: Query executed: " + query);

                while (resultSet.next()) {
                    SaleReportItem item = new SaleReportItem();
                    item.setId(resultSet.getInt("ID"));
                    item.setPlu(resultSet.getString("PLU"));
                    item.setBarcode(resultSet.getString("Barcode"));
                    item.setDescription(resultSet.getString("Description"));
                    item.setSubDepartment(resultSet.getString("Sub_Department"));
                    item.setSupplier(resultSet.getString("Supplier"));
                    item.setDepartment(resultSet.getString("Department"));
                    try {
                        String marginStr = resultSet.getString("Margin");
                        double margin = 0.0;  // Default value
                        if (marginStr != null && marginStr.matches("-?\\d+(\\.\\d+)?")) {
                            margin = Double.parseDouble(marginStr);
                        }
                        item.setMargin(margin);  // Set the margin value

                        String markupStr = resultSet.getString("Markup");
                        double markup = 0.0;  // Default value
                        if (markupStr != null && markupStr.matches("-?\\d+(\\.\\d+)?")) {
                            markup = Double.parseDouble(markupStr);
                        }
                        item.setMarkup(markup);  // Set the markup value
                    } catch (SQLException e) {
                        Log.e(TAG, "Error fetching Margin or Markup", e);
                        // Handle any SQL exceptions that might occur
                    }

                    // Add the item to the list
                    saleReportItems.add(item);
                }

                // Log after fetching data
                Log.d(TAG, "doInBackground: Data fetched, total rows: " + saleReportItems.size());

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Error occurred while fetching data.", e);
            } finally {
                // Close resources and log the closure
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                    Log.d(TAG, "doInBackground: Database resources closed.");
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: Error closing database resources.", e);
                }
            }
            return saleReportItems;
        }

        @Override
        protected void onPostExecute(List<SaleReportItem> saleReportItems) {
            super.onPostExecute(saleReportItems);
            // Log when the AsyncTask is complete
            Log.d(TAG, "onPostExecute: Data processing complete. Updating RecyclerView.");

            if (saleReportItems != null && !saleReportItems.isEmpty()) {
                // Pass data to your adapter and set the RecyclerView
                adapter = new SaleReportAdapter(saleReportItems, getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
                Log.d(TAG, "onPostExecute: No data to display.");
            }
        }


    }

    private class FetchSaleReportDatasearch extends AsyncTask<Object, Void, List<SaleReportItem>> {

        @Override
        protected List<SaleReportItem> doInBackground(Object... params) {
            List<SaleReportItem> saleReportItems = new ArrayList<>();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                // Log before connecting to the database
                Log.d(TAG, "doInBackground: Connecting to SQL Server database.");

                // Use the jTDS driver for SQL Server
                String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(url, username, password);

                String supplierName = (String) params[0]; // Retrieve supplierName

                // Modify the query to filter by departmentId, BrandDone, subDepartmentId, and supplierName
                String query = "SELECT * FROM tbl_Products WHERE Barcode = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, supplierName);  // Set supplierName in the query

                resultSet = preparedStatement.executeQuery();

                // Log the query execution
                Log.d(TAG, "doInBackground: Query executed: " + query);

                while (resultSet.next()) {
                    SaleReportItem item = new SaleReportItem();
                    item.setId(resultSet.getInt("ID"));
                    item.setPlu(resultSet.getString("PLU"));
                    item.setBarcode(resultSet.getString("Barcode"));
                    item.setDescription(resultSet.getString("Description"));
                    item.setSubDepartment(resultSet.getString("Sub_Department"));
                    item.setSupplier(resultSet.getString("Supplier"));
                    item.setDepartment(resultSet.getString("Department"));

                    try {
                        String marginStr = resultSet.getString("Margin");
                        double margin = 0.0;  // Default value
                        if (marginStr != null && marginStr.matches("-?\\d+(\\.\\d+)?")) {
                            margin = Double.parseDouble(marginStr);
                        }
                        item.setMargin(margin);  // Set the margin value

                        String markupStr = resultSet.getString("Markup");
                        double markup = 0.0;  // Default value
                        if (markupStr != null && markupStr.matches("-?\\d+(\\.\\d+)?")) {
                            markup = Double.parseDouble(markupStr);
                        }
                        item.setMarkup(markup);  // Set the markup value
                    } catch (SQLException e) {
                        Log.e(TAG, "Error fetching Margin or Markup", e);
                        // Handle any SQL exceptions that might occur
                    }

                    // Add the item to the list
                    saleReportItems.add(item);
                }

                // Log after fetching data
                Log.d(TAG, "doInBackground: Data fetched, total rows: " + saleReportItems.size());

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Error occurred while fetching data.", e);
            } finally {
                // Close resources and log the closure
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                    Log.d(TAG, "doInBackground: Database resources closed.");
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: Error closing database resources.", e);
                }
            }
            return saleReportItems;
        }



        @Override
        protected void onPostExecute(List<SaleReportItem> saleReportItems) {
            super.onPostExecute(saleReportItems);
            // Log when the AsyncTask is complete
            Log.d(TAG, "onPostExecute: Data processing complete. Updating RecyclerView.");

            if (saleReportItems != null && !saleReportItems.isEmpty()) {
                // Pass data to your adapter and set the RecyclerView
                adapter = new SaleReportAdapter(saleReportItems, getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);

                Log.d(TAG, "onPostExecute: No data to display.");
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
