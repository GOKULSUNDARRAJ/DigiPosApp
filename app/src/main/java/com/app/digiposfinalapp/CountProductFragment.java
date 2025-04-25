package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public class CountProductFragment extends Fragment {

    private static final String TAG = "CountProductFragment";
    private String ipAddress, portNumber, databaseName, username, password;
    private String barcode1,description;
    private Button buttonInsert;
    private EditText qtyedt;
    private TextView currentqtytxt;
    private String laststocktakeid;

    TextView message;
    int currectqty;

    int instervalue;

    int newqty;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            description= getArguments().getString("description");
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



        TextView descriptiontxt=view.findViewById(R.id.description);
        descriptiontxt.setText(description);


       ImageView backimage=view.findViewById(R.id.imageView);
        backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockTakesFragment productManagementFragment = new StockTakesFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });


        TextView message=view.findViewById(R.id.message123);

        qtyedt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // This method is called before the text is changed
                // You can use this to perform any action before the change occurs
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called as the text is being changed
                // You can use this to perform any action while the text is changing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int difference = 0;

                if (editable.toString().isEmpty()) {
                    return; // Exit if the EditText is empty
                }

                // Parse entered quantity
                int enterquantity = Integer.parseInt(qtyedt.getText().toString());

                // ✅ If current stock is negative (e.g., -1), treat it as 0 for comparison
                int effectiveCurrentQty = (currectqty < 0) ? 0 : currectqty;

                if (enterquantity > effectiveCurrentQty) {
                    difference = enterquantity - effectiveCurrentQty;
                    message.setText("Stock quantity increased by: " + difference);
                    message.setTextColor(getResources().getColor(R.color.green));
                } else if (enterquantity < effectiveCurrentQty) {
                    difference = effectiveCurrentQty - enterquantity;
                    message.setText("Stock quantity decreased by: " + difference);
                    message.setTextColor(getResources().getColor(R.color.red));
                } else {
                    message.setText("Stock quantity remains unchanged.");
                    message.setTextColor(getResources().getColor(R.color.gray));
                }
            }

        });


        return view;
    }

    private class CheckBarcodeExistsTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String barcode = params[0];
            Connection connection = null;
            int quantity = -999;  // Default for "not found"

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionString, username, password);

                String query = "SELECT Quantity FROM tbl_SoldItems WHERE Barcode = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, barcode);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    quantity = resultSet.getInt("Quantity"); // ✅ keep DB value (can be -1)
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
            if (quantity != -999) {
                insertBarcode(quantity);

                currentqtytxt.setText("THE CURRENT STOCK IS " + quantity);

                // ✅ Set color depending on value
                if (quantity < 0) {
                    currentqtytxt.setTextColor(getResources().getColor(R.color.red));
                } else {
                    currentqtytxt.setTextColor(getResources().getColor(R.color.green1)); // or any normal color
                }

                currectqty = quantity;
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
                    String sql = "SELECT TOP 1 StockTakeID FROM StockCount ORDER BY ID DESC";
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
             //   Toast.makeText(requireActivity(), "Count Stock Take Id " + result, Toast.LENGTH_LONG).show();
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
          //
            //  Toast.makeText(getActivity(), "Stock Take ID is missing" + laststocktakeid, Toast.LENGTH_SHORT).show();
            return; // Exit the method early
        }

        int addstockid = Integer.parseInt(laststocktakeid);
        String stockTakeIDStr = String.valueOf(addstockid);
        String barcode = barcode1;
        int enterquantity = Integer.parseInt(qtyedt.getText().toString());

        int stockTakeID;
        try {
            stockTakeID = Integer.parseInt(stockTakeIDStr) + 1;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid StockTakeID: " + stockTakeIDStr, e);
           // Toast.makeText(getActivity(), "Invalid Stock Take ID", Toast.LENGTH_SHORT).show();
            return; // Exit if the parsing fails
        }

        int differnece=0;
        if (enterquantity > currectqty){
           differnece=enterquantity-currectqty;

            new InsertBarcodesTask(stockTakeID, barcode, enterquantity,currectqty,differnece).execute();
            new UpdateQuantityTask(barcode, enterquantity).execute();

        }else if (enterquantity<currectqty){
            differnece=currectqty-enterquantity;
            new InsertBarcodesTask(stockTakeID, barcode, differnece,currectqty,enterquantity).execute();
            new UpdateQuantityTask(barcode, enterquantity).execute();
        }





    }




    private class InsertBarcodesTask extends AsyncTask<Void, Void, Void> {
        private int stockTakeID;
        private String barcode;
        private int qty;
        private int currentStock; // New field for CurrentStock
        private int stockAdjustment; // StockAdjustment

        public InsertBarcodesTask(int stockTakeID, String barcode, int qty, int currentStock, int stockAdjustment) {
            this.stockTakeID = stockTakeID;
            this.barcode = barcode;
            this.qty = qty;
            this.currentStock = currentStock;
            this.stockAdjustment = stockAdjustment; // Initialize StockAdjustment
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... params) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
                connection = DriverManager.getConnection(connectionUrl, username, password);

                // Adjusted the query to include [StockAdjustment] column
                String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[StockCount] " +
                        "([StockTakeID], [Barcode], [new_quantity], [CurrentStock], [date], [time], [StockAdjustment]) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, stockTakeID); // StockTakeID
                preparedStatement.setString(2, barcode); // Barcode
                preparedStatement.setInt(3, qty); // Qty
                preparedStatement.setInt(4, currentStock); // CurrentStock

                // Setting date and time columns
                String currentDateStr = java.time.LocalDate.now().toString(); // Convert LocalDate to String
                java.sql.Date currentDate = java.sql.Date.valueOf(currentDateStr); // Convert String to java.sql.Date

                // Remove milliseconds from the time
                LocalTime currentTimeWithoutMillis = java.time.LocalTime.now().truncatedTo(ChronoUnit.SECONDS); // Remove milliseconds
                String currentTimeStr = currentTimeWithoutMillis.toString(); // Convert LocalTime to String (HH:mm:ss)
                java.sql.Time currentTime = java.sql.Time.valueOf(currentTimeStr); // Convert String to java.sql.Time

                preparedStatement.setDate(5, currentDate); // date
                preparedStatement.setTime(6, currentTime); // time
                preparedStatement.setInt(7, stockAdjustment); // StockAdjustment (insert the StockAdjustment value)

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

                    StockTakesFragment productManagementFragment = new StockTakesFragment();
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
