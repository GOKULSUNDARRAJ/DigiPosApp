package com.app.digiposfinalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginActivityNew extends AppCompatActivity {

    private static final String TAG = "LoginActivityNew"; // Tag for logging
    EditText Usernameedt, Passwordedt;
    Button Loginbtn;

    String ipAddress, portNumber, databaseName, dbUsername, dbPassword;
    String savedUsername,savedPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        Usernameedt = findViewById(R.id.username);
        Passwordedt = findViewById(R.id.password);
        Loginbtn = findViewById(R.id.loginbtn);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        dbUsername = Constants.USERNAME;  // Use actual database username
        dbPassword = Constants.PASSWORD;  // Use actual database password
        savedUsername = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        savedPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, null);


        Log.d(TAG, "Database Connection Details: " + ipAddress + ":" + portNumber);

        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = Usernameedt.getText().toString().trim();
                String inputPassword = Passwordedt.getText().toString().trim();

                if (inputUsername.isEmpty()) {
                    Toast.makeText(LoginActivityNew.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Login attempt with empty username");
                } else if (inputPassword.isEmpty()) {
                    Toast.makeText(LoginActivityNew.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Login attempt with empty password");
                } else {
                    // Perform login in the background
                    Log.d(TAG, "Attempting to login with username: " + inputUsername);
                    new CheckUserTask(inputUsername, inputPassword).execute();
                }
            }
        });
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
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                    ";databaseName=" + databaseName + ";user=" + dbUsername + ";password=" + dbPassword;

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
                Toast.makeText(LoginActivityNew.this, "Login Successful as " + userType, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivityNew.this, HomeActivityNew.class);
                startActivity(intent);
                finish();
            } else {
                // Show error if the username or password is incorrect
                Toast.makeText(LoginActivityNew.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
