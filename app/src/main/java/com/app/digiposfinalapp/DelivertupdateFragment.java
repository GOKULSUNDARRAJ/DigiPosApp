package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DelivertupdateFragment extends Fragment {

    String barcode1, outerBarcode1, price1, productDescription, CasePrice, quantity;
    private String ipAddress, portNumber, databaseName, username, password;
    TextView Productnametv;
    EditText Receivedqtyedt;
    String supplierName;
    String referenceId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivertupdate, container, false);


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
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        supplierName = sharedPreferences2.getString("supplierName", "");
        referenceId = sharedPreferences2.getString("ReferenceId", "");

        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DelivermanagmentsearchFragment productManagementFragment = new DelivermanagmentsearchFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Productnametv = view.findViewById(R.id.Productnametv);
        Receivedqtyedt = view.findViewById(R.id.Receivedqtyedt);
        Productnametv.setText(productDescription);

        Button btnSave = view.findViewById(R.id.savestocktakebtn);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup1);

        btnSave.setOnClickListener(v -> {
            String receivedQty = Receivedqtyedt.getText().toString().trim();

            if (receivedQty.isEmpty()) {
                Toast.makeText(getContext(), "Please enter received quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            String quantityType;
            if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton11) {
                quantityType = "CASE";
            } else {
                quantityType = "UNIT";
            }

            insertDeliveryData(receivedQty, quantityType);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            outerBarcode1 = getArguments().getString("OuterBarcode"); // Get outer barcode
            price1 = getArguments().getString("Price");
            productDescription = getArguments().getString("description");
            CasePrice = getArguments().getString("CasePrice");
            quantity = getArguments().getString("quantity");
        }
    }

    private void insertDeliveryData(String receivedQty, String quantityType) {
        new InsertDataTask(receivedQty, quantityType).execute();
    }

    private class InsertDataTask extends AsyncTask<Void, Void, String> {
        private String receivedQty;
        private String quantityType;

        InsertDataTask(String receivedQty, String quantityType) {
            this.receivedQty = receivedQty;
            this.quantityType = quantityType;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName;
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                connection = DriverManager.getConnection(connectionUrl, username, password);

                // Modified SQL to include OuterBarcode
                String sql = "INSERT INTO tbl_Deliveries (Barcode, OuterBarcode, Supplier, Quantity_Case, Quantity_Unit, ReferenceNo, done) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, barcode1);
                preparedStatement.setString(2, outerBarcode1 != null ? outerBarcode1 : ""); // Insert outer barcode or empty string if null
                preparedStatement.setString(3, supplierName);

                if (quantityType.equals("CASE")) {
                    preparedStatement.setString(4, receivedQty);
                    preparedStatement.setString(5, "0");
                } else {
                    preparedStatement.setString(4, "0");
                    preparedStatement.setString(5, receivedQty);
                }

                preparedStatement.setString(6, referenceId);
                preparedStatement.setInt(7, 1);

                int rowsInserted = preparedStatement.executeUpdate();
                return rowsInserted > 0 ? "Data Inserted Successfully!" : "Failed to Insert Data!";

            } catch (ClassNotFoundException | SQLException e) {
                Log.e("SQL_ERROR", "Error inserting data: " + e.getMessage());
                return "Error: " + e.getMessage();
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    Log.e("SQL_ERROR", "Error closing connection: " + e.getMessage());
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            DelivermanagmentsearchFragment productManagementFragment = new DelivermanagmentsearchFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}