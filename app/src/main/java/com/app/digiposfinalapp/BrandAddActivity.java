package com.app.digiposfinalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

public class BrandAddActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BrandAddActivity";

    EditText brandNameEditText, doneEditText;
    Button addButton;
    Connection connection;

    String ipAddress, portNumber, databaseName, username, password;

    // ExecutorService for background tasks
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_brand_add);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        brandNameEditText = findViewById(R.id.name);
        doneEditText = findViewById(R.id.done);
        addButton = findViewById(R.id.add);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String brandName = brandNameEditText.getText().toString();
                String done = doneEditText.getText().toString();

                if (brandName.isEmpty() || done.isEmpty()) {
                    Toast.makeText(BrandAddActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    insertDataIntoDatabaseAsync(brandName, done);
                }

            }
        });

    }

    // Method to insert data into the database asynchronously
    private void insertDataIntoDatabaseAsync(final String brandName, final String done) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connection == null) {
                        // Establish database connection
                        try {
                            Class.forName("net.sourceforge.jtds.jdbc.Driver");
                            String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                                    ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                            connection = DriverManager.getConnection(connectURL);
                        } catch (ClassNotFoundException | SQLException e) {
                            Log.e(LOG_TAG, "Failed to establish database connection: " + e.getMessage());
                            return;
                        }
                    }

                    if (connection != null) {
                        // Create SQL insert statement
                        String insertStatement = "INSERT INTO tbl_Brand (Brand, Done) VALUES (?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(insertStatement);

                        // Set values for placeholders
                        preparedStatement.setString(1, brandName);
                        preparedStatement.setString(2, done);

                        // Execute the insert statement
                        int rowsInserted = preparedStatement.executeUpdate();

                        // Notify user on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (rowsInserted > 0) {
                                    Toast.makeText(BrandAddActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(BrandAddActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        // Close the connection
                        connection.close();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BrandAddActivity.this, "Database connection failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Database operation failed", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BrandAddActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    public void gobackadd(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
