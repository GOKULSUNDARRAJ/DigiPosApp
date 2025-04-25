package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PromoItemsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromoItemAdapter adapter;
    private List<PromoItem> promoItems = new ArrayList<>();
    private List<PromoItem> filteredItems = new ArrayList<>();
    private ProgressBar progressBar;
    private EditText searchEditText;
    private String description, barcode, subDepartment, supplier, department, vat, ageLimit, Itemcode, Brand, UnitPerCase, CostPerCase, Price, sellingprice, Margin, plu, outerBarcode, price, addbarcode, endDate, startDate, dd_Price, ddpoint, manageStock, weight, capacitys, currentStock1, qty, minStock, reorderleve, Markup, discount, expiry_date, buyPrice, CasePrice, CaseUnit, VatValue1;


    private String barcodeValue;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private boolean isBarcodeDetected = false;

    String TAG = "PromoItemsFragment";

    private RadioGroup searchFilterRadioGroup;
    private RadioButton radioPromoId, radioBarcode, radioPLU;
    private String currentSearchType = "PromoID"; // Default search type

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_items, container, false);


        searchFilterRadioGroup = view.findViewById(R.id.searchFilterRadioGroup);
        radioPromoId = view.findViewById(R.id.radioPromoId);
        radioBarcode = view.findViewById(R.id.radioBarcode);
        radioPLU = view.findViewById(R.id.radioPLU);


        searchFilterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioPromoId) {
                    currentSearchType = "PromoID";
                    searchEditText.setHint("Search by Promo ID");
                    searchEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                } else if (checkedId == R.id.radioBarcode) {
                    currentSearchType = "Barcode";
                    searchEditText.setHint("Search by Barcode");
                    searchEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                } else if (checkedId == R.id.radioPLU) {
                    currentSearchType = "PLU";
                    searchEditText.setHint("Search by PLU");
                    searchEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                }

                // Clear search and reload all items when changing search type
                searchEditText.setText("");
                filterItems("");
            }
        });





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

        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchEditText = view.findViewById(R.id.searchEditText);

        searchEditText.requestFocus();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PromoItemAdapter(filteredItems, getContext(), getParentFragmentManager());
        recyclerView.setAdapter(adapter);

        setupSearch();
        loadPromoItems();

        ImageView back = view.findViewById(R.id.imageViewback);
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

        ImageView camera = view.findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentpromotionsearch productManagementFragment = new BarCodeScanFragmentpromotionsearch();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        searchEditText.setText(barcode);

        return view;
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String searchValue = searchEditText.getText().toString();

                // Only perform database search for barcode (if that's the selected filter)
                if ("Barcode".equals(currentSearchType) && !searchValue.isEmpty()) {
                    barcodeValue = searchValue;
                    new DatabaseTask().execute();
                }

                v.clearFocus();
                return true;
            }
            return false;
        });
    }

    private void filterItems(String query) {
        requireActivity().runOnUiThread(() -> {
            filteredItems.clear();

            if (query == null || query.isEmpty()) {
                filteredItems.addAll(promoItems);
            } else {
                String searchQuery = query.toLowerCase();
                for (PromoItem item : promoItems) {
                    boolean matches = false;

                    switch (currentSearchType) {
                        case "PromoID":
                            matches = String.valueOf(item.getPromoId()).contains(searchQuery);
                            break;
                        case "Barcode":
                            matches = item.getBarcode() != null &&
                                    item.getBarcode().toLowerCase().contains(searchQuery);
                            break;
                        case "PLU":
                            matches = item.getPlu() != null &&
                                    item.getPlu().toLowerCase().contains(searchQuery);
                            break;
                    }

                    if (matches) {
                        filteredItems.add(item);
                    }
                }
            }

            adapter.notifyDataSetChanged();

            if (filteredItems.isEmpty() && !query.isEmpty()) {
                Toast.makeText(getContext(), "No promotions found for this " + currentSearchType.toLowerCase(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPromoItems() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        new AsyncTask<Void, Void, List<PromoItem>>() {
            @Override
            protected List<PromoItem> doInBackground(Void... voids) {
                List<PromoItem> items = new ArrayList<>();
                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;

                try {
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                    String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
                    String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
                    String databaseName = Constants.DATABASE_NAME;
                    String username = Constants.USERNAME;
                    String password = Constants.PASSWORD;

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            "/" + databaseName + ";ssl=request";

                    connection = DriverManager.getConnection(connectionUrl, username, password);

                    String query = "SELECT [ID], [PLU], [PromoID], [done], [Barcode], " +
                            "[DDPrice], [DiscountPrice], [PromotionPrice] " +
                            "FROM [STAR_RETAIL].[dbo].[Promo_Items]";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        PromoItem item = new PromoItem(
                                resultSet.getInt("ID"),
                                resultSet.getString("PLU"),
                                resultSet.getInt("PromoID"),
                                resultSet.getInt("done") == 1,
                                resultSet.getString("Barcode"), // Changed to getString
                                parseDoubleSafe(resultSet.getString("DDPrice")),
                                parseDoubleSafe(resultSet.getString("DiscountPrice")),
                                parseDoubleSafe(resultSet.getString("PromotionPrice"))
                        );
                        items.add(item);
                    }
                } catch (Exception e) {
                    Log.e("PromoItems", "Database error", e);
                    return null;
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        Log.e("PromoItems", "Error closing connection", e);
                    }
                }
                return items;
            }

            @Override
            protected void onPostExecute(List<PromoItem> result) {
                progressBar.setVisibility(View.GONE);

                if (result == null) {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Database connection failed", Toast.LENGTH_SHORT).show();
                } else if (result.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No promotions available", Toast.LENGTH_SHORT).show();
                } else {
                    promoItems.clear();
                    promoItems.addAll(result);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing double value: " + value, e);
            return 0.0;
        }
    }

    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department, id, Expiry_date,
                saleWithVAT, Markup, discount, costPerCase, price, vat, margin, ageLimit, itemcode, Brand, UnitPerCase, currentstock, minStock, reorderleve,
                CostPerCase, Price, sellingprice, Margin, outerbarcode, costprice, addbarcode, startDate, enddate, dd_price, ddpoints, manageStock, weight, capatitys, CasePrice,
                CaseUnit, VatValue;

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

                        filterItems(barcode);


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

                isBarcodeDetected = false; // Reset detection for the next barcode scan
            }

            if (barcode != null) {
                filterItems(barcode);
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
                bundle.putString("fromPromoItemsFragment", "fromPromoItemsFragment");

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