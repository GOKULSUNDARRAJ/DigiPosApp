package com.app.digiposfinalapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class ChillerAddActivity extends AppCompatActivity {

    private EditText chillerNameEditText, temperatureEditText, dateEditText, timeEditText, changeIdEditText, dayEditText;
    private Button addButton;

    private Connection connection;

    String ipAddress, portNumber, databaseName, username, password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chiller_add);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;


        // Initialize views
        addButton = findViewById(R.id.update);
        chillerNameEditText = findViewById(R.id.autocompleteTextView2);
        temperatureEditText = findViewById(R.id.autocompleteTextView24);
        dateEditText = findViewById(R.id.autocompleteTextView246);
        timeEditText = findViewById(R.id.autocompleteTextView24676);
        changeIdEditText = findViewById(R.id.autocompleteTextView624676);
        dayEditText = findViewById(R.id.dayedt);

        // Set OnClickListener for the "Add" button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the entered data
                String chillerName = chillerNameEditText.getText().toString();
                String temperature = temperatureEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();
                String changeId = changeIdEditText.getText().toString();
                String day = dayEditText.getText().toString();

                // If input data is valid, insert the new row
                if (isValid(chillerName, temperature, date, time, changeId, day)) {
                    new InsertDataTask().execute(chillerName, temperature, date, time, changeId, day);
                } else {
                    Toast.makeText(ChillerAddActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to validate input data
    private boolean isValid(String chillerName, String temperature, String date, String time, String changeId, String day) {
        return !chillerName.isEmpty() && !temperature.isEmpty() && !date.isEmpty() && !time.isEmpty() && !changeId.isEmpty() && !day.isEmpty();
    }

    private class InsertDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String chillerName = params[0];
            String temperature = params[1];
            String date = params[2];
            String time = params[3];
            String changeId = params[4];
            String day = params[5];

            Connection conn = null;
            try {
                if (conn == null) {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                    conn = DriverManager.getConnection(connectURL);
                }

                if (conn != null) {
                    String insertStatement = "INSERT INTO Chiller_Report (chiller_name, temperature, date, time, Changeid, day) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(insertStatement);
                    preparedStatement.setString(1, chillerName);
                    preparedStatement.setString(2, temperature);
                    preparedStatement.setString(3, date);
                    preparedStatement.setString(4, time);
                    preparedStatement.setString(5, changeId);
                    preparedStatement.setString(6, day);
                    int rowsInserted = preparedStatement.executeUpdate();
                    return rowsInserted > 0;
                }
            } catch (Exception e) {
                Log.e("Error", "Database operation failed: " + e.getMessage());
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    Log.e("Error", "Failed to close database connection: " + e.getMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(ChillerAddActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChillerAddActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
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
