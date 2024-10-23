package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import android.util.Log;

public class ProductManagmentAddFragment extends Fragment {

    String ipAddress, portNumber, databaseName, username, password;
    private static final String TAG = "ProductManagementAddFragment";

    EditText itemcode1, barcide1, description1, unitpercase1, costpercase1,
             margin1,pludt,sellingpriceedt,outerbarcodeedt,additionalbarcode,ddpriceedt,startdate,enddatedt,weightedt,currentstock,capacity,quantity,minstaockedt,reorderlevel;
    Spinner departmentSpinner, subdepartmentSpinner,supplierSpinner,brandSpinner,vatSpinner;

    int subdepartmentId, departmentId,BrandDone;
    String supplierName;
    String barcodeValue,vatValue,Pluvalu;
    private Spinner ageSpinner; // Updated variable name
    String selectedage;
    TextView costpriceedt;
    Double costPrice;

    Button clear1;
    private RadioGroup radioGroup;
    private int enableDisableValue = 0;

    private RadioGroup radioGroupManage;
    private int enableDisableValuemangestock = 0;

    private RadioGroup radioGroupddprice;
    private int enableDisableValueddprice = 0;

    int  discountValue = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Fragment created");
        View view = inflater.inflate(R.layout.fragment_product_managment, container, false);


        if (getArguments() != null) {
            barcodeValue = getArguments().getString("barcode");
            Pluvalu = getArguments().getString("PLU");
        }
        clear1=view.findViewById(R.id.clearbtn);

        itemcode1 = view.findViewById(R.id.itemcodeedt);
        barcide1 = view.findViewById(R.id.barcodeedt);
        description1 = view.findViewById(R.id.descriptionedt);
        unitpercase1 = view.findViewById(R.id.unitPerCaseedt);
        costpercase1 = view.findViewById(R.id.costpercaseedt);
        margin1 = view.findViewById(R.id.marginedt);
        departmentSpinner = view.findViewById(R.id.spinner_department);
        subdepartmentSpinner = view.findViewById(R.id.spinner_subdepartment);
        supplierSpinner=view.findViewById(R.id.spinner_spuulier);
        brandSpinner=view.findViewById(R.id.spinner_brand);
        vatSpinner =view.findViewById(R.id.vat_spinner);
        pludt=view.findViewById(R.id.pludt);
        costpriceedt=view.findViewById(R.id.costpricetxt);
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
        barcide1.setText(barcodeValue);

        pludt.setText(Pluvalu);


        ImageView back =view.findViewById(R.id.imageView);
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


        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // on below line we are getting
                    // the instance of our calendar.
                    final Calendar c = Calendar.getInstance();

                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our text view.

