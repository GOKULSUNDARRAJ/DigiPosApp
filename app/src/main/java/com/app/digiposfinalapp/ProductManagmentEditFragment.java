package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.PRINT_SERVICE;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductManagmentEditFragment extends Fragment {

    String ipAddress, portNumber, databaseName, username, password;
    ImageView back;
    private String description, barcode, subDepartment, supplier, department, vat, ageLimit, Itemcode, Brand, UnitPerCase,
            CostPerCase, Price, sellingprice, Margin, plu, outerBarcode, price, addbarcode, endDate, startDate, dd_Price, ddpoint,
            manageStock, weight, capacitys, currentStock1, qty, minStock, reorderleve, Markup, discount, expiry_date, buyPrice, CasePrice, CaseUnit, VatValue1, Unit_scale;


    Button addtobarcode;
    EditText itemcode1, barcide1, description1, unitpercase1, costpercase1, margin1,
            sellingpriceedt, outerbarcodeedt, additionalbarcode, ddpriceedt, startdate, enddatedt,
            weightedt, currentstock, capacity, quantity, minstaockedt, reorderlevel, markupedt, quantityedt1, casepricewdt, caseunitedt;
    String age;
    EditText expiry_dateedt1;
    Spinner vatSpinner;
    AutoCompleteTextView departmentSpinner, subdepartmentSpinner, supplierSpinner, brandSpinner;
    String selectedage;
    TextView costpriceedt;
    private Spinner ageSpinner; // Updated variable name
    String barcodeValue, vatValue, Pluvalu;
    Double costPrice;
    String supplierName;
    int subdepartmentId, departmentId, BrandDone;
    String brandName;

    private RadioGroup radioGroup;
    private int enableDisableValue = 0;

    private RadioGroup radioGroupManage;
    private int enableDisableValuemangestock = 0;
    private RadioGroup radioGroupddprice;
    private int enableDisableValueddprice = 0;
    int discountValue = 0;
    private BroadcastReceiver scanReceiver;
    TextView pludt;

    String quantity4;

    int currentstockvalue;
    String vat1;


    private RadioGroup radioGroupUnit;
    private RadioButton radioButtonUnit1, radioButtonKg2,radioButton0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product_managment_edit, container, false);
        // Find the NestedScrollView


        ImageView home = view.findViewById(R.id.home);
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


        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Get the BottomNavigationView from the MainActivity
        LinearLayout bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        // Add a scroll listener to the NestedScrollView
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check if we're scrolling down
                if (scrollY > oldScrollY) {
                    // Hide the BottomNavigationView when scrolling down
                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(300);
                } else if (scrollY < oldScrollY) {
                    // Show the BottomNavigationView when scrolling up
                    bottomNavigationView.animate().translationY(0).setDuration(300);
                }
            }
        });





        // Initialize RadioGroup and RadioButtons
        radioGroupUnit = view.findViewById(R.id.radioGroupunit);
        radioButtonUnit1 = view.findViewById(R.id.radioButtonunit1);
        radioButtonKg2 = view.findViewById(R.id.radioButtonkg2);
        radioButton0= view.findViewById(R.id.radioButton0);


        if (Unit_scale != null) {
            String normalizedUnit = Unit_scale.trim(); // Trim + uppercase
            if ("Unit".equals(normalizedUnit)) {
                radioButtonUnit1.setChecked(true);
            } else if ("Kg".equals(normalizedUnit)) {
                radioButtonKg2.setChecked(true);
            } else if ("0".equals(normalizedUnit)) {
                radioButton0.setChecked(true);
            }
            else {
                Log.e("UnitDebug", "Unhandled Unit_scale: " + normalizedUnit);
            }
        }


        // Retrieve database connection details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentNewSearch bottomBarFragment = new BarCodeScanFragmentNewSearch();
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
        pludt = view.findViewById(R.id.pludt);
        sellingpriceedt = view.findViewById(R.id.sellingprice);
        outerbarcodeedt = view.findViewById(R.id.outerbarcodeedt);
        additionalbarcode = view.findViewById(R.id.additionalbarcode);
        ddpriceedt = view.findViewById(R.id.ddpriceedt);
        startdate = view.findViewById(R.id.startdate);
        enddatedt = view.findViewById(R.id.enddatedt);
        weightedt = view.findViewById(R.id.weightedt);
        currentstock = view.findViewById(R.id.currentstock);
        capacity = view.findViewById(R.id.capacity);
        quantity = view.findViewById(R.id.quantity);
        minstaockedt = view.findViewById(R.id.minstaockedt);
        reorderlevel = view.findViewById(R.id.reorderlevel);
        costpriceedt = view.findViewById(R.id.costpricetxt);
        markupedt = view.findViewById(R.id.markupedt);
        quantityedt1 = view.findViewById(R.id.quantityedt);

        casepricewdt = view.findViewById(R.id.casepriceedt);
        caseunitedt = view.findViewById(R.id.caseunitedt);

        casepricewdt.setText(CasePrice);
        caseunitedt.setText(CaseUnit);

        barcide1.setText(barcode);
        description1.setText(description);
        itemcode1.setText(Itemcode);
        pludt.setText(plu);
        outerbarcodeedt.setText(outerBarcode);
        unitpercase1.setText(UnitPerCase);
        costpercase1.setText(CostPerCase);
        costpriceedt.setText(buyPrice);
        additionalbarcode.setText(addbarcode);
        sellingpriceedt.setText(price);
        margin1.setText(Margin);
        markupedt.setText(Markup);

        expiry_dateedt1 = view.findViewById(R.id.expiry_dateedt1);

        expiry_dateedt1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Get the current date (today's date)
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // Create the DatePickerDialog with the current date
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Use String.format to ensure proper zero-padding for month and day
                            String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                            expiry_dateedt1.setText(formattedDate); // Set the selected date
                        }
                    }, year, month, day); // Initialize with the current date

                    // Set the minimum date (today's date) to prevent selecting past dates
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

                    // Set OnDismissListener to clear focus when the dialog is dismissed
                    datePickerDialog.setOnDismissListener(dialog -> {
                        expiry_dateedt1.clearFocus(); // Cancel focus on the EditText
                    });

                    // Show the DatePickerDialog
                    datePickerDialog.show();
                }
            }
        });

        expiry_dateedt1.setText(convertToReadableDate(expiry_date));

        departmentSpinner = view.findViewById(R.id.spinner_department);
        subdepartmentSpinner = view.findViewById(R.id.spinner_subdepartment);
        supplierSpinner = view.findViewById(R.id.spinner_spuulier);
        brandSpinner = view.findViewById(R.id.spinner_brand);
        vatSpinner = view.findViewById(R.id.vat_spinner);

        // Toast.makeText(getContext(), ""+VatValue1, Toast.LENGTH_SHORT).show();

        new FetchDepartmentDataEdit(getContext(), departmentSpinner, department).execute();
        new FetchSubDepartmentEdit(getContext(), subdepartmentSpinner, subDepartment, Integer.parseInt(department)).execute();
        new FetchSupplierDataEdit(getContext(), supplierSpinner, supplier).execute();
        new FetchBrandDataEdit(getContext(), brandSpinner, Brand).execute();

        ageSpinner = view.findViewById(R.id.age_spinner); // Updated ID

        departmentSpinner.setOnItemClickListener((parent, itemView, position, id) -> {
            Departmentspinner selectedDepartment = (Departmentspinner) parent.getItemAtPosition(position);
            departmentId = selectedDepartment.getId(); // Get the ID of the selected department
            String departmentName = selectedDepartment.getDepartment(); // Get the name of the selected department
            new FetchSubDepartmentEdit(getContext(), subdepartmentSpinner, subDepartment, departmentId).execute();
            // Display selected department information
            //Toast.makeText(itemView.getContext(), "Selected: " + departmentName + " (ID: " + departmentId + ")", Toast.LENGTH_SHORT).show();

            // Save selected department to SharedPreferences
            SharedPreferences sharedPreferencesd = getActivity().getSharedPreferences("DepartmentPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesd.edit();
            editor.putString("selectedDepartmentID", String.valueOf(selectedDepartment.getId()));
            editor.putString("selectedDepartmentName", selectedDepartment.getDepartment());
            editor.apply();

            age = selectedDepartment.getAgestring(); // "Anyone" or "18+"
            updateAgeSpinner(age);

            Log.d("DepartmentDebug", departmentName + age);

            vat1 = selectedDepartment.getVatstring();
            updateVATSpinner(vat1);


        });
        updateAgeSpinner(ageLimit);
        updateVATSpinner(VatValue1);


        departmentSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                departmentSpinner.showDropDown(); // Show all items when focused

            }
        });
        departmentSpinner.setOnClickListener(v -> departmentSpinner.showDropDown()); // Show all items when clicked


        subdepartmentSpinner.setOnItemClickListener((parent, itemView, position, id) -> {
            SubDepartmentspinner selectedSubDepartment = (SubDepartmentspinner) parent.getItemAtPosition(position);
            subdepartmentId = selectedSubDepartment.getId(); // Get the ID of the selected sub-department
            String subDepartmentName = selectedSubDepartment.getSubDepartment(); // Get the name of the selected sub-department
            SharedPreferences sharedPreferencess = getActivity().getSharedPreferences("SubDepartmentPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencess.edit();
            editor.putString("selectedSubDepartmentID", String.valueOf(selectedSubDepartment.getId()));
            editor.putString("selectedSubDepartmentName", selectedSubDepartment.getSubDepartment());
            editor.apply();
            // Display selected sub-department information
            //Toast.makeText(view.getContext(), "Selected: " + subDepartmentName + " (ID: " + subDepartmentId + ")", Toast.LENGTH_SHORT).show();
        });
        subdepartmentSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                subdepartmentSpinner.showDropDown(); // Show all items when focused
            }
        });
        subdepartmentSpinner.setOnClickListener(v -> subdepartmentSpinner.showDropDown()); // Show all items when clicked

        supplierSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                int supplierId = selectedSupplier.getId(); // Get the ID of the selected supplier
                supplierName = selectedSupplier.getSupplier(); // Get the name of the selected supplier
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedSupplierID", String.valueOf(selectedSupplier.getId()));
                editor.putString("selectedSupplierName", selectedSupplier.getSupplier());
                editor.apply();
            }
        });


        supplierSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                supplierSpinner.showDropDown(); // Show all items when focused
            }
        });
        supplierSpinner.setOnClickListener(v -> supplierSpinner.showDropDown()); // Show all items when clicked

        brandSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrandSpinner selectedBrand = (BrandSpinner) parent.getItemAtPosition(position);
                int brandId = selectedBrand.getId(); // Get the ID of the selected brand
                brandName = selectedBrand.getBrand();
                BrandDone = selectedBrand.getId();// Get the name of the selected brand
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("BrandPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedBrandID", String.valueOf(selectedBrand.getId()));
                editor.putString("selectedBrandName", selectedBrand.getBrand());
                editor.apply();
            }
        });

        brandSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                brandSpinner.showDropDown(); // Show all items when focused
            }
        });
        brandSpinner.setOnClickListener(v -> brandSpinner.showDropDown()); // Show all items when clicked


        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedage = (String) parentView.getItemAtPosition(position);
                // You can add any additional handling here if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default to "Anyone" if nothing is selected
                selectedage = "Anyone";
            }
        });


        vatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                vatValue = (String) parentView.getItemAtPosition(position);
                calculateCostPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default to 0% if nothing is selected
                vatValue = "0%";
                calculateCostPrice();
            }
        });

        quantityedt1.setText(qty);


        Button edit1 = view.findViewById(R.id.save1);
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve inputs from EditTexts
                String Barcode = barcide1.getText().toString();
                String description = description1.getText().toString();
                String itemcode = itemcode1.getText().toString();
                String plu = pludt.getText().toString();

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DepartmentPrefs", MODE_PRIVATE);
                String savedDepartmentID = sharedPreferences.getString("selectedDepartmentID", "");
                String savedDepartmentName = sharedPreferences.getString("selectedDepartmentName", "");

                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("SubDepartmentPrefs", MODE_PRIVATE);
                String savedsubDepartmentID = sharedPreferences2.getString("selectedSubDepartmentID", "");
                String savedsubDepartmentName = sharedPreferences2.getString("selectedSubDepartmentName", "");

                SharedPreferences sharedPreferences23 = requireContext().getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
                String savedSupplierID = sharedPreferences23.getString("selectedSupplierID", "");
                String savedSupplierName = sharedPreferences23.getString("selectedSupplierName", "");

                SharedPreferences sharedPreferences234 = requireContext().getSharedPreferences("BrandPrefs", MODE_PRIVATE);
                String savedBrandID = sharedPreferences234.getString("selectedBrandID", "");
                String savedBrandName = sharedPreferences234.getString("selectedBrandName", "");

                String department = String.valueOf(savedDepartmentID);
                String subdepartment = String.valueOf(savedsubDepartmentID);
                String supplier = savedSupplierName;
                String brand = String.valueOf(savedBrandName);
                String outerbarcode = outerbarcodeedt.getText().toString();
                String agelimit = selectedage;
                String vatValueTxt = vatValue.replace("%", "").trim();
                String costpercase = costpercase1.getText().toString();
                String additionalbarcode1 = additionalbarcode.getText().toString();
                String ss_price = sellingpriceedt.getText().toString();
                String margin = margin1.getText().toString();
                String caseunit = caseunitedt.getText().toString();

                String quantityText = quantityedt1.getText().toString().trim();

                // Input validation
                if (Barcode.isEmpty()) {
                    Toast.makeText(getContext(), "Barcode cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (sellingpriceedt.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Selling price cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("sellingprice", sellingpriceedt.getText().toString());

                if (!validateDepartmentSelection()) {
                    return; // Stop if department is invalid
                }

                if (quantityText.isEmpty()) {
                    quantity4 = "0"; // Default to "0"
                } else {
                    try {
                        Double.parseDouble(quantityText); // Ensure it's numeric
                        quantity4 = quantityText;
                    } catch (NumberFormatException e) {
                        quantityedt1.setError("Invalid number");
                        return;
                    }
                }

                String unitPerCaseText = unitpercase1.getText().toString();
                if (unitPerCaseText.isEmpty()) {
                    unitPerCaseText = "0";  // Set it to "0" if empty
                }

                String inputDate = expiry_dateedt1.getText().toString().trim(); // Get text from EditText
                String formattedDate = convertDateFormat(inputDate); // Convert to YYYYMMDD format

                String formattedValuecaseprice = getFormattedValue(casepricewdt);

                double calvat = (Double.parseDouble(sellingpriceedt.getText().toString()) * Double.parseDouble(vatValueTxt)) / (100 + Double.parseDouble(vatValueTxt));


                String buyprice = (costPrice != null) ? String.format(Locale.getDefault(), "%.2f", costPrice) : "0.00";

                String quantity = quantityedt1.getText().toString().trim();
                if (quantity.isEmpty()) {
                    quantity = "0";
                }
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                Date currentDate1 = new Date();


                int selectedId = radioGroupUnit.getCheckedRadioButtonId();
                String selectedUnit = "";
                String selectedUnitnum;// Will store "UNIT" or "KG"

                if (selectedId == R.id.radioButtonunit1) {
                    selectedUnit = "Unit";
                    selectedUnitnum = "2";
                } else if (selectedId == R.id.radioButtonkg2) {
                    selectedUnit = "Kg";
                    selectedUnitnum = "1";
                } else if (selectedId == R.id.radioButton0) {
                    selectedUnit = "0";
                    selectedUnitnum = "0";
                } else {
                    Toast.makeText(getContext(), "Please select a unit!", Toast.LENGTH_SHORT).show();
                    return;
                }


                String safeBrandName = (brandName != null) ? brandName : "Default";

                SharedPreferences sharedPreferences3 = getActivity().getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
                String selectedSupplierName = sharedPreferences3.getString("selectedSupplierName", "");

                updateProduct(
                        Barcode, itemcode, description, plu, department, subdepartment,
                        selectedSupplierName, safeBrandName, outerbarcode, agelimit,
                        String.valueOf(calvat), unitPerCaseText, costpercase, ss_price,
                        additionalbarcode1, String.valueOf(buyprice), margin,
                        markupedt.getText().toString(), quantity, formattedDate,
                        ss_price, formattedValuecaseprice, caseunit, vatValue,
                        dateFormat1.format(currentDate1),
                        selectedUnit  // Pass the selected Unit_scale
                );

                new UpdateQuantityByBarcodeTask().execute();
            }
        });

        radioGroupddprice = view.findViewById(R.id.ddprice); // Replace with your RadioGroup ID
        //    Toast.makeText(getContext(), ""+dd_Price, Toast.LENGTH_SHORT).show();

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

        radioGroup = view.findViewById(R.id.ddpoints); // Replace with your RadioGroup ID
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

        CheckBox didCountCheckBox = view.findViewById(R.id.didcount); // Initialize your CheckBox


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

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DepartmentPrefs", MODE_PRIVATE);
                String savedDepartmentID = sharedPreferences.getString("selectedDepartmentID", "");
                String savedDepartmentName = sharedPreferences.getString("selectedDepartmentName", "");

                SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("SubDepartmentPrefs", MODE_PRIVATE);
                String savedsubDepartmentID = sharedPreferences2.getString("selectedSubDepartmentID", "");
                String savedsubDepartmentName = sharedPreferences2.getString("selectedSubDepartmentName", "");

                SharedPreferences sharedPreferences23 = requireContext().getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
                String savedSupplierID = sharedPreferences23.getString("selectedSupplierID", "");
                String savedSupplierName = sharedPreferences23.getString("selectedSupplierName", "");

                SharedPreferences sharedPreferences234 = requireContext().getSharedPreferences("BrandPrefs", MODE_PRIVATE);
                String savedBrandID = sharedPreferences234.getString("selectedBrandID", "");
                String savedBrandName = sharedPreferences234.getString("selectedBrandName", "");

                String department = String.valueOf(savedDepartmentID);
                String subdepartment = String.valueOf(savedsubDepartmentName);
                String supplier = savedSupplierName;
                String brand = String.valueOf(savedSupplierID);
                String outerbarcode = outerbarcodeedt.getText().toString();
                String agelimit = selectedage;
                String vat = vatValue;
                String unitpercase = unitpercase1.getText().toString();
                String costpercase = costpercase1.getText().toString();
                String price = costpriceedt.getText().toString();
                String additionalbarcode1 = additionalbarcode.getText().toString();
                String ss_price = sellingpriceedt.getText().toString();
                String margin = margin1.getText().toString();
                String startdate1 = startdate.getText().toString();
                String enddate = enddatedt.getText().toString();
                String ddprice = String.valueOf(enableDisableValueddprice);
                String ddpoints = String.valueOf(enableDisableValue);
                String managestock = String.valueOf(enableDisableValuemangestock);
                String weight = weightedt.getText().toString();
                String capacity1 = capacity.getText().toString();
                String currentstock1 = currentstock.getText().toString();
                String quanityy = quantity.getText().toString();
                String reorderlevel1 = reorderlevel.getText().toString();
                String minstock = minstaockedt.getText().toString();
                String discount = String.valueOf(discountValue);


                // Input validation
                if (Barcode.isEmpty()) {
                    Toast.makeText(getContext(), "Barcode cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (sellingprice.isEmpty()) {
                    Toast.makeText(getContext(), "Selling price cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Show a progress dialog if desired
                // ProgressDialog progressDialog = ProgressDialog.show(getContext(), "Updating Product", "Please wait...", true);

                new UpdateProductTask().execute(Barcode, description, plu, department, subdepartment, supplier, brand, outerbarcode, agelimit, vat, unitpercase, costpercase, price, additionalbarcode1, ss_price, margin, startdate1, enddate, ddprice, ddpoints, managestock, weight, currentstock1, quanityy, reorderlevel1, minstock, discount, capacity1, // Pass capacity here
                        itemcode // Pass the item code last for the WHERE clause
                );


            }
        });

        addtobarcode = view.findViewById(R.id.addtobarcode);
        addtobarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = new Date();
                double price;
                String costPriceText = costpriceedt.getText().toString();
                if (costPriceText.isEmpty()) {
                    price = 0.0;  // Default to 0 if the EditText is empty
                } else {
                    price = Double.parseDouble(costPriceText);  // Parse the value if not empty
                }

                new InsertBarcodeTask(barcode, plu, description, "", price, date, capacitys, quantityedt1.getText().toString()).execute();

            }
        });

        unitpercase1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCostPrice();
                calculatecurrentstock();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        costpercase1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCostPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        caseunitedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatecurrentstock();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        margin1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateSellingPriceFromMargin();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        markupedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdatingMarkup && !s.toString().isEmpty()) {
                    calculateSellingPriceFromMarkup();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        Button title4545 = view.findViewById(R.id.print);
        title4545.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintManager printManager = (PrintManager) getActivity().getSystemService(PRINT_SERVICE);
                printManager.print("ShopBill", new MyPrintDocumentAdapterAll(getContext(), barcide1.getText().toString(), "1.60", "2",
                        description1.getText().toString()), null);
            }
        });

        costpercase1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a pattern without commas
                decimalFormat = new DecimalFormat("0.00"); // No grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    costpercase1.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        costpercase1.setText(currentText);
                        costpercase1.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        costpercase1.setText("");
                    }

                    costpercase1.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        sellingpriceedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCostPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        sellingpriceedt.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a pattern without commas
                decimalFormat = new DecimalFormat("0.00"); // No grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    sellingpriceedt.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        sellingpriceedt.setText(currentText);
                        sellingpriceedt.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        sellingpriceedt.setText("");
                    }

                    sellingpriceedt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });


        margin1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat = new DecimalFormat("0.00"); // 2 decimal places

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    margin1.removeTextChangedListener(this); // Prevent infinite loop

                    String cleanString = s.toString().replaceAll("[^\\d]", ""); // Remove non-digits

                    if (!cleanString.isEmpty()) {
                        // Convert to decimal (e.g., "1234" → 12.34)
                        double parsedValue = Double.parseDouble(cleanString) / 100;

                        // Cap the value at 99.99
                        if (parsedValue > 99.99) {
                            parsedValue = 99.99;
                        }


                        currentText = decimalFormat.format(parsedValue);
                        margin1.setText(currentText);
                        margin1.setSelection(currentText.length()); // Move cursor to end
                    } else {
                        currentText = "";
                        margin1.setText("");
                    }

                    margin1.addTextChangedListener(this); // Reattach listener
                    calculateSellingPriceFromMargin(); // Update selling price
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        markupedt.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a custom pattern without commas
                decimalFormat = new DecimalFormat("0.00"); // No grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    markupedt.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        markupedt.setText(currentText);
                        markupedt.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        markupedt.setText("");
                    }

                    markupedt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        casepricewdt.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a pattern without commas
                decimalFormat = new DecimalFormat("0.00"); // No grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    casepricewdt.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        casepricewdt.setText(currentText);
                        casepricewdt.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        casepricewdt.setText("");
                    }

                    casepricewdt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });


        scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("nlscan.action.SCANNER_RESULT".equals(intent.getAction())) {
                    String scanResult = intent.getStringExtra("SCAN_BARCODE1"); // Get scanned text
                    if (scanResult != null) {
                        outerbarcodeedt.setText(scanResult); // Set scanned QR code text in EditText
                    }
                }
            }
        };

        // Register the receiver using requireContext()
        IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        registerReceiver(requireContext(), scanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);


        setupZeroClearingEditText(outerbarcodeedt);

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
            Unit_scale = getArguments().getString("Unit_scale");
        }
    }


    private void updateProduct(
            final String barcode, final String itemCode, final String description,
            final String plu, final String department, final String subdepartment,
            final String supplier, final String brand, final String outerbarcode,
            final String agelimit, final String vat, final String unitpercase,
            final String costpercase, final String price, final String additionalbarcode1,
            final String ss_price, final String margin, final String markup,
            final String quantity, final String expiredate, final String saleWithVAT,
            final String casePrice, final String caseUnit, final String vatValue,
            final String dateAdded,
            final String unitScale
    ) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String result = performUpdate(
                    barcode, itemCode, description, plu, department, subdepartment,
                    supplier, brand, outerbarcode, agelimit, vat, unitpercase,
                    costpercase, price, additionalbarcode1, ss_price, margin,
                    markup, quantity, expiredate, saleWithVAT, casePrice,
                    caseUnit, vatValue, dateAdded, unitScale
            );

            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            });
        });
    }


    private String performUpdate(
            String barcode, String itemCode, String description, String plu,
            String department, String subdepartment, String supplier, String brand,
            String outerbarcode, String agelimit, String vat, String unitpercase,
            String costpercase, String price, String additionalbarcode1,
            String ss_price, String margin, String markup, String quantity,
            String expiredate, String saleWithVAT, String casePrice,
            String caseUnit, String vatValue, String dateAdded,
            String unitScale
    ) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
            connection = DriverManager.getConnection(connectionUrl, username, password);

            if (connection != null) {
                // Get the selected unit value
                int selectedId = radioGroupUnit.getCheckedRadioButtonId();
                String selectedUnitnum = "";

                if (selectedId == R.id.radioButtonunit1) {
                    selectedUnitnum = "2"; // Unit
                } else if (selectedId == R.id.radioButtonkg2) {
                    selectedUnitnum = "1"; // Kg
                } else if (selectedId == R.id.radioButton0) {
                    selectedUnitnum = "0"; // Kg
                } else {
                    return "Please select a unit!";
                }

                String sql = "UPDATE tbl_Products SET Barcode = ?, Item_code = ?, Description = ?, PLU = ?, Department = ?, " +
                        "Sub_department = ?, Supplier = ?, Brand = ?, OuterBarcode = ?, Age_Limit = ?, VAT = ?, " +
                        "UnitPerCase = ?, CostPerCase = ?, Price = ?, AdditionalBarcode1 = ?, Buy_Price = ?, " +
                        "Margin = ?, Markup = ?, Quantity = ?, Expiry_date = ?, SaleWithVAT = ?, CasePrice = ?, " +
                        "CaseUnit = ?, VatValue = ?, DateAdded = ?, Unit_scale = ?, Num = ? WHERE Barcode = ?";

                statement = connection.prepareStatement(sql);

                // Bind parameters
                statement.setString(1, barcode);
                statement.setString(2, itemCode);
                statement.setString(3, description);
                statement.setString(4, plu);
                statement.setString(5, department);
                statement.setString(6, subdepartment);
                statement.setString(7, supplier);
                statement.setString(8, brand);
                statement.setString(9, outerbarcode);
                statement.setString(10, agelimit);
                statement.setString(11, vat);
                statement.setString(12, unitpercase);
                statement.setString(13, costpercase);
                statement.setString(14, price);
                statement.setString(15, additionalbarcode1);
                statement.setString(16, ss_price);
                statement.setString(17, margin);
                statement.setString(18, markup);
                statement.setString(19, quantity);
                statement.setString(20, expiredate);
                statement.setString(21, saleWithVAT);
                statement.setString(22, casePrice);
                statement.setString(23, caseUnit);
                statement.setString(24, vatValue);
                statement.setString(25, dateAdded);
                statement.setString(26, unitScale);
                statement.setString(27, selectedUnitnum); // Num column value
                statement.setString(28, barcode);    // WHERE clause

                int rowsUpdated = statement.executeUpdate();
                return (rowsUpdated > 0) ? "Update Successful" : "Update Failed";
            }
        } catch (SQLException e) {
            return "SQL Error: " + e.getMessage();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
            }
        }
        return "Connection Failed";
    }

    private class UpdateProductTask extends AsyncTask<String, Void, String> {
        @SuppressLint("WrongThread")
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
            String capacity1 = params[27];
            String itemCode = params[28]; // Unique identifier for the item
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                // Connect to the database
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                if (connection != null) {
                    // SQL query to update all relevant fields in the tbl_Products table
                    String sql = "UPDATE tbl_Products SET Barcode = ?, Description = ?, PLU = ?, Department = ?, Sub_department = ?, Supplier = ?, Brand = ?, " + "OuterBarcode = ?, Age_Limit = ?, VAT = ?, UnitPerCase = ?, CostPerCase = ?, SS_Price = ?, AdditionalBarcode1 = ?, " + "Price = ?, Margin = ?, StartDate = ?, EndDate = ?, DD_Price = ?, SS_Points = ?, ManageStock = ?, Weight = ?, " + "CurrentStock = ?, Quantity = ?, ReorderLevel = ?, MinStock = ?, Discount = ?, Capacity = ? " + "WHERE item_code = ?";

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
                    statement.setString(13, ss_price);
                    statement.setString(14, additionalbarcode1);
                    statement.setString(15, price);
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
                } else {
                    return "Connection Failed";
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                e.printStackTrace();
                return "SQL Error: Duplicate entry for a unique field.";
            } catch (SQLException e) {
                e.printStackTrace();
                return "SQL Error: " + e.getMessage();
            } finally {
                // Close resources
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result of the update
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }


    private class UpdateQuantityByBarcodeTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "UpdateQuantityByBarcodeTask"; // Define a tag for logging

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Construct the database connection URL dynamically using SharedPreferences values
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + ";databaseName=" + databaseName;
            Log.d(TAG, "Database URL: " + url); // Log the connection URL

            // Adjusted update query to only modify the Quantity field based on Barcode
            String updateQuery = "UPDATE [dbo].[tbl_SoldItems] " + "SET [Quantity] = ? " + "WHERE [Barcode] = ?";

            try {
                // Log the start of the connection attempt
                Log.d(TAG, "Attempting to connect to the database...");

                // Load the jTDS driver (this is sometimes not necessary with recent versions of Android)
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                // Establish connection to the database
                Connection connection = DriverManager.getConnection(url, username, password);
                Log.d(TAG, "Connection successful.");

                // Prepare the update statement
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

                // Set the values for the placeholders in the query
                preparedStatement.setInt(1, Integer.parseInt(quantity4)); // New Quantity
                preparedStatement.setString(2, barcide1.getText().toString()); // Barcode

                // Log the prepared statement execution
                Log.d(TAG, "Executing update query: " + updateQuery);

                // Execute the update
                int result = preparedStatement.executeUpdate();
                Log.d(TAG, "Update result: " + result);

                // Close the connection
                preparedStatement.close();
                connection.close();
                Log.d(TAG, "Database connection closed.");

                // If update is successful, return true
                return result > 0;

            } catch (Exception e) {
                // Log the exception
                Log.e(TAG, "Error during database operation", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                // Show a success message
                Log.d(TAG, "Quantity updated successfully.");

                Date date = new Date();
                double price1;
                String costPriceText = costpriceedt.getText().toString();
                if (costPriceText.isEmpty()) {
                    price1 = 0.0;  // Default to 0 if the EditText is empty
                } else {
                    price1 = Double.parseDouble(costPriceText);  // Parse the value if not empty
                }

                new InsertBarcodeTask(barcode, plu, description, "", price1, date, capacitys, quantityedt1.getText().toString()).execute();

                BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();


                // Toast.makeText(getContext(), "Quantity updated successfully!", Toast.LENGTH_SHORT).show();
                logPriceChange(barcode, price, sellingpriceedt.getText().toString(), "3");
            } else {
                // Show an error message
                Log.d(TAG, "Failed to update quantity.");
                Toast.makeText(getContext(), "Failed to update quantity.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFormattedValue(EditText editText) {
        String input = editText.getText().toString();
        double value = input.isEmpty() ? 0 : Double.parseDouble(input);
        return String.format("%.2f", value);
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

    public String convertToReadableDate(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            return ""; // Return empty if no input
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()); // Input format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Output format

        try {
            Date date = inputFormat.parse(inputDate); // Convert to Date object
            return outputFormat.format(date); // Convert back to formatted string
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty in case of error
        }
    }

    public String convertDateFormat(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            return ""; // Return empty if no input
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Input format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()); // Output format

        try {
            Date date = inputFormat.parse(inputDate); // Convert to Date object
            return outputFormat.format(date); // Convert back to formatted string
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty in case of error
        }
    }

    private void calculateCostPrice() {
        String unitPerCaseTxt = unitpercase1.getText().toString().trim();
        String costPerCaseTxt = costpercase1.getText().toString().trim().replaceAll(",", ""); // Remove commas
        String vatValueTxt = vatValue.replace("%", "").trim();

        // Check if inputs are not empty
        if (unitPerCaseTxt.isEmpty() || costPerCaseTxt.isEmpty() || vatValueTxt.isEmpty()) {
            costpriceedt.setText(""); // Clear the result if any field is empty
            return; // Exit the method
        }

        try {
            // Parse the input strings to Double
            double unitPerCase = Double.parseDouble(unitPerCaseTxt);
            double costPerCase = Double.parseDouble(costPerCaseTxt);
            double vatValue = Double.parseDouble(vatValueTxt); // Convert VAT value to numeric

            // Calculate VAT multiplier
            double vatMultiplier = (vatValue + 100) / 100;

            // Check for division by zero
            if (unitPerCase == 0) {
                costpriceedt.setText("0.00"); // Set result to 0 if division by zero
                return; // Exit the method
            }

            // Calculate cost price
            costPrice = (costPerCase * vatMultiplier) / unitPerCase;

            // Set the result to costpriceedt
            costpriceedt.setText(String.format(Locale.getDefault(), "%.2f", costPrice));

            calculateMargin(costPrice);
            calculateMarkup(BigDecimal.valueOf(costPrice));

        } catch (NumberFormatException e) {
            costpriceedt.setText(""); // Clear the result if input is invalid
        }
    }


    private boolean isCalculating = false;

// Then modify both calculation methods:

    private void calculateMargin(Double costPrice) {
        if (isCalculating) return;
        isCalculating = true;

        try {
            String sellingPriceStr = sellingpriceedt.getText().toString().trim();

            if (!sellingPriceStr.isEmpty()) {
                double sellingPrice = Double.parseDouble(sellingPriceStr);

                if (sellingPrice != 0) {
                    double margin = ((sellingPrice - costPrice) / sellingPrice) * 100;
                    margin1.setText(String.format("%.2f", margin));
                } else {
                    margin1.setText("0");
                }
            } else {
                margin1.setText("");
            }
        } catch (NumberFormatException e) {
            margin1.setText("Invalid input");
        } finally {
            isCalculating = false;
        }
    }

    private void calculateSellingPriceFromMargin() {
        if (isCalculating) return;
        isCalculating = true;

        try {
            String marginStr = margin1.getText().toString().trim();
            String costPriceStr = costpriceedt.getText().toString().trim();

            if (!marginStr.isEmpty() && !costPriceStr.isEmpty()) {
                double margin = Double.parseDouble(marginStr);
                double costPrice = Double.parseDouble(costPriceStr);

                if (margin != 100) {
                    double sellingPrice = costPrice / (1 - (margin / 100));
                    sellingpriceedt.setText(String.format(Locale.getDefault(), "%.2f", sellingPrice));
                }
            }
        } catch (NumberFormatException e) {
            // Handle error
        } finally {
            isCalculating = false;
        }
    }

    // Add this field to your class to prevent circular updates
    private boolean isUpdatingMarkup = false;


    // Add this method to calculate selling price from markup
    private void calculateSellingPriceFromMarkup() {
        try {
            String markupStr = markupedt.getText().toString().replace("%", "").trim();
            String costPriceStr = costpriceedt.getText().toString().trim();

            if (markupStr.isEmpty() || costPriceStr.isEmpty()) {
                return;
            }

            BigDecimal markup = new BigDecimal(markupStr);
            BigDecimal costPrice = new BigDecimal(costPriceStr);

            if (costPrice.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }

            // Selling Price = Cost Price * (1 + Markup/100)
            BigDecimal sellingPrice = costPrice.multiply(
                    BigDecimal.ONE.add(markup.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP))
            );

            isUpdatingMarkup = true;
            sellingpriceedt.setText(sellingPrice.setScale(2, RoundingMode.HALF_UP).toString());
            isUpdatingMarkup = false;

        } catch (NumberFormatException e) {
            // Handle invalid input
        } catch (ArithmeticException e) {
            // Handle calculation error
        }
    }

    // Modify your existing calculateMarkup method to prevent circular updates
    private void calculateMarkup(BigDecimal costPrice) {
        if (isUpdatingMarkup) return;

        try {
            String sellingPriceStr = sellingpriceedt.getText().toString().trim();

            if (sellingPriceStr.isEmpty()) {
                markupedt.setText("Enter selling price");
                return;
            }

            BigDecimal sellingPrice = new BigDecimal(sellingPriceStr);

            if (costPrice.compareTo(BigDecimal.ZERO) == 0) {
                markupedt.setText("Cost price cannot be zero");
                return;
            }

            BigDecimal markup = sellingPrice.subtract(costPrice)
                    .multiply(new BigDecimal(100))
                    .divide(costPrice, 2, RoundingMode.HALF_UP);

            isUpdatingMarkup = true;
            markupedt.setText(markup + "%");
            isUpdatingMarkup = false;

        } catch (NumberFormatException e) {
            markupedt.setText("Invalid number");
        } catch (ArithmeticException e) {
            markupedt.setText("Calculation error");
        }
    }


    private void calculatecurrentstock() {
        String unitPerCaseTxt = unitpercase1.getText().toString().trim();
        String caseUnitTxt = caseunitedt.getText().toString().trim();

        // Check if inputs are not empty
        if (unitPerCaseTxt.isEmpty() || caseUnitTxt.isEmpty()) {
            quantityedt1.setText(""); // Clear the result if any field is empty
            return; // Exit the method
        }

        try {
            // Parse unitPerCase as integer
            int unitPerCase = Integer.parseInt(unitPerCaseTxt);

            // Try to parse caseUnit as integer, if fails default to 1
            int caseUnit;
            try {
                caseUnit = Integer.parseInt(caseUnitTxt);
            } catch (NumberFormatException e) {
                caseUnit = 1; // Default value if caseUnit is not a number (like "each")
            }

            // Calculate current stock value
            currentstockvalue = unitPerCase * caseUnit;

            // Set the result to quantityedt1
            quantityedt1.setText(String.valueOf(currentstockvalue));
        } catch (NumberFormatException e) {
            quantityedt1.setText(""); // Clear the result if input is invalid
        }
    }


    private boolean validateDepartmentSelection() {
        String selected = departmentSpinner.getText().toString().trim();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Please select a department", Toast.LENGTH_SHORT).show();
            departmentSpinner.requestFocus();
            departmentSpinner.showDropDown();
            return false;
        }

        for (int i = 0; i < departmentSpinner.getAdapter().getCount(); i++) {
            Departmentspinner item = (Departmentspinner) departmentSpinner.getAdapter().getItem(i);
            if (item.getDepartment().equalsIgnoreCase(selected)) {
                departmentId = item.getId();
                return true;
            }
        }

        Toast.makeText(getContext(), "Please select a valid department from the list", Toast.LENGTH_SHORT).show();
        departmentSpinner.requestFocus();
        departmentSpinner.showDropDown();
        return false;
    }


    private boolean validateSubDepartmentSelection() {
        String selected = subdepartmentSpinner.getText().toString().trim();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Please select a sub-department", Toast.LENGTH_SHORT).show();
            subdepartmentSpinner.requestFocus();
            subdepartmentSpinner.showDropDown();
            return false;
        }

        for (int i = 0; i < subdepartmentSpinner.getAdapter().getCount(); i++) {
            SubDepartmentspinner item = (SubDepartmentspinner) subdepartmentSpinner.getAdapter().getItem(i);
            if (item.getSubDepartment().equalsIgnoreCase(selected)) {
                subdepartmentId = item.getId();
                return true;
            }
        }

        Toast.makeText(getContext(), "Please select a valid sub-department from the list", Toast.LENGTH_SHORT).show();
        subdepartmentSpinner.requestFocus();
        subdepartmentSpinner.showDropDown();
        return false;
    }

    private boolean validateSupplierSelection() {
        String selected = supplierSpinner.getText().toString().trim();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Please select a supplier", Toast.LENGTH_SHORT).show();
            supplierSpinner.requestFocus();
            supplierSpinner.showDropDown();
            return false;
        }

        for (int i = 0; i < supplierSpinner.getAdapter().getCount(); i++) {
            SupplierSpinner item = (SupplierSpinner) supplierSpinner.getAdapter().getItem(i);
            if (item.getSupplier().equalsIgnoreCase(selected)) {
                // Assuming you have a supplierId field
                supplierName = item.getSupplier();
                return true;
            }
        }

        Toast.makeText(getContext(), "Please select a valid supplier from the list", Toast.LENGTH_SHORT).show();
        supplierSpinner.requestFocus();
        supplierSpinner.showDropDown();
        return false;
    }

    private boolean validateBrandSelection() {
        String selected = brandSpinner.getText().toString().trim();
        if (selected.isEmpty()) {
            Toast.makeText(getContext(), "Please select a brand", Toast.LENGTH_SHORT).show();
            brandSpinner.requestFocus();
            brandSpinner.showDropDown();
            return false;
        }

        for (int i = 0; i < brandSpinner.getAdapter().getCount(); i++) {
            BrandSpinner item = (BrandSpinner) brandSpinner.getAdapter().getItem(i);
            if (item.getBrand().equalsIgnoreCase(selected)) {

                brandName = item.getBrand();
                BrandDone = item.getId(); // Set the selected brand ID
                return true;
            }
        }

        Toast.makeText(getContext(), "Please select a valid brand from the list", Toast.LENGTH_SHORT).show();
        brandSpinner.requestFocus();
        brandSpinner.showDropDown();
        return false;
    }

    private class InsertBarcodeTask extends AsyncTask<Void, Void, Boolean> {
        private String barcode;
        private String plu;
        private String detail;
        private String shop;
        private double price;
        private Date date;
        private String capacity;
        private String qty;
        private boolean barcodeExists = false;
        String TAG = "barcode";

        public InsertBarcodeTask(String barcode, String plu, String detail, String shop, double price, Date date, String capacity, String qty) {
            this.barcode = barcode;
            this.plu = plu;
            this.detail = detail;
            this.shop = shop;
            this.price = price;
            this.date = date;
            this.capacity = capacity;
            this.qty = qty;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connection connection = null;
            PreparedStatement checkStatement = null;
            PreparedStatement insertStatement = null;
            PreparedStatement updateStatement = null;

            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                // Check if barcode exists
                String checkQuery = "SELECT COUNT(*) FROM [STAR_RETAIL].[dbo].[tblBarcode] WHERE Barcode = ?";
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, barcode);
                java.sql.ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    barcodeExists = true;

                    // UPDATE existing barcode record
                    String updateQuery = "UPDATE [STAR_RETAIL].[dbo].[tblBarcode] SET " + "[PLU] = ?, " + "[Detail] = ?, " + "[Shop] = ?, " + "[Price] = ?, " + "[dtDate] = ?, " + "[Capacity] = ?, " + "[Qty] = ? " + "WHERE [Barcode] = ?";

                    updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, plu);
                    updateStatement.setString(2, detail);
                    updateStatement.setString(3, shop);
                    updateStatement.setString(4, "£" + String.format("%.2f", price));
                    updateStatement.setString(5, new SimpleDateFormat("dd/MM/yyyy").format(date));
                    updateStatement.setString(6, capacity);
                    updateStatement.setString(7, qty);
                    updateStatement.setString(8, barcode);

                    int rowsUpdated = updateStatement.executeUpdate();
                    return rowsUpdated > 0;
                } else {
                    // INSERT new barcode record
                    String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[tblBarcode] " + "([PLU], [Barcode], [Detail], [Shop], [Price], [dtDate], [Capacity], [Qty]) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, plu);
                    insertStatement.setString(2, barcode);
                    insertStatement.setString(3, detail);
                    insertStatement.setString(4, shop);
                    insertStatement.setString(5, "£" + String.format("%.2f", price));
                    insertStatement.setString(6, new SimpleDateFormat("dd/MM/yyyy").format(date));
                    insertStatement.setString(7, capacity);
                    insertStatement.setString(8, qty);

                    insertStatement.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                Log.e(TAG, "Database error: " + e.getMessage());
                return false;
            } finally {
                try {
                    if (checkStatement != null) checkStatement.close();
                    if (insertStatement != null) insertStatement.close();
                    if (updateStatement != null) updateStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    Log.e(TAG, "Error closing resources: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (isSuccess) {
                String message = barcodeExists ? "Barcode updated successfully" : "Barcode inserted successfully";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Database operation failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logPriceChange(String barcode, String oldPrice, String newPrice, String doneBy) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String result = insertPriceChangeLog(barcode, oldPrice, newPrice, doneBy);
            getActivity().runOnUiThread(() -> {
                Log.d("PriceChangeLog", result);
            });
        });
    }

    private String insertPriceChangeLog(String barcode, String oldPrice, String newPrice, String doneBy) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
            connection = DriverManager.getConnection(connectionUrl, username, password);

            if (connection != null) {
                String sql = "INSERT INTO [STAR_RETAIL].[dbo].[Price_change_Logs] " + "([Barcode], [Pricechange_Date], [Pricechange_Time], [Old_Price], [New_Price], [Done_by]) " + "VALUES (?, ?, ?, ?, ?, ?)";

                // Get current date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                String currentTime = timeFormat.format(new Date());

                statement = connection.prepareStatement(sql);
                statement.setString(1, barcode);
                statement.setString(2, currentDate);
                statement.setString(3, currentTime);
                statement.setString(4, oldPrice);
                statement.setString(5, newPrice);
                statement.setString(6, doneBy);

                int rowsInserted = statement.executeUpdate();
                return (rowsInserted > 0) ? "Price change logged successfully" : "Failed to log price change";
            }
        } catch (SQLException e) {
            Log.e("PriceChangeLog", "SQL Exception: " + e.getMessage(), e);
            return "SQL Error: " + e.getMessage();
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Log.e("PriceChangeLog", "Failed to close resources: " + e.getMessage(), e);
            }
        }
        return "Connection Failed";
    }


    private void updateAgeSpinner(String age) {
        // Create list of all available age options
        List<String> ageOptions = new ArrayList<>();
        ageOptions.add("Anyone");
        ageOptions.add("18+");

        // If the department's age is null/empty or not in our options, default to "Anyone"
        if (age == null || age.isEmpty() || !ageOptions.contains(age)) {
            age = "Anyone";
        }

        // Set adapter with all options
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, ageOptions);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);

        // Set the selected item to the department's age restriction
        int position = ageAdapter.getPosition(age);
        if (position >= 0) {
            ageSpinner.setSelection(position);
        }
    }


    private void updateVATSpinner(String vat) {
        // Create list of all available VAT options
        List<String> vatOptions = new ArrayList<>();
        vatOptions.add("0%");
        vatOptions.add("5%");
        vatOptions.add("20%");

        // If the department's VAT is null/empty or not in our options, default to "0%"
        if (vat == null || vat.isEmpty() || !vatOptions.contains(vat)) {
            vat = "0%";
        }

        // Set adapter with all options
        ArrayAdapter<String> vatAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, vatOptions);
        vatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vatSpinner.setAdapter(vatAdapter);

        // Set the selected item to the department's VAT
        int position = vatAdapter.getPosition(vat);
        if (position >= 0) {
            vatSpinner.setSelection(position);
        }
    }


    private void setupZeroClearingEditText(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && "0".equals(editText.getText().toString().trim())) {
                    editText.setText("");
                }
            }
        });
    }

}
