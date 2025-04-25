package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class CountProductFragment2 extends Fragment {

    private static final String TAG = "CountProductFragment2";
    private String ipAddress, portNumber, databaseName, username, password;
    private String barcode1, description;
    private Button buttonInsert;
    private EditText qtyedt;
    private TextView currentqtytxt;
    private String laststocktakeid;

    TextView message;
    int addqty;

    int instervalue;
    int newQty;
    String reason;
    Spinner reasonSpinner;
    boolean isIncrementEnabled = true;

    RadioButton radioIncrement, radioDecrement;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            description = getArguments().getString("description");
            Log.d(TAG, "Received Barcode: " + barcode1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_count_product2, container, false);
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

        // Get reference to radio buttons
        radioIncrement = view.findViewById(R.id.radioButton11);
        radioDecrement = view.findViewById(R.id.radioButton21);

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


        TextView descriptiontxt = view.findViewById(R.id.description);
        descriptiontxt.setText(description);


        ImageView backimage = view.findViewById(R.id.imageView);
        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockadjustmentsearchFragment productManagementFragment = new StockadjustmentsearchFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        reasonSpinner = view.findViewById(R.id.reasonSpinner);
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReasonType selectedReasonType = (ReasonType) parent.getItemAtPosition(position);
                reason = selectedReasonType.getReason(); // Fetch the reason
                String section = selectedReasonType.getSection(); // Fetch the section
                int done = selectedReasonType.getDone(); // Fetch the done status

                // Optionally perform actions based on the selected reason and section
                Log.d("ReasonSpinner", "Selected reason: " + reason + ", Section: " + section + ", Done: " + done);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("ReasonSpinner", "No reason selected.");
            }
        });

// Execute data fetch
        new FetchReasonEditData(getContext(), reasonSpinner).execute();


        return view;
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
            if  (quantity != -999)  {
                // Toast.makeText(getContext(), "Barcode exists. Quantity: " + quantity, Toast.LENGTH_SHORT).show();
                insertBarcode(quantity);
                currentqtytxt.setText("THE CURRENT STOCK IS" + "\t" + String.valueOf(quantity));

                addqty = quantity;

            } else {
                Toast.makeText(getContext(), "Barcode does not exist in the database.", Toast.LENGTH_SHORT).show();
                buttonInsert.setEnabled(true);

            }
        }

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
                //  Toast.makeText(requireActivity(), "Count Stock Take Id " + result, Toast.LENGTH_LONG).show();
            } else {
                // Set default Stock Take ID if the table is empty
                laststocktakeid = "3000"; // Set default value
                Toast.makeText(requireActivity(), "No Stock Take ID found. Using default: " + laststocktakeid, Toast.LENGTH_LONG).show();
            }
            // Continue with the next task
            new CheckBarcodeExistsTask().execute(barcode1);
        }
    }


    private void insertBarcode(Integer q) {
        if (laststocktakeid == null) {
            // Toast.makeText(getActivity(), "Stock Take ID is missing" + laststocktakeid, Toast.LENGTH_SHORT).show();
            return; // Exit the method early
        }

        int addstockid = Integer.parseInt(laststocktakeid);
        String stockTakeIDStr = String.valueOf(addstockid);
        String barcode = barcode1;
        int addedquantity = Integer.parseInt(qtyedt.getText().toString());

        int stockTakeID;
        try {
            stockTakeID = Integer.parseInt(stockTakeIDStr) + 1;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid StockTakeID: " + stockTakeIDStr, e);
            //  Toast.makeText(getActivity(), "Invalid Stock Take ID", Toast.LENGTH_SHORT).show();
            return; // Exit if the parsing fails
        }


        handleStockChange();

        new InsertBarcodesTask(stockTakeID, barcode, newQty, addqty, reason, addedquantity).execute();


    }


    private class InsertBarcodesTask extends AsyncTask<Void, Void, Void> {
        private int stockTakeID;
        private String barcode;
        private int qty;
        private int currentStock; // CurrentStock field
        private String reason;    // Reason field
        private int stockAdjustment; // New field for stock adjustment

        public InsertBarcodesTask(int stockTakeID, String barcode, int qty, int currentStock, String reason, int stockAdjustment) {
            this.stockTakeID = stockTakeID;
            this.barcode = barcode;
            this.qty = qty;
            this.currentStock = currentStock;
            this.reason = reason; // Initialize reason
            this.stockAdjustment = stockAdjustment; // Initialize stockAdjustment
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[StockTake] " +
                        "([StockTakeID], [Barcode], [new_quantity], [CurrentStock], [Reason], [StockAdjustment], [date], [time], [Done]) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, stockTakeID); // StockTakeID
                preparedStatement.setString(2, barcode); // Barcode
                preparedStatement.setInt(3, qty); // new_quantity
                preparedStatement.setInt(4, currentStock); // CurrentStock
                preparedStatement.setString(5, reason); // Reason
                preparedStatement.setInt(6, stockAdjustment); // StockAdjustment
                // Assuming you want current date and time:
                preparedStatement.setDate(7, new java.sql.Date(System.currentTimeMillis())); // date
                preparedStatement.setTime(8, new java.sql.Time(System.currentTimeMillis())); // time
                preparedStatement.setBoolean(9, true); // Done (initially not done)

                // Setting date and time columns
                String currentDateStr = java.time.LocalDate.now().toString(); // Convert LocalDate to String
                java.sql.Date currentDate = java.sql.Date.valueOf(currentDateStr); // Convert String to java.sql.Date

                // Remove milliseconds from the time
                LocalTime currentTimeWithoutMillis = LocalTime.now().truncatedTo(ChronoUnit.SECONDS); // Remove milliseconds
                String currentTimeStr = currentTimeWithoutMillis.toString(); // Convert LocalTime to String (HH:mm:ss)
                java.sql.Time currentTime = java.sql.Time.valueOf(currentTimeStr); // Convert String to java.sql.Time

                preparedStatement.setDate(7, currentDate); // date
                preparedStatement.setTime(8, currentTime); // time

                preparedStatement.executeUpdate();
                Log.d(TAG, "Barcode with reason and stock adjustment inserted successfully");

                StockadjustmentsearchFragment productManagementFragment = new StockadjustmentsearchFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

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


    private void handleStockChange() {
        String enteredQtyStr = qtyedt.getText().toString();
        if (enteredQtyStr.isEmpty()) {
            Toast.makeText(getContext(), "Enter a Quantity..", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse entered quantity
        int enteredQty = Integer.parseInt(enteredQtyStr);

        // Get the current stock quantity from currentqtytxt
        int currentQty = addqty;


        // Check which radio button is selected
        if (radioIncrement.isChecked()) {
            // Increment the stock
            newQty = currentQty + enteredQty;
            currentqtytxt.setText("THE CURRENT STOCK IS " + "\t" + newQty); // Update UI with new stock quantity
        } else if (radioDecrement.isChecked()) {
            // Decrement the stock
            if (currentQty >= enteredQty) {
                newQty = currentQty - enteredQty;
                currentqtytxt.setText("THE CURRENT STOCK IS " + "\t" + newQty); // Update UI with new stock quantity
            } else {
                // Show a message if the entered quantity exceeds the current stock
                Toast.makeText(getContext(), "Cannot decrement beyond current stock.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where no radio button is selected
            Toast.makeText(getContext(), "Please select an action (Increment or Decrement).", Toast.LENGTH_SHORT).show();
        }

        // Optionally, save the changes to the database here (increment/decrement logic as required)
        // You can invoke your insert or update methods here based on your logic.
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
