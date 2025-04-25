package com.app.digiposfinalapp;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;


public class CustomDialogpromotionUpdate extends Dialog {

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
    int promoId;
    PromoProductAdapter padapter;

    private RadioGroup radioGroupCustomerType;
    private RadioButton radioEveryone, radioDdCustomers;
    private String promoTarget; // Add this to store the promoTarget value

    public CustomDialogpromotionUpdate(Activity activity, String selectedppromodes, String buy,
                                       String selectedpromotype, String price, int itemCount, String promotionName,
                                       String startDate, String endDate, int promoId, PromoProductAdapter padapter,
                                       String promoTarget) {  // Add this parameter
        super(activity);
        this.activity = activity;
        this.selectedppromodes = selectedppromodes;
        this.buy = buy;
        this.selectedpromotype = selectedpromotype;
        this.price = price;
        this.size = itemCount;
        this.promotionName = promotionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.promoId = promoId;
        this.padapter = padapter;
        this.promoTarget = promoTarget; // Store the promoTarget value
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logoutpromotionupdate);


        // Initialize customer type radio group
        radioGroupCustomerType = findViewById(R.id.radioGroup);
        radioEveryone = findViewById(R.id.radioButton2);
        radioDdCustomers = findViewById(R.id.radioButton1);

// Set the checked state based on promoTarget
        if (promoTarget != null && promoTarget.equals("1")) {
            radioDdCustomers.setChecked(true);
        } else {
            radioEveryone.setChecked(true); // Default to EVERYONE
        }



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




