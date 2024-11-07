package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountProductFragment extends Fragment {

    private static final String TAG = "CountProductFragment";
    private String ipAddress, portNumber, databaseName, username, password;
    private String barcode1;
    private Button buttonInsert;
    private EditText qtyedt;
    private TextView currentqtytxt;
    private String laststocktakeid;

    TextView message;
    int addqty;

    int instervalue;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            Log.d(TAG, "Received Barcode: " + barcode1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_count_product, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");

        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;



        qtyedt = view.findViewById(R.id.qtyedt);
        buttonInsert = view.findViewById(R.id.savestocktakebtn);
        currentqtytxt = view.findViewById(R.id.currentqtytxt);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qtyedt.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Enter a Quantity..", Toast.LENGTH_SHORT).show();
                } else {
                    new GetLastPLUTask().execute();
                }
            }
        });



        new CheckBarcodeExistsTask().execute(barcode1);

        message = view.findViewById(R.id.message);

        qtyedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int tart, int count, int after) {
                // No need to calculate before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                calculateAndUpdateMessage();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Optionally, if you want to calculate again after text has changed
                calculateAndUpdateMessage();
            }
        });


        // Define your RadioButtons
        RadioButton radioButton1 = view.findViewById(R.id.radioButton1);
        RadioButton radioButton2 = view.findViewById(R.id.radioButton2);
        radioButton2.setChecked(true);
// Set up listeners to prevent both from being enabled simultaneously
        radioButton1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If radioButton1 is checked, uncheck radioButton2
                radioButton2.setChecked(false);
            }
        });

        radioButton2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If radioButton2 is checked, uncheck radioButton1
                radioButton1.setChecked(false);
            }
        });


        return view;
    }


    private class GetLastPLUTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            laststocktakeid = null;

            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                try (Connection connection = DriverManager.getConnection(connectionUrl, username, password)) {
                    String sql = "SELECT TOP 1 StockTakeID FROM StockTake ORDER BY ID DESC";
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {

                        if (resultSet.next()) {
                            laststocktakeid = resultSet.getString("StockTakeID");
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
            }
            return laststocktakeid;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(requireActivity(), "Count Stock Take Id " + result, Toast.LENGTH_LONG).show();
            } else {
                // Set default Stock Take ID if the table is empty
                laststocktakeid = "3000"; // Set default value
                Toast.makeText(requireActivity(), "No Stock Take ID found. Using default: " + laststocktakeid, Toast.LENGTH_LONG).show();
            }
            // Continue with the next task
            new CheckBarcodeExistsTask().execute(barcode1);
        }
    }


    private class CheckBarcodeExistsTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String barcode = params[0];
            Connection connection = null;
            int quantity = -1;  // Initialize with -1 to indicate no quantity found

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionString, username, password);

                String query = "SELECT Quantity FROM tbl_SoldItems WHERE Barcode = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, barcode);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    quantity = resultSet.getInt("Quantity"); // Retrieve Quantity if the barcode exists
                }

                resultSet.close();
                statement.close();

            } catch (SQLException e) {
                Log.e(TAG, "Database Error", e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "Failed to close connection", e);
                    }
                }
            }
            return quantity;
        }

        @Override
        protected void onPostExecute(Integer quantity) {
            if (quantity >= 0) {
                Toast.makeText(getContext(), "Barcode exists. Quantity: " + quantity, Toast.LENGTH_SHORT).show();
                insertBarcode(quantity);
                currentqtytxt.setText("THE CURRENT STOCK IS" + "\t" + String.valueOf(quantity));

                addqty = quantity;

            } else {
                Toast.makeText(getContext(), "Barcode does not exist in the database.", Toast.LENGTH_SHORT).show();
                buttonInsert.setEnabled(true);

            }
        }

    }


    private void insertBarcode(Integer q) {
        if (laststocktakeid == null) {
            Toast.makeText(getActivity(), "Stock Take ID is missing" + laststocktakeid, Toast.LENGTH_SHORT).show();
            return; // Exit the method early
        }

        int addstockid = Integer.parseInt(laststocktakeid);
        String stockTakeIDStr = String.valueOf(addstockid);
        String barcode = barcode1;
        int addedquantity = instervalue;

        int stockTakeID;
        try {
            stockTakeID = Integer.parseInt(stockTakeIDStr) + 1;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid StockTakeID: " + stockTakeIDStr, e);
            Toast.makeText(getActivity(), "Invalid Stock Take ID", Toast.LENGTH_SHORT).show();
            return; // Exit if the parsing fails
        }


        new InsertBarcodesTask(stockTakeID, barcode, addedquantity).execute();
        new UpdateQuantityTask(barcode, addedquantity).execute();

    }


    private class InsertBarcodesTask extends AsyncTask<Void, Void, Void> {
        private int stockTakeID;
        private String barcode;
        private int qty;

        public InsertBarcodesTask(int stockTakeID, String barcode, int qty) {
            this.stockTakeID = stockTakeID;
            this.barcode = barcode;
            this.qty = qty;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[StockTake] ([StockTakeID], [Barcode], [Qty], [DateTime]) VALUES (?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, stockTakeID); // StockTakeID
                preparedStatement.setString(2, barcode); // Barcode
                preparedStatement.setInt(3, qty); // Qty
                preparedStatement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis())); // DateTime

                preparedStatement.executeUpdate();
                Log.d(TAG, "Barcode inserted successfully");

            } catch (SQLException e) {
                Log.e(TAG, "Database connection error: " + e.getMessage());
            } finally {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Error closing resources: " + e.getMessage());
                }
            }
            return null;
        }
    }


    private class UpdateQuantityTask extends AsyncTask<Void, Void, Void> {
        private String barcode;
        private int newQuantity;

        public UpdateQuantityTask(String barcode, int newQuantity) {
            this.barcode = barcode;
            this.newQuantity = newQuantity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName, username, password)) {

                String updateQuery = "UPDATE tbl_SoldItems SET Quantity = ? WHERE Barcode = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setInt(1, newQuantity);
                    statement.setString(2, barcode);
                    statement.executeUpdate();
                    Log.d(TAG, "Quantity updated successfully");

                    BarCodeScanStockTakeFragment productManagementFragment = new BarCodeScanStockTakeFragment();
                    FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                    fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                    fragmentTransaction.commit();
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error updating quantity: " + e.getMessage());
            }
            return null;
        }
    }


    private void calculateAndUpdateMessage() {
        String qtyText = qtyedt.getText().toString().trim();
        int currentQuantity = addqty;
        if (!qtyText.isEmpty()) {
            try {
                int enteredQuantity = Integer.parseInt(qtyText);
                int result;
                String updateMessage;

                // Check if enteredQuantity is less than currentQuantity
                if (enteredQuantity < currentQuantity) {
                    result = currentQuantity - enteredQuantity; // Calculate the difference
                    updateMessage = "The stock Quantity will be reduced by " + result;
                    message.setTextColor(getResources().getColor(R.color.redColor));
                    instervalue = result;

                } else {
                    result = enteredQuantity - currentQuantity; // Calculate the increase
                    updateMessage = "The stock Quantity will be Increased by " + result;
                    message.setTextColor(getResources().getColor(R.color.green));
                    instervalue = Integer.parseInt(qtyText);

                }

                message.setText(updateMessage);
            } catch (NumberFormatException e) {
                // Handle potential NumberFormatException if qtyedt has invalid input
                message.setText("Invalid input");
            }
        } else {
            message.setText("No quantity entered");
        }
    }


}
