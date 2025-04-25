package com.app.digiposfinalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SeparateActivity extends AppCompatActivity {
    LinearLayout cardView1, cardView2;
    String ipAddress, portNumber;
    private static final String TAG = "LagCat";
    String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    String savedUsername, savedPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_separate);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;
        dbPassword1 = Constants.PASSWORD;
        savedUsername = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        savedPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, null);

        SharedPreferences sharedPreferences2 = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences2.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences2.getString(Constants.KEY_PORT, "");

        cardView1 = findViewById(R.id.card1);
        cardView2 = findViewById(R.id.card2);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectToDatabaseTask().execute(ipAddress, portNumber);
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeparateActivity.this, ReportloginActivity.class);
                startActivity(intent);
            }
        });
    }

    private class ConnectToDatabaseTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show(); // Show loading dialog
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String ip = params[0];
            String port = params[1];
            Connection connection = null;
            boolean isConnected = false;

            try {
                String connectionString = "jdbc:jtds:sqlserver://" + ip + ":" + port +
                        ";databaseName=" + Constants.DATABASE_NAME + ";user=" + Constants.USERNAME + ";password=" + Constants.PASSWORD;
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(connectionString);
                isConnected = true;
            } catch (ClassNotFoundException e) {
                errorMessage = "Database driver not found";
                Log.d(TAG, "Database driver not found: " + e.getMessage());
            } catch (SQLException e) {
                errorMessage = "Failed to connect to database";
                Log.d(TAG, "Failed to connect to database: " + e.getMessage());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.d(TAG, "Failed to close database connection: " + e.getMessage());
                    }
                }
            }
            return isConnected;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss(); // Hide loading dialog

            if (result) {
                showToast("Connected to the database successfully!");
                new CheckUserTask(savedUsername, savedPassword).execute();
            } else {
                showToast(errorMessage);
                Intent i = new Intent(SeparateActivity.this, AddressActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private class CheckUserTask extends AsyncTask<Void, Void, Boolean> {
        String inputUsername, inputPassword;
        String userType;

        CheckUserTask(String inputUsername, String inputPassword) {
            this.inputUsername = inputUsername;
            this.inputPassword = inputPassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show(); // Show loading dialog
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isValid = false;
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 +
                    ";databaseName=" + databaseName1 + ";user=" + dbUsername1 + ";password=" + dbPassword1;
            try {
                Connection connection = DriverManager.getConnection(connectionUrl);
                Log.d(TAG, "Database connection established.");

                String query = "SELECT Type, Control FROM admin WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, inputUsername);
                preparedStatement.setString(2, inputPassword);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userType = resultSet.getString("Type");
                    isValid = true;
                    Log.d(TAG, "Login successful for user: " + inputUsername + ", Type: " + userType);
                } else {
                    Log.e(TAG, "Login failed for user: " + inputUsername);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                Log.e(TAG, "Database connection error: " + e.getMessage(), e);
            }
            return isValid;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            super.onPostExecute(isValid);
            progressDialog.dismiss(); // Hide loading dialog

            if (isValid) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_USERNAME, inputUsername);
                editor.putString(Constants.KEY_PASSWORD, inputPassword);
                editor.putString(Constants.KEY_USERTYPE, userType);
                editor.apply();

                Toast.makeText(SeparateActivity.this, "Login Successful as " + userType, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SeparateActivity.this, HomeActivityNew.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SeparateActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SeparateActivity.this, LoginActivityNew.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}