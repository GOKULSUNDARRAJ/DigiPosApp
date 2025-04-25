package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import com.app.CustomDialogpromotionsave;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PromoItemEditFragment extends Fragment {

    private PromoItemAdapter2.PromoItem2 promoDetails;
    EditText promotionnameedt;
    int promoId;
    String selectedpromotype, selectedppromodes;
    Spinner promotypeSpinner, promodesSpinner;

    private EditText buyEditText, getedt;
    String[] spinnerItems;
    TextView tvpd;

    EditText priceedt1;
    private String ipAddress, portNumber, databaseName, username, password;

    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    Double totalprice;

    Button btnsave;


    private RecyclerView recyclerView;
    private PromoProductAdapter adapter;

    int totalnewlist;

    private String description, barcode, itemcode, Price, costprice, PromoID, plu;

    EditText searchbarcode;
    private BroadcastReceiver scanReceiver;
    private String barcodeValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_item_edit, container, false);


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


        ImageView camera = view.findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentnewpromotionedit productManagementFragment = new BarCodeScanFragmentnewpromotionedit();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        SharedPreferences sharedPref = requireContext().getSharedPreferences("PromoPrefs", Context.MODE_PRIVATE);

        // Option 1: Retrieve JSON and convert back to object
        Gson gson = new Gson();
        String promoJson = sharedPref.getString("promo_details", null);
        promoDetails = gson.fromJson(promoJson, PromoItemAdapter2.PromoItem2.class);
        promoId = sharedPref.getInt("promo_id", -1);

        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password

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
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                barcodeValue = searchbarcode.getText().toString();

                // Then check if exists in database promotions
                new CheckSingleItemTask(barcodeValue).execute();

                v.clearFocus();
                return true;
            }
            return false;
        });

        SharedPreferences sharedPreferences1 = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences1.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences1.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        promodesSpinner = view.findViewById(R.id.promodesSpinner); // Updated ID


        tvpd = view.findViewById(R.id.tvpd);

        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoItemsFragment1 productManagementFragment = new PromoItemsFragment1();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        promotionnameedt = view.findViewById(R.id.promotionnameedt);
        promotypeSpinner = view.findViewById(R.id.promotype_spinner);

        if (promoDetails != null) {
            promotionnameedt.setText(promoDetails.getPromoName());

            spinnerItems = new String[]{"FIXED DISCOUNT", "DISCOUNT"}; // Your spinner items

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, spinnerItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            promotypeSpinner.setAdapter(adapter);

            // Get the promo type from your promoDetails object
            String promoType = promoDetails.getType(); // Assuming you have this method

            // Find the position of the promo type in spinner items
            int position = -1;
            for (int i = 0; i < spinnerItems.length; i++) {
                if (spinnerItems[i].equalsIgnoreCase(promoType)) {
                    position = i;
                    break;
                }
            }

            // Set the selection if found, otherwise default to first item
            promotypeSpinner.setSelection(position != -1 ? position : 0);

            promotypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedpromotype = spinnerItems[position];
                    calculateAndDisplayResult();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Optional handling
                }
            });


        }

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

        buyEditText.setText(promoDetails.getRuleValue());
        priceedt1.setText(promoDetails.getTypeValue());
        totalprice = 0.0;

        btnsave = view.findViewById(R.id.btnsave);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CustomDialogpromotionUpdate cdd = new CustomDialogpromotionUpdate(getActivity(), selectedppromodes, buyEditText.getText().toString(),
                        selectedpromotype, priceedt1.getText().toString(), totalnewlist,
                        promotionnameedt.getText().toString(), promoDetails.getStartDate(), promoDetails.getEndDate(), promoId, adapter, promoDetails.getPromoTarget());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerViewPromoProducts);

        fetchBarcodesAndPricesForPromo(promoId);


        return view;
    }


    private void removeProduct(int position) {
        List<PromoProductEdit> products = getProductsFromSharedPreferences();
        if (products != null && position < products.size()) {
            // Get the product being removed
            PromoProductEdit removedProduct = products.get(position);

            // Remove the product from the list
            products.remove(position);
            totalnewlist = products.size();

            // Save the updated list
            saveProductsToSharedPreferences(products);

            // Update the adapter
            adapter.updateProducts(products);

            // Recalculate the total price
            calculateTotalPrice(products);

            // Show confirmation message
            Toast.makeText(getContext(),
                    removedProduct.getDescription() + " removed from promotion",
                    Toast.LENGTH_SHORT).show();

            // Also remove from database (you may want to add this)
            new RemoveProductFromPromoTask(removedProduct.getBarcode(), promoId).execute();
        }
    }

    private void calculateTotalPrice(List<PromoProductEdit> products) {
        totalprice = 0.0;
        for (PromoProductEdit product : products) {
            totalprice += product.getPrice();
        }
        calculateAndDisplayResult();
    }


    private void calculateAndDisplayResult() {
        // Check if views are initialized
        if (priceedt1 == null || !isAdded()) {
            return;
        }

        try {
            // Get total price - use 0 if totalprice is null
            double totalPrice = totalprice != null ? totalprice : 0.0;

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
                        int totalItems = totalnewlist;
                        double salePrice = Double.parseDouble(salePriceText);

                        if (totalItems > 0) {
                            double result = ((totalPrice * buyValue) / totalItems) - salePrice;
                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            String formattedResult = df.format(result);

                            Log.d("PROMO_CALCULATION",
                                    "Calculation details:\n" +
                                            "totalPrice = " + totalPrice + "\n" +
                                            "buyValue = " + buyValue + "\n" +
                                            "totalItems = " + totalItems + "\n" +
                                            "salePrice = " + salePrice + "\n" +
                                            "RESULT = " + result);

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


        }
    }

    private void fetchBarcodesAndPricesForPromo(int promoId) {
        new AsyncTask<Integer, Void, List<PromoProductEdit>>() {
            @Override
            protected List<PromoProductEdit> doInBackground(Integer... params) {
                List<PromoProductEdit> products = new ArrayList<>();
                Connection connection = null;
                PreparedStatement statement = null;
                ResultSet resultSet = null;

                try {
                    String connectionString = buildConnectionString();
                    connection = DriverManager.getConnection(connectionString);

                    // Get all barcodes for this promo
                    String barcodeQuery = "SELECT Barcode FROM [STAR_RETAIL].[dbo].[Promo_Items] WHERE PromoID = ?";
                    statement = connection.prepareStatement(barcodeQuery);
                    statement.setInt(1, params[0]);
                    resultSet = statement.executeQuery();

                    List<String> barcodes = new ArrayList<>();
                    while (resultSet.next()) {
                        barcodes.add(resultSet.getString("Barcode"));
                    }

                    // Get complete product details for each barcode
                    if (!barcodes.isEmpty()) {
                        closeResources(resultSet, statement, null); // Close only resultSet and statement

                        String inClause = String.join(",", Collections.nCopies(barcodes.size(), "?"));
                        String productQuery = "SELECT Barcode, Price, Buy_Price AS CostPrice, Description, Item_code, PLU " +
                                "FROM [STAR_RETAIL].[dbo].[tbl_Products] " +
                                "WHERE Barcode IN (" + inClause + ")";

                        statement = connection.prepareStatement(productQuery);
                        for (int i = 0; i < barcodes.size(); i++) {
                            statement.setString(i + 1, barcodes.get(i));
                        }

                        resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            PromoProductEdit product = new PromoProductEdit();
                            product.setBarcode(resultSet.getString("Barcode"));
                            product.setPrice(resultSet.getDouble("Price"));
                            product.setCostPrice(resultSet.getDouble("CostPrice")); // Fixed to use CostPrice
                            product.setDescription(resultSet.getString("Description"));
                            product.setItemCode(resultSet.getString("Item_code"));
                            product.setPul(resultSet.getString("PLU")); // Assuming setPlu exists

                            products.add(product);
                        }
                    }
                } catch (SQLException e) {
                    Log.e("DB_ERROR", "Database error", e);
                    return null;
                } finally {
                    closeResources(resultSet, statement, connection);
                }
                return products;
            }

            @Override
            protected void onPostExecute(List<PromoProductEdit> products) {
                if (products == null) {
                    Toast.makeText(getContext(), "Error fetching product data", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!products.isEmpty()) {
                    // Calculate total price
                    double total = 0.0;
                    for (PromoProductEdit product : products) {
                        total += product.getPrice();
                    }
                    totalprice = total;

                    // Save products to SharedPreferences
                    saveProductsToSharedPreferences(products);


                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    // Get products from SharedPreferences
                    List<PromoProductEdit> products1 = getProductsFromSharedPreferences();

                    totalnewlist = products1.size();


                    // Initialize adapter
                    adapter = new PromoProductAdapter(getContext(), products1, new PromoProductAdapter.OnProductClickListener() {
                        @Override
                        public void onRemoveClick(int position) {
                            removeProduct(position);
                        }

                        @Override
                        public void onBuyClick(int position) {
                            PromoProductEdit product = products1.get(position);
                            // Handle buy action
                            Toast.makeText(getContext(), "Buy: " + product.getDescription(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onGetClick(int position) {
                            PromoProductEdit product = products1.get(position);
                            // Handle get action
                            Toast.makeText(getContext(), "Get: " + product.getDescription(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    recyclerView.setAdapter(adapter);


                    // Update UI
                    calculateAndDisplayResult();

                    // Log product details
                    for (PromoProductEdit product : products) {
                        Log.d("PRODUCT_DETAILS",
                                "Barcode: " + product.getBarcode() +
                                        ", Price: " + product.getPrice() +
                                        ", Description: " + product.getDescription());
                    }
                } else {
                    Toast.makeText(getContext(), "No products found for this promo", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(promoId);
    }

    private void saveProductsToSharedPreferences(List<PromoProductEdit> products) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PromoProducts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert products list to JSON for storage
        Gson gson = new Gson();
        String jsonProducts = gson.toJson(products);
        editor.putString("promo_products", jsonProducts);
        editor.apply();
    }

    private String buildConnectionString() {
        return "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                "/" + databaseName + ";user=" + username + ";password=" + password;
    }

    private void closeResources(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            Log.e("DB_CLOSE", "Error closing resources", e);
        }
    }

    private List<PromoProductEdit> getProductsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PromoProducts", Context.MODE_PRIVATE);
        String jsonProducts = sharedPreferences.getString("promo_products", null);

        if (jsonProducts != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PromoProductEdit>>() {
            }.getType();
            return gson.fromJson(jsonProducts, type);
        }
        return null;
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

            // Create a new PromoProductEdit object with the fetched details
            PromoProductEdit newProduct = new PromoProductEdit();
            newProduct.setDescription(description);
            newProduct.setBarcode(barcode);
            newProduct.setPul(id);


            // Get existing products from SharedPreferences
            List<PromoProductEdit> products = getProductsFromSharedPreferences();
            if (products == null) {
                products = new ArrayList<>();
            }

            // Check if product already exists in the list
            boolean exists = false;
            for (PromoProductEdit product : products) {
                if (product.getBarcode().equals(barcode)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                // Add the new product
                products.add(newProduct);

                // Save updated list to SharedPreferences
                saveProductsToSharedPreferences(products);

                // Update RecyclerView
                if (adapter != null) {
                    adapter.updateProducts(products);
                    recyclerView.scrollToPosition(products.size() - 1);
                }

                // Recalculate total price
                calculateTotalPrice(products);

                // Update total items count
                totalnewlist = products.size();
                calculateAndDisplayResult();

                Toast.makeText(getContext(), "Product added to promotion", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Product already in promotion", Toast.LENGTH_SHORT).show();
            }

            // Clear the barcode field for next scan
            searchbarcode.setText("");
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
            try {
                // Create a new Bundle to pass the barcode value
                Bundle bundle = new Bundle();
                bundle.putString("barcode", barcodeValue);
                bundle.putString("fromBarCodeScanFragmentNewPromotionsearchedit", "fromBarCodeScanFragmentNewPromotionsearchedit");

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


    private class RemoveProductFromPromoTask extends AsyncTask<Void, Void, Boolean> {
        private String barcode;
        private int promoId;

        public RemoveProductFromPromoTask(String barcode, int promoId) {
            this.barcode = barcode;
            this.promoId = promoId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                String connectionString = buildConnectionString();
                connection = DriverManager.getConnection(connectionString);

                String query = "DELETE FROM [STAR_RETAIL].[dbo].[Promo_Items] " +
                        "WHERE PromoID = ? AND Barcode = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, promoId);
                statement.setString(2, barcode);

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLException e) {
                Log.e("DB_ERROR", "Error removing product from promo", e);
                return false;
            } finally {
                closeResources(null, statement, connection);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(getContext(),
                        "Failed to remove product from database",
                        Toast.LENGTH_SHORT).show();
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