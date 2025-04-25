package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TemperatureLogsFragment extends Fragment {

    EditText startdate;
    AutoCompleteTextView timeAutoComplete;
    AutoCompleteTextView chillernameSpinner;
    private int chillerId; // To store selected chiller ID

    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;

    private EditText temperatureInput, noteInput;

    private String selectedChillerName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_logs, container, false);


        ImageView home = view.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });
        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password

        startdate = view.findViewById(R.id.startdate);
        String currentDate = getCurrentDate();
        startdate.setText(currentDate);

        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false;

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            startdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                }
            }
        });

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                }
            }
        });

        // Set current time in AutoCompleteTextView
        timeAutoComplete = view.findViewById(R.id.spinner_time);

        // Set up the adapter for time suggestions
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.railway_time_array, android.R.layout.simple_list_item_1);

        timeAutoComplete.setAdapter(timeAdapter);
        timeAutoComplete.setThreshold(1);

        timeAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedTime = (String) parent.getItemAtPosition(position);
            // Handle the selected time (optional)
        });

        timeAutoComplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                timeAutoComplete.showDropDown(); // Show all items when focused
            }
        });
        timeAutoComplete.setOnClickListener(v -> timeAutoComplete.showDropDown()); // Show all items when clicked

        ImageView back = view.findViewById(R.id.imageViewback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment priceSubFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Button addButton = view.findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogforTemplogs cdd = new CustomDialogforTemplogs(getContext(), getChildFragmentManager());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();
            }
        });

        chillernameSpinner = view.findViewById(R.id.chillernameSpinner);

        temperatureInput = view.findViewById(R.id.tempedt);

        noteInput = view.findViewById(R.id.notesedt);

        Button saveButton = view.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTemperatureLog();
            }
        });

        // Update your chiller spinner item click listener
        chillernameSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChillerSpinner selectedChiller = (ChillerSpinner) parent.getItemAtPosition(position);
                chillerId = selectedChiller.getId();
                selectedChillerName = selectedChiller.getChillerName();
                Toast.makeText(getContext(), "Selected: " + selectedChillerName, Toast.LENGTH_SHORT).show();
            }
        });

        chillernameSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                chillernameSpinner.showDropDown(); // Show all items when focused
            }
        });

        chillernameSpinner.setOnClickListener(v -> chillernameSpinner.showDropDown());

        // Fetch chiller data
        new FetchChillerData2(getContext(), chillernameSpinner).execute();

        return view;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


    private void saveTemperatureLog() {
        // Get all input values
        String temperatureStr = temperatureInput.getText().toString().trim();
        String note = noteInput.getText().toString().trim();
        String date = startdate.getText().toString();
        String time = timeAutoComplete.getText().toString();
        String day = new SimpleDateFormat("EEEE", Locale.getDefault()).format(Calendar.getInstance().getTime());

        SharedPreferences prefs = getContext().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        int userId = prefs.getInt(Constants.KEY_USER_ID, -1);

        String username = prefs.getString(Constants.KEY_USERNAME, "");
        String userType = prefs.getString(Constants.KEY_USERTYPE, "");
        String control = prefs.getString(Constants.KEY_CONTROL, "");
        int done = prefs.getInt(Constants.KEY_DONE, 0);
        String changeId = String.valueOf(userId); // Default value or get from your system

        // Validate inputs
        if (chillerId == 0) {
            Toast.makeText(getContext(), "Please select a unit", Toast.LENGTH_SHORT).show();
            return;
        }

        if (temperatureStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter temperature", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate temperature format
        float temperature;
        try {
            temperature = Float.parseFloat(temperatureStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid temperature number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving temperature log...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Execute the insert operation with float value
        new InsertTemperatureLogTask(progressDialog).execute(String.valueOf(chillerId), selectedChillerName, String.valueOf(temperature), // Convert back to string for AsyncTask params
                convertToDatabaseFormat(date), time, changeId, day, note);
    }

    private String convertToDatabaseFormat(String displayDate) {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = displayFormat.parse(displayDate);
            return dbFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // or handle error appropriately
        }
    }

    private class InsertTemperatureLogTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        public InsertTemperatureLogTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected String doInBackground(String... params) {
            Connection connection = null;
            try {
                // Load the JDBC driver
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                // Create connection string
                String connectionString = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1 + ";user=" + dbUsername1 + ";password=" + dbPassword1;

                // Establish connection
                connection = DriverManager.getConnection(connectionString);

                // Prepare SQL statement
                // Modified SQL statement
                String sql = "INSERT INTO [STAR_RETAIL].[dbo].[Chiller_Report] " + "([Chiller_Name], [Temperature], [Date], [Time], " + "[Changeid], [Day], [Note]) VALUES (?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, params[1]); // Chiller_Name
                statement.setString(2, params[2]); // Temperature
                statement.setString(3, params[3]); // Date
                statement.setString(4, params[4]); // Time
                statement.setString(5, params[5]); // Changeid
                statement.setString(6, params[6]); // Day
                statement.setString(7, params[7]); // Note

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0 ? "SUCCESS" : "FAILED";

            } catch (ClassNotFoundException e) {
                return "JDBC driver not found: " + e.getMessage();
            } catch (SQLException e) {
                return "Database error: " + e.getMessage();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if ("SUCCESS".equals(result)) {
                Toast.makeText(getContext(), "Temperature log saved successfully", Toast.LENGTH_SHORT).show();
                // Clear fields
                temperatureInput.setText("");
                noteInput.setText("");

                HomeFragment priceSubFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                // Optionally refresh your data or UI
            } else {
                String errorMessage = result.startsWith("Error") ? result : "Failed to save temperature log";
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                Log.e("TemperatureLog", "Error saving log: " + result);

            }
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            // Disable back press
            ((AppCompatActivity) context).getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Do nothing to prevent back press
                }
            });
        }
    }



    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Format yyyy-MM-dd
                        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                                year, monthOfYear + 1, dayOfMonth);
                        startdate.setText(formattedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

}