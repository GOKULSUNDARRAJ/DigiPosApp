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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyBottomSheetFragmentpromotion extends BottomSheetDialogFragment {

    private static final String TAG = "MyBottomSheetFragment";

    private EditText ID, promoId1edt, description1edt, receipt1edt, ruleNo1edt, ruleValue1edt, type1edt, typeValue1edt, start1edt, end1edt, itemCount1edt, plu1edt, done1edt;
    private Button button;
    private String ipAddress, portNumber, databaseName, username, password;
    private String id1, promoId1, description1, receipt1, ruleNo1, ruleValue1, type1, typeValue1, start1, end1, itemCount1, plu1, done1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bottom_sheet_edit_promotion, container, false);

        // Retrieve SharedPreferences from the activity
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName = Constants.DATABASE_NAME;
        username = Constants.USERNAME;
        password = Constants.PASSWORD;

        // Initialize UI components
        ID = view.findViewById(R.id.autocompleteTextView);
        promoId1edt = view.findViewById(R.id.autocompleteTextView2);
        description1edt = view.findViewById(R.id.autocompleteTextView24);
        receipt1edt = view.findViewById(R.id.autocompleteTextView246);
        ruleNo1edt = view.findViewById(R.id.autocompleteTextView24676);
        ruleValue1edt = view.findViewById(R.id.autocompleteTextView624676);
        type1edt = view.findViewById(R.id.ruletype);
        typeValue1edt = view.findViewById(R.id.typevalue);
        start1edt = view.findViewById(R.id.startedt);
        end1edt = view.findViewById(R.id.endedt);
        itemCount1edt = view.findViewById(R.id.itemcountedt);
        plu1edt = view.findViewById(R.id.pluedt);
        done1edt = view.findViewById(R.id.doneedt);

        ImageView closeButton = view.findViewById(R.id.imageView);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the BottomSheetDialogFragment
            }
        });

        button = view.findViewById(R.id.update);

        // Populate fields with existing data
        populateFields();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    new UpdateDataTask().execute(
                            Integer.parseInt(promoId1edt.getText().toString()),
                            description1edt.getText().toString(),
                            receipt1edt.getText().toString(),
                            ruleNo1edt.getText().toString(),
                            Double.parseDouble(ruleValue1edt.getText().toString()),
                            type1edt.getText().toString(),
                            Double.parseDouble(typeValue1edt.getText().toString()),
                            start1edt.getText().toString(),
                            end1edt.getText().toString(),
                            Integer.parseInt(itemCount1edt.getText().toString()),
                            plu1edt.getText().toString(),
                            Integer.parseInt(done1edt.getText().toString())
                    );
                } else {
                    Log.e(TAG, "Validation failed. Please check input fields.");
                }
            }
        });

        return view;
    }

    private void populateFields() {
        ID.setText(id1);
        promoId1edt.setText(promoId1);
        description1edt.setText(description1);
        receipt1edt.setText(receipt1);
        ruleNo1edt.setText(ruleNo1);
        ruleValue1edt.setText(ruleValue1);
        type1edt.setText(type1);
        typeValue1edt.setText(typeValue1);
        start1edt.setText(start1);
        end1edt.setText(end1);
        itemCount1edt.setText(itemCount1);
        plu1edt.setText(plu1);
        done1edt.setText(done1);
    }

    private boolean validateInputs() {
        try {
            Integer.parseInt(promoId1edt.getText().toString());
            Double.parseDouble(ruleValue1edt.getText().toString());
            Double.parseDouble(typeValue1edt.getText().toString());
            Integer.parseInt(itemCount1edt.getText().toString());
            Integer.parseInt(done1edt.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number format exception: " + e.getMessage());
            return false;
        }
        return !ID.getText().toString().trim().isEmpty() &&
                !promoId1edt.getText().toString().trim().isEmpty() &&
                !description1edt.getText().toString().trim().isEmpty() &&
                !receipt1edt.getText().toString().trim().isEmpty() &&
                !ruleNo1edt.getText().toString().trim().isEmpty() &&
                !ruleValue1edt.getText().toString().trim().isEmpty() &&
                !type1edt.getText().toString().trim().isEmpty() &&
                !typeValue1edt.getText().toString().trim().isEmpty() &&
                !start1edt.getText().toString().trim().isEmpty() &&
                !end1edt.getText().toString().trim().isEmpty() &&
                !itemCount1edt.getText().toString().trim().isEmpty() &&
                !plu1edt.getText().toString().trim().isEmpty() &&
                !done1edt.getText().toString().trim().isEmpty();
    }

    public void setDetails(Context context, int id, int promoId, String description, String receipt, String ruleNo, double ruleValue, String type, double typeValue, String start, String end, int itemCount, String plu, int done) {
        if (context != null) {
            id1 = String.valueOf(id);
            promoId1 = String.valueOf(promoId);
            description1 = description;
            receipt1 = receipt;
            ruleNo1 = ruleNo;
            ruleValue1 = String.valueOf(ruleValue);
            type1 = type;
            typeValue1 = String.valueOf(typeValue);
            start1 = start;
            end1 = end;
            itemCount1 = String.valueOf(itemCount);
            plu1 = plu;
            done1 = String.valueOf(done);
        } else {
            Log.e(TAG, "Context is null");
        }
    }

    private class UpdateDataTask extends AsyncTask<Object, Void, Boolean> {
        private static final String UPDATE_STATEMENT = "UPDATE tbl_Promotion SET PromoID = ?, Description = ?, Receipt = ?, Ruleno = ?, RuleValue = ?, Type = ?, TypeValue = ?, Start = ?, Enddate = ?, Item_Count = ?, PLU = ?, done = ? WHERE ID = ?";

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                // Establish connection
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectURL = "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber +
                        ";databaseName=" + databaseName + ";user=" + username + ";password=" + password;
                Connection connection = DriverManager.getConnection(connectURL);

                // Prepare statement
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STATEMENT)) {
                    preparedStatement.setInt(1, (Integer) params[0]);
                    preparedStatement.setString(2, (String) params[1]);
                    preparedStatement.setString(3, (String) params[2]);
                    preparedStatement.setString(4, (String) params[3]);
                    preparedStatement.setDouble(5, (Double) params[4]);
                    preparedStatement.setString(6, (String) params[5]);
                    preparedStatement.setDouble(7, (Double) params[6]);
                    preparedStatement.setString(8, (String) params[7]);
                    preparedStatement.setString(9, (String) params[8]);
                    preparedStatement.setInt(10, (Integer) params[9]);
                    preparedStatement.setString(11, (String) params[10]);
                    preparedStatement.setInt(12, (Integer) params[11]);
                    preparedStatement.setString(13, id1);

                    // Execute update
                    int rowsUpdated = preparedStatement.executeUpdate();
                    return rowsUpdated > 0;
                } catch (SQLException e) {
                    Log.e(TAG, "SQL Exception during update: " + e.getMessage());
                    return false;
                } finally {
                    connection.close();
                }
            } catch (ClassNotFoundException | SQLException e) {
                Log.e(TAG, "Exception during update: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
