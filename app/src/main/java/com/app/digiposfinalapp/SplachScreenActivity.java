package com.app.digiposfinalapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SplachScreenActivity extends AppCompatActivity {
    String ipAddress, portNumber;

    private static final String TAG = "LagCat";

    String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    String savedUsername,savedPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splach_screen);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password
        savedUsername = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        savedPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, null);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences2 = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                ipAddress = sharedPreferences2.getString(Constants.KEY_IP, "");
                portNumber = sharedPreferences2.getString(Constants.KEY_PORT, "");

                try {
                    new SplachScreenActivity.ConnectToDatabaseTask().execute(ipAddress,portNumber);
                }catch (Exception e){

                }
            }
        }, 1000);


    }

    private class ConnectToDatabaseTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                Intent i = new Intent(SplachScreenActivity.this, AddressActivity.class);
                startActivity(i);
                finish();
            } catch (SQLException e) {
                errorMessage = "Failed to connect to database";
                Intent i = new Intent(SplachScreenActivity.this, AddressActivity.class);
                startActivity(i);
                finish();
                Log.d(TAG, "Failed to connect to database: " + e.getMessage());
            } finally {
                // Ensure connection is closed
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {


                        Intent i = new Intent(SplachScreenActivity.this, AddressActivity.class);
                        startActivity(i);
                        finish();

                        Log.d(TAG, "Failed to close database connection: " + e.getMessage());
                    }
                }
            }

            return isConnected;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);


            if (result) {
                showToast("Connected to the database successfully!");
                new SplachScreenActivity.CheckUserTask(savedUsername, savedPassword).execute();
            } else {
                showToast(errorMessage);
            }
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    // AsyncTask to perform database operation in background
    private class CheckUserTask extends AsyncTask<Void, Void, Boolean> {
        String inputUsername, inputPassword;
        String userType;

        CheckUserTask(String inputUsername, String inputPassword) {
            this.inputUsername = inputUsername;
            this.inputPassword = inputPassword;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isValid = false;
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 +
                    ";databaseName=" + databaseName1 + ";user=" + dbUsername1 + ";password=" + dbPassword1;

            try {
                // Establish connection to the database
                Connection connection = DriverManager.getConnection(connectionUrl);
                Log.d(TAG, "Database connection established.");

                // Query to check if the username and password exist in the database
                String query = "SELECT Type, Control FROM admin WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, inputUsername);
                preparedStatement.setString(2, inputPassword);

                // Execute the query
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Extract user type and control
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
            if (isValid) {
                // Save username and password in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_USERNAME, inputUsername);
                editor.putString(Constants.KEY_PASSWORD, inputPassword); // Save plain text password (consider hashing)
                editor.apply(); // Save changes

                // If the username exists, navigate to HomeFragment
                Toast.makeText(SplachScreenActivity.this, "Login Successful as " + userType, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplachScreenActivity.this, HomeActivityNew.class);
                startActivity(intent);
                finish();
            } else {
                // Show error if the username or password is incorrect
                Toast.makeText(SplachScreenActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplachScreenActivity.this, LoginActivityNew.class);
                startActivity(intent);
            }
        }
    }
}