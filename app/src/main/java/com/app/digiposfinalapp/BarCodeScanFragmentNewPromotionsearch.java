package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.CustomDialogpromotionsave;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BarCodeScanFragmentNewPromotionsearch extends Fragment {

    private String barcodeValue;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private boolean isBarcodeDetected = false;
    private BroadcastReceiver scanReceiver;

    private String description, barcode, itemcode, Price, costprice, PromoID, plu;
    EditText searchbarcode;

    private RecyclerView recyclerView;
    private NewPromotionAdapter adapter;
    private List<NewPromotion> promotionList = new ArrayList<>();

    Spinner promotypeSpinner, promodesSpinner;
    String selectedpromotype, selectedppromodes;

    EditText priceedt1, promotionnameedt;
    private EditText buyEditText, getedt;
    TextView tvpd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bar_code_scan_new_promotionsearch, container, false);


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

        promodesSpinner = view.findViewById(R.id.promodesSpinner); // Updated ID

        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password


        ImageView camera = view.findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentnewpromotion productManagementFragment = new BarCodeScanFragmentnewpromotion();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubnewpromotionFragment productManagementFragment = new SubnewpromotionFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        searchbarcode = view.findViewById(R.id.barcodeedt);
        searchbarcode.setText(barcode);


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
                barcodeValue = searchbarcode.getText().toString();

                // First check if already in current list
                if (isBarcodeAlreadyExists(barcodeValue)) {
                    Toast.makeText(getContext(), "This product is already added", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Then check if exists in database promotions
                new CheckSingleItemTask(barcodeValue).execute();

                v.clearFocus();
                return true;
            }
            return false;
        });


        // In onCreateView, replace your current RecyclerView setup with:
        recyclerView = view.findViewById(R.id.recyclerViewPromotions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

// Initialize the list and adapter together
        promotionList = new ArrayList<>();
        adapter = new NewPromotionAdapter(promotionList, getContext());
        recyclerView.setAdapter(adapter);

// Then load data
        loadPromotionsFromSharedPreferences();


        promotypeSpinner = view.findViewById(R.id.promotype_spinner); // Updated ID
        // Define an array of integer values

        String[] spinnerItems = {"FIXED DISCOUNT", "DISCOUNT" /*,"FREE" ,"DAILY PROMO" ,"SIGNUP PROMO" ,"LADDAR PROMO","MEAL DEAL"*/};
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, spinnerItems);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        promotypeSpinner.setAdapter(adapter);
        // Set a listener for the spinner
        promotypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedpromotype = spinnerItems[position];
                //  Toast.makeText(getContext(), "Selected Value: " + selectedage, Toast.LENGTH_SHORT).show();
                calculateAndDisplayResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle the case when nothing is selected
            }
        });

        priceedt1 = view.findViewById(R.id.salepriceedt);

        priceedt1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a custom pattern
                decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setGroupingUsed(true); // Enable grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    priceedt1.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        priceedt1.setText(currentText);
                        priceedt1.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        priceedt1.setText("");
                    }

                    priceedt1.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        getedt = view.findViewById(R.id.getedt);
        buyEditText = view.findViewById(R.id.buyEditText);
        buyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayResult();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        priceedt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayResult();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        promotionnameedt = view.findViewById(R.id.promotionnameedt);
        Button btnsave1 = view.findViewById(R.id.btnsave);
        btnsave1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promotionList == null || promotionList.isEmpty()) {
                    Toast.makeText(getContext(), "Please add at least one product to create a promotion", Toast.LENGTH_SHORT).show();
                    return;
                }


                if ("DISCOUNT".equals(selectedpromotype)) {
                    if (priceedt1.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Discount percentage is required", Toast.LENGTH_SHORT).show();
                    } else {
                        showLogoutDialog();
                    }


                    if (promotionnameedt.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Promotion Name is required", Toast.LENGTH_SHORT).show();
                    } else {
                        showLogoutDialog();
                    }


                } else if ("FIXED DISCOUNT".equals(selectedpromotype)) {
                    if (buyEditText.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Buy Quantity is required", Toast.LENGTH_SHORT).show();
                    } else if (priceedt1.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Discount percentage is required", Toast.LENGTH_SHORT).show();
                    } else {
                        showLogoutDialog();
                    }

                }

            }
        });

        tvpd = view.findViewById(R.id.tvpd);


        promotypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedpromotype = spinnerItems[position];

                // Disable buyEditText and set default value for DISCOUNT
                if ("DISCOUNT".equals(selectedpromotype)) {
                    buyEditText.setEnabled(false);
                    getedt.setEnabled(false);
                    buyEditText.setBackgroundResource(R.drawable.disableedt);
                    getedt.setBackgroundResource(R.drawable.disableedt);
                    tvpd.setText("DISCOUNT");

                } else if ("FIXED DISCOUNT".equals(selectedpromotype)) {
                    getedt.setEnabled(false);
                    buyEditText.setEnabled(true);
                    buyEditText.setBackgroundResource(R.drawable.blackline);
                    getedt.setBackgroundResource(R.drawable.disableedt);
                    tvpd.setText("PRICE");
                }

                calculateAndDisplayResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        return view;

    }


    private class CheckSingleItemTask extends AsyncTask<Void, Void, Boolean> {
        private String barcodeToCheck;

        public CheckSingleItemTask(String barcode) {
            this.barcodeToCheck = barcode;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 +
                        "/" + databaseName1 + ";user=" + dbUsername1 + ";password=" + dbPassword1;
                connection = DriverManager.getConnection(connectionString);

                String query = "SELECT TOP 1 Barcode FROM [STAR_RETAIL].[dbo].[Promo_Items] WHERE Barcode = ?";
                pstmt = connection.prepareStatement(query);
                pstmt.setString(1, barcodeToCheck);

                rs = pstmt.executeQuery();
                return rs.next(); // Returns true if barcode exists in promotions

            } catch (Exception e) {
                Log.e("CheckSingleItem", "Error checking item: " + e.getMessage());
                return false;
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (pstmt != null) pstmt.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    Log.e("CheckSingleItem", "Error closing resources: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean existsInDatabase) {
            if (existsInDatabase) {
                // Show warning about existing promotion
                new AlertDialog.Builder(getContext())
                        .setTitle("Item Already in Promotion")
                        .setMessage("This item is already part of another promotion." + "\n" + barcodeValue)
                        .setNegativeButton("Ok", null)
                        .show();
            } else {
                // No existing promotion - safe to add
                new DatabaseTask().execute();
            }
        }
    }

    private void calculateAndDisplayResult() {
        // Check if views are initialized
        if (priceedt1 == null || !isAdded()) {
            return;
        }

        try {
            // Get total price
            double totalPrice = 0.0;
            for (NewPromotion promotion : promotionList) {
                totalPrice += Double.parseDouble(promotion.getPrice());
            }

            String salePriceText = priceedt1.getText().toString();

            if (!salePriceText.isEmpty()) {
                if ("DISCOUNT".equals(selectedpromotype)) {
                    // DISCOUNT case - only need percentage value
                    String wholeNumber = String.valueOf((int) Double.parseDouble(salePriceText));
                    String[] spinnerItems = new String[]{
                            "DISCOUNT " + wholeNumber + "%"
                    };

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, spinnerItems);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    promodesSpinner.setAdapter(adapter);

                } else {
                    // Other promotion types (FIXED DISCOUNT, etc.)
                    String buyText = buyEditText.getText().toString();
                    if (!buyText.isEmpty()) {
                        int buyValue = Integer.parseInt(buyText);
                        int totalItems = promotionList.size();
                        double salePrice = Double.parseDouble(salePriceText);

                        if (totalItems > 0) {
                            double result = ((totalPrice * buyValue) / totalItems) - salePrice;
                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            String formattedResult = df.format(result);

                            String[] spinnerItems;
                            if ("FIXED DISCOUNT".equals(selectedpromotype)) {
                                spinnerItems = new String[]{
                                        "BUY " + buyText + " SAVE £ " + formattedResult,
                                        "SAVE £ " + formattedResult,
                                        "ANY " + buyText + " FOR £ " + salePriceText,
                                        buyText + " FOR £ " + salePriceText,
                                        "ONLY £ " + salePriceText
                                };
                            } else {
                                spinnerItems = new String[]{};
                            }

                            if (spinnerItems.length > 0) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                        android.R.layout.simple_spinner_item, spinnerItems);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                promodesSpinner.setAdapter(adapter);
                            }
                        }
                    }
                }


                // Set spinner selection listener
                promodesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedppromodes = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Optional: Handle the case when nothing is selected
                    }
                });
            }
        } catch (NumberFormatException e) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scanReceiver != null) {
            requireContext().unregisterReceiver(scanReceiver); // Unregister receiver in Fragment
        }
    }


    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, subDepartment, supplier, buyPrice, quantity, department, id, Expiry_date,
                saleWithVAT, Markup, discount, costPerCase, price, vat, margin, ageLimit, Brand, UnitPerCase, currentstock, minStock, reorderleve,
                CostPerCase, sellingprice, Margin, outerbarcode, addbarcode, startDate, enddate, dd_price, ddpoints, manageStock, weight, capatitys, CasePrice,
                CaseUnit, VatValue, promoID;

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
                        itemcode = resultSet.getString("Item_code");
                        Brand = resultSet.getString("Brand");
                        UnitPerCase = resultSet.getString("UnitPerCase");
                        CostPerCase = resultSet.getString("CostPerCase");
                        Price = resultSet.getString("Price");
                        Markup = resultSet.getString("Markup");
                        sellingprice = resultSet.getString("SS_PRICE");
                        Margin = resultSet.getString("Margin");
                        outerbarcode = resultSet.getString("OuterBarcode");
                        costprice = resultSet.getString("Price");
                        addbarcode = resultSet.getString("AdditionalBarcode1");
                        startDate = resultSet.getString("StartDate");
                        enddate = resultSet.getString("EndDate");
                        dd_price = resultSet.getString("DD_Price");
                        ddpoints = resultSet.getString("SS_POINTS");
                        manageStock = resultSet.getString("ManageStock");
                        weight = resultSet.getString("Weight");
                        capatitys = resultSet.getString("Capacity");
                        currentstock = resultSet.getString("CurrentStock");
                        minStock = resultSet.getString("MinStock");
                        reorderleve = resultSet.getString("ReorderLevel");
                        Expiry_date = resultSet.getString("Expiry_date");
                        CasePrice = resultSet.getString("CasePrice");
                        CaseUnit = resultSet.getString("CaseUnit");
                        VatValue = resultSet.getString("VatValue");
                        promoID = resultSet.getString("PromoID");

                        NewPromotion promotion = new NewPromotion(description, barcode, itemcode, Price, costprice, promoID, plu);
                        promotionList.add(promotion);
                        Log.d("RecyclerViewDebug", "Adding promotion: " + promotion.getDescription());
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
            requireActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                savePromotionsToSharedPreferences();
                Log.d("DatabaseTask", "Data updated. Notifying adapter.");
            });

            if (plu != null) {
                requireActivity().runOnUiThread(() -> {
                    if (isBarcodeAlreadyExists(barcode)) {
                        Toast.makeText(getContext(), "This product is already added", Toast.LENGTH_SHORT).show();
                    }
                });
                isBarcodeDetected = false;
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
                bundle.putString("fromBarCodeScanFragmentNewPromotionsearch", "fromBarCodeScanFragmentNewPromotionsearch");

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
            itemcode = getArguments().getString("Price");
            Price = getArguments().getString("Price");
            costprice = getArguments().getString("costprice");
            PromoID = getArguments().getString("PromoID");
            plu = getArguments().getString("plu");


            if (!isBarcodeAlreadyExists(barcode)) {
                NewPromotion promotion = new NewPromotion(description, barcode, itemcode, Price, costprice, PromoID, plu);
                promotionList.add(promotion);
            } else {
                Toast.makeText(getContext(), "This product is already added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePromotionsToSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PromotionPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert list to JSON
        Gson gson = new Gson();
        String json = gson.toJson(promotionList);
        editor.putString("promotion_list", json);
        editor.apply();
        updateTotalPrice();
    }

    private void loadPromotionsFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PromotionPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("promotion_list", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NewPromotion>>() {
            }.getType();

            // Clear existing data first
            promotionList.clear();

            // Add all new items
            List<NewPromotion> loadedList = gson.fromJson(json, type);
            promotionList.addAll(loadedList);

            // Update adapter
            adapter.notifyDataSetChanged();

            updateTotalPrice();

            Log.d("RecyclerViewFix", "Data loaded. Item count: " + promotionList.size());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPromotionsFromSharedPreferences();
    }

    private boolean isBarcodeAlreadyExists(String barcodeToCheck) {
        for (NewPromotion promotion : promotionList) {
            if (promotion.getBarcode().equals(barcodeToCheck)) {
                return true;
            }
        }
        return false;
    }

    private void updateTotalPrice() {
        calculateAndDisplayResult();

    }

    public void showLogoutDialog() {
        CustomDialogpromotionsave cdd = new CustomDialogpromotionsave(getActivity(), selectedppromodes, buyEditText.getText().toString(),
                selectedpromotype, priceedt1.getText().toString(), promotionList.size(), promotionList, adapter, promotionnameedt.getText().toString());
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();
    }

    private class CheckExistingItemsTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> existingBarcodes = new ArrayList<>();
            Connection connection = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 +
                        "/" + databaseName1 + ";user=" + dbUsername1 + ";password=" + dbPassword1;
                connection = DriverManager.getConnection(connectionString);

                // Build query with all barcodes
                StringBuilder query = new StringBuilder(
                        "SELECT DISTINCT Barcode FROM [STAR_RETAIL].[dbo].[Promo_Items] WHERE Barcode IN (");

                // Add placeholders for each barcode
                for (int i = 0; i < promotionList.size(); i++) {
                    query.append("?,");
                }
                query.deleteCharAt(query.length() - 1); // Remove last comma
                query.append(")");

                pstmt = connection.prepareStatement(query.toString());

                // Set parameters
                for (int i = 0; i < promotionList.size(); i++) {
                    pstmt.setString(i + 1, promotionList.get(i).getBarcode());
                }

                rs = pstmt.executeQuery();
                while (rs.next()) {
                    existingBarcodes.add(rs.getString("Barcode"));
                }

            } catch (Exception e) {
                Log.e("CheckExisting", "Error checking existing items: " + e.getMessage());
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (pstmt != null) pstmt.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    Log.e("CheckExisting", "Error closing resources: " + e.getMessage());
                }
            }
            return existingBarcodes;
        }

        @Override
        protected void onPostExecute(List<String> existingBarcodes) {
            if (existingBarcodes.isEmpty()) {
                // No duplicates found - proceed with save
                new DatabaseTask().execute();
            } else {
                // Show warning about duplicates
                showDuplicateWarning(existingBarcodes);
            }
        }
    }


    private void showDuplicateWarning(List<String> existingBarcodes) {
        StringBuilder message = new StringBuilder("These items already exist in promotions:\n");
        for (String barcode : existingBarcodes) {
            // Find the item in our list to show description
            for (NewPromotion promo : promotionList) {
                if (promo.getBarcode().equals(barcode)) {
                    message.append(promo.getDescription()).append(" (").append(barcode).append(")\n");
                    break;
                }
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Duplicate Items Found")
                .setMessage(message.toString())
                .setNegativeButton("Ok", null)
                .show();
    }
}

