package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {

    EditText departmentedt, age, scaledt, weigthedt, pointsedit, idetd;
    String dpt, age1, scal, weight1, points1, id1;
    Button button;

    String ipAddress, portNumber, databaseName, username, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_edit, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        departmentedt = view.findViewById(R.id.autocompleteTextView2);
        age = view.findViewById(R.id.autocompleteTextView24);
        scaledt = view.findViewById(R.id.autocompleteTextView246);
        weigthedt = view.findViewById(R.id.autocompleteTextView24676);
        pointsedit = view.findViewById(R.id.autocompleteTextView624676);
        idetd = view.findViewById(R.id.autocompleteTextView);

        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(v -> dismiss()); // Close the BottomSheetDialogFragment

        departmentedt.setText(dpt);
        age.setText(age1);
        scaledt.setText(scal);
        weigthedt.setText(weight1);
        pointsedit.setText(points1);
        idetd.setText(id1);

        button = view.findViewById(R.id.update);
        button.setOnClickListener(view1 -> {
            if (departmentedt.getText().toString().isEmpty() || age.getText().toString().isEmpty() || scaledt.getText().toString().isEmpty() || weigthedt.getText().toString().isEmpty() || pointsedit.getText().toString().isEmpty() || idetd.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                new UpdateDataTask().execute(
                        departmentedt.getText().toString(),
                        age.getText().toString(),
                        Integer.parseInt(scaledt.getText().toString()),
                        Integer.parseInt(weigthedt.getText().toString()),
                        Integer.parseInt(pointsedit.getText().toString()),
                        Integer.parseInt(idetd.getText().toString())
                );
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setDetails(Context context, String department, String age, int sale, int weight, int points, int id) {
        // Check if the context is null
        if (context != null) {
            // Create a message string using the passed parameters
            dpt = department;
            age1 = age;
            scal = String.valueOf(sale);
            weight1 = String.valueOf(weight);
            points1 = String.valueOf(points);
            id1 = String.valueOf(id);

            // Show the message in a Toast
            Toast.makeText(context, dpt + id1, Toast.LENGTH_SHORT).show();
        } else {
            // Log a message if the context is null (for debugging)
            Log.e("MyBottomSheetFragment", "Context is null");
        }
    }

    private class UpdateDataTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            String department = (String) params[0];
            String age = (String) params[1];
            int sale = (int) params[2];
            int weight = (int) params[3];
            int points = (int) params[4];
            int id = (int) params[5];

            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
            try (Connection connection = DriverManager.getConnection(connectionUrl);
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "UPDATE tbl_Departments SET Department = ?, Age = ?, Num = ?, NoShop = ?, Points = ? WHERE ID = ?")) {

                preparedStatement.setString(1, department);
                preparedStatement.setString(2, age);
                preparedStatement.setInt(3, sale);
                preparedStatement.setInt(4, weight);
                preparedStatement.setInt(5, points);
                preparedStatement.setInt(6, id);

                int rowsUpdated = preparedStatement.executeUpdate();

                return rowsUpdated > 0 ? "Data updated successfully" : "No data updated";

            } catch (SQLException e) {
                return "Database error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
