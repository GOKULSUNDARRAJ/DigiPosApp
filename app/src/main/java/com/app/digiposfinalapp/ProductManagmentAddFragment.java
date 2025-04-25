package com.app.digiposfinalapp;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.app.digiposfinalapp.util.SettingsHelper;
import com.app.digiposfinalapp.util.UIHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.device.ZebraIllegalArgumentException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

public class ProductManagmentAddFragment extends Fragment {

    String ipAddress, portNumber, databaseName, username, password;
    private static final String TAG = "ProductManagementAddFragment";

    EditText itemcode1, barcide1, description1, unitpercase1, costpercase1,
            margin1, sellingpriceedt, outerbarcodeedt, additionalbarcode, ddpriceedt, startdate, enddatedt, weightedt,
            currentstock, capacity, quantity, minstaockedt, reorderlevel, markupedt, quantityedt1, casepricewdt, caseunitedt;
    AutoCompleteTextView departmentSpinner, supplierSpinner, subdepartmentSpinner, brandSpinner;
    Spinner vatSpinner;

    TextView pludt;
    EditText expiry_dateedt1;
    int subdepartmentId, departmentId, BrandDone;
    String brandName;
    String supplierName;
    String barcodeValue, vatValue, Pluvalu, formattedValuemargin, fromProductmagementfullFragment,
            fromPriceSubFragment, fromStockSubFragment, fromStockSubFragment2, fromOrderCategoryFragment,
            fromDelivermanagmentsearchFragment, fromBarCodeScanFragmentNewPromotionsearch, fromPromoItemsFragment,
            fromActiveCountProductFragment, fromLablePrintFragment, fromBarCodeScanFragmentNewSearchLablePrintQuick,
            fromBarCodeScanFragmentNewPromotionsearchedit,StockSnapshotsearchFragment;
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

    int discountValue = 0;

    private String currentInputSellingPrice = "";

    StringBuilder currentNumber = new StringBuilder(); // For appending numbers

    String formattedDate;

    LinearLayout layoutToConvert, layoutToConvert2, layoutToConvert3;

    int currentstockvalue;

    private View selectedLayout;
    private Button printerbtn;
    private RadioGroup choselayout;

    private static final String DEFAULT_BLE_ADDRESS = "8C:D5:4A:13:77:AD";
    private UIHelper helper;
    private Bitmap bitmap;
    String age, vat;

    private BroadcastReceiver scanReceiver;

    private RadioGroup radioGroupUnit;
    private RadioButton radioUnit, radioKg,radioButtondefault;
    private String selectedUnit;
    private String selectedUnitnum;// Will store "UNIT" or "KG"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Fragment created");
        View view = inflater.inflate(R.layout.fragment_product_managment, container, false);


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


