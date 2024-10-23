package com.app.digiposfinalapp;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PromotionAddActivity extends AppCompatActivity {

    private static final String TAG = "PromotionAddActivity";

    EditText promoIdEditText, descriptionEditText, receiptEditText, ruleNoEditText, ruleValueEditText, typeEditText, typeValueEditText, startEditText, endEditText, itemCountEditText, pluEditText, doneEditText;
    Button addButton;

    Connection connection;

    String ipAddress, portNumber, databaseName, username, password;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promotion_add);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize EditText fields
        promoIdEditText = findViewById(R.id.autocompleteTextView2);
        descriptionEditText = findViewById(R.id.autocompleteTextView24);
        receiptEditText = findViewById(R.id.autocompleteTextView246);
        ruleNoEditText = findViewById(R.id.autocompleteTextView24676);
        ruleValueEditText = findViewById(R.id.autocompleteTextView624676);
        typeEditText = findViewById(R.id.ruletype);
        typeValueEditText = findViewById(R.id.typevalue);
        startEditText = findViewById(R.id.startedt);
        endEditText = findViewById(R.id.endedt);
        itemCountEditText = findViewById(R.id.itemcountedt);
        pluEditText = findViewById(R.id.pluedt);
        doneEditText = findViewById(R.id.doneedt);
        addButton = findViewById(R.id.update);

        // Set OnClickListener for the add button
        addButton.setOnClickListener(view -> {
            // Get the input from EditText fields
            String promoId = promoIdEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String receipt = receiptEditText.getText().toString();
            String ruleNo = ruleNoEditText.getText().toString();
            String ruleValue = ruleValueEditText.getText().toString();
            String type = typeEditText.getText().toString();
            String typeValue = typeValueEditText.getText().toString();
            String start = startEditText.getText().toString();
            String end = endEditText.getText().toString();
            String itemCount = itemCountEditText.getText().toString();
            String plu = pluEditText.getText().toString();
            String done = doneEditText.getText().toString();

            if (promoId.isEmpty() || description.isEmpty() || receipt.isEmpty() || ruleNo.isEmpty() ||
                    ruleValue.isEmpty() || type.isEmpty() || typeValue.isEmpty() || start.isEmpty() ||
                    end.isEmpty() || itemCount.isEmpty() || plu.isEmpty() || done.isEmpty()) {
                Toast.makeText(PromotionAddActivity.this, "Fields cannot be Empty", Toast.LENGTH_SHORT).show();
            } else {
                // Execute database insertion in a background thread
                executorService.execute(() -> insertPromotionIntoDatabase(promoId, description, receipt, ruleNo, ruleValue, type, typeValue, start, end, itemCount, plu, done));
            }
        });
    }

    private void insertPromotionIntoDatabase(String promoId, String description, String receipt, String ruleNo, String ruleValue, String type, String typeValue, String start, String end, String itemCount, String plu, String done) {
        Connection conn = null;
        try {
            if (connection == null) {
                // Establish database connection
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                conn = DriverManager.getConnection(connectURL);
            }

            if (conn != null) {
                // Create SQL insert statement
                String insertStatement = "INSERT INTO tbl_Promotion (PromoID, Description, Receipt, Ruleno, RuleValue, Type, TypeValue, Start, Enddate, Item_Count, PLU, done) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(insertStatement);

                // Set values for placeholders
                preparedStatement.setString(1, promoId);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, receipt);
                preparedStatement.setString(4, ruleNo);
                preparedStatement.setString(5, ruleValue);
                preparedStatement.setString(6, type);
                preparedStatement.setString(7, typeValue);
                preparedStatement.setString(8, start);
                preparedStatement.setString(9, end);
                preparedStatement.setString(10, itemCount);
                preparedStatement.setString(11, plu);
                preparedStatement.setString(12, done);

                // Execute the insert statement
                int rowsInserted = preparedStatement.executeUpdate();

                runOnUiThread(() -> {
                    if (rowsInserted > 0) {
                        Toast.makeText(PromotionAddActivity.this, "Promotion added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PromotionAddActivity.this, "Failed to add promotion", Toast.LENGTH_SHORT).show();
                    }
                });

                // Close the connection
                conn.close();
            } else {
                runOnUiThread(() -> Toast.makeText(PromotionAddActivity.this, "Database connection is null", Toast.LENGTH_SHORT).show());
            }
        } catch (ClassNotFoundException | SQLException e) {
            Log.e(TAG, "Error inserting promotion: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(PromotionAddActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    public void gobackadd(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
