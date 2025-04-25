package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class PriceCheckMainFragment extends Fragment {

    private static final String TAG = "PriceCheckMainFragment";
    String ipAddress, portNumber, databaseName, username, password;
    String barcode1,price1,productDescription,CaseUnit,CasePrice,quantity,capatitys,plu;
    EditText priceedt1,casepriceedt1;
    Button savepricecheckbtn;
    String newPrice;
    String barcodeCode;
    TextView Productnametv1;
    Button addtobarcode;

    AutoCompleteTextView  supplierSpinner;
    private String  supplier,CostPerCase;
    String supplierName;
    EditText costpercaseedt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_check_main, container, false);

        Log.d(TAG, "onCreateView: Initializing SharedPreferences and retrieving database credentials.");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        ImageView home=view.findViewById(R.id.homeimage);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Log.d(TAG, "onCreateView: Retrieved IP: " + ipAddress + ", Port: " + portNumber);

        priceedt1=view.findViewById(R.id.priceedt);
        priceedt1.setText(price1);

        casepriceedt1=view.findViewById(R.id.casepriceedt);

        priceedt1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                decimalFormat = new DecimalFormat("0.00");
                decimalFormat.setGroupingUsed(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    priceedt1.removeTextChangedListener(this);

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
            }
        });

        casepriceedt1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                decimalFormat = new DecimalFormat("0.00");
                decimalFormat.setGroupingUsed(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    casepriceedt1.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString) / 100;
                            currentText = decimalFormat.format(parsed);
                            casepriceedt1.setText(currentText);
                            casepriceedt1.setSelection(currentText.length());
                        } catch (NumberFormatException e) {
                            currentText = "";
                            casepriceedt1.setText("");
                        }
                    } else {
                        currentText = "";
                        casepriceedt1.setText("");
                    }

                    casepriceedt1.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });





        barcodeCode = barcode1;

        savepricecheckbtn=view.findViewById(R.id.savepricecheckbtn);
        savepricecheckbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        costpercaseedt=view.findViewById(R.id.costpercaseedt);
        costpercaseedt.setText(CostPerCase);


        costpercaseedt.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                decimalFormat = new DecimalFormat("0.00");
                decimalFormat.setGroupingUsed(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    costpercaseedt.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString) / 100;
                            currentText = decimalFormat.format(parsed);
                            costpercaseedt.setText(currentText);
                            costpercaseedt.setSelection(currentText.length());
                        } catch (NumberFormatException e) {
                            currentText = "";
                            costpercaseedt.setText("");
                        }
                    } else {
                        currentText = "";
                        costpercaseedt.setText("");
                    }

                    costpercaseedt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        supplierSpinner = view.findViewById(R.id.spinner_spuulier);

        new FetchSupplierDataEdit(getContext(), supplierSpinner,
                (supplier != null) ? supplier : "").execute();

        supplierSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                int supplierId = selectedSupplier.getId();
                supplierName = selectedSupplier.getSupplier();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedSupplierID", String.valueOf(selectedSupplier.getId()));
                editor.putString("selectedSupplierName", selectedSupplier.getSupplier());
                editor.apply();
            }
        });
        supplierSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                supplierSpinner.showDropDown();
            }
        });
        supplierSpinner.setOnClickListener(v -> supplierSpinner.showDropDown());

        Productnametv1=view.findViewById(R.id.Productnametv);
        Productnametv1.setText(productDescription);

        ImageView back =view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceCheckFragment productManagementFragment = new PriceCheckFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        casepriceedt1.setText(CasePrice);

        addtobarcode=view.findViewById(R.id.addtobarcode);
        addtobarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                double price;
                String costPriceText = priceedt1.getText().toString();

                try {
                    if (costPriceText.isEmpty()) {
                        price = 0.0;
                    } else {
                        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                        Number number = format.parse(costPriceText);
                        price = number.doubleValue();
                    }

                    new InsertBarcodeTask(barcode1, plu, productDescription, "", price, date, capatitys, quantity).execute();

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
                    price = 0.0;
                }
            }
        });

        return view;
    }

    private class UpdatePriceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "UpdatePriceTask: Starting AsyncTask for updating price, case price, cost per case, and supplier.");
        }

        @Override
        protected String doInBackground(String... params) {
            String itemCode = params[0];       // Barcode
            String newPrice = params[1];       // Price
            String newCasePrice = params[2];   // CasePrice
            String newCostPerCase = params[3]; // CostPerCase
            String supplierName = params[4];   // Supplier Name

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Log.d(TAG, "doInBackground: Attempting to connect to the database.");
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                if (connection != null) {
                    Log.d(TAG, "doInBackground: Database connection successful.");

                    String sql = "UPDATE tbl_Products SET Price = ?, SaleWithVAT = ?, CasePrice = ?, CostPerCase = ?, Supplier = ? WHERE Barcode = ?";
                    statement = connection.prepareStatement(sql);

                    statement.setString(1, newPrice);
                    statement.setString(2, newPrice);
                    statement.setString(3, newCasePrice);
                    statement.setString(4, newCostPerCase);
                    statement.setString(5, supplierName);
                    statement.setString(6, itemCode);

                    int rowsUpdated = statement.executeUpdate();
                    Log.d(TAG, "doInBackground: Number of rows updated: " + rowsUpdated);

                    return (rowsUpdated > 0) ? "Price, Case Price, Cost Per Case & Supplier Update Successful" : "Update Failed";

                } else {
                    Log.e(TAG, "doInBackground: Database connection failed.");
                    return "Connection Failed";
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                Log.e(TAG, "doInBackground: Integrity constraint violation", e);
                return "SQL Error: Duplicate entry for a unique field.";
            } catch (SQLException e) {
                Log.e(TAG, "doInBackground: SQL error occurred", e);
                return "SQL Error: " + e.getMessage();
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                    Log.d(TAG, "doInBackground: Database resources closed.");
                } catch (SQLException e) {
                    Log.e(TAG, "doInBackground: Error closing database resources", e);
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Update result - " + result);
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            price1 = getArguments().getString("Price");
            productDescription = getArguments().getString("description");
            CasePrice = getArguments().getString("CasePrice");
            quantity = getArguments().getString("quantity");
            capatitys = getArguments().getString("Capacity");
            plu = getArguments().getString("plu");
            supplier = getArguments().getString("supplier");
            CostPerCase = getArguments().getString("CostPerCase");
        }
    }

    private void showCustomDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_custom_layout_pricecheck);

        Button dialogCancelButton = dialog.findViewById(R.id.dialogCancelButton);
        Button dialogOkButton = dialog.findViewById(R.id.dialogOkButton);

        dialogCancelButton.setOnClickListener(v -> dialog.dismiss());

        dialogOkButton.setOnClickListener(v -> {
            newPrice = priceedt1.getText().toString();
            String newCasePrice = casepriceedt1.getText().toString();
            String newCostPerCase = costpercaseedt.getText().toString();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SupplierPrefs", MODE_PRIVATE);
            String selectedSupplierName = sharedPreferences.getString("selectedSupplierName", "");

            new UpdatePriceTask().execute(
                    barcodeCode,
                    newPrice,
                    newCasePrice,
                    newCostPerCase,
                    selectedSupplierName
            );

            logPriceChange(barcode1, price1, priceedt1.getText().toString(), "3");
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
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
            return "";
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
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

        public InsertBarcodeTask(String barcode, String plu, String detail, String shop,
                                 double price, Date date, String capacity, String qty) {
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

                String checkQuery = "SELECT COUNT(*) FROM [STAR_RETAIL].[dbo].[tblBarcode] WHERE Barcode = ?";
                checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, barcode);
                java.sql.ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    barcodeExists = true;

                    String updateQuery = "UPDATE [STAR_RETAIL].[dbo].[tblBarcode] SET " +
                            "[PLU] = ?, " +
                            "[Detail] = ?, " +
                            "[Shop] = ?, " +
                            "[Price] = ?, " +
                            "[dtDate] = ?, " +
                            "[Capacity] = ?, " +
                            "[Qty] = ? " +
                            "WHERE [Barcode] = ?";

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    String formattedDate = dateFormat.format(date);

                    updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setString(1, plu);
                    updateStatement.setString(2, detail);
                    updateStatement.setString(3, shop);
                    updateStatement.setString(4, "£" + String.format("%.2f", price));
                    updateStatement.setString(5, formattedDate);
                    updateStatement.setString(6, capacity);
                    updateStatement.setString(7, qty);
                    updateStatement.setString(8, barcode);

                    int rowsUpdated = updateStatement.executeUpdate();
                    return rowsUpdated > 0;
                } else {
                    String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[tblBarcode] " +
                            "([PLU], [Barcode], [Detail], [Shop], [Price], [dtDate], [Capacity], [Qty]) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
        String cleanedOldPrice = oldPrice.replaceAll("[^\\d.]", "");
        String cleanedNewPrice = newPrice.replaceAll("[^\\d.]", "");

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
            connection = DriverManager.getConnection(connectionUrl, username, password);

            if (connection != null) {
                String sql = "INSERT INTO [STAR_RETAIL].[dbo].[Price_change_Logs] " +
                        "([Barcode], [Pricechange_Date], [Pricechange_Time], [Old_Price], [New_Price], [Done_by], [PrintStatus]) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                String currentTime = timeFormat.format(new Date());

                statement = connection.prepareStatement(sql);
                statement.setString(1, barcode);
                statement.setString(2, currentDate);
                statement.setString(3, currentTime);
                statement.setString(4, cleanedOldPrice);
                statement.setString(5, cleanedNewPrice);
                statement.setString(6, doneBy);
                statement.setString(7, "0");

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
}