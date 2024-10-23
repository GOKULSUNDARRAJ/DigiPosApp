package com.app.digiposfinalapp;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductReduce extends AppCompatActivity {

    String ipAddress, portNumber, databaseName, username, password;

    private static final String TAG = "SQLConnection";

    EditText search;

    ImageView searchbtn;

    CardView layout1;

    ProgressBar progressbar;

    String  barcoder;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_reduce);

        Intent intent=getIntent();

        barcoder=intent.getStringExtra("barcode");

        Toast.makeText(this, ""+barcoder, Toast.LENGTH_SHORT).show();

        layout1=findViewById(R.id.layout1);

        search=findViewById(R.id.etFilter);

        searchbtn=findViewById(R.id.enter);

        progressbar=findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;


        search.setText(barcoder);

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbtn.setVisibility(View.INVISIBLE);
                progressbar.setVisibility(View.VISIBLE);

                new DatabaseTask().execute();


            }
        });


        new DatabaseTask().execute();

    }


    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department,
                saleWithVAT, discount, costPerCase, price, vat, margin, ageLimit;

        @Override
        protected Void doInBackground(Void... voids) {
            String searchQuery = search.getText().toString(); // Get the search query from the EditText
            String query = ""; // Initialize query
            String filterValue = ""; // Initialize filter value

            if (!searchQuery.isEmpty()) {
                // If the search query is not empty, search by the query
                query = "SELECT * FROM tbl_Products WHERE Barcode = ?";
                filterValue = searchQuery;
            } else if (barcoder != null && !barcoder.isEmpty()) {
                // If the search query is empty, search by the barcode received from Intent
                query = "SELECT * FROM tbl_Products WHERE Barcode = ?";
                filterValue = barcoder;
            } else {
                return null; // Exit early if both search query and barcode are empty
            }

            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + ";databaseName=" + databaseName;

            try (Connection connection = DriverManager.getConnection(connectionUrl, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, filterValue); // Set either searchQuery or barcoder as the filter
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        plu = resultSet.getString("PLU");
                        description = resultSet.getString("Description");
                        barcode = resultSet.getString("Barcode");
                        subDepartment = resultSet.getString("Sub_Department");
                        supplier = resultSet.getString("Supplier");
                        buyPrice = resultSet.getString("Buy_Price");
                        quantity = resultSet.getString("Quantity");
                        department = resultSet.getString("Department");
                        saleWithVAT = resultSet.getString("SaleWithVAT");
                        discount = resultSet.getString("Discount");
                        costPerCase = resultSet.getString("CostPerCase");
                        price = resultSet.getString("Price");
                        vat = resultSet.getString("VAT");
                        margin = resultSet.getString("Margin");
                        ageLimit = resultSet.getString("Age_Limit");
                    } else {
                        runOnUiThread(() -> {
                            layout1.setVisibility(View.INVISIBLE);
                            progressbar.setVisibility(View.INVISIBLE);
                            searchbtn.setVisibility(View.VISIBLE);
                            Toast.makeText(ProductReduce.this, "No product found", Toast.LENGTH_SHORT).show();

                            CustomDialogClassfoeadd cdd = new CustomDialogClassfoeadd(ProductReduce.this);
                            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            cdd.show();

                        });
                    }
                }

            } catch (SQLException e) {
                runOnUiThread(() -> {
                    progressbar.setVisibility(View.INVISIBLE);
                    searchbtn.setVisibility(View.VISIBLE);
                    Toast.makeText(ProductReduce.this, "Database error", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "SQL Exception: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Update UI with the retrieved data
            if (plu != null) {
                runOnUiThread(() -> {


                    TextView pluTextView = findViewById(R.id.pluTextView);
                    TextView productDescriptionTextView = findViewById(R.id.productNameTextView);
                    TextView productBarcodeTextView = findViewById(R.id.productBrandTextView);
                    TextView productSubDepartmentTextView = findViewById(R.id.productSubDepartmentTextView);
                    TextView productSupplierTextView = findViewById(R.id.productSupplierTextView);
                    TextView productBuyPriceTextView = findViewById(R.id.productBuyPriceTextView);
                    TextView productQuantityTextView = findViewById(R.id.productQuantityTextView);
                    TextView productDepartmentTextView = findViewById(R.id.productDepartmentTextView);
                    TextView productSaleVATTextView = findViewById(R.id.productSaleVATTextView);
                    TextView productDiscountTextView = findViewById(R.id.productDiscountTextView);
                    TextView productCostPerCaseTextView = findViewById(R.id.productCostPerCaseTextView);
                    TextView productPriceTextView = findViewById(R.id.productPriceTextView);
                    TextView productVATTextView = findViewById(R.id.productVATTextView);
                    TextView productMarginTextView = findViewById(R.id.productMarginTextView);
                    TextView productAgeLimitTextView = findViewById(R.id.productAgeLimitTextView);

                    // Set the text for each TextView
                    pluTextView.setText(plu);
                    productDescriptionTextView.setText(description);
                    productBarcodeTextView.setText(barcode);
                    productSubDepartmentTextView.setText(subDepartment);
                    productSupplierTextView.setText(supplier);
                    productBuyPriceTextView.setText(buyPrice);
                    productQuantityTextView.setText(quantity);
                    productDepartmentTextView.setText(department);
                    productSaleVATTextView.setText(saleWithVAT);
                    productDiscountTextView.setText(discount);
                    productCostPerCaseTextView.setText(costPerCase);
                    productPriceTextView.setText(price);
                    productVATTextView.setText(vat);
                    productMarginTextView.setText(margin);
                    productAgeLimitTextView.setText(ageLimit);

                    layout1.setVisibility(View.VISIBLE);

                    Toast.makeText(ProductReduce.this, "Product details updated", Toast.LENGTH_SHORT).show();

                    progressbar.setVisibility(View.INVISIBLE);
                    searchbtn.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    public void gotobarcodeActivity(View view) {
        startActivity(new Intent(view.getContext(),BarCodeScanActivityproductreduce.class));
        finish();
    }

    public void gotoback(View view) {
        onBackPressed();
    }

}