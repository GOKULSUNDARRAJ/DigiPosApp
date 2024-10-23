package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductManagmentEditFragment extends Fragment{

    String ipAddress, portNumber, databaseName, username, password;
    ImageView back;
    private String description, barcode, subDepartment, supplier, department, vat, ageLimit,Itemcode,
            Brand,UnitPerCase,CostPerCase,Price,sellingprice,Margin,plu,outerBarcode,price,addbarcode,
            endDate,startDate,dd_Price,ddpoint,manageStock,weight,capacitys,currentStock1,qty,minStock,reorderleve,discount;

    EditText itemcode1, barcide1, description1, unitpercase1, costpercase1,
            margin1,pludt,sellingpriceedt,outerbarcodeedt,additionalbarcode,ddpriceedt,startdate,enddatedt,weightedt,currentstock,capacity,quantity,minstaockedt,reorderlevel;

    Spinner departmentSpinner, subdepartmentSpinner,supplierSpinner,brandSpinner,vatSpinner;
    String selectedage;
    TextView costpriceedt;
    private Spinner ageSpinner; // Updated variable name
    String barcodeValue,vatValue,Pluvalu;
    Double costPrice;
    String supplierName;
    int subdepartmentId, departmentId,BrandDone;

    private RadioGroup radioGroup;
    private int enableDisableValue = 0;

    private RadioGroup radioGroupManage;
    private int enableDisableValuemangestock = 0;
    private RadioGroup radioGroupddprice;
    private int enableDisableValueddprice = 0;
    int  discountValue = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_product_managment_edit, container, false);


        // Retrieve database connection details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        back=view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductmagementfullFragment bottomBarFragment = new ProductmagementfullFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.commit();
            }
        });

        itemcode1 = view.findViewById(R.id.itemcodeedt);
        barcide1 = view.findViewById(R.id.barcodeedt);
        description1 = view.findViewById(R.id.descriptionedt);
        unitpercase1 = view.findViewById(R.id.unitPerCaseedt);
        costpercase1 = view.findViewById(R.id.costpercaseedt);
        margin1 = view.findViewById(R.id.marginedt);
        pludt=view.findViewById(R.id.pludt);
        sellingpriceedt=view.findViewById(R.id.sellingprice);
        outerbarcodeedt=view.findViewById(R.id.outerbarcodeedt);
        additionalbarcode=view.findViewById(R.id.additionalbarcode);
        ddpriceedt=view.findViewById(R.id.ddpriceedt);
        startdate=view.findViewById(R.id.startdate);
        enddatedt=view.findViewById(R.id.enddatedt);
        weightedt=view.findViewById(R.id.weightedt);
        currentstock=view.findViewById(R.id.currentstock);
        capacity=view.findViewById(R.id.capacity);
        quantity=view.findViewById(R.id.quantity);
        minstaockedt=view.findViewById(R.id.minstaockedt);
        reorderlevel=view.findViewById(R.id.reorderlevel);
        costpriceedt=view.findViewById(R.id.costpricetxt);



        barcide1.setText(barcode);
        description1.setText(description);
        itemcode1.setText(Itemcode);
        pludt.setText(plu);
        outerbarcodeedt.setText(outerBarcode);
        unitpercase1.setText(UnitPerCase);
        costpercase1.setText(CostPerCase);
        costpriceedt.setText(Price);
        additionalbarcode.setText(addbarcode);
        sellingpriceedt.setText(sellingprice);
        margin1.setText(Margin);

        departmentSpinner = view.findViewById(R.id.spinner_department);
        subdepartmentSpinner = view.findViewById(R.id.spinner_subdepartment);
        supplierSpinner=view.findViewById(R.id.spinner_spuulier);
        brandSpinner=view.findViewById(R.id.spinner_brand);
        vatSpinner =view.findViewById(R.id.vat_spinner);

        new FetchDepartmentDataEdit(getContext(), departmentSpinner, department).execute();
        new FetchSubDepartmentEdit(getContext(), subdepartmentSpinner,subDepartment).execute();
        new FetchSupplierDataEdit(getContext(), supplierSpinner,supplier).execute();
        new FetchBrandDataEdit(getContext(), brandSpinner,Brand).execute();
        new FetchVatData(getContext(), vatSpinner).execute();


        ageSpinner = view.findViewById(R.id.age_spinner); // Updated ID

