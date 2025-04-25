package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SaleReportAdapter extends RecyclerView.Adapter<SaleReportAdapter.ViewHolder> {

    private static final String TAG = "SaleReportAdapter"; // Log tag
    private final List<SaleReportItem> saleReportList;

    Context context;
    String ipAddress, portNumber, databaseName, username, password;
    public SaleReportAdapter(List<SaleReportItem> saleReportList, Context context) {
        this.saleReportList = saleReportList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Creating new view holder.");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleReportItem item = saleReportList.get(position);

        // Log the binding process for each item
        Log.d(TAG, "onBindViewHolder: Binding item at position " + position + ", Description: " + item.getDescription());

        holder.tvDescription.setText(item.getDescription()); // Assuming 'Description' is a string
        holder.tvbarcode.setText(item.getBarcode());
        holder.tvmargin.setText(String.valueOf(item.getMargin()));
        holder.tvmarkup.setText(String.valueOf(item.getMarkup()));

        // Fetch the department name asynchronously
        getDepartmentNameById(Integer.parseInt(item.getDepartment()), holder);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: Total items in the list: " + saleReportList.size());
        return saleReportList.size();
    }

    // Async method to get department name by ID
// Async method to get department name by ID
    public void getDepartmentNameById(int departmentId, ViewHolder holder) {
        Log.d(TAG, "getDepartmentNameById: Fetching department name for ID: " + departmentId);

        new Thread(() -> {

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
            portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
            databaseName = Constants.DATABASE_NAME;
            username = Constants.USERNAME;
            password = Constants.PASSWORD;

            try (Connection connection = DriverManager.getConnection(
                    "jdbc:jtds:sqlserver://" + ipAddress + ":" + portNumber + "/" + databaseName, username, password)) {

                String query = "SELECT [Department] FROM tbl_Departments WHERE ID = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, departmentId);

                Log.d(TAG, "getDepartmentNameById: Executing query: " + query);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String departmentName = resultSet.getString("Department");
                    Log.d(TAG, "getDepartmentNameById: Department found: " + departmentName);

                    // Update UI on the main thread
                    if (context instanceof Activity) {
                        // Cast context to Activity and update UI
                        ((Activity) context).runOnUiThread(() -> {
                            holder.tvdepartment.setText(departmentName);
                        });
                    }
                } else {
                    Log.w(TAG, "getDepartmentNameById: No department found for ID: " + departmentId);
                }
            } catch (SQLException e) {
                Log.e(TAG, "getDepartmentNameById: SQL error while fetching department name.", e);
            }

        }).start();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvbarcode, tvitemcode, tvdepartment, tvmargin, tvmarkup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.txt_PRODUCT_NAME);
            tvbarcode = itemView.findViewById(R.id.txt_BARCODE);
            tvitemcode = itemView.findViewById(R.id.txt_ITEM_CODE);
            tvdepartment = itemView.findViewById(R.id.txt_DEPARTMENT);
            tvmargin = itemView.findViewById(R.id.txt_MARGIN);
            tvmarkup = itemView.findViewById(R.id.txt_MARKUP);
        }
    }
}