        try {
            // Find the NestedScrollView
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

            if (getArguments() != null) {
                barcodeValue = getArguments().getString("barcode");
                Pluvalu = getArguments().getString("PLU");
                fromProductmagementfullFragment = getArguments().getString("fromProductmagementfullFragment");
                fromPriceSubFragment = getArguments().getString("fromPriceSubFragment");
                fromStockSubFragment = getArguments().getString("fromStockSubFragment");
                fromStockSubFragment2 = getArguments().getString("fromStockSubFragment2");
                fromOrderCategoryFragment = getArguments().getString("fromOrderCategoryFragment");
                fromDelivermanagmentsearchFragment = getArguments().getString("fromDelivermanagmentsearchFragment");
                fromBarCodeScanFragmentNewPromotionsearch = getArguments().getString("fromBarCodeScanFragmentNewPromotionsearch");
                fromPromoItemsFragment = getArguments().getString("fromPromoItemsFragment");
                fromActiveCountProductFragment = getArguments().getString("fromActiveCountProductFragment");
                fromLablePrintFragment = getArguments().getString("fromLablePrintFragment");
                fromBarCodeScanFragmentNewSearchLablePrintQuick = getArguments().getString("fromBarCodeScanFragmentNewSearchLablePrintQuick");
                fromBarCodeScanFragmentNewPromotionsearchedit = getArguments().getString("fromBarCodeScanFragmentNewPromotionsearchedit");
                StockSnapshotsearchFragment = getArguments().getString("StockSnapshotsearchFragment");
            }

            clear1 = view.findViewById(R.id.clearbtn);
            itemcode1 = view.findViewById(R.id.itemcodeedt);
            barcide1 = view.findViewById(R.id.barcodeedt);
            description1 = view.findViewById(R.id.descriptionedt);
            unitpercase1 = view.findViewById(R.id.unitPerCaseedt);
            costpercase1 = view.findViewById(R.id.costpercaseedt);
            margin1 = view.findViewById(R.id.marginedt);
            departmentSpinner = view.findViewById(R.id.spinner_department);
            subdepartmentSpinner = view.findViewById(R.id.spinner_subdepartment);
            supplierSpinner = view.findViewById(R.id.spinner_spuulier);
            brandSpinner = view.findViewById(R.id.spinner_brand);
            vatSpinner = view.findViewById(R.id.vat_spinner);
            pludt = view.findViewById(R.id.pludt);
            costpriceedt = view.findViewById(R.id.costpricetxt);
            markupedt = view.findViewById(R.id.markupedt);
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
            quantityedt1 = view.findViewById(R.id.quantityedt);
            barcide1.setText(barcodeValue);
            pludt.setText(Pluvalu);
            ageSpinner = view.findViewById(R.id.age_spinner); // Updated ID
            casepricewdt = view.findViewById(R.id.casepriceedt);
            caseunitedt = view.findViewById(R.id.caseunitedt);

            ImageView back = view.findViewById(R.id.imageViewback);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("fromProductmagementfullFragment".equals(fromProductmagementfullFragment)) {
                        BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromPriceSubFragment".equals(fromPriceSubFragment)) {
                        PriceCheckFragment priceSubFragment = new PriceCheckFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromStockSubFragment".equals(fromStockSubFragment)) {
                        StockTakesFragment priceSubFragment = new StockTakesFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromStockSubFragment2".equals(fromStockSubFragment2)) {
                        StockadjustmentsearchFragment priceSubFragment = new StockadjustmentsearchFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromOrderCategoryFragment".equals(fromOrderCategoryFragment)) {
                        BarCodeScanOrderCreateSerachFragment priceSubFragment = new BarCodeScanOrderCreateSerachFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromDelivermanagmentsearchFragment".equals(fromDelivermanagmentsearchFragment)) {
                        DeliveryManagmentFragment priceSubFragment = new DeliveryManagmentFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromBarCodeScanFragmentNewPromotionsearch".equals(fromBarCodeScanFragmentNewPromotionsearch)) {
                        BarCodeScanFragmentNewPromotionsearch priceSubFragment = new BarCodeScanFragmentNewPromotionsearch();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromPromoItemsFragment".equals(fromPromoItemsFragment)) {
                        PromoItemsFragment priceSubFragment = new PromoItemsFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromActiveCountProductFragment".equals(fromActiveCountProductFragment)) {
                        ActiveCountProductFragment priceSubFragment = new ActiveCountProductFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromLablePrintFragment".equals(fromLablePrintFragment)) {
                        BarCodeScanFragmentNewSearchLablePrint priceSubFragment = new BarCodeScanFragmentNewSearchLablePrint();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromBarCodeScanFragmentNewSearchLablePrintQuick".equals(fromBarCodeScanFragmentNewSearchLablePrintQuick)) {
                        BarCodeScanFragmentNewSearchLablePrintQuick priceSubFragment = new BarCodeScanFragmentNewSearchLablePrintQuick();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("fromBarCodeScanFragmentNewPromotionsearchedit".equals(fromBarCodeScanFragmentNewPromotionsearchedit)) {
                        PromoItemEditFragment priceSubFragment = new PromoItemEditFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else if ("StockSnapshotsearchFragment".equals(StockSnapshotsearchFragment)) {
                        StockSnapshotsearchFragment priceSubFragment = new StockSnapshotsearchFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

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

            String currentDate = getCurrentDate();

            startdate.setText(currentDate);
            enddatedt.setText(currentDate);

            radioGroup = view.findViewById(R.id.ddpoints); // Replace with your RadioGroup ID

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

            radioGroupManage = view.findViewById(R.id.managestocks); // Replace with your RadioGroup ID

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

            radioGroupddprice = view.findViewById(R.id.ddprice); // Replace with your RadioGroup ID

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

            costpriceedt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String unitPerCaseTxt = unitpercase1.getText().toString().trim();
                    String costPerCaseTxt = costpercase1.getText().toString().trim();
                    String vatValueTxt = vatValue.replace("%", "").trim(); // Remove '%' from vatValue

                    // Check if inputs are not empty
                    if (unitPerCaseTxt.isEmpty() || costPerCaseTxt.isEmpty() || vatValueTxt.isEmpty()) {
                        Toast.makeText(v.getContext(), "Please enter all values", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(v.getContext(), "Units per case cannot be zero", Toast.LENGTH_SHORT).show();
                            return; // Exit the method
                        }

                        // Calculate cost price
                        double costPrice = (costPerCase * vatMultiplier) / unitPerCase;

                        // Set the result to costpriceedt
                        costpriceedt.setText(String.format(Locale.getDefault(), "%.2f", costPrice));
                    } catch (NumberFormatException e) {
                        // Handle number format exceptions
                        Toast.makeText(v.getContext(), "Invalid input, please enter numeric values", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            new FetchDepartmentData2(getContext(), departmentSpinner).execute();
            new FetchSubDepartmentData2(
                    getContext(),
                    subdepartmentSpinner,
                    departmentId != 0 ? departmentId : 1  // Default to 1 if 0
            ).execute();
            Log.d("DepartmentDebug", "Fetching sub-departments for departmentId: " + departmentId);
            new FetchSupplierData2(getContext(), supplierSpinner).execute();
            new FetchBrandData2(getContext(), brandSpinner).execute();

            departmentSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Departmentspinner selectedDepartment = (Departmentspinner) parent.getItemAtPosition(position);
                    departmentId = selectedDepartment.getId(); // Get the ID of the selected department
                    String departmentName = selectedDepartment.getDepartment(); // Get the name of the selected department

                    new FetchSubDepartmentData2(getContext(), subdepartmentSpinner, departmentId).execute();

                    // Get age restriction from department
                    age = selectedDepartment.getAgestring(); // "Anyone" or "18+"

                    updateAgeSpinner(age);

                    vat = selectedDepartment.getVatstring();
                    updateVATSpinner(vat);

                }
            });

            updateAgeSpinner(age);
            updateVATSpinner(vat);


            departmentSpinner.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    departmentSpinner.showDropDown(); // Show all items when focused
                }
            });
            departmentSpinner.setOnClickListener(v -> departmentSpinner.showDropDown()); // Show all items when clicked


            subdepartmentSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SubDepartmentspinner selectedDepartment = (SubDepartmentspinner) parent.getItemAtPosition(position);
                    subdepartmentId = selectedDepartment.getId(); // Get the ID of the selected subdepartment
                    String departmentName = selectedDepartment.getSubDepartment(); // Get the name of the selected subdepartment

                    // Display selected subdepartment information
                    //  Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + subdepartmentId + ")", Toast.LENGTH_SHORT).show();
                }
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
                    supplierName = selectedSupplier.getSupplier();
                    // Toast.makeText(getContext(), "Selected: " + supplierName, Toast.LENGTH_SHORT).show();
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
                    brandName = selectedBrand.getBrand(); // Get the name of the selected brand
                    BrandDone = selectedBrand.getId(); // Set the selected brand ID

                    // Display selected brand information (optional)
                    //  Toast.makeText(view.getContext(), "Selected: " + brandName + " (ID: " + brandId + ")", Toast.LENGTH_SHORT).show();
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


            // Retrieve database connection details from SharedPreferences
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
            portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
            databaseName = Constants.DATABASE_NAME;
            username = Constants.USERNAME;
            password = Constants.PASSWORD;

            expiry_dateedt1 = view.findViewById(R.id.expiry_dateedt1);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate2 = dateFormat.format(calendar.getTime());

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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getContext(), new DatePickerDialog.OnDateSetListener() {
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

            radioGroupUnit = view.findViewById(R.id.radioGroupunit);
            radioUnit = view.findViewById(R.id.radioButton2); // UNIT
            radioKg = view.findViewById(R.id.radioButton1);   // KG
            radioButtondefault=view.findViewById(R.id.radioButton0);


// Set default value (since UNIT is checked by default in XML)
            selectedUnit = "0";
            selectedUnitnum = "0";

// Listen for changes
            radioGroupUnit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radioButton2) {
                        selectedUnit = "Unit";
                        selectedUnitnum = "2";
                    } else if (checkedId == R.id.radioButton1) {
                        selectedUnit = "Kg";
                        selectedUnitnum = "1";
                    }else if (checkedId == R.id.radioButton0) {
                        selectedUnit = "0";
                        selectedUnitnum = "0";
                    }
                }
            });


            Button addButton = view.findViewById(R.id.save1);
            addButton.setOnClickListener(v -> {
                Log.d(TAG, "Add button clicked");

                if (barcide1.getText().toString().isEmpty()) {
                    barcide1.setError("Barcode is required");
                    Toast.makeText(getContext(), "Barcode is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description1.getText().toString().isEmpty()) {
                    description1.setError("Description is required");
                    Toast.makeText(getContext(), "Description is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pludt.getText().toString().isEmpty()) {
                    pludt.setError("PLU is required");
                    Toast.makeText(getContext(), "PLU is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sellingpriceedt.getText().toString().isEmpty()) {
                    sellingpriceedt.setError("selling price is required");
                    Toast.makeText(getContext(), "selling price is required", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!validateDepartmentSelection()) {
                    return; // Stop if department is invalid
                }

// Usage - call this before processing sub-department
                if (!validateSubDepartmentSelection()) {
                    return; // Stop if sub-department is invalid
                }


                String formattedValue = getFormattedValue(sellingpriceedt);
                String formattedValuecostpercase = getFormattedValue(costpercase1);
                String formattedValuemarkup = getFormattedValue(markupedt);
                String formattedValuemargin = getFormattedValue(margin1);
                String formattedValuecaseprice = getFormattedValue(casepricewdt);


                String inputDate = expiry_dateedt1.getText().toString().trim(); // Get text from EditText
                String formattedDate;

                if (inputDate.isEmpty()) {
                    formattedDate = "0";  // ✅ default if not selected
                } else {
                    formattedDate = convertDateFormat(inputDate); // Convert to YYYYMMDD format
                }




                String unitPerCaseText = unitpercase1.getText().toString();
                if (unitPerCaseText.isEmpty()) {
                    unitPerCaseText = "0";  // Set it to "0" if empty
                }

                String buyprice = (costPrice != null)
                        ? String.format(Locale.getDefault(), "%.2f", costPrice)
                        : "0.00";


                String vatValueTxt = vatValue.replace("%", "").trim();

                String quantity = quantityedt1.getText().toString().trim();
                if (quantity.isEmpty()) {
                    quantity = "0";
                }

                String itemCode = itemcode1.getText().toString().trim();
                if (itemCode.isEmpty()) {
                    itemCode = "0";
                }


                Date currentDate1 = new Date();

                // Format the date as yyyyMMdd
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());


                double calvat = (Double.parseDouble(sellingpriceedt.getText().toString()) * Double.parseDouble(vatValueTxt)) / (100 + Double.parseDouble(vatValueTxt));


                String safeSupplierName = (supplierName != null) ? supplierName : "Default";
                String safeBrandName = (brandName != null) ? brandName : "Default";


                String caseUnitText = (caseunitedt.getText() != null && !caseunitedt.getText().toString().trim().isEmpty())
                        ? caseunitedt.getText().toString().trim()
                        : "0";



                String outerBarcode = outerbarcodeedt.getText().toString().trim();
                if (outerBarcode.isEmpty()) {
                    outerBarcode = "0"; // default value
                }



                // Call AsyncTask to add product with all 47 parameters
                new AddProductTask().execute(
                        //"389",                       // ID
                        pludt.getText().toString(),// PLU
                        barcide1.getText().toString(),// Barcode
                        description1.getText().toString(),// Description
                        String.valueOf(subdepartmentId), // Sub_Department
                        safeSupplierName,// Supplier
                        buyprice,// Buy_Price
                        quantity,// Quantity3
                        String.valueOf(departmentId),// Department
                        formattedValue,                       // SaleWithVAT
                        "0.00",                       // Discount
                        formattedValuecostpercase,// CostPerCase
                        formattedValue,// Price
                        String.valueOf(calvat),// VAT
                        formattedValuemargin,// Margin
                        String.valueOf(selectedage),// Age_Limit
                        "NULL",                       // ProImage
                        "0",                          // PromoID
                        unitPerCaseText,// UnitPerCase
                        "yes",                        // Activated
                        dateFormat1.format(currentDate1),                  // DateAdded
                        vatValue,                         // VatValue
                        "Stock",                      // Class
                        "0",                          // QTYSOLD
                        "0",                          // Capacity
                        "1",                          // done
                        "0.00",                       // Price2
                        "0",                          // SS_QTYS
                        "0.00",// SS_PRICE
                        "1",                          // SS_POINTS
                        "0",                          // SS_PRO_TYPE
                        selectedUnit,                          // Unit_scale
                        itemCode,// Item_code
                        "0",                          // ITEM_TYPE
                        "0",                          // FOOD_TYPE
                        "0",                          // MENU_TYPE
                        "0",                          // SUB_MENU_TYPE
                        "0",                          // MENU_TYPE_NO
                        "0",                          // SUB_PRODUCT_NO
                        safeBrandName,     // Brand
                        formattedDate,                          // Expiry_Date
                        "0",                          // Profit_Inc_VAT
                        "0",                          // Profit_Ex_VAT
                        "0",                          // Cost_Inc_VAT
                        formattedValuemarkup,         // Markup
                        selectedUnitnum,                          // Num
                        "0",                          // Cost_Inc_VAT_1unit
                        outerBarcode,//OuterBarcode
                        "0",//StartDate
                        "0",//EndDate
                        "0",//DD_Price
                        "0",//ManageStock
                        "0",//Weight
                        "0",//CurrentStock
                        additionalbarcode.getText().toString(),//AdditionalBarcode1
                        "0",//AdditionalBarcode2
                        "0.00",
                        "0.00",
                        formattedValuecaseprice,
                        caseUnitText

                );


            });

            Button addButton2 = view.findViewById(R.id.save);
            addButton2.setOnClickListener(v -> {
                Log.d(TAG, "Add button clicked");

                if (itemcode1.getText().toString().isEmpty()) {
                    itemcode1.setError("Item code is required");
                    return;
                }
                if (barcide1.getText().toString().isEmpty()) {
                    barcide1.setError("Barcode is required");
                    return;
                }
                if (description1.getText().toString().isEmpty()) {
                    description1.setError("Description is required");
                    return;
                }
                if (costpercase1.getText().toString().isEmpty()) {
                    costpercase1.setError("Cost per case is required");
                    return;
                }
                if (margin1.getText().toString().isEmpty()) {
                    margin1.setError("Margin is required");
                    return;
                }
                if (unitpercase1.getText().toString().isEmpty()) {
                    unitpercase1.setError("Units per case is required");
                    return;
                }
                if (pludt.getText().toString().isEmpty()) {
                    pludt.setError("PLU is required");
                    return;
                }


                // Prepare supplier and brand names with default values if null
                String safeSupplierName = (supplierName != null) ? supplierName : "Default";
                String safeBrandName = (brandName != null) ? brandName : "Default";

                // Call AsyncTask to add product with all 47 parameters
                new AddProductTask().execute(
                        //"389",                       // ID
                        pludt.getText().toString(),// PLU
                        barcide1.getText().toString(),// Barcode
                        description1.getText().toString(),// Description
                        String.valueOf(subdepartmentId), // Sub_Department
                        safeSupplierName,// Supplier
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
                        "Stock",                      // Class
                        "0",                          // QTYSOLD
                        capacity.getText().toString(),                          // Capacity
                        "1",                          // done
                        "0.00",                       // Price2
                        "0",                          // SS_QTYS
                        sellingpriceedt.getText().toString(),// SS_PRICE
                        String.valueOf(enableDisableValue),  // SS_POINTS
                        "0",                          // SS_PRO_TYPE
                        selectedUnit,                          // Unit_scale
                        itemcode1.getText().toString(),// Item_code
                        "0",                          // ITEM_TYPE
                        "0",                          // FOOD_TYPE
                        "0",                          // MENU_TYPE
                        "0",                          // SUB_MENU_TYPE
                        "0",                          // MENU_TYPE_NO
                        "0",                          // SUB_PRODUCT_NO
                        safeBrandName,     // Brand
                        "0",                          // Expiry_Date
                        "0",                          // Profit_Inc_VAT
                        "0",                          // Profit_Ex_VAT
                        "0",                          // Cost_Inc_VAT
                        "0",                          // Markup
                        selectedUnitnum,                          // Num
                        "0",                          // Cost_Inc_VAT_1unit
                        startdate.getText().toString(),//StartDate
                        outerbarcodeedt.getText().toString(),//OuterBarcode
                        enddatedt.getText().toString(),//EndDate
                        String.valueOf(enableDisableValueddprice),//DD_Price
                        String.valueOf(enableDisableValuemangestock),//ManageStock
                        weightedt.getText().toString(),//Weight
                        currentstock.getText().toString(),//CurrentStock
                        additionalbarcode.getText().toString(),//AdditionalBarcode1
                        "0",//AdditionalBarcode2
                        minstaockedt.getText().toString(),
                        reorderlevel.getText().toString(),
                        casepricewdt.getText().toString(),
                        caseunitedt.getText().toString()
                );

            });

            Button clear1;
            clear1 = view.findViewById(R.id.clearbtn);
            clear1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

            unitpercase1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    calculateCostPrice(); // Call calculation method
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
                    calculateCostPrice(); // Call calculation method
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


// Add this TextWatcher to markupedt in your onCreateView
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
                    // Start printing
                    PrintManager printManager = (PrintManager) getActivity().getSystemService(PRINT_SERVICE);
                    printManager.print("ShopBill", new MyPrintDocumentAdapterAll(getContext(), barcide1.getText().toString(), "2", "2", description1.getText().toString()), null);
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

            Button addtobarcode = view.findViewById(R.id.addtobarcode);
            addtobarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String barcode = barcide1.getText().toString();
                    String plu = pludt.getText().toString();
                    String detail = description1.getText().toString();
                    String shop = "";
                    String costPriceText = costpriceedt.getText().toString();
                    double price;

                    if (costPriceText.isEmpty()) {
                        price = 0.0;  // Default to 0 if the EditText is empty
                    } else {
                        price = Double.parseDouble(costPriceText);  // Parse the value if not empty
                    }

                    Date date = new Date(); // Use the current date or provide a specific date
                    int capacity = 0;
                    int qty = 0;  // Default value

// Check if the EditText is not empty, then parse the value
                    if (!quantityedt1.getText().toString().isEmpty()) {
                        qty = Integer.parseInt(quantityedt1.getText().toString());
                    }


                    new InsertBarcodeTask(barcode, plu, detail, shop, price, date, capacity, qty).execute();


                }
            });

            // Initialize views
            layoutToConvert = view.findViewById(R.id.layoutToConvertlable);
            layoutToConvert2 = view.findViewById(R.id.layoutToConvertlable2);
            layoutToConvert3 = view.findViewById(R.id.layoutToConvertlable3); // New third layout

            printerbtn = view.findViewById(R.id.print);
            choselayout = view.findViewById(R.id.radioGroup);