                                    startdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }
                            },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();
                }
            }
        });


        enddatedt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // on below line we are getting
                    // the instance of our calendar.
                    final Calendar c = Calendar.getInstance();

                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our text view.

                                    enddatedt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }
                            },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();
                }
            }
        });

        radioGroup =view.findViewById(R.id.ddpoints); // Replace with your RadioGroup ID

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


        radioGroupManage =view.findViewById(R.id.managestocks); // Replace with your RadioGroup ID

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




        radioGroupddprice =view.findViewById(R.id.ddprice); // Replace with your RadioGroup ID

        radioGroupddprice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which RadioButton is selected using if-else
                if (checkedId == R.id.yes) { // Replace with your RadioButton ID for ENABLE
                    enableDisableValueddprice = 1; // ENABLE is selected
                } else if (checkedId == R.id.no) { // Replace with your RadioButton ID for DISABLE
                    enableDisableValueddprice = 0; // DISABLE is selected
                }
            }
        });


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

        new FetchDepartmentData(getContext(), departmentSpinner).execute();
        new FetchSubDepartmentData(getContext(), subdepartmentSpinner).execute();
        new FetchSupplierData(getContext(), supplierSpinner).execute();
        new FetchBrandData(getContext(), brandSpinner).execute();
        new FetchVatData(getContext(), vatSpinner).execute();

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



        ageSpinner = view.findViewById(R.id.age_spinner); // Updated ID

        // Define an array of integer values
        final int[] spinnerValues = {1, 2}; // Corresponding to your spinner items
        String[] spinnerItems = {"18+", "Any One"}; // Displayed text in spinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, spinnerItems);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        ageSpinner.setAdapter(adapter);

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

        // Retrieve database connection details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Button addButton = view.findViewById(R.id.save1);
        addButton.setOnClickListener(v -> {
            Log.d(TAG, "Add button clicked");

            if (itemcode1.getText().toString().isEmpty() || barcide1.getText().toString().isEmpty() || description1.getText().toString().isEmpty()
                      || costpercase1.getText().toString().isEmpty() || margin1.getText().toString().isEmpty() ||
                    unitpercase1.getText().toString().isEmpty() ||pludt.getText().toString().isEmpty()) {
                Toast.makeText(v.getContext(), "Please fil all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Call AsyncTask to add product with all 47 parameters
                new AddProductTask().execute(
                        //"389",                       // ID
                        pludt.getText().toString(),// PLU
                        barcide1.getText().toString(),// Barcode
                        description1.getText().toString(),// Description
                        String.valueOf(subdepartmentId), // Sub_Department
                        supplierName,// Supplier
                        "0.00",// Buy_Price
                        "",// Quantity
                        String.valueOf(departmentId),// Department
                        "0.00",                       // SaleWithVAT
                        "0.00",                       // Discount
                        costpercase1.getText().toString(),// CostPerCase
                        String.valueOf(costPrice),// Price
                        vatValue,// VAT
                        margin1.getText().toString(),// Margin
                        String.valueOf(selectedage),// Age_Limit
                        "NULL",                       // ProImage
                        "0",                          // PromoID
                        unitpercase1.getText().toString(),// UnitPerCase
                        "0",                        // Activated
                        "0",                  // DateAdded
                        "0",                         // VatValue
                        "0",                      // Class
                        "0",                          // QTYSOLD
                        "0",                          // Capacity
                        "1",                          // done
                        "0.00",                       // Price2
                        "0",                          // SS_QTYS
                        sellingpriceedt.getText().toString(),// SS_PRICE
                        "0",                          // SS_POINTS
                        "0",                          // SS_PRO_TYPE
                        "0",                          // Unit_scale
                        itemcode1.getText().toString(),// Item_code
                        "0",                          // ITEM_TYPE
                        "0",                          // FOOD_TYPE
                        "0",                          // MENU_TYPE
                        "0",                          // SUB_MENU_TYPE
                        "0",                          // MENU_TYPE_NO
                        "0",                          // SUB_PRODUCT_NO
                       String.valueOf(BrandDone),     // Brand
                        "0",                          // Expiry_Date
                        "0",                          // Profit_Inc_VAT
                        "0",                          // Profit_Ex_VAT
                        "0",                          // Cost_Inc_VAT
                        "0",                          // Markup
                        "0",                          // Num
                        "0",                          // Cost_Inc_VAT_1unit
                        outerbarcodeedt.getText().toString(),//OuterBarcode
                        "0",//StartDate
                        "0",//EndDate
                        "0",//DD_Price
                        "0",//ManageStock
                        "0",//Weight
                        "0",//CurrentStock
                        additionalbarcode.getText().toString(),//AdditionalBarcode1
                        "0",//AdditionalBarcode2
                        " ",
                        " "




                );
            }


        });

        Button addvancebtn = view.findViewById(R.id.addvancebtn);
        LinearLayout advancedlayout = view.findViewById(R.id.addvancelayout);

        addvancebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advancedlayout.getVisibility() == View.VISIBLE) {
                    advancedlayout.setVisibility(View.GONE);
                    addButton.setVisibility(View.VISIBLE);
                } else {
                    advancedlayout.setVisibility(View.VISIBLE);
                    addButton.setVisibility(View.GONE);

                }
            }
        });




        Button addButton2 = view.findViewById(R.id.save);
        addButton2.setOnClickListener(v -> {
            Log.d(TAG, "Add button clicked");

            if (itemcode1.getText().toString().isEmpty() || barcide1.getText().toString().isEmpty() || description1.getText().toString().isEmpty()
                    || costpercase1.getText().toString().isEmpty() || margin1.getText().toString().isEmpty() ||
                    unitpercase1.getText().toString().isEmpty() ||pludt.getText().toString().isEmpty()) {
                Toast.makeText(v.getContext(), "Please fil all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Call AsyncTask to add product with all 47 parameters
                new AddProductTask().execute(
                        //"389",                       // ID
                        pludt.getText().toString(),// PLU
                        barcide1.getText().toString(),// Barcode
                        description1.getText().toString(),// Description
                        String.valueOf(subdepartmentId), // Sub_Department
                        supplierName,// Supplier
                        "0.00",// Buy_Price
                        quantity.getText().toString(),// Quantity
                        String.valueOf(departmentId),// Department
                        "0.00",                       // SaleWithVAT
                        String.valueOf(discountValue),                       // Discount
                        costpercase1.getText().toString(),// CostPerCase
                        String.valueOf(costPrice),// Price
                        vatValue,// VAT
                        margin1.getText().toString(),// Margin
                        String.valueOf(selectedage),// Age_Limit
                        "NULL",                       // ProImage
                        "0",                          // PromoID
                        unitpercase1.getText().toString(),// UnitPerCase
                        "0",                        // Activated
                        "0",                  // DateAdded
                        "0",                         // VatValue
                        "0",                      // Class
                        "0",                          // QTYSOLD
                        capacity.getText().toString(),                          // Capacity
                        "1",                          // done
                        "0.00",                       // Price2
                        "0",                          // SS_QTYS
                        sellingpriceedt.getText().toString(),// SS_PRICE
                        String.valueOf(enableDisableValue),  // SS_POINTS
                        "0",                          // SS_PRO_TYPE
                        "0",                          // Unit_scale
                        itemcode1.getText().toString(),// Item_code
                        "0",                          // ITEM_TYPE
                        "0",                          // FOOD_TYPE
                        "0",                          // MENU_TYPE
                        "0",                          // SUB_MENU_TYPE
                        "0",                          // MENU_TYPE_NO
                        "0",                          // SUB_PRODUCT_NO
                        String.valueOf(BrandDone),     // Brand
                        "0",                          // Expiry_Date
                        "0",                          // Profit_Inc_VAT
                        "0",                          // Profit_Ex_VAT
                        "0",                          // Cost_Inc_VAT
                        "0",                          // Markup
                        "0",                          // Num
                        "0",                          // Cost_Inc_VAT_1unit
                        outerbarcodeedt.getText().toString(),//OuterBarcode
                        startdate.getText().toString(),//StartDate
                        enddatedt.getText().toString(),//EndDate
                        String.valueOf(enableDisableValueddprice),//DD_Price
                        String.valueOf(enableDisableValuemangestock),//ManageStock
                        weightedt.getText().toString(),//Weight
                        currentstock.getText().toString(),//CurrentStock
                        additionalbarcode.getText().toString(),//AdditionalBarcode1
                        "0",//AdditionalBarcode2
                        minstaockedt.getText().toString(),
                        reorderlevel.getText().toString()



                );
            }

        });

        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Hii", Toast.LENGTH_SHORT).show();
                itemcode1.setText("");
                description1.setText("");
                unitpercase1.setText("");
                costpercase1.setText("");
                margin1.setText("");
                sellingpriceedt.setText("");
                outerbarcodeedt.setText("");
                additionalbarcode.setText("");
                ddpriceedt.setText("");
                startdate.setText("");
                enddatedt.setText("");
                weightedt.setText("");
                currentstock.setText("");
                capacity.setText("");
                quantity.setText("");
                minstaockedt.setText("");
                reorderlevel.setText("");
            }
        });


        return view;
    }

    // AsyncTask to add product in the background
    private class AddProductTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Map parameters to variables (all 45 fields)
            // Map parameters to variables
            // String productId = params[0];         // ID
            String plu = params[0];                // PLU
            String barcode = params[1];             // Barcode
            String description = params[2];        // Description
            String subDepartment = params[3];      // Sub_Department
            String supplier = params[4];           // Supplier
            String buyPrice = params[5];           // Buy_Price
            String quantity = params[6];           // Quantity
            String department = params[7];         // Department
            String saleWithVAT = params[8];       // SaleWithVAT
            String discount = params[9];          // Discount
            String costPerCase = params[10];      // CostPerCase
            String price = params[11];             // Price
            String vat = params[12];               // VAT
            String margin = params[13];            // Margin
            String ageLimit = params[14];          // Age_Limit
            String proImage = params[15];          // ProImage
            String promoID = params[16];           // PromoID
            String unitPerCase = params[17];       // UnitPerCase
            String activated = params[18];         // Activated
            String dateAdded = params[19];         // DateAdded
            String vatValue = params[20];          // VatValue
            String productClass = params[21];      // Class
            String qtySold = params[22];           // QTYSOLD
            String capacity = params[23];          // Capacity
            String done = params[24];              // done
            String price2 = params[25];            // Price2
            String ssQtys = params[26];            // SS_QTYS
            String ssPrice = params[27];           // SS_PRICE
            String ssPoints = params[28];          // SS_POINTS
            String ssProType = params[29];         // SS_PRO_TYPE
            String unitScale = params[30];         // Unit_scale
            String itemCode = params[31];          // Item_code
            String itemType = params[32];          // ITEM_TYPE
            String foodType = params[33];          // FOOD_TYPE
            String menuType = params[34];          // MENU_TYPE
            String subMenuType = params[35];       // SUB_MENU_TYPE
            String menuTypeNo = params[36];        // MENU_TYPE_NO
            String subProductNo = params[37];      // SUB_PRODUCT_NO
            String brand = params[38];             // Brand
            String expiryDate = params[39];        // Expiry_Date
            String profitIncVat = params[40];      // Profit_Inc_VAT
            String profitExVat = params[41];       // Profit_Ex_VAT
            String costIncVat = params[42];        // Cost_Inc_VAT
            String markup = params[43];            // Markup
            String num = params[44];               // Num (add if it exists in your dataset)
            String costIncVat1Unit = params[45];   // Cost_Inc_VAT_1unit (add if it exists in your dataset)
            String outerbarcode = params[46];   // outerbarcode
            String StartDate = params[47];   // StartDate
            String EndDate = params[48];   // EndDate
            String DD_Price = params[49];   // DD_Price
            String ManageStock = params[50];   // ManageStock
            String Weight = params[51];   // Weight
            String CurrentStock = params[52];   // CurrentStock
            String AdditionalBarcode1 = params[53];   // AdditionalBarcode1
            String AdditionalBarcode2 = params[54];   // AdditionalBarcode2
            String minstock = params[55];   // minstock
            String reorderlevel = params[56];   // reorderlevel


            try {
                // Connect to the database
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                Connection connection = DriverManager.getConnection(connectionUrl, username, password);

                if (connection != null) {
                    // SQL query to insert all fields into the tbl_Products table

                    String sql = "INSERT INTO tbl_Products (PLU, Barcode, Description, Sub_Department, Supplier, Buy_Price, Quantity, \n" +
                            "Department, SaleWithVAT, Discount, CostPerCase, Price, VAT, Margin, Age_Limit, ProImage, PromoID, \n" +
                            "UnitPerCase, Activated, DateAdded, VatValue, Class, QTYSOLD, Capacity, done, Price2, SS_QTYS, \n" +
                            "SS_PRICE, SS_POINTS, SS_PRO_TYPE, Unit_scale, Item_code, ITEM_TYPE, FOOD_TYPE, MENU_TYPE, \n" +
                            "SUB_MENU_TYPE, MENU_TYPE_NO, SUB_PRODUCT_NO, Brand, Expiry_Date, Profit_Inc_VAT, Profit_Ex_VAT, \n" +
                            "Cost_Inc_VAT, Markup, Num, Cost_Inc_VAT_1unit,OuterBarcode,StartDate,EndDate,DD_Price,ManageStock,Weight,CurrentStock," +
                            "AdditionalBarcode1,AdditionalBarcode2,MinStock,Reorderleve) \n" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                            "      , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                            "        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                            "        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                            "        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                            "?,?,?,?,?,?,?)\n";


                    PreparedStatement statement = connection.prepareStatement(sql);
// Set parameters (with NULL handling where applicable)
//                    statement.setString(1, productId);
                    statement.setString(1, plu);
                    statement.setString(2, barcode);
                    statement.setString(3, description);
                    statement.setString(4, subDepartment);
                    statement.setString(5, supplier);
                    statement.setString(6, buyPrice);
                    statement.setString(7, quantity);
                    statement.setString(8, department);
                    statement.setString(9, saleWithVAT);
                    statement.setString(10, discount);
                    statement.setString(11, costPerCase);
                    statement.setString(12, price);
                    statement.setString(13, vat);
                    statement.setString(14, margin);
                    statement.setString(15, ageLimit);

// Handle ProImage
                    if (proImage.equals("NULL") || proImage.isEmpty()) {
                        statement.setNull(16, java.sql.Types.BLOB); // Change to BLOB if image type
                    } else {
                        try {
                            File imgFile = new File(proImage);
                            if (imgFile.exists()) {
                                try (FileInputStream fis = new FileInputStream(imgFile)) {
                                    byte[] imageBytes = new byte[(int) imgFile.length()];
                                    fis.read(imageBytes);
                                    statement.setBytes(16, imageBytes);
                                }
                            } else {
                                statement.setNull(16, java.sql.Types.BLOB);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Image read error: " + e.getMessage());
                            statement.setNull(16, java.sql.Types.BLOB);
                        }
                    }

// Handle PromoID
                    if (promoID.equals("NULL") || promoID.isEmpty()) {
                        statement.setNull(17, java.sql.Types.VARCHAR);
                    } else {
                        statement.setString(17, promoID);
                    }

// Set remaining parameters directly
                    statement.setString(18, unitPerCase);
                    statement.setString(19, activated);
                    statement.setString(20, dateAdded);
                    statement.setString(21, vatValue);
                    statement.setString(22, productClass);
                    statement.setString(23, qtySold);
                    statement.setString(24, capacity);
                    statement.setString(25, done);
                    statement.setString(26, price2);
                    statement.setString(27, ssQtys);
                    statement.setString(28, ssPrice);
                    statement.setString(29, ssPoints);
                    statement.setString(30, ssProType);
                    statement.setString(31, unitScale);
                    statement.setString(32, itemCode);
                    statement.setString(33, itemType);
                    statement.setString(34, foodType);
                    statement.setString(35, menuType);
                    statement.setString(36, subMenuType);
                    statement.setString(37, menuTypeNo);
                    statement.setString(38, subProductNo);
                    statement.setString(39, brand);

// Handle Expiry_Date
                    if (expiryDate.equals("NULL") || expiryDate.isEmpty()) {
                        statement.setNull(40, java.sql.Types.VARCHAR);
                    } else {
                        statement.setString(40, expiryDate);
                    }

// Handle Profit_Inc_VAT
                    if (profitIncVat.equals("NULL") || profitIncVat.isEmpty()) {
                        statement.setNull(41, java.sql.Types.DECIMAL);
                    } else {
                        statement.setBigDecimal(41, new BigDecimal(profitIncVat));
                    }

// Handle Profit_Ex_VAT
                    if (profitExVat.equals("NULL") || profitExVat.isEmpty()) {
                        statement.setNull(42, java.sql.Types.DECIMAL);
                    } else {
                        statement.setBigDecimal(42, new BigDecimal(profitExVat));
                    }

// Handle Cost_Inc_VAT
                    if (costIncVat.equals("NULL") || costIncVat.isEmpty()) {
                        statement.setNull(43, java.sql.Types.DECIMAL);
                    } else {
                        statement.setBigDecimal(43, new BigDecimal(costIncVat));
                    }

// Handle Markup
                    if (markup.equals("NULL") || markup.isEmpty()) {
                        statement.setNull(44, java.sql.Types.DECIMAL);
                    } else {
                        statement.setBigDecimal(44, new BigDecimal(markup));
                    }

// Handle Num (assuming it's a String)
                    if (num.equals("NULL") || num.isEmpty()) {
                        statement.setNull(45, java.sql.Types.VARCHAR);
                    } else {
                        statement.setString(45, num);
                    }

// Handle Cost_Inc_VAT_1unit (assuming it's a String)
                    if (costIncVat1Unit.equals("NULL") || costIncVat1Unit.isEmpty()) {
                        statement.setNull(46, java.sql.Types.DECIMAL);
                    } else {
                        statement.setBigDecimal(46, new BigDecimal(costIncVat1Unit));
                    }

 //OuterBarcode
                    statement.setString(47, outerbarcode);
                    statement.setString(48, StartDate);
                    statement.setString(49, EndDate);
                    statement.setString(50, DD_Price);
                    statement.setString(51, ManageStock);
                    statement.setString(52, Weight);
                    statement.setString(53, CurrentStock);
                    statement.setString(54, AdditionalBarcode1);
                    statement.setString(55, AdditionalBarcode2);
                    statement.setString(56, minstock);
                    statement.setString(57, reorderlevel);




                    // Execute the query
                    int rowsInserted = statement.executeUpdate();
                    statement.close();
                    connection.close();

                    if (rowsInserted > 0) {
                        return "Product added successfully!";
                    } else {
                        return "Failed to add product.";
                    }

                } else {
                    return "Failed to connect to database.";
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception3: " + e.getMessage());
                return "SQL Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(requireActivity(), result, Toast.LENGTH_LONG).show();
            Log.e(TAG, "SQL Exception3: " + result);
        }
    }

}
