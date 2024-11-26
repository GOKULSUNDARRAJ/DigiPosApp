package com.app.digiposfinalapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ShiftReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private LogAdapter logAdapter;
    private List<LogEntry> logList;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;

    Spinner tillNoSpinner;
    EditText startdate, enddatedt;
    String tillNo;
    int tillId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shift_report, container, false);

        // Find the NestedScrollView
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Get the BottomNavigationView from the MainActivity
        ConstraintLayout bottomNavigationView = view.findViewById(R.id.bottom_navigation1);

        // Add a scroll listener to the NestedScrollView
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check if we're scrolling down
                if (scrollY > oldScrollY) {
                    // Hide the BottomNavigationView when scrolling down
                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(300);
                } else if (scrollY < oldScrollY) {
                    // Show the BottomNavigationView when scrolling up
                    bottomNavigationView.animate().translationY(0).setDuration(300);
                }
            }
        });



        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Fetch data from the database with filters
        startdate = view.findViewById(R.id.startdate);

        enddatedt = view.findViewById(R.id.enddate);

        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // on below line we are getting
                    // the instance of our calendar.
                    final Calendar c = Calendar.getInstance();
                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Use String.format to ensure proper zero-padding for month and day
                            String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                            startdate.setText(formattedDate); // For start date field
                        }

                    },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();
                }
            }
        });



        enddatedt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // on below line we are getting
                    // the instance of our calendar.
                    final Calendar c = Calendar.getInstance();
                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                            enddatedt.setText(formattedDate); // For end date field
                        }

                    },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();
                }
            }
        });



        tillNoSpinner = view.findViewById(R.id.spinner_tillno);

        new FetchTillNoData(getContext(), tillNoSpinner).execute();

        tillNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TillNoType selectedTillNoType = (TillNoType) parent.getItemAtPosition(position);
                tillId = selectedTillNoType.getId(); // Get the ID of the selected TillNo
                tillNo = selectedTillNoType.getTillNo(); // Get the TillNo value
                String workshop = selectedTillNoType.getWorkshop(); // Get the Workshop value

                // Display selected TillNo information
                // Optionally, perform additional actions based on the selected TillNo
                // For example, you could save the selected TillNo's ID or value for further use
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
                Toast.makeText(parent.getContext(), "No Till No selected", Toast.LENGTH_SHORT).show();
            }
        });



        view.findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String originalDate = startdate.getText().toString().trim(); // Trim to remove extra spaces
                String modifiedDate = originalDate.replace("-", ""); // Replace dashes with empty string

                String originalDateend = enddatedt.getText().toString().trim(); // Trim to remove extra spaces
                String modifiedDateend = originalDateend.replace("-", ""); // Replace dashes with empty string


                new FetchDataAsyncTask().execute(modifiedDate, modifiedDateend, String.valueOf(tillId));

            }
        });



        return view;
    }

    private class FetchDataAsyncTask extends AsyncTask<String, Void, List<LogEntry>> {

        @Override
        protected List<LogEntry> doInBackground(String... params) {
            List<LogEntry> logEntries = new ArrayList<>();

            // Get the filter values
            String startdate = params[0];
            String enddate = params[1];
            String tillno = params[2];

            // Update query to filter by startdate, enddate, and tillno
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;
            String query = "SELECT * FROM Logs WHERE ShiftDate BETWEEN ? AND ? AND TillNo = ?";

            try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, startdate);
                preparedStatement.setString(2, enddate);
                preparedStatement.setString(3, tillno);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {

                    int id = resultSet.getInt("ID");
                    String logID = resultSet.getString("LogID");
                    String userID = resultSet.getString("UserID");
                    String transID = resultSet.getString("TransID");
                    String shiftStartTime = resultSet.getString("ShiftStartTime");
                    String shiftEndTime = resultSet.getString("ShiftEndTime");
                    String shiftDate = resultSet.getString("ShiftDate");
                    String shiftEndDate = resultSet.getString("ShiftEndDate");
                    int tillNo = resultSet.getInt("TillNo");
                    String states = resultSet.getString("States");
                    String username = resultSet.getString("Username");
                    String done = resultSet.getString("done");

                    logEntries.add(new LogEntry(id, logID, userID, transID, shiftStartTime, shiftEndTime, shiftDate, shiftEndDate, tillNo, states, username, done));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return logEntries;
        }


        @Override
        protected void onPostExecute(List<LogEntry> logEntries) {
            logList = logEntries;

            if (logList.isEmpty()) {
                // Display a toast if no records are found
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "No records found for the selected filters", Toast.LENGTH_SHORT).show();
            } else {
                logAdapter = new LogAdapter(logList, getContext());
                recyclerView.setAdapter(logAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

    }
}
