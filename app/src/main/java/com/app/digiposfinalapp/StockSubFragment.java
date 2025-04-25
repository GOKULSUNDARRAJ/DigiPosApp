package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockSubFragment extends Fragment {

    LinearLayout cardView1, cardView2, cardView3, cardView4; // Added cardView3
    private SharedPreferences sharedPreferences;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_sub, container, false);




        // SharedPreferences for DB connection
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;
        dbPassword1 = Constants.PASSWORD;



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

        // Initialize card views
        cardView1 = view.findViewById(R.id.card1);
        cardView2 = view.findViewById(R.id.card2);
        cardView3 = view.findViewById(R.id.card3);
        cardView4 = view.findViewById(R.id.card4);// Added cardView3

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("user_preferences", MODE_PRIVATE);

        // Get values from SharedPreferences
        boolean stockInBy = sharedPreferences.getBoolean("stock_in_by_products", false);
        boolean stockTakes = sharedPreferences.getBoolean("stock_takes", false);
        boolean card3Visibility = sharedPreferences.getBoolean("card3_visibility", false); // Added card3Visibility

        boolean card4 = sharedPreferences.getBoolean("card4_visibility", false); // Added card3Visibility

        // Set visibility of card views based on SharedPreferences values
        if (stockInBy) {
            cardView1.setVisibility(View.GONE);
        } else {
            cardView1.setVisibility(View.GONE);
        }

        if (stockTakes) {
            cardView2.setVisibility(View.VISIBLE);
        } else {
            cardView2.setVisibility(View.GONE);
        }

        if (card3Visibility) { // Added condition for cardView3 visibility
            cardView3.setVisibility(View.VISIBLE);
        } else {
            cardView3.setVisibility(View.GONE);
        }

        if (card4) { // Added condition for cardView3 visibility
            cardView4.setVisibility(View.VISIBLE);
        } else {
            cardView4.setVisibility(View.GONE);
        }


        // OnClick listener for cardView1
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockInByProductsFragment productManagementFragment = new StockInByProductsFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // OnClick listener for cardView2
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockTakesFragment productManagementFragment = new StockTakesFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // OnClick listener for cardView3 (example)
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You can replace with the fragment of your choice
                StockadjustmentsearchFragment anotherFragment = new StockadjustmentsearchFragment(); // Replace with your fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, anotherFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(() -> {
                    String lastBatchId = null;
                    int lastStatus = -1;

                    String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;

                    try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
                        String sql = "SELECT TOP 1 BatchID, Status FROM [STAR_RETAIL].[dbo].[StockSnapShot] ORDER BY ID DESC";
                        try (PreparedStatement statement = connection.prepareStatement(sql);
                             ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet.next()) {
                                lastBatchId = resultSet.getString("BatchID");
                                lastStatus = resultSet.getInt("Status");
                            }
                        }
                    } catch (SQLException e) {
                        Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
                    }

                    if (lastStatus == 0 && lastBatchId != null) {
                        String finalLastBatchId = lastBatchId;
                        requireActivity().runOnUiThread(() -> {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Pending Stock Snapshot")
                                    .setMessage(
                                            "There is a pending stock snapshot.\n\n" +
                                                    "Batch ID: " + finalLastBatchId + "\n" +
                                                    "Status:Draft.\n\n" +
                                                    "Please review this batch before proceeding."
                                    )
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();

                                        // Navigate to the StockSnapshotsearchFragment
                                        StockSnapshotsearchFragment anotherFragment = new StockSnapshotsearchFragment();
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.frame_layout, anotherFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    })
                                    .setCancelable(false)
                                    .show();
                        });
                    }else {
                        StockSnapshotsearchFragment anotherFragment = new StockSnapshotsearchFragment();
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, anotherFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                }).start();

            }
        });



        // Back button to navigate to HomeFragment
        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment productManagementFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
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




}
