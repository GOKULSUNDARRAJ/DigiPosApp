package com.app.digiposfinalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginActivityNew extends AppCompatActivity {

    private static final String TAG = "LoginActivityNew";
    EditText Usernameedt, Passwordedt;
    Button Loginbtn;

    String ipAddress, portNumber, databaseName, dbUsername, dbPassword;
    String savedUsername, savedPassword;

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
        dbUsername = Constants.USERNAME;
        dbPassword = Constants.PASSWORD;
        savedUsername = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        savedPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, null);

        // Auto-fill if credentials exist
        if (savedUsername != null) {
            Usernameedt.setText(savedUsername);
        }
        if (savedPassword != null) {
            Passwordedt.setText(savedPassword);
        }

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
                    Log.d(TAG, "Attempting to login with username: " + inputUsername);
                    new CheckUserTask(inputUsername, inputPassword).execute();
                }
            }
        });

        // Keyboard handling code remains the same...
    }

    private class CheckUserTask extends AsyncTask<Void, Void, User> {
        String inputUsername, inputPassword;

        CheckUserTask(String inputUsername, String inputPassword) {
            this.inputUsername = inputUsername;
            this.inputPassword = inputPassword;
        }

        @Override
        protected User doInBackground(Void... voids) {
            User user = null;
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                    ";databaseName=" + databaseName + ";user=" + dbUsername + ";password=" + dbPassword;

            try {
                Connection connection = DriverManager.getConnection(connectionUrl);
                Log.d(TAG, "Database connection established.");

                // Query to get all user details
                String query = "SELECT ID, username, password, Type, Control, done FROM admin WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, inputUsername);
                preparedStatement.setString(2, inputPassword);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Create User object with all details
                    user = new User(
                            resultSet.getInt("ID"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("Type"),
                            resultSet.getString("Control"),
                            resultSet.getInt("done")
                    );
                    Log.d(TAG, "User found: " + user.toString());
                } else {
                    Log.e(TAG, "Login failed for user: " + inputUsername);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                Log.e(TAG, "Database connection error: " + e.getMessage(), e);
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                // Save all user details in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Save all relevant user information
                editor.putInt(Constants.KEY_USER_ID, user.getId());
                editor.putString(Constants.KEY_USERNAME, user.getUsername());
                editor.putString(Constants.KEY_PASSWORD, user.getPassword());
                editor.putString(Constants.KEY_USERTYPE, user.getType());
                editor.putString(Constants.KEY_CONTROL, user.getControl());
                editor.putInt(Constants.KEY_DONE, user.getDone());
                editor.apply();

                Toast.makeText(LoginActivityNew.this, "Login Successful as " + user.getType(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivityNew.this, HomeActivityNew.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivityNew.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Simple User class to hold all user details
    private class User {
        private int id;
        private String username;
        private String password;
        private String type;
        private String control;
        private int done;

        public User(int id, String username, String password, String type, String control, int done) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.type = type;
            this.control = control;
            this.done = done;
        }

        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getType() { return type; }
        public String getControl() { return control; }
        public int getDone() { return done; }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", type='" + type + '\'' +
                    ", control='" + control + '\'' +
                    ", done=" + done +
                    '}';
        }
    }
}