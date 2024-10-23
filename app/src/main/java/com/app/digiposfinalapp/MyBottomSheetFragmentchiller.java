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

public class MyBottomSheetFragmentchiller extends BottomSheetDialogFragment {

    private static final String TAG = "MyBottomSheetFragmentchiller";

    String id1, chillerName1, temperature1, date1, time1, changeId1, day1;

    EditText chillerName1edt, temperature1edt, id1edt, date1edt, time1edt, changeId1edt, day1edt;

    Button button;
    Connection connection;

    private String ipAddress, portNumber, databaseName, username, password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_edit_chiller, container, false);

        // Initialize SharedPreferences and get database connection details
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        id1edt = view.findViewById(R.id.autocompleteTextView);
        chillerName1edt = view.findViewById(R.id.autocompleteTextView2);
        temperature1edt = view.findViewById(R.id.autocompleteTextView24);
        date1edt = view.findViewById(R.id.autocompleteTextView246);
        time1edt = view.findViewById(R.id.autocompleteTextView24676);
        changeId1edt = view.findViewById(R.id.autocompleteTextView624676);
        day1edt = view.findViewById(R.id.dayedt);

        button = view.findViewById(R.id.update);

        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the BottomSheetDialogFragment
            }
        });

        // Set initial values if available
        chillerName1edt.setText(chillerName1);
        temperature1edt.setText(temperature1);
        id1edt.setText(id1);
        date1edt.setText(date1);
        time1edt.setText(time1);
        changeId1edt.setText(changeId1);
        day1edt.setText(day1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the AsyncTask to update the database
                new UpdateDatabaseTask(
                        chillerName1edt.getText().toString(),
                        temperature1edt.getText().toString(),
                        date1edt.getText().toString(),
                        time1edt.getText().toString(),
                        changeId1edt.getText().toString(),
                        day1edt.getText().toString()
                ).execute();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setDetails(Context context, String id, String chillerName, String temperature, String date, String time, String changeId, String day) {
        if (context != null) {
            id1 = id;
            chillerName1 = chillerName;
            temperature1 = temperature;
            date1 = date;
            time1 = time;
            changeId1 = changeId;
            day1 = day;
        } else {
            // Log a message if the context is null (for debugging)
            Log.e(TAG, "Context is null");
        }
    }

    private class UpdateDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private String chillerName, temperature, date, time, changeId, day;

        UpdateDatabaseTask(String chillerName, String temperature, String date, String time, String changeId, String day) {
            this.chillerName = chillerName;
            this.temperature = temperature;
            this.date = date;
            this.time = time;
            this.changeId = changeId;
            this.day = day;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Establish database connection
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                connection = DriverManager.getConnection(connectURL);

                // Create SQL update statement
                String updateStatement = "UPDATE Chiller_Report SET chiller_name = ?, temperature = ?, date = ?, time = ?, Changeid = ?, day = ? WHERE ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateStatement);

                // Set values for placeholders
                preparedStatement.setString(1, chillerName);
                preparedStatement.setString(2, temperature);
                preparedStatement.setString(3, date);
                preparedStatement.setString(4, time);
                preparedStatement.setString(5, changeId);
                preparedStatement.setString(6, day);
                preparedStatement.setString(7, id1); // Assuming id is the primary key

                // Execute the update statement
                int rowsUpdated = preparedStatement.executeUpdate();
                connection.close();
                return rowsUpdated > 0;
            } catch (Exception e) {
                Log.e(TAG, "Exception occurred: ", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Log.d(TAG, "Data updated successfully");
                Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Failed to update data");
                Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
