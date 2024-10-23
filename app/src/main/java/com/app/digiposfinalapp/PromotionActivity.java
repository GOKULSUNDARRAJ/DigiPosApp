package com.app.digiposfinalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PromotionActivity extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener{

    private CustomSpinner spinner_fruits;

    private FruitAdapter statusadapter;



    private static final String TAG = "PromotionActivity";

    RecyclerView recyclerView;
    PromotionAdapter adapter;
    List<PromotionModel> promotionList;
    Connection connection;
    String ipAddress, portNumber, databaseName, username, password;
    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        spinner_fruits =findViewById(R.id.spinner_fruits);

        spinner_fruits.setSpinnerEventsListener(this);

        statusadapter = new FruitAdapter(PromotionActivity.this, StatusData.getFruitList());

        spinner_fruits.setAdapter(statusadapter);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        recyclerView = findViewById(R.id.recyclerView);
        promotionList = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new PromotionAdapter(promotionList, fragmentManager);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        // Fetch data from the database and populate the RecyclerView
        fetchDataFromDatabase();
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
                    runOnUiThread(() -> Log.e(TAG, "Failed to establish database connection: " + e.getMessage()));
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

                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged(); // Notify adapter of data change
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "Error closing connection: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(PromotionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void gotoAddpromotion(View view) {
        Intent intent = new Intent(PromotionActivity.this, PromotionAddActivity.class);
        startActivity(intent); // Start MainActivity with the intent
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void gotoback(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
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
