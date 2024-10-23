package com.app.digiposfinalapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SupplierAddActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SupplierAddActivity"; // Common log tag

    private EditText supplierNameEditText, doneEditText;
    private Button addButton;

    String ipAddress, portNumber, databaseName, username, password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_supplier);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        supplierNameEditText = findViewById(R.id.name);
        doneEditText = findViewById(R.id.done);
        addButton = findViewById(R.id.add);

        // Set onClickListener for the add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve input values
                String supplierName = supplierNameEditText.getText().toString();
                String done = doneEditText.getText().toString();

                // Validate input data
                if (supplierName.isEmpty() || done.isEmpty()) {
                    Toast.makeText(SupplierAddActivity.this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    new InsertSupplierTask().execute(supplierName, done);
                }
            }
        });
    }

    private class InsertSupplierTask extends AsyncTask<String, Void, Boolean> {

        private String supplierName;
        private String done;

        @Override
        protected Boolean doInBackground(String... params) {
            supplierName = params[0];
            done = params[1];
            Connection connection = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectURL);

                String insertStatement = "INSERT INTO M_Supplier (M_Supplier, done) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertStatement)) {
                    preparedStatement.setString(1, supplierName);
                    preparedStatement.setString(2, done);

                    int rowsInserted = preparedStatement.executeUpdate();
                    return rowsInserted > 0;
                }
            } catch (ClassNotFoundException | SQLException e) {
                Log.e(LOG_TAG, "Error inserting supplier: " + e.getMessage());
                return false;
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    Log.e(LOG_TAG, "Error closing connection: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Clear input fields after successful insertion
                supplierNameEditText.setText("");
                doneEditText.setText("");
                Toast.makeText(SupplierAddActivity.this, "Supplier added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SupplierAddActivity.this, "Failed to add supplier", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void gobackadd(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
