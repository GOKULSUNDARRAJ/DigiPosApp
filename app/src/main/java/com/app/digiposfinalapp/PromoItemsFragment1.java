package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PromoItemsFragment1 extends Fragment {
    private RecyclerView recyclerView;
    private PromoItemAdapter2 adapter;
    private List<PromoItem> promoItems = new ArrayList<>();
    private ProgressBar progressBar;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    String TAG = "PromoItemsFragment";

    TextView error;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_items1, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;
        dbPassword1 = Constants.PASSWORD;

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PromoItemAdapter2(promoItems, getContext(), getParentFragmentManager());

        recyclerView.setAdapter(adapter);



        ImageView back = view.findViewById(R.id.imageViewback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubnewpromotionFragment productManagementFragment = new SubnewpromotionFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        ImageView camera = view.findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentpromotionsearch productManagementFragment = new BarCodeScanFragmentpromotionsearch();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        ImageView search = view.findViewById(R.id.searchimg);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoItemsFragment productManagementFragment = new PromoItemsFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        error = view.findViewById(R.id.error);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadPromoItems();
    }

    private void loadPromoItems() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        new AsyncTask<Void, Void, List<PromoItem>>() {
            @Override
            protected List<PromoItem> doInBackground(Void... voids) {
                List<PromoItem> items = new ArrayList<>();
                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;

                try {
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                    String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
                    String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
                    String databaseName = Constants.DATABASE_NAME;
                    String username = Constants.USERNAME;
                    String password = Constants.PASSWORD;

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                            "/" + databaseName + ";ssl=request";

                    connection = DriverManager.getConnection(connectionUrl, username, password);

                    String query = "SELECT [ID], [PLU], [PromoID], [done], [Barcode], " +
                            "[DDPrice], [DiscountPrice], [PromotionPrice] " +
                            "FROM [STAR_RETAIL].[dbo].[Promo_Items]";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        PromoItem item = new PromoItem(
                                resultSet.getInt("ID"),
                                resultSet.getString("PLU"),
                                resultSet.getInt("PromoID"),
                                resultSet.getInt("done") == 1,
                                resultSet.getString("Barcode"),
                                parseDoubleSafe(resultSet.getString("DDPrice")),
                                parseDoubleSafe(resultSet.getString("DiscountPrice")),
                                parseDoubleSafe(resultSet.getString("PromotionPrice"))
                        );
                        items.add(item);
                    }
                } catch (Exception e) {
                    Log.e("PromoItems", "Database error", e);
                    return null;
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        Log.e("PromoItems", "Error closing connection", e);
                    }
                }
                return items;
            }

            @Override
            protected void onPostExecute(List<PromoItem> result) {
                progressBar.setVisibility(View.GONE);

                if (result == null) {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Database connection failed", Toast.LENGTH_SHORT).show();
                } else if (result.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No promotions available", Toast.LENGTH_SHORT).show();
                } else {
                    promoItems.clear();
                    // Reverse the list before adding it
                    Collections.reverse(result);
                    promoItems.addAll(result);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing double value: " + value, e);
            return 0.0;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Do nothing to prevent back press
                }
            });
        }
    }
}