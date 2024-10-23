package com.app.digiposfinalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class AddDepartment extends AppCompatActivity {

    // Declare EditText fields
    EditText departmentEditText;
    EditText ageEditText;
    EditText saleEditText;
    EditText weightEditText;
    EditText pointsEditText;

    String ipAddress, portNumber, databaseName, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_department);


        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize EditText fields
        departmentEditText = findViewById(R.id.autocompleteTextView2);
        ageEditText = findViewById(R.id.autocompleteTextView24);
        saleEditText = findViewById(R.id.autocompleteTextView246);
        weightEditText = findViewById(R.id.autocompleteTextView24676);
        pointsEditText = findViewById(R.id.autocompleteTextView624676);

        Button saveButton = findViewById(R.id.update);
        saveButton.setOnClickListener(v -> {
            if (isInputValid()) {
                new InsertDataTask().execute(
                        departmentEditText.getText().toString(),
                        ageEditText.getText().toString(),
                        saleEditText.getText().toString(),
                        weightEditText.getText().toString(),
                        pointsEditText.getText().toString()
                );
            } else {
                Toast.makeText(AddDepartment.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private boolean isInputValid() {
        return !departmentEditText.getText().toString().isEmpty() &&
                !ageEditText.getText().toString().isEmpty() &&
                !saleEditText.getText().toString().isEmpty() &&
                !weightEditText.getText().toString().isEmpty() &&
                !pointsEditText.getText().toString().isEmpty();
    }

    private class InsertDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String department = params[0];
            String age = params[1];
            int sale = Integer.parseInt(params[2]);
            int weight = Integer.parseInt(params[3]);
            int points = Integer.parseInt(params[4]);

            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                    ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;

            try (Connection connection = DriverManager.getConnection(connectionUrl);
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO tbl_Departments (Department, Age, Num, NoShop, Points) VALUES (?, ?, ?, ?, ?)")) {

                preparedStatement.setString(1, department);
                preparedStatement.setString(2, age);
                preparedStatement.setInt(3, sale);
                preparedStatement.setInt(4, weight);
                preparedStatement.setInt(5, points);

                int rowsInserted = preparedStatement.executeUpdate();

                return rowsInserted > 0 ? "Data inserted successfully" : "No data inserted";

            } catch (SQLException e) {
                return "Database error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AddDepartment.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    public void gobackadd(View view) {
        onBackPressed();
    }
}
