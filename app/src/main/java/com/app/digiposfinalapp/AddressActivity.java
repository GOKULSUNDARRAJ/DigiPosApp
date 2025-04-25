package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AddressActivity extends AppCompatActivity {

    private static final String TAG = "LagCat";

    private EditText ipEditText;
    private EditText portEditText;
    private Button saveButton;
    private ProgressBar progressBar;
    String ipAddress, portNumber;

    String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    String savedUsername, savedPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password
        savedUsername = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        savedPassword = sharedPreferences.getString(Constants.KEY_PASSWORD, null);

        progressBar = findViewById(R.id.progressbar);
        ipEditText = findViewById(R.id.ipedt);
        portEditText = findViewById(R.id.postedt);
        saveButton = findViewById(R.id.save);

        SharedPreferences sharedPreferences3 = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String savedIp = sharedPreferences3.getString(Constants.KEY_IP, "");
        String savedPort = sharedPreferences3.getString(Constants.KEY_PORT, "");
        ipEditText.setText(savedIp);
        portEditText.setText(savedPort);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipEditText.getText().toString().trim();
                String port = portEditText.getText().toString().trim();

                if (!validateIPAddress(ip)) {
                    showToast("Invalid IP address. Please enter a valid IP.");
                    return;
                }

                if (!validatePort(port)) {
                    showToast("Invalid port number. Please enter a valid port (1-65535).");
                    return;
                }

                // Save IP and port to SharedPreferences
                savePreferences(ip, port);

                new ConnectToDatabaseTask().execute(ip, port);
            }
        });

        SharedPreferences sharedPreferences2 = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences2.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences2.getString(Constants.KEY_PORT, "");



        ipEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                v.clearFocus(); // Hide keyboard
                return true;
            }
            return false;
        });

        portEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                v.clearFocus(); // Hide keyboard
                return true;
            }
            return false;
        });


    }

    private class ConnectToDatabaseTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String ip = params[0];
            String port = params[1];
            Connection connection = null;
            boolean isConnected = false;

            try {
                // Building connection string
                StringBuilder connectionStringBuilder = new StringBuilder();
                connectionStringBuilder.append("jdbc:jtds:sqlserver://")
                        .append(ip).append(":").append(port)
                        .append(";databaseName=").append(Constants.DATABASE_NAME)
                        .append(";user=").append(Constants.USERNAME)
                        .append(";password=").append(Constants.PASSWORD);

                String connectionString = connectionStringBuilder.toString();
                Log.d(TAG, "Attempting to connect using: " + connectionString);  // Log connection string without credentials

                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(connectionString);
                isConnected = true;

            } catch (ClassNotFoundException e) {
                errorMessage = "Database driver not found: " + e.getMessage();
                Log.d(TAG, errorMessage);
            } catch (SQLException e) {
                errorMessage = "Failed to connect to database: " + e.getMessage() +
                        "\nSQLState: " + e.getSQLState() +
                        "\nErrorCode: " + e.getErrorCode();
                Log.d(TAG, errorMessage);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.d(TAG, "Failed to close database connection:" + e.getMessage());
                    }
                }
            }

            return isConnected;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            saveButton.setVisibility(View.VISIBLE);

            if (result) {
                showToast("Connected to the database successfully!");
                Intent intent = new Intent(AddressActivity.this, LoginActivityNew.class);
                startActivity(intent);
                finish();
            } else {
                showToast("Connection failed: " + errorMessage);
            }
        }
    }

    private void savePreferences(String ip, String port) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_IP, ip);
        editor.putString(Constants.KEY_PORT, port);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateIPAddress(String ip) {
        String ipPattern =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    private boolean validatePort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber >= 1 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
