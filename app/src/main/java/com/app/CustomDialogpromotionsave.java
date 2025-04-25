package com.app;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.digiposfinalapp.Constants;
import com.app.digiposfinalapp.CustomDialogpromotionsave2;
import com.app.digiposfinalapp.NewPromotion;
import com.app.digiposfinalapp.NewPromotionAdapter;
import com.app.digiposfinalapp.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CustomDialogpromotionsave extends Dialog {

    private Context context;
    EditText startdate, enddatedt;

    RadioGroup radioGroupDateOptions;
    RadioButton radioMonthEnd, radioYearEnd, radioNoEndDate;
    String ipAddress, portNumber, databaseName, username, password;
    private String endDate, startDate;

    private Activity activity;
    String selectedppromodes;
    String buy;
    String selectedpromotype;
    String price;
    int size;
    List<NewPromotion> promotionList;
    NewPromotionAdapter adapter;
    String promotionName;

    private RadioGroup radioGroupCustomerType;
    private RadioButton radioEveryone, radioDdCustomers;


    public CustomDialogpromotionsave(@NonNull Activity activity , String selectedppromodes, String buy, String selectedpromotype,
                                     String price, int size, List<NewPromotion> promotionList, NewPromotionAdapter adapter, String promotionName) {
        super(activity);
        this.activity = activity;
        this.context = activity;
        this.selectedppromodes = selectedppromodes;
        this.buy = buy;
        this.selectedpromotype = selectedpromotype;
        this.price = price;
        this.size = size;
        this.promotionList=promotionList;
        this.adapter=adapter;
        this.promotionName=promotionName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logoutpromotion);
        // Retrieve database connection details from SharedPreferences

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        Button clear = findViewById(R.id.cancelbtn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        radioGroupDateOptions = findViewById(R.id.radioGroup2); // Make sure this ID matches your XML
        radioMonthEnd = findViewById(R.id.monthendradio); // Replace with your actual RadioButton IDs
        radioYearEnd = findViewById(R.id.yearendradio);
        radioNoEndDate = findViewById(R.id.noendradio);

        startdate = findViewById(R.id.startdate);
        enddatedt = findViewById(R.id.enddatedt);

        if (startDate == null || startDate.isEmpty()) {
            Calendar c = Calendar.getInstance();
            startdate.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));
            enddatedt.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));
        }

        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(startdate);
                }
            }
        });

        enddatedt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(enddatedt);
                }
            }
        });


        radioGroupDateOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (checkedId == R.id.monthendradio) { // MONTH END selected
                    // Calculate last day of the current month
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    String monthEndDate = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (month + 1) + "-" + year;
                    enddatedt.setText(monthEndDate);
                }
                else if (checkedId == R.id.yearendradio) { // YEAR END selected
                    // Set to last day of the year (31-12-YYYY)
                    String yearEndDate = "31-12-" + year;
                    enddatedt.setText(yearEndDate);
                }
                else if (checkedId == R.id.noendradio) { // NO END DATE selected
                    // Add 10 years to the current date
                    calendar.add(Calendar.YEAR, 10); // Move 10 years ahead
                    String tenYearsLater = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
                    enddatedt.setText(tenYearsLater);
                }
            }
        });

        Button next=findViewById(R.id.savebtn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savePromotionData();
            }
        });

        radioGroupCustomerType = findViewById(R.id.radioGroup);
        radioEveryone = findViewById(R.id.radioButton2);
        radioDdCustomers = findViewById(R.id.radioButton1);





    }
    // Helper method to show DatePickerDialog
    private void showDatePicker(final EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {
        // Do nothing on back press
    }

    public void showLogoutDialog() {
        dismiss();
        CustomDialogpromotionsave2 cdd = new CustomDialogpromotionsave2(activity,size);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cdd.show();
    }
    private void insertPromotionData(String description, String receipt, String ruleNo,
                                     String ruleValue, String type, String typeValue,
                                     String startDate, String endDate, int itemCount,
                                     String plu, String promoName, String dealType,
                                     String promoTarget, String maxUses, String status,
                                     String unitPrice) {
        final String TAG = "PromotionDB";
        Log.d(TAG, "Starting promotion data insertion");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                Statement stmt = null;
                PreparedStatement pstmt = null;
                ResultSet rs = null;

                try {
                    // Create database connection
                    String connectionString = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            "/" + databaseName + ";user=" + username + ";password=" + password;
                    connection = DriverManager.getConnection(connectionString);
                    Log.d(TAG, "Database connection established");

                    // 1. Get the next PromoID (not ID)
                    String getLastIdQuery = "SELECT MAX(PromoID) AS LastId FROM [STAR_RETAIL].[dbo].[tbl_Promotion]";
                    stmt = connection.createStatement();
                    rs = stmt.executeQuery(getLastIdQuery);

                    int newPromoId = 1;
                    if (rs.next()) {
                        newPromoId = rs.getInt("LastId") + 1;
                    }
                    Log.d(TAG, "Using PromoID: " + newPromoId);

                    // 2. Prepare insert statement (excluding the ID column)
                    String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[tbl_Promotion] " +
                            "(PromoID, Description, Receipt, Ruleno, RuleValue, Type, " +
                            "TypeValue, Start, Enddate, Item_Count, PLU, done, PromoName, " +
                            "DealType, PromoTarget, MaxUses, Status, UnitPrice) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    pstmt = connection.prepareStatement(insertQuery);

                    // Set parameters (starting from 1 since we removed ID)
                    pstmt.setInt(1, newPromoId);
                    setStringOrNull(pstmt, 2, description);
                    setStringOrNull(pstmt, 3, receipt);
                    setStringOrNull(pstmt, 4, ruleNo);
                    setStringOrNull(pstmt, 5, ruleValue);
                    setStringOrNull(pstmt, 6, type);
                    setStringOrNull(pstmt, 7, typeValue);
                    setStringOrNull(pstmt, 8, startDate);
                    setStringOrNull(pstmt, 9, endDate);
                    pstmt.setInt(10, itemCount);
                    setStringOrNull(pstmt, 11, plu);
                    pstmt.setString(12, "1"); // done = 1
                    setStringOrNull(pstmt, 13, promoName);
                    setStringOrNull(pstmt, 14, dealType);
                    setStringOrNull(pstmt, 15, promoTarget);
                    setStringOrNull(pstmt, 16, maxUses);
                    setStringOrNull(pstmt, 17, status);
                    setStringOrNull(pstmt, 18, unitPrice);

                    // 3. Execute insert
                    int rowsAffected = pstmt.executeUpdate();
                    Log.d(TAG, "Rows affected: " + rowsAffected);


                    new SavePromoItemsTask(
                            promotionList,String.valueOf(newPromoId)
                    ).execute();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (rowsAffected > 0) {
                                showLogoutDialog();
                            } else {
                                Toast.makeText(context, "Failed to save promotion", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage(), e);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (stmt != null) stmt.close();
                        if (pstmt != null) pstmt.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "Error closing resources: " + e.getMessage());
                    }
                }
            }
        }).start();
    }


    // Helper method for handling nullable string fields
    private void setStringOrNull(PreparedStatement pstmt, int parameterIndex, String value) throws SQLException {
        if (value != null && !value.trim().equalsIgnoreCase("NULL") && !value.trim().isEmpty()) {
            pstmt.setString(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, java.sql.Types.VARCHAR);
        }
    }

    private String formatDateForDatabase(String displayDate) {
        // Convert from "dd-MM-yyyy" to "yyyyMMdd"
        try {
            String[] parts = displayDate.split("-");
            return parts[2] + parts[1] + parts[0]; // yyyyMMdd
        } catch (Exception e) {
            return "20250101"; // default date if parsing fails
        }
    }

    private class SavePromoItemsTask extends AsyncTask<Void, Void, Boolean> {
        private List<NewPromotion> promotionList;
        private String promoId;
        private String errorMessage = "";

        public SavePromoItemsTask(List<NewPromotion> promotionList, String promoId) {
            this.promotionList = promotionList;
            this.promoId = promoId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Connection connection = null;
            PreparedStatement pstmtInsert = null;
            PreparedStatement pstmtUpdate = null;

            try {
                // Create database connection
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        "/" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectionString);
                connection.setAutoCommit(false); // Start transaction

                // Prepare insert statement for Promo_Items table
                String insertQuery = "INSERT INTO [STAR_RETAIL].[dbo].[Promo_Items] " +
                        "(PLU, PromoID, done, Barcode, DDPrice, DiscountPrice, PromotionPrice) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                // Prepare update statement for tbl_Products table
                String updateQuery = "UPDATE [STAR_RETAIL].[dbo].[tbl_Products] " +
                        "SET PromoID = ? WHERE PLU = ?";

                pstmtInsert = connection.prepareStatement(insertQuery);
                pstmtUpdate = connection.prepareStatement(updateQuery);

                // Process each promotion item
                for (NewPromotion promotion : promotionList) {
                    // Insert into Promo_Items table
                    pstmtInsert.setString(1, promotion.getPlu()); // PLU
                    pstmtInsert.setString(2, promoId); // PromoID
                    pstmtInsert.setString(3, "1"); // done (assuming 1 means done)
                    pstmtInsert.setString(4, promotion.getBarcode()); // Barcode
                    pstmtInsert.setDouble(5, 0); // DDPrice
                    pstmtInsert.setDouble(6, 0); // DiscountPrice
                    pstmtInsert.setDouble(7, 0); // PromotionPrice
                    pstmtInsert.addBatch();

                    // Update PromoID in tbl_Products table
                    pstmtUpdate.setString(1, promoId); // PromoID
                    pstmtUpdate.setString(2, promotion.getPlu()); // PLU
                    pstmtUpdate.addBatch();
                }

                // Execute both batches
                int[] insertResults = pstmtInsert.executeBatch();
                int[] updateResults = pstmtUpdate.executeBatch();

                // Check if all operations were successful
                for (int result : insertResults) {
                    if (result <= 0) {
                        connection.rollback();
                        return false;
                    }
                }

                for (int result : updateResults) {
                    if (result <= 0) {
                        connection.rollback();
                        return false;
                    }
                }

                connection.commit(); // Commit transaction if all operations succeeded
                return true;

            } catch (Exception e) {
                try {
                    if (connection != null) connection.rollback();
                } catch (SQLException ex) {
                    Log.e("PromoItems", "Error rolling back transaction: " + ex.getMessage());
                }
                errorMessage = e.getMessage();
                Log.e("PromoItems", "Error saving promo items: " + e.getMessage(), e);
                return false;
            } finally {
                try {
                    if (pstmtInsert != null) pstmtInsert.close();
                    if (pstmtUpdate != null) pstmtUpdate.close();
                    if (connection != null) {
                        connection.setAutoCommit(true); // Reset auto-commit
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e("PromoItems", "Error closing resources: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(context, "Promotion items saved successfully", Toast.LENGTH_SHORT).show();
                clearPromotionsFromSharedPreferences();

            } else {
                Toast.makeText(context, "The product is already exits", Toast.LENGTH_SHORT).show();

                Log.e("PromoItems", "Error closing resources: " + errorMessage);
            }
        }
    }

    private void clearPromotionsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PromotionPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("promotion_list");  // Removes only this key
        editor.apply();  // Apply changes

        promotionList.clear();  // Clear the local list
        adapter.notifyDataSetChanged();
    }






    private void savePromotionData() {
        double unitprice = 0.0;
        try {
            if ((buy != null && !buy.isEmpty()) && (price != null && !price.isEmpty())) {
                double buyValue = Double.parseDouble(buy);
                double priceValue = Double.parseDouble(price);
                unitprice = priceValue != 0 ? buyValue % priceValue : 0.0;
            }
        } catch (NumberFormatException e) {
            unitprice = 0.0;
        }

        String formattedStartDate = formatDateToDatabaseFormat(startdate.getText() != null ? startdate.getText().toString() : "");
        String formattedEndDate = formatDateToDatabaseFormat(enddatedt.getText() != null ? enddatedt.getText().toString() : "");

        // Determine promoTarget based on radio button selection
        String promoTarget = "0"; // Default to "EVERY ONE"
        if (radioDdCustomers.isChecked()) {
            promoTarget = "1"; // "DD CUSTOMERS"
        }

        if ("FIXED DISCOUNT".equals(selectedpromotype)) {
            selectedpromotype="Fixed Price";
        }

        insertPromotionData(
                selectedppromodes != null ? selectedppromodes : "",
                selectedppromodes != null ? selectedppromodes : "",
                "Buy Any",
                buy != null ? buy : "",
                selectedpromotype != null ? selectedpromotype : "",
                price != null ? price : "",
                formattedStartDate,
                formattedEndDate,
                size,
                null,
                promotionName != null ? promotionName : "",
                null,
                promoTarget, // This is where we pass the determined value
                null,
                "Active",
                String.valueOf(String.format("%.2f", unitprice))
        );
    }


    private String formatDateToDatabaseFormat(String displayDate) {
        if (displayDate == null || displayDate.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inputFormat.parse(displayDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMdd");
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("DateFormat", "Error parsing date: " + e.getMessage());
            return "";
        }
    }
}