// Set the first radio button as the default selected option
            choselayout.check(R.id.radioLayout1); // Default to the first layout
            selectedLayout = layoutToConvert; // Set the default selected layout

// Set radio group listener to store the selected layout
            choselayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radioLayout1) {
                        selectedLayout = layoutToConvert; // Store layout 1
                    } else if (checkedId == R.id.radioLayout2) {
                        selectedLayout = layoutToConvert2; // Store layout 2
                    } else if (checkedId == R.id.radioLayout3) { // Handling new third radio button
                        selectedLayout = layoutToConvert3; // Store layout 3
                    }
                }
            });

// Set button click listener
            printerbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedLayout == null) {
                        // Show a message to the user to select a layout first
                        Toast.makeText(getContext(), "Please select a layout first", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Generate barcodes
                    Bitmap qrCodeBitmap = null;
                    Bitmap barcodeBitmap = null;

                    try {
                        qrCodeBitmap = generateBarcode(barcodeValue, 400, 400, BarcodeFormat.QR_CODE);
                        barcodeBitmap = generateBarcodelinear(barcodeValue, 400, 100);
                    } catch (Exception e) {
                        Log.e("BarcodeGeneration", "Error generating barcode", e);
                        return;
                    }

                    if (qrCodeBitmap != null && barcodeBitmap != null) {
                        // Update text and image views within the selected layout
                        ((TextView) selectedLayout.findViewById(R.id.bill_title)).setText(description1.getText().toString());
                        ((TextView) selectedLayout.findViewById(R.id.textView10)).setText("£0");
                        ((TextView) selectedLayout.findViewById(R.id.priceedt)).setText("Was £" + sellingpriceedt.getText().toString());
                        ((TextView) selectedLayout.findViewById(R.id.barcode)).setText(barcodeValue);

                        ImageView qrCodeImageView = selectedLayout.findViewById(R.id.barcode_image5);
                        qrCodeImageView.setImageBitmap(barcodeBitmap);

                        ImageView qrCodeImageView2 = selectedLayout.findViewById(R.id.barcode_image2);
                        qrCodeImageView2.setImageBitmap(qrCodeBitmap);

                        // Convert the selected layout to an image
                        convertLayoutToImage(selectedLayout);
                    } else {
                        Log.e("BarcodeGeneration", "QR Code or barcode bitmap generation failed.");
                    }
                }
            });

            helper = new UIHelper(getActivity());


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


        } catch (RuntimeException e) {
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }


        return view;
    }

    private void convertLayoutToImage(View layout) {
        // Measure and layout the view
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        // Create a bitmap with the same size as the layout
        Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);  // Draw the layout onto the canvas

        // Trim extra transparent space
        Bitmap trimmedBitmap = trimBitmap(bitmap);

        // Convert Bitmap to ByteArray
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        trimmedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        sendimage(byteArray);
    }


    private void sendimage(byte[] byteArray) {
        // First check if we have a bitmap from arguments
        if (getArguments() != null && getArguments().getByteArray("image") != null) {
            byteArray = getArguments().getByteArray("image");
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        // If we still don't have a bitmap, create one from the layout
        if (bitmap == null) {
            // Convert the selected layout to bitmap
            selectedLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            selectedLayout.layout(0, 0, selectedLayout.getMeasuredWidth(), selectedLayout.getMeasuredHeight());

            bitmap = Bitmap.createBitmap(selectedLayout.getMeasuredWidth(),
                    selectedLayout.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            selectedLayout.draw(canvas);
        }

        if (bitmap != null) {
            printPhotoFromExternal(bitmap);
        } else {
            Toast.makeText(getContext(), "Failed to create image for printing", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to trim transparent edges
    private Bitmap trimBitmap(Bitmap bitmap) {

        int imgHeight = bitmap.getHeight();
        int imgWidth = bitmap.getWidth();

        int minX = imgWidth, minY = imgHeight;
        int maxX = -1, maxY = -1;

        int[] pixels = new int[imgWidth * imgHeight];
        bitmap.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int pixel = pixels[y * imgWidth + x];
                if ((pixel >> 24) != 0x00) { // Check for non-transparent pixels
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) return bitmap; // No non-transparent pixels found

        return Bitmap.createBitmap(bitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    // AsyncTask to add product in the background
    private class AddProductTask extends AsyncTask<String, Void, String> {
        @SuppressLint("WrongThread")
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
            String casePrice = params[57];         // CasePrice
            String caseUnit = params[58];          // CaseUnit

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
                            "AdditionalBarcode1,AdditionalBarcode2,MinStock,ReorderLevel,casePrice,caseUnit,DiscountStatus,SecondarySupplierID) \n" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                            "      , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                            "        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                            "        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                            "        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                            "?,?,?,?,?,?,?,?,?,?,?)\n";

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
                    statement.setString(58, casePrice);
                    statement.setString(59, caseUnit);
                    statement.setString(60, "0"); // DiscountStatus (default to "0")
                    statement.setString(61, "0"); // SecondarySupplierID (default to "0")

                    // Execute the query
                    int rowsInserted = statement.executeUpdate();
                    statement.close();
                    connection.close();

                    new InsertDataTask().execute();

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
            // Check if the result contains the specific SQL error message
            if (result.contains("Violation of UNIQUE KEY constraint")) {
                // Show toast with "Barcode already exists"
                Toast.makeText(requireActivity(), "Barcode already exists", Toast.LENGTH_LONG).show();

            } else {
                // Show the result message as it is
                Toast.makeText(requireActivity(), result, Toast.LENGTH_LONG).show();
            }

            Log.e(TAG, "SQL Exception3: " + result);
        }

    }


    private boolean isCalculating = false;
    private boolean isUpdatingMarkup = false;

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


    private String getCurrentDate() {
        // Create a Calendar instance to get the current date
        Calendar calendar = Calendar.getInstance();

        // Define the date format as dd-MM-yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format the current date
        return dateFormat.format(calendar.getTime());
    }


    private class InsertDataTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "InsertDataTask";

        @Override
        protected Boolean doInBackground(Void... voids) {
            String url = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + ";databaseName=" + databaseName;
            Log.d(TAG, "Database URL: " + url);

            // Updated insert query to include Low_stock
            String insertQuery = "INSERT INTO [dbo].[tbl_SoldItems] " +
                    "([ID], [PLU], [Barcode], [Quantity], [Description], [CaseUnit], [Saleprocess_qtys], [Low_stock]) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                Log.d(TAG, "Attempting to connect to the database...");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Log.d(TAG, "Connection successful.");

                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                // Set the values for each parameter
                preparedStatement.setInt(1, Integer.parseInt(pludt.getText().toString())); // ID
                preparedStatement.setString(2, pludt.getText().toString()); // PLU
                preparedStatement.setString(3, barcide1.getText().toString()); // Barcode

                int quantity = quantityedt1.getText().toString().isEmpty()
                        ? 0
                        : Integer.parseInt(quantityedt1.getText().toString());
                preparedStatement.setInt(4, quantity); // Quantity

                preparedStatement.setString(5, description1.getText().toString()); // Description

                // Handle CaseUnit
                String caseUnitText = caseunitedt.getText().toString();
                int caseUnit = caseUnitText.isEmpty() ? 0 : Integer.parseInt(caseUnitText);
                preparedStatement.setInt(6, caseUnit); // CaseUnit

                preparedStatement.setInt(7, quantity); // Saleprocess_qtys

                // Handle Low_stock (default to 0 or get from UI if available)
                preparedStatement.setInt(8, 0); // Low_stock

                Log.d(TAG, "Executing insert query: " + insertQuery);
                int result = preparedStatement.executeUpdate();
                Log.d(TAG, "Insert result: " + result);

                preparedStatement.close();
                connection.close();
                Log.d(TAG, "Database connection closed.");

                return result > 0;

            } catch (Exception e) {
                Log.e(TAG, "Error during database operation", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Log.d(TAG, "Data inserted successfully.");

                String barcode1 = barcide1.getText().toString();
                String plu1 = pludt.getText().toString();
                String detail1 = description1.getText().toString();
                String shop1 = "";
                String costPriceText1 = costpriceedt.getText().toString();
                double price1 = costPriceText1.isEmpty() ? 0.0 : Double.parseDouble(costPriceText1);

                Date date1 = new Date();
                int capacity1 = 0;
                int qty1 = quantityedt1.getText().toString().isEmpty()
                        ? 0
                        : Integer.parseInt(quantityedt1.getText().toString());

                new InsertBarcodeTask(barcode1, plu1, detail1, shop1, price1, date1, capacity1, qty1).execute();


                if ("fromProductmagementfullFragment".equals(fromProductmagementfullFragment)) {
                    BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromPriceSubFragment".equals(fromPriceSubFragment)) {
                    PriceCheckFragment priceSubFragment = new PriceCheckFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromStockSubFragment".equals(fromStockSubFragment)) {
                    StockTakesFragment priceSubFragment = new StockTakesFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromStockSubFragment2".equals(fromStockSubFragment2)) {
                    StockadjustmentsearchFragment priceSubFragment = new StockadjustmentsearchFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromOrderCategoryFragment".equals(fromOrderCategoryFragment)) {
                    BarCodeScanOrderCreateSerachFragment priceSubFragment = new BarCodeScanOrderCreateSerachFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromDelivermanagmentsearchFragment".equals(fromDelivermanagmentsearchFragment)) {
                    DeliveryManagmentFragment priceSubFragment = new DeliveryManagmentFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromBarCodeScanFragmentNewPromotionsearch".equals(fromBarCodeScanFragmentNewPromotionsearch)) {
                    BarCodeScanFragmentNewPromotionsearch priceSubFragment = new BarCodeScanFragmentNewPromotionsearch();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromPromoItemsFragment".equals(fromPromoItemsFragment)) {
                    PromoItemsFragment priceSubFragment = new PromoItemsFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromActiveCountProductFragment".equals(fromActiveCountProductFragment)) {
                    ActiveCountProductFragment priceSubFragment = new ActiveCountProductFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromLablePrintFragment".equals(fromLablePrintFragment)) {
                    BarCodeScanFragmentNewSearchLablePrint priceSubFragment = new BarCodeScanFragmentNewSearchLablePrint();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromBarCodeScanFragmentNewSearchLablePrintQuick".equals(fromBarCodeScanFragmentNewSearchLablePrintQuick)) {
                    BarCodeScanFragmentNewSearchLablePrintQuick priceSubFragment = new BarCodeScanFragmentNewSearchLablePrintQuick();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if ("fromBarCodeScanFragmentNewPromotionsearchedit".equals(fromBarCodeScanFragmentNewPromotionsearchedit)) {
                    PromoItemEditFragment priceSubFragment = new PromoItemEditFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }


                // ... other else-if conditions
            } else {
                Log.d(TAG, "Failed to insert data.");
                Toast.makeText(getContext(), "Failed to insert data.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getFormattedValue(EditText editText) {
        String input = editText.getText().toString().trim();

        // Remove all commas (assuming they are thousands separators)
        input = input.replace(",", "");

        // Ensure only one decimal point exists
        int firstDotIndex = input.indexOf(".");
        if (firstDotIndex != -1) {
            input = input.substring(0, firstDotIndex + 1) +
                    input.substring(firstDotIndex + 1).replace(".", "");
        }

        // Convert to double safely
        double value = input.isEmpty() ? 0 : Double.parseDouble(input);

        // Format with two decimal places
        return String.format(Locale.US, "%.2f", value);
    }

    private class InsertBarcodeTask extends AsyncTask<Void, Void, Boolean> {
        private String barcode;
        private String plu;
        private String detail;
        private String shop;
        private double price;
        private Date date;
        private int capacity;
        private int qty;
        private boolean barcodeExists = false; // To track if barcode exists

        // Constructor to initialize values
        public InsertBarcodeTask(String barcode, String plu, String detail, String shop, double price, Date date, int capacity, int qty) {
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

            try {
                // Create connection string
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                // Check if barcode already exists
                String checkQuery = "SELECT COUNT(*) FROM [STAR_RETAIL].[dbo].[tblBarcode] WHERE Barcode = ?";
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, barcode);
                java.sql.ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    barcodeExists = true; // Barcode already exists
                    return false;
                }

                // Check if price is empty or zero, and set it to 0.00 if true

                // Format price with £ symbol
                String formattedPrice = "£" + sellingpriceedt.getText().toString();

                // Format the date to "dd/MM/yyyy"
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(date);

                // If barcode does not exist, insert it into the table
                String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[tblBarcode] ([PLU], [Barcode], [Detail], [Shop], [Price], [dtDate], [Capacity], [Qty]) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, plu); // PLU
                insertStatement.setString(2, barcode); // Barcode
                insertStatement.setString(3, detail); // Detail
                insertStatement.setString(4, shop); // Shop
                insertStatement.setString(5, formattedPrice); // Price with £ symbol
                insertStatement.setString(6, formattedDate); // Formatted Date in "dd/MM/yyyy"
                insertStatement.setInt(7, capacity); // Capacity
                insertStatement.setInt(8, qty); // Qty

                // Execute the insert statement
                insertStatement.executeUpdate();
                Log.d(TAG, "Barcode inserted successfully");


                return true;

            } catch (SQLException e) {
                Log.e(TAG, "Database connection error: " + e.getMessage());
            } finally {
                // Close resources
                try {
                    if (checkStatement != null) {
                        checkStatement.close();
                    }
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Error closing resources: " + e.getMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (barcodeExists) {
                Toast.makeText(getContext(), "Barcode already exists", Toast.LENGTH_SHORT).show();
            } else if (isSuccess) {
                // Toast.makeText(getContext(), "Barcode inserted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to insert barcode", Toast.LENGTH_SHORT).show();
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

    // Generate barcode or QR code
    private Bitmap generateBarcode(String data, int width, int height, BarcodeFormat format) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();

        // Set encoding hints for the barcode
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);  // Optional: Set margin for barcode

        // Generate the barcode or QR code matrix
        BitMatrix bitMatrix = writer.encode(data, format, width, height, hints);

        // Convert BitMatrix to Bitmap
        int bitmapWidth = bitMatrix.getWidth();
        int bitmapHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Set the bitmap to be transparent (0x00000000 represents fully transparent)
        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT); // Make the white parts transparent
            }
        }

        return bitmap;
    }

    private Bitmap generateBarcodelinear(String data, int width, int height) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height, hints);

        int bitmapWidth = bitMatrix.getWidth();
        int bitmapHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        return bitmap;
    }

    private void calculatecurrentstock() {
        String unitPerCaseTxt = unitpercase1.getText().toString().trim();
        String costPerCaseTxt = caseunitedt.getText().toString().trim().replaceAll(",", ""); // Remove commas

        // Check if inputs are not empty
        if (unitPerCaseTxt.isEmpty() || costPerCaseTxt.isEmpty()) {
            quantityedt1.setText(""); // Clear the result if any field is empty
            return; // Exit the method
        }

        try {
            // Parse the input strings to Double
            int unitPerCase = Integer.parseInt(unitPerCaseTxt);
            int costPerCase = Integer.parseInt(costPerCaseTxt);


            // Check for division by zero
            if (unitPerCase == 0) {
                quantityedt1.setText("0"); // Set result to 0 if division by zero
                return; // Exit the method
            }

            // Calculate cost price
            currentstockvalue = (unitPerCase * costPerCase);

            // Set the result to costpriceedt
            quantityedt1.setText(String.valueOf(currentstockvalue));
        } catch (NumberFormatException e) {
            quantityedt1.setText(""); // Clear the result if input is invalid
        }
    }

    private void printPhotoFromExternal(final Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            requireActivity().runOnUiThread(() -> {
                helper.dismissLoadingDialog();
                helper.showErrorDialogOnGuiThread2("No valid image to print", () -> {
                    // Restart the fragment when dialog is dismissed
                    restartFragment();
                });
            });
            return;
        }

        helper.showLoadingDialog("Sending image to printer");
        new Thread(() -> {
            try {
                getAndSaveSettings();
                Looper.prepare();
                com.zebra.sdk.comm.Connection connection = getZebraPrinterConn();
                connection.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                // Rotate bitmap if necessary
                Bitmap rotatedBitmap = rotateBitmap(bitmap, 0);
                int width = rotatedBitmap.getWidth();
                int height = rotatedBitmap.getHeight();

                // Print directly (remove checkbox logic)
                printer.printImage(new ZebraImageAndroid(rotatedBitmap), 0, 0, width, height, false);

                connection.close();
            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
                requireActivity().runOnUiThread(() ->
                        helper.showErrorDialogOnGuiThread2(e.getMessage(), () -> {
                            // Restart the fragment when dialog is dismissed
                            restartFragment();
                        }));
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                requireActivity().runOnUiThread(() -> helper.dismissLoadingDialog());
                Looper.myLooper().quit();
            }
        }).start();
    }

    // Method to restart the current fragment
    private void restartFragment() {

        ProductmagementfullFragment productManagementFragment = new ProductmagementfullFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private com.zebra.sdk.comm.Connection getZebraPrinterConn() {
        // Use the default BLE address
        return new BluetoothLeConnection(DEFAULT_BLE_ADDRESS, requireContext());
    }

    private void getAndSaveSettings() {
        // Save the default BLE address to SharedPreferences
        SettingsHelper.saveBluetoothAddress(requireContext(), DEFAULT_BLE_ADDRESS);
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


        Toast.makeText(getContext(), "Please select a valid department from the list",
                Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getContext(), "Please select a valid sub-department from the list",
                Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getContext(), "Please select a valid supplier from the list",
                Toast.LENGTH_SHORT).show();
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

        Toast.makeText(getContext(), "Please select a valid brand from the list",
                Toast.LENGTH_SHORT).show();
        brandSpinner.requestFocus();
        brandSpinner.showDropDown();
        return false;
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


//    private decimal CalculateCostPrice(decimal price, decimal units, decimal vatRate)
//    {
//        decimal unitPrice = price / units;
//        return unitPrice * (1 + vatRate / 100);
//    }
//    private decimal CalculateMargin(decimal sellingPrice, decimal costPrice)
//    {
//        return (sellingPrice - costPrice) * 100 / sellingPrice;
//    }
//    private decimal CalculateMarkup(decimal sellingPrice, decimal costPrice)
//    {
//        return (sellingPrice - costPrice) * 100 / costPrice;
//    }


}


