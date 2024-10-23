package com.app.digiposfinalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {


    String ipAddress, portNumber, databaseName, username, password;

    private static final String TAG = "SQLConnection";


    RecyclerView recyclerView;
    DepartmentAdapter departmentAdapter;
    List<DepartmentModel> departmentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        recyclerView = findViewById(R.id.recyclerView); // Ensure this ID matches your layout file

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        departmentAdapter = new DepartmentAdapter(departmentList);
        recyclerView.setAdapter(departmentAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        new DatabaseTask().execute();

    }

    public void gotoback(View view) {
        onBackPressed();
    }


    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + ";databaseName=" + databaseName;

            try (Connection connection = DriverManager.getConnection(connectionUrl, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM tbl_Departments")) {

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("ID");
                        String age = resultSet.getString("Age");
                        String department = resultSet.getString("Department");
                        int num = resultSet.getInt("Num");
                        int noshop = resultSet.getInt("Noshop");
                        int points = resultSet.getInt("Points");
                        int done = resultSet.getInt("Done");
                        String image = resultSet.getString("Image");

                        DepartmentModel departmentModel = new DepartmentModel(id,age, department, num, noshop, points, done, image);
                        departmentList.add(departmentModel);
                    }
                }

            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Database error", Toast.LENGTH_SHORT).show());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            departmentAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Data loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoAdddept(View view) {
        startActivity(new Intent(view.getContext(),AddDepartment.class));
    }
}