            Calendar c = Calendar.getInstance();
            startdate.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));
            enddatedt.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));


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
                } else if (checkedId == R.id.yearendradio) { // YEAR END selected
                    // Set to last day of the year (31-12-YYYY)
                    String yearEndDate = "31-12-" + year;
                    enddatedt.setText(yearEndDate);
                } else if (checkedId == R.id.noendradio) { // NO END DATE selected
                    // Add 10 years to the current date
                    calendar.add(Calendar.YEAR, 10); // Move 10 years ahead
                    String tenYearsLater = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
                    enddatedt.setText(tenYearsLater);
                }
            }
        });




        Button next = findViewById(R.id.savebtn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get updated values from UI
                String updatedStartDate = startdate.getText().toString();
                String updatedEndDate = enddatedt.getText().toString();

                // Get the current product list from the adapter
                List<PromoProductEdit> products = padapter.getProducts(); // You'll need to add this method to your adapter


                if ("FIXED DISCOUNT".equals(selectedpromotype)) {
                    selectedpromotype="Fixed Price";
                }
                // Update the promotion header
                updatePromotionInDatabase(promoId, promotionName, selectedpromotype, selectedppromodes,
                        buy, price, size, updatedStartDate, updatedEndDate);

                // Update the promotion items
                if (products != null && !products.isEmpty()) {
                    updatePromoItemsInDatabase(promoId, products);
                }

                Log.d("PromotionUpdate", "Updating promotion with " + (products != null ? products.size() : 0) + " items");

                dismiss();
            }
        });


    }

    private void updatePromotionInDatabase(int promoId, String promoName, String promoType,
                                           String promoMode, String buy, String price,
                                           int itemCount, String startDate, String endDate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Convert dates to SQL Server format (yyyy-MM-dd)
                String sqlStartDate = convertToSqlDateFormat(startDate);
                String sqlEndDate = convertToSqlDateFormat(endDate);

                // Calculate unit price
                double unitPrice = 0.0;
                try {
                    if ((buy != null && !buy.isEmpty()) && (price != null && !price.isEmpty())) {
                        double buyValue = Double.parseDouble(buy);
                        double priceValue = Double.parseDouble(price);
                        unitPrice = priceValue != 0 ? buyValue / priceValue : 0.0;
                    }
                } catch (NumberFormatException e) {
                    unitPrice = 0.0;
                }

                String promoTargetValue = "0"; // Default to EVERYONE
                if (radioDdCustomers.isChecked()) {
                    promoTargetValue = "1"; // DD CUSTOMERS
                }

                String sql = "UPDATE [STAR_RETAIL].[dbo].[tbl_Promotion] SET " +
                        "[Description] = ?, " +        // 1
                        "[Receipt] = ?, " +             // 2
                        "[Ruleno] = ?, " +              // 3
                        "[RuleValue] = ?, " +          // 4
                        "[Type] = ?, " +                // 5
                        "[TypeValue] = ?, " +           // 6
                        "[Start] = ?, " +               // 7
                        "[Enddate] = ?, " +             // 8
                        "[Item_Count] = ?, " +         // 9
                        "[PLU] = ?, " +                 // 10
                        "[done] = ?, " +                // 11
                        "[PromoName] = ?, " +           // 12
                        "[DealType] = ?, " +            // 13
                        "[PromoTarget] = ?, " +         // 14
                        "[MaxUses] = ?, " +             // 15
                        "[Status] = ?, " +             // 16
                        "[UnitPrice] = ? " +            // 17
                        "WHERE [PromoID] = ?";          // 18

                try (Connection connection = DriverManager.getConnection(
                        "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                "/" + databaseName + ";user=" + username + ";password=" + password);
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    // Set all parameters
                    statement.setString(1, promoMode);          // Description
                    statement.setString(2, promoMode);          // Receipt
                    statement.setString(3, "Buy Any");          // Ruleno
                    statement.setString(4, buy);               // RuleValue
                    statement.setString(5, promoType);         // Type
                    statement.setString(6, price);              // TypeValue
                    statement.setString(7, sqlStartDate);       // Start
                    statement.setString(8, sqlEndDate);         // Enddate
                    statement.setInt(9, itemCount);             // Item_Count
                    statement.setString(10, "");                // PLU
                    statement.setString(11, "1");               // done
                    statement.setString(12, promoName);         // PromoName
                    statement.setString(13, "");                // DealType
                    statement.setString(14, promoTargetValue);                // PromoTarget
                    statement.setString(15, "");                // MaxUses
                    statement.setString(16, "Active");          // Status
                    statement.setDouble(17, unitPrice);         // UnitPrice
                    statement.setInt(18, promoId);              // PromoID

                    int rowsAffected = statement.executeUpdate();

                    activity.runOnUiThread(() -> {
                        if (rowsAffected > 0) {
                            Toast.makeText(activity, "Promotion updated successfully",
                                    Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(activity, "No changes made to promotion",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() ->
                            Toast.makeText(activity, "Error updating promotion: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }



    // Helper method to convert date format from dd-MM-yyyy to yyyyMMdd
    private String convertToSqlDateFormat(String inputDate) {
        try {
            String[] parts = inputDate.split("-");
            if (parts.length == 3) {
                // Ensure day and month are two digits
                String day = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                String year = parts[2];

                return year + month + day; // yyyyMMdd
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputDate; // fallback to original format if conversion fails
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



    private void updatePromoItemsInDatabase(int promoId, List<PromoProductEdit> products) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                PreparedStatement checkStatement = null;
                PreparedStatement updateStatement = null;
                PreparedStatement insertStatement = null;

                try {
                    // Validate input
                    if (products == null) {
                        throw new IllegalArgumentException("Products list is null");
                    }

                    Log.d("PromoItems", "Starting to process " + products.size() + " products");

                    // Create database connection
                    connection = DriverManager.getConnection(
                            "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                    "/" + databaseName + ";user=" + username + ";password=" + password);
                    connection.setAutoCommit(false);

                    // Prepare SQL statements
                    String checkSql = "SELECT COUNT(*) FROM [STAR_RETAIL].[dbo].[Promo_Items] " +
                            "WHERE PromoID = ? AND (Barcode = ? OR (? IS NOT NULL AND PLU = ?))";
                    checkStatement = connection.prepareStatement(checkSql);

                    String updateSql = "UPDATE [STAR_RETAIL].[dbo].[Promo_Items] " +
                            "SET PromotionPrice = ?, DDPrice = 0.0, DiscountPrice = 0.0, done = '1' " +
                            "WHERE PromoID = ? AND (Barcode = ? OR (? IS NOT NULL AND PLU = ?))";
                    updateStatement = connection.prepareStatement(updateSql);

                    String insertSql = "INSERT INTO [STAR_RETAIL].[dbo].[Promo_Items] " +
                            "(PromoID, Barcode, PromotionPrice, DDPrice, DiscountPrice, PLU, done) " +
                            "VALUES (?, ?, ?, 0.0, 0.0, ?, '1')";
                    insertStatement = connection.prepareStatement(insertSql);

                    int successCount = 0;
                    int updateCount = 0;
                    int failCount = 0;
                    int skippedCount = 0;

                    // Process each product
                    for (int i = 0; i < products.size(); i++) {
                        PromoProductEdit product = products.get(i);
                        try {
                            // Validate product
                            if (product == null) {
                                Log.w("PromoItems", "Product #" + (i+1) + " is null");
                                skippedCount++;
                                continue;
                            }

                            String barcode = product.getBarcode();
                            String plu = product.getPul();

                            // Check if we have at least one valid identifier
                            boolean hasBarcode = barcode != null && !barcode.trim().isEmpty();
                            boolean hasPLU = plu != null && !plu.trim().isEmpty();

                            if (!hasBarcode && !hasPLU) {
                                Log.w("PromoItems", "Skipping product #" + (i+1) + " - no valid identifiers");
                                skippedCount++;
                                continue;
                            }

                            // Check if item exists (with NULL-safe comparison)
                            checkStatement.setInt(1, promoId);
                            checkStatement.setString(2, hasBarcode ? barcode.trim() : "");
                            checkStatement.setString(3, plu);
                            checkStatement.setString(4, hasPLU ? plu.trim() : "");
                            ResultSet rs = checkStatement.executeQuery();
                            rs.next();
                            int count = rs.getInt(1);
                            rs.close();

                            if (count > 0) {
                                // Update existing record
                                updateStatement.setString(1, String.valueOf(product.getPrice()));
                                updateStatement.setInt(2, promoId);
                                updateStatement.setString(3, hasBarcode ? barcode.trim() : "");
                                updateStatement.setString(4, plu);
                                updateStatement.setString(5, hasPLU ? plu.trim() : "");
                                int updated = updateStatement.executeUpdate();
                                if (updated > 0) {
                                    updateCount++;
                                    Log.d("PromoItems", "Updated product #" + (i+1));
                                }
                            } else {
                                // Insert new record - ensure we have at least one identifier
                                if (hasBarcode || hasPLU) {
                                    insertStatement.setInt(1, promoId);

                                    // Handle Barcode (bigint)
                                    if (hasBarcode) {
                                        try {
                                            long barcodeValue = Long.parseLong(barcode.trim());
                                            insertStatement.setLong(2, barcodeValue);
                                        } catch (NumberFormatException e) {
                                            Log.e("PromoItems", "Invalid barcode format for product #" + (i+1) + ": " + barcode);
                                            failCount++;
                                            continue;
                                        }
                                    } else {
                                        insertStatement.setNull(2, Types.BIGINT);
                                    }

                                    insertStatement.setString(3, String.valueOf(product.getPrice()));
                                    insertStatement.setString(4, hasPLU ? plu.trim() : null);
                                    insertStatement.executeUpdate();
                                    successCount++;
                                    Log.d("PromoItems", "Inserted product #" + (i+1));
                                }
                            }
                        } catch (SQLException e) {
                            Log.e("PromoItems", "Error processing product #" + (i+1) + ": " + e.getMessage());
                            failCount++;

                            // Special handling for unique constraint violations
                            if (e.getMessage() != null && e.getMessage().contains("Violation of UNIQUE KEY constraint")) {
                                try {
                                    // Try to update instead
                                    String barcode = product.getBarcode();
                                    String plu = product.getItemCode();
                                    boolean hasBarcode = barcode != null && !barcode.trim().isEmpty();
                                    boolean hasPLU = plu != null && !plu.trim().isEmpty();

                                    updateStatement.setString(1, String.valueOf(product.getPrice()));
                                    updateStatement.setInt(2, promoId);
                                    updateStatement.setString(3, hasBarcode ? barcode.trim() : "");
                                    updateStatement.setString(4, plu);
                                    updateStatement.setString(5, hasPLU ? plu.trim() : "");
                                    int updated = updateStatement.executeUpdate();
                                    if (updated > 0) {
                                        updateCount++;
                                        failCount--; // Recovered from error
                                        Log.d("PromoItems", "Recovered from unique constraint by updating product #" + (i+1));
                                    }
                                } catch (SQLException ex) {
                                    Log.e("PromoItems", "Failed to recover from unique constraint for product #" + (i+1), ex);
                                }
                            }
                        }
                    }

                    connection.commit();

                    // Report results
                    final String message = String.format(
                            "Processed %d items (%d updated, %d inserted, %d skipped, %d failed)",
                            products.size(), updateCount, successCount, skippedCount, failCount
                    );

                    Log.d("PromoItems", message);
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();

                        if (activity instanceof AppCompatActivity) {
                            PromoItemsFragment1 productManagementFragment = new PromoItemsFragment1();
                            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }



                    });

                } catch (Exception e) {
                    try {
                        if (connection != null) connection.rollback();
                    } catch (SQLException ex) {
                        Log.e("PromoItems", "Error rolling back transaction", ex);
                    }

                    final String errorMsg = "Error updating items: " + e.getMessage();
                    Log.e("PromoItems", errorMsg, e);
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                    });

                } finally {
                    // Close resources
                    try {
                        if (checkStatement != null) checkStatement.close();
                        if (updateStatement != null) updateStatement.close();
                        if (insertStatement != null) insertStatement.close();
                        if (connection != null) {
                            connection.setAutoCommit(true);
                            connection.close();
                        }
                    } catch (SQLException e) {
                        Log.e("PromoItems", "Error closing resources", e);
                    }
                }
            }
        }).start();
    }



}


