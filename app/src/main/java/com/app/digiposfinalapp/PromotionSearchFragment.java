package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PromotionSearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private PromotionSearchAdapter adapter;
    private List<PromotionSearch> promotionSearchList = new ArrayList<>();
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotion_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PromotionSearchAdapter(promotionSearchList, getContext());
        recyclerView.setAdapter(adapter);

        loadPromotionData();

        return view;
    }

    private void loadPromotionData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        new AsyncTask<Void, Void, List<PromotionSearch>>() {
            @Override
            protected List<PromotionSearch> doInBackground(Void... voids) {
                List<PromotionSearch> promotions = new ArrayList<>();
                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;

                try {
                    // Retrieve database connection details from SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
                    String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
                    String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
                    String databaseName = Constants.DATABASE_NAME;
                    String username = Constants.USERNAME;
                    String password = Constants.PASSWORD;

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName + ";ssl=request";

                    connection = DriverManager.getConnection(connectionUrl, username, password);

                    String query = "SELECT * FROM tbl_Promotion";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        PromotionSearch promotion = new PromotionSearch(resultSet.getInt("ID"), resultSet.getInt("PromoID"), resultSet.getString("Description"), resultSet.getString("Receipt"), resultSet.getString("Ruleno"), resultSet.getInt("RuleValue"), resultSet.getString("Type"), resultSet.getString("TypeValue"), resultSet.getString("Start"), resultSet.getString("Enddate"), resultSet.getInt("Item_Count"), resultSet.getString("PLU"), resultSet.getBoolean("done"), resultSet.getString("PromoName"), resultSet.getString("DealType"), resultSet.getString("PromoTarget"), resultSet.getInt("MaxUses"), resultSet.getString("Status"), resultSet.getDouble("UnitPrice"));
                        promotions.add(promotion);
                    }
                } catch (Exception e) {
                    Log.e("PromotionSearch", "Database error", e);
                    return null; // Return null to indicate error
                } finally {
                    try {
                        if (resultSet != null) resultSet.close();
                        if (statement != null) statement.close();
                        if (connection != null) connection.close();
                    } catch (SQLException e) {
                        Log.e("PromotionSearch", "Error closing connection", e);
                    }
                }
                return promotions;
            }

            @Override
            protected void onPostExecute(List<PromotionSearch> result) {
                progressBar.setVisibility(View.GONE);

                if (result == null) {
                    // Error occurred

                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to connect to database", Toast.LENGTH_SHORT).show();
                } else if (result.isEmpty()) {
                    // No data

                    recyclerView.setVisibility(View.GONE);
                } else {
                    // Success
                    promotionSearchList.clear();
                    promotionSearchList.addAll(result);
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);

                }
            }
        }.execute();
    }
}