// Define an array of integer values
        final int[] spinnerValues = {1, 2}; // Corresponding to  your spinner items
        String[] spinnerItems = {"18+", "Any One"}; // Displayed text in spinner

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, spinnerItems);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        ageSpinner.setAdapter(adapter);

// Get the passed `ageLimit` from arguments
        String ageLimit = getArguments().getString("ageLimit");

// Check if `ageLimit` is not null and set the spinner selection based on it
        if (ageLimit != null) {
            // Loop through the spinner items to find a match with the `ageLimit`
            for (int i = 0; i < spinnerItems.length; i++) {
                if (spinnerItems[i].equals(ageLimit)) {
                    ageSpinner.setSelection(i); // Set the spinner selection to the matching item
                    break;
                }
            }
        }

// Set a listener for the spinner
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedage = spinnerItems[position];
                Toast.makeText(getContext(), "Selected Value: " + selectedage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle the case when nothing is selected
            }
        });




        costpriceedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unitPerCaseTxt = unitpercase1.getText().toString();
                String costPerCaseTxt = costpercase1.getText().toString();
                // Check if inputs are not empty
                if (unitPerCaseTxt.isEmpty() || costPerCaseTxt.isEmpty()) {
                    // Handle empty input, e.g., show a Toast message
                    Toast.makeText(v.getContext(), "Please enter both values", Toast.LENGTH_SHORT).show();
                    return; // Exit the method
                }

                try {
                    // Parse the input strings to Double
                    Double unitPerCase = Double.parseDouble(unitPerCaseTxt);
                    Double costPerCase = Double.parseDouble(costPerCaseTxt);

                    // Check for division by zero
                    if (costPerCase == 0) {
                        Toast.makeText(v.getContext(), "Cost per case cannot be zero", Toast.LENGTH_SHORT).show();
                        return; // Exit the method
                    } else {
                        // Calculate cost price
                        costPrice = unitPerCase / costPerCase;
                        // Set the result to costpriceedt
                        costpriceedt.setText(String.valueOf(costPrice));
                    }
                } catch (NumberFormatException e) {
                    // Handle number format exceptions
                    Toast.makeText(v.getContext(), "Invalid input, please enter numeric values", Toast.LENGTH_SHORT).show();
                }
            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Departmentspinner selectedDepartment = (Departmentspinner) parent.getItemAtPosition(position);
                departmentId = selectedDepartment.getId(); // Get the ID of the selected department
                String departmentName = selectedDepartment.getDepartment(); // Get the name of the selected department

                // Display selected department information
                Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + departmentId + ")", Toast.LENGTH_SHORT).show();
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

                // Display selected department information
                Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + subdepartmentId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });


        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                int supplierId = selectedSupplier.getId(); // Get the ID of the selected supplier
                supplierName = selectedSupplier.getSupplier(); // Get the name of the selected supplier

                // Display selected supplier information
                Toast.makeText(view.getContext(), "Selected: " + supplierName + " (ID: " + supplierId + ")", Toast.LENGTH_SHORT).show();
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
                int brandId = selectedBrand.getId(); // Get the ID of the selected brand
                String brandName = selectedBrand.getBrand();
                BrandDone = selectedBrand.getId();// Get the name of the selected brand

                // Display selected brand information
                Toast.makeText(view.getContext(), "Selected: " + brandName + " (ID: " + brandId + ")", Toast.LENGTH_SHORT).show();

                // Optionally, you can perform additional actions based on the selected brand
                // For example, you can save the selected brand's ID or name for further use
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
                Toast.makeText(parent.getContext(), "No brand selected", Toast.LENGTH_SHORT).show();
            }
        });


        vatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VatType selectedVatType = (VatType) parent.getItemAtPosition(position);
                int vatId = selectedVatType.getId(); // Get the ID of the selected VAT type
                vatValue = selectedVatType.getVat(); // Get the VAT percentage value
                int vatDone = selectedVatType.getDone(); // Get the status of the selected VAT type

                // Display selected VAT information
                Toast.makeText(view.getContext(), "Selected VAT: " + vatValue + " (ID: " + vatId + ")", Toast.LENGTH_SHORT).show();

                // Optionally, you can perform additional actions based on the selected VAT type
                // For example, you can save the selected VAT's ID or value for further use
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
                Toast.makeText(parent.getContext(), "No VAT type selected", Toast.LENGTH_SHORT).show();
            }
        });



        Button edit1 = view.findViewById(R.id.save1);
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve inputs from EditTexts
                String Barcode = barcide1.getText().toString();
                String description = description1.getText().toString();
                String itemcode = itemcode1.getText().toString();
                String plu = pludt.getText().toString();
                String department = String.valueOf(departmentId);
                String subdepartment = String.valueOf(subdepartmentId);
                String supplier = supplierName;
                String brand = String.valueOf(BrandDone);
                String outerbarcode = outerbarcodeedt.getText().toString();
                String agelimit = selectedage;
                String vat = vatValue;
                String unitpercase = unitpercase1.getText().toString();
                String costpercase = costpercase1.getText().toString();
                String price = costpriceedt.getText().toString();
                String additionalbarcode1 = additionalbarcode.getText().toString();
                String ss_price = sellingpriceedt.getText().toString();
                String margin = margin1.getText().toString();

                // Input validation
                if (Barcode.isEmpty() || itemcode.isEmpty()) {
                    Toast.makeText(getContext(), "Barcode and Item Code cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if validation fails
                }

                // Show a progress dialog if desired
                // ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Updating Product", "Please wait...", true);

                // Execute the update task
                updateProduct(Barcode, itemcode, description, plu, department, subdepartment, supplier, brand, outerbarcode, agelimit, vat, unitpercase, costpercase, price, additionalbarcode1, ss_price, margin);
            }
        });




        Button addvancebtn = view.findViewById(R.id.addvancebtn);
        LinearLayout advancedlayout = view.findViewById(R.id.addvancelayout);

        addvancebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advancedlayout.getVisibility() == View.VISIBLE) {
                    advancedlayout.setVisibility(View.GONE);
                    edit1.setVisibility(View.VISIBLE);
                } else {

                    advancedlayout.setVisibility(View.VISIBLE);
                    edit1.setVisibility(View.GONE);

                    startdate.setText(startDate);
                    enddatedt.setText(endDate);

                }
            }
        });





        radioGroupddprice =view.findViewById(R.id.ddprice); // Replace with your RadioGroup ID
        Toast.makeText(getContext(), ""+dd_Price, Toast.LENGTH_SHORT).show();

        if ("1".equals(dd_Price)) { // Check if dd_Price is "1"
            radioGroupddprice.check(R.id.enable1); // Replace with your RadioButton ID for ENABLE
            enableDisableValueddprice = 1; // Set to ENABLE
        } else {
            radioGroupddprice.check(R.id.disable1); // Replace with your RadioButton ID for DISABLE
            enableDisableValueddprice = 0; // Set to DISABLE
        }

        radioGroupddprice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which RadioButton is selected using if-else
                if (checkedId == R.id.enable1) { // Replace with your RadioButton ID for ENABLE
                    enableDisableValueddprice = 1; // ENABLE is selected
                } else if (checkedId == R.id.disable1) { // Replace with your RadioButton ID for DISABLE
                    enableDisableValueddprice = 0; // DISABLE is selected
                }
            }
        });



        radioGroup =view.findViewById(R.id.ddpoints); // Replace with your RadioGroup ID
        if ("1".equals(ddpoint)) { // Check if dd_Price is "1"
            radioGroup.check(R.id.enable); // Replace with your RadioButton ID for ENABLE
            enableDisableValue = 1; // Set to ENABLE
        } else {
            radioGroup.check(R.id.disable); // Replace with your RadioButton ID for DISABLE
            enableDisableValue = 0; // Set to DISABLE
        }

        // Set the checked change listener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which RadioButton is selected using if-else
                if (checkedId == R.id.enable) { // Replace with your RadioButton ID for ENABLE
                    enableDisableValue = 1; // ENABLE is selected
                } else if (checkedId == R.id.disable) { // Replace with your RadioButton ID for DISABLE
                    enableDisableValue = 0; // DISABLE is selected
                }
            }
        });




        radioGroupManage = view.findViewById(R.id.managestocks);
        // Set the initial selected RadioButton based on manageStockValue
        if ("1".equals(manageStock)) { // Check if manageStockValue is "1"
            radioGroupManage.check(R.id.yes); // Replace with your RadioButton ID for ENABLE
            enableDisableValuemangestock = 1; // Set to ENABLE
        } else {
            radioGroupManage.check(R.id.no); // Replace with your RadioButton ID for DISABLE
            enableDisableValuemangestock = 0; // Set to DISABLE
        }

        // Set the checked change listener
        radioGroupManage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which RadioButton is selected using if-else
                if (checkedId == R.id.yes) { // Replace with your RadioButton ID for ENABLE
                    enableDisableValuemangestock = 1; // ENABLE is selected
                } else if (checkedId == R.id.no) { // Replace with your RadioButton ID for DISABLE
                    enableDisableValuemangestock = 0; // DISABLE is selected
                }
            }
        });

        weightedt.setText(weight);
        capacity.setText(capacitys);
        currentstock.setText(currentStock1);
        quantity.setText(qty);
        minstaockedt.setText(minStock);
        reorderlevel.setText(reorderleve);


        CheckBox didCountCheckBox =view.findViewById(R.id.didcount); // Initialize your CheckBox


        didCountCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discountValue = 1; // Checked means discount is not applicable
                } else {
                    discountValue = 0; // Unchecked means discount is applicable
                }
            }
        });



        // Check if the discount value is "1" to set the CheckBox
        if ("1".equals(discount)) {
            didCountCheckBox.setChecked(true); // Check the CheckBox
        } else {
            didCountCheckBox.setChecked(false); // Uncheck the CheckBox
        }


        Button edit2 = view.findViewById(R.id.save);
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve inputs from EditTexts
                String Barcode = barcide1.getText().toString();
                String description = description1.getText().toString();
                String itemcode = itemcode1.getText().toString();
                String plu = pludt.getText().toString();
                String department = String.valueOf(departmentId);
                String subdepartment = String.valueOf(subdepartmentId);
                String supplier = supplierName;
                String brand = String.valueOf(BrandDone);
                String outerbarcode = outerbarcodeedt.getText().toString();
                String agelimit = selectedage;
                String vat = vatValue;
                String unitpercase = unitpercase1.getText().toString();
                String costpercase = costpercase1.getText().toString();
                String price = costpriceedt.getText().toString();
                String additionalbarcode1 = additionalbarcode.getText().toString();
                String ss_price = sellingpriceedt.getText().toString();
                String margin = margin1.getText().toString();
                String startdate1=startdate.getText().toString();
                String enddate=enddatedt.getText().toString();
                String ddprice=String.valueOf(enableDisableValueddprice);
                String ddpoints=String.valueOf(enableDisableValue);
                String managestock=String.valueOf(enableDisableValuemangestock);
                String weight=weightedt.getText().toString();
                String capacity1=capacity.getText().toString();
                String currentstock1=currentstock.getText().toString();
                String quanityy=quantity.getText().toString();
                String reorderlevel1=reorderlevel.getText().toString();
                String minstock=minstaockedt.getText().toString();
                String discount=String.valueOf(discountValue);



                // Input validation
                if (Barcode.isEmpty() || itemcode.isEmpty()) {
                    Toast.makeText(getContext(), "Barcode and Item Code cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Exit the method if validation fails
                }

                // Show a progress dialog if desired
                // ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Updating Product", "Please wait...", true);

                new UpdateProductTask().execute(
                        Barcode,
                        description,
                        plu,
                        department,
                        subdepartment,
                        supplier,
                        brand,
                        outerbarcode,
                        agelimit,
                        vat,
                        unitpercase,
                        costpercase,
                        price,
                        additionalbarcode1,
                        ss_price,
                        margin,
                        startdate1,
                        enddate,
                        ddprice,
                        ddpoints,
                        managestock,
                        weight,
                        currentstock1,
                        quanityy,
                        reorderlevel1,
                        minstock,
                        discount,
                        capacity1, // Pass capacity here
                        itemcode // Pass the item code last for the WHERE clause
                );

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
            supplier = getArguments().getString("supplier");
            department = getArguments().getString("department");
            vat = getArguments().getString("vat");
            ageLimit = getArguments().getString("ageLimit");
            Itemcode=getArguments().getString("Itemcode");
            Brand=getArguments().getString("Brand");
            UnitPerCase=getArguments().getString("UnitPerCase");
            CostPerCase=getArguments().getString("CostPerCase");
            Price=getArguments().getString("Price");
            sellingprice=getArguments().getString("SS_PRICE");
            Margin=getArguments().getString("Margin");
            plu=getArguments().getString("plu");
            outerBarcode=getArguments().getString("OuterBarcode");
            price=getArguments().getString("Price");
            addbarcode=getArguments().getString("AdditionalBarcode1");
            startDate=getArguments().getString("StartDate");
            endDate=getArguments().getString("EndDate");
            dd_Price=getArguments().getString("DD_Price");
            ddpoint=getArguments().getString("SS_POINTS");
            manageStock=getArguments().getString("ManageStock");
            weight=getArguments().getString("Weight");
            capacitys=getArguments().getString("Capacity");
            currentStock1=getArguments().getString("CurrentStock");
            qty=getArguments().getString("quantity");
            minStock=getArguments().getString("MinStock");
            reorderleve=getArguments().getString("Reorderleve");
            discount=getArguments().getString("Discount");
        }
    }



    private void updateProduct(final String barcode, final String itemCode, final String description, final String plu, final String department, final String subdepartment,
                               final String supplier, final String brand, final String outerbarcode, final String agelimit, final String vat,
                               final String unitpercase, final String costpercase, final String price, final String additionalbarcode1,
                               final String ss_price, final String margin) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String result = performUpdate(barcode, itemCode, description, plu, department, subdepartment, supplier, brand, outerbarcode, agelimit, vat, unitpercase, costpercase, price, additionalbarcode1, ss_price, margin);

            // Run on UI thread to update the UI
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                // If using a progress dialog, dismiss it here
                // progressDialog.dismiss();
            });
        });
    }

    // Method to perform the database update
    private String performUpdate(String barcode, String itemCode, String description, String plu, String department, String subdepartment,
                                 String supplier, String brand, String outerbarcode, String agelimit, String vat,
                                 String unitpercase, String costpercase, String price, String additionalbarcode1,
                                 String ss_price, String margin) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Connect to the database
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
            connection = DriverManager.getConnection(connectionUrl, username, password);

            if (connection != null) {
                // SQL query to update the product details in the tbl_Products table
                String sql = "UPDATE tbl_Products SET Barcode = ?, Description = ?, PLU = ?, Department = ?, Sub_department = ?, Supplier = ?, Brand = ?, " +
                        "OuterBarcode = ?, Age_Limit = ?, VAT = ?, UnitPerCase = ?, CostPerCase = ?, SS_Price = ?, AdditionalBarcode1 = ?, Price = ?, Margin = ? " +
                        "WHERE item_code = ?";

                statement = connection.prepareStatement(sql);

                // Set the parameters for the update
                statement.setString(1, barcode);
                statement.setString(2, description);
                statement.setString(3, plu);
                statement.setString(4, department);
                statement.setString(5, subdepartment);
                statement.setString(6, supplier);
                statement.setString(7, brand);
                statement.setString(8, outerbarcode);
                statement.setString(9, agelimit);
                statement.setString(10, vat);
                statement.setString(11, unitpercase);
                statement.setString(12, costpercase);
                statement.setString(13, price);
                statement.setString(14, additionalbarcode1);
                statement.setString(15, ss_price);
                statement.setString(16, margin);
                statement.setString(17, itemCode);

                // Execute the update
                int rowsUpdated = statement.executeUpdate();
                return (rowsUpdated > 0) ? "Update Successful" : "Update Failed";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "SQL Error: " + e.getMessage();
        } finally {
            // Close the PreparedStatement and Connection
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Connection Failed";
    }

    private class UpdateProductTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Retrieve the parameters
            String barcode = params[0];
            String description = params[1];
            String plu = params[2];
            String department = params[3];
            String subdepartment = params[4];
            String supplier = params[5];
            String brand = params[6];
            String outerbarcode = params[7];
            String agelimit = params[8];
            String vat = params[9];
            String unitpercase = params[10];
            String costpercase = params[11];
            String price = params[12];
            String additionalbarcode1 = params[13];
            String ss_price = params[14];
            String margin = params[15];
            String startdate = params[16];
            String enddate = params[17];
            String ddprice = params[18];
            String ddpoints = params[19];
            String managestock = params[20];
            String weight = params[21];
            String currentstock = params[22];
            String quantity = params[23];
            String reorderlevel = params[24];
            String minstock = params[25];
            String discount = params[26];
            String capacity1 = params[27]; // Add this line for capacity
            String itemCode = params[28]; // Unique identifier for the item

            try {
                // Connect to the database
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                Connection connection = DriverManager.getConnection(connectionUrl, username, password);

                if (connection != null) {



                    // SQL query to update all relevant fields in the tbl_Products table
                    String sql = "UPDATE tbl_Products SET Barcode = ?, Description = ?, PLU = ?, Department = ?, Sub_department = ?, Supplier = ?, Brand = ?, " +
                            "OuterBarcode = ?, Age_Limit = ?, VAT = ?, UnitPerCase = ?, CostPerCase = ?, SS_Price = ?, AdditionalBarcode1 = ?, " +
                            "Price = ?, Margin = ?, StartDate = ?, EndDate = ?, DD_Price = ?, SS_Points = ?, ManageStock = ?, Weight = ?, " +
                            "CurrentStock = ?, Quantity = ?, ReorderLeve = ?, MinStock = ?, Discount = ?, Capacity = ? " +
                            "WHERE item_code = ?";

                    PreparedStatement statement = connection.prepareStatement(sql);

                    // Set the parameters for the update
                    statement.setString(1, barcode);
                    statement.setString(2, description);
                    statement.setString(3, plu);
                    statement.setString(4, department);
                    statement.setString(5, subdepartment);
                    statement.setString(6, supplier);
                    statement.setString(7, brand);
                    statement.setString(8, outerbarcode);
                    statement.setString(9, agelimit);
                    statement.setString(10, vat);
                    statement.setString(11, unitpercase);
                    statement.setString(12, costpercase);
                    statement.setString(13, price);
                    statement.setString(14, additionalbarcode1);
                    statement.setString(15, ss_price);
                    statement.setString(16, margin);
                    statement.setString(17, startdate);
                    statement.setString(18, enddate);
                    statement.setString(19, ddprice);
                    statement.setString(20, ddpoints);
                    statement.setString(21, managestock);
                    statement.setString(22, weight);
                    statement.setString(23, currentstock);
                    statement.setString(24, quantity);
                    statement.setString(25, reorderlevel);
                    statement.setString(26, minstock);
                    statement.setString(27, discount);
                    statement.setString(28, capacity1); // Set capacity
                    statement.setString(29, itemCode); // Identifier for the WHERE clause

                    // Execute the update
                    int rowsUpdated = statement.executeUpdate();
                    return (rowsUpdated > 0) ? "Update Successful" : "Update Failed";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "SQL Error: " + e.getMessage();
            }
            return "Connection Failed";
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result of the update
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }



}