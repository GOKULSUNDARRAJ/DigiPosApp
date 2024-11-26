package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
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

public class LogAdapterDayReport extends RecyclerView.Adapter<LogAdapterDayReport.LogViewHolder> {

    private List<LogEntry> logList;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    Context context;

    public LogAdapterDayReport(List<LogEntry> logList, Context context) {
        this.logList = logList;
        this.context = context;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item_newdayreport, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry log = logList.get(position);
        holder.OPENING_DATETIME.setText(String.valueOf(log.getShiftDate())+"/"+String.valueOf(log.getShiftStartTime()));
        holder.CLOSING_DATETIME.setText(log.getShiftEndDate()+"/"+String.valueOf(log.getShiftEndTime()));

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password

        String userId = log.getUserID();
        String logId =log.getUserID();
        // Query the database for user information based on userId
        new Thread(() -> {
            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;
                String query = "SELECT * FROM admin WHERE ID = ?";  // Assuming userID corresponds to admin ID

                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, userId);  // Set userId as parameter in query
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        // Assuming admin table has columns: ID, username, password, Type, Control, done
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String type = resultSet.getString("Type");
                        String control = resultSet.getString("Control");
                        String done = resultSet.getString("done");

                        // Update UI on the main thread (since you can't update UI from a background thread)
                        holder.txt_PREVIOUS_PRICE.post(() -> {
                            holder.txt_PREVIOUS_PRICE.setText(username);  // Update username TextView
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;
                String query = "SELECT CASHPAYMENT_AMT FROM TRANSACTION_HDR WHERE Logid = ?";

                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, logId); // Set Logid as parameter in query
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String cashPaymentAmt = resultSet.getString("CASHPAYMENT_AMT");

                        // Update UI on the main thread (since you can't update UI from a background thread)
                        holder.EXPECTED_CASH_AMOUNT_txt.post(() -> {
                            holder.EXPECTED_CASH_AMOUNT_txt.setText(""+cashPaymentAmt);  // Update TextView with CASHPAYMENT_AMT
                        });
                    } else {
                        // No data found for the given Logid
                        holder.EXPECTED_CASH_AMOUNT_txt.post(() -> {
                            holder.EXPECTED_CASH_AMOUNT_txt.setText("");
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                holder.EXPECTED_CASH_AMOUNT_txt.post(() -> {
                    holder.EXPECTED_CASH_AMOUNT_txt.setText("Error retrieving data");
                });
            }
        }).start();

        new Thread(() -> {
            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;
                String query = "SELECT CARDPAYMENT_AMT FROM TRANSACTION_HDR WHERE Logid = ?";

                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, logId); // Set Logid as parameter in query
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        String cardPaymentAmt = resultSet.getString("CARDPAYMENT_AMT");

                        // Update UI on the main thread (since you can't update UI from a background thread)
                        holder.Card_Sales_txt.post(() -> {
                            holder.Card_Sales_txt.setText(""+cardPaymentAmt);  // Update TextView with CASHPAYMENT_AMT
                        });
                    } else {
                        // No data found for the given Logid
                        holder.Card_Sales_txt.post(() -> {
                            holder.Card_Sales_txt.setText("");
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                holder.Card_Sales_txt.post(() -> {
                    holder.Card_Sales_txt.setText("Error retrieving data");
                });
            }
        }).start();

    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {

        TextView OPENING_DATETIME,CLOSING_DATETIME,txt_PREVIOUS_PRICE,EXPECTED_CASH_AMOUNT_txt,Card_Sales_txt;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            OPENING_DATETIME = itemView.findViewById(R.id.txt_OPENING_DATETIME);
            CLOSING_DATETIME=itemView.findViewById(R.id.txt_CLOSING_DATETIME);
            txt_PREVIOUS_PRICE=itemView.findViewById(R.id.txt_PREVIOUS_PRICE);
            EXPECTED_CASH_AMOUNT_txt=itemView.findViewById(R.id.EXPECTED_CASH_AMOUNT_txt);
            Card_Sales_txt=itemView.findViewById(R.id.Card_Sales_txt);

        }

    }

}


