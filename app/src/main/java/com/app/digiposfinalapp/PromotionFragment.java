package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PromotionFragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener {

    private CustomSpinner spinner_fruits;
    private FruitAdapter statusadapter;
    private static final String TAG = "PromotionFragment";

    RecyclerView recyclerView;
    PromotionAdapter adapter;
    List<PromotionModel> promotionList;
    Connection connection;
    String ipAddress, portNumber, databaseName, username, password;
    private ExecutorService executorService;


    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_promotion, container, false);

        imageView=view.findViewById(R.id.imageView);

        spinner_fruits = view.findViewById(R.id.spinner_fruits);
        spinner_fruits.setSpinnerEventsListener(this);

        statusadapter = new FruitAdapter(getContext(), StatusData.getFruitList());
        spinner_fruits.setAdapter(statusadapter);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = view.findViewById(R.id.recyclerView);
        promotionList = new ArrayList<>();
        FragmentManager fragmentManager =getParentFragmentManager();;
        adapter = new PromotionAdapter(promotionList, fragmentManager);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Fetch data from the database and populate the RecyclerView
        fetchDataFromDatabase();



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment productManagementFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        // on below line we are initializing our variables.
        EditText pickDateBtn = view.findViewById(R.id.startdate);
        // on below line we are adding click listener for our pick date button
        pickDateBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our text view.

                                    pickDateBtn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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


        // on below line we are initializing our variables.
        EditText enddate =view.findViewById(R.id.enddate);

        enddate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our text view.

                                    enddate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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


        return view;
    }

    private void fetchDataFromDatabase() {
        executorService.execute(() -> {
            if (connection == null) {
                // Establish database connection
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                    connection = DriverManager.getConnection(connectURL);
                } catch (ClassNotFoundException | SQLException e) {
                    requireActivity().runOnUiThread(() -> Log.e(TAG, "Failed to establish database connection: " + e.getMessage()));
                    return;
                }
            }

            try {
                String sqlStatement = "SELECT ID, PromoID, Description, Receipt, Ruleno, RuleValue, Type, TypeValue, Start, Enddate, Item_Count, PLU, done FROM tbl_Promotion";
                Statement smt = connection.createStatement();
                ResultSet set = smt.executeQuery(sqlStatement);

                // Clear existing data
                promotionList.clear();

                // Populate promotionList with data from ResultSet
                while (set.next()) {
                    PromotionModel promotion = new PromotionModel();
                    promotion.setId(Integer.parseInt(set.getString("ID")));
                    promotion.setPromoId(Integer.parseInt(set.getString("PromoID")));
                    promotion.setDescription(set.getString("Description"));
                    promotion.setReceipt(set.getString("Receipt"));
                    promotion.setRuleNo(set.getString("Ruleno"));
                    promotion.setRuleValue(Double.parseDouble(set.getString("RuleValue")));
                    promotion.setType(set.getString("Type"));
                    promotion.setTypeValue(Double.parseDouble(set.getString("TypeValue")));
                    promotion.setStart(set.getString("Start"));
                    promotion.setEnd(set.getString("Enddate"));
                    promotion.setItemCount(Integer.parseInt(set.getString("Item_Count")));
                    promotion.setPlu(set.getString("PLU"));
                    promotion.setDone(Integer.parseInt(set.getString("done")));

                    // Set other data accordingly
                    promotionList.add(promotion);
                }

                requireActivity().runOnUiThread(() -> {
                    adapter.notifyDataSetChanged(); // Notify adapter of data change
                });

                // Close connection after use
                connection.close();
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinner_fruits.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        spinner_fruits.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit));
    }


}
