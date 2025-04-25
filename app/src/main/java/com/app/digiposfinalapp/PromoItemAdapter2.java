package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PromoItemAdapter2 extends RecyclerView.Adapter<PromoItemAdapter2.PromoItemViewHolder> {
    private List<PromoItem> promoItems;
    private List<PromoItem2> detailedItems = new ArrayList<>();
    private Context context;
    private FragmentManager fragmentManager;

    public PromoItemAdapter2(List<PromoItem> promoItems, Context context, FragmentManager fragmentManager) {
        this.promoItems = promoItems != null ? promoItems : new ArrayList<>();
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public PromoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promo_item, parent, false);
        return new PromoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PromoItem item = promoItems.get(position);
        holder.txtPromoId.setText(String.valueOf("PROMOID : " + item.getPromoId()));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NAVIGATION", "Edit button clicked for position: " + position);

                if (position < detailedItems.size() && detailedItems.get(position) != null) {
                    PromoItemAdapter2.PromoItem2 promoItem = detailedItems.get(position);
                    Log.d("NAVIGATION", "Saving promo details - Name: " + promoItem.getPromoName() +
                            ", ID: " + promoItem.getPromoId());

                    // 1. Save the entire promoItem as JSON
                    Gson gson = new Gson();
                    String promoJson = gson.toJson(promoItem);

                    // 2. Save promoId separately (for quick access)
                    int promoId = promoItem.getPromoId();

                    // Store in SharedPreferences
                    SharedPreferences sharedPref = v.getContext().getSharedPreferences("PromoPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("promo_details", promoJson);  // Entire object
                    editor.putInt("promo_id", promoId);             // Individual ID
                    editor.apply();

                    Log.d("SHARED_PREFS", "Saved promoId: " + promoId + " and full promo details");

                    // Navigate to the edit fragment (no Bundle needed)
                    PromoItemEditFragment editFragment = new PromoItemEditFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, editFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    Log.d("NAVIGATION", "Fragment transaction committed");
                } else {
                    Log.e("NAVIGATION", "Invalid position or null item at position: " + position);
                }
            }
        });


        new FetchPromoDetailsTask(holder, position).execute(item.getPromoId());




        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete this promotion item?");

                // Set positive button (Delete)
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the barcode to delete
                        String barcodeToDelete = promoItems.get(position).getBarcode();

                        // Execute delete task with barcode
                        new DeletePromoTask(position, barcodeToDelete).execute();
                    }
                });

                // Set negative button (Cancel)
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Just close the dialog
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Optional: Customize button colors
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.black));

                        Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.black));
                    }
                });
            }
        });


        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < detailedItems.size() && detailedItems.get(position) != null) {
                    PromoItem2 promoItem = detailedItems.get(position);
                    showStatusChangeDialog(promoItem, position, holder);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return promoItems.size();
    }

    public static class PromoItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtPromoId, txtPromoName, txtDates, txtitemcount, txtpromotype;
        Button edit,delete,btnStatus;

        public PromoItemViewHolder(View itemView) {
            super(itemView);
            txtPromoId = itemView.findViewById(R.id.txtid);
            txtPromoName = itemView.findViewById(R.id.txtPromoName);
            txtDates = itemView.findViewById(R.id.txtDates);
            txtitemcount = itemView.findViewById(R.id.txtitemcount);
            txtpromotype = itemView.findViewById(R.id.txtpromotype);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            btnStatus = itemView.findViewById(R.id.btnstatus);
        }
    }

    public static class PromoItem2 implements Parcelable {
        private int id;
        private int promoId;
        private String description;
        private String receipt;
        private String ruleno;
        private String ruleValue;
        private String type;
        private String typeValue;
        private String startDate;
        private String endDate;
        private String itemCount;
        private String plu;
        private String done;
        private String promoName;
        private String dealType;
        private String promoTarget;
        private String maxUses;
        private String status;
        private String unitPrice;

        public PromoItem2() {}

        protected PromoItem2(Parcel in) {
            id = in.readInt();
            promoId = in.readInt();
            description = in.readString();
            receipt = in.readString();
            ruleno = in.readString();
            ruleValue = in.readString();
            type = in.readString();
            typeValue = in.readString();
            startDate = in.readString();
            endDate = in.readString();
            itemCount = in.readString();
            plu = in.readString();
            done = in.readString();
            promoName = in.readString();
            dealType = in.readString();
            promoTarget = in.readString();
            maxUses = in.readString();
            status = in.readString();
            unitPrice = in.readString();
        }

        public static final Creator<PromoItem2> CREATOR = new Creator<PromoItem2>() {
            @Override
            public PromoItem2 createFromParcel(Parcel in) {
                return new PromoItem2(in);
            }

            @Override
            public PromoItem2[] newArray(int size) {
                return new PromoItem2[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeInt(promoId);
            dest.writeString(description);
            dest.writeString(receipt);
            dest.writeString(ruleno);
            dest.writeString(ruleValue);
            dest.writeString(type);
            dest.writeString(typeValue);
            dest.writeString(startDate);
            dest.writeString(endDate);
            dest.writeString(itemCount);
            dest.writeString(plu);
            dest.writeString(done);
            dest.writeString(promoName);
            dest.writeString(dealType);
            dest.writeString(promoTarget);
            dest.writeString(maxUses);
            dest.writeString(status);
            dest.writeString(unitPrice);
        }

        // Getters and Setters for all fields
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getPromoId() { return promoId; }
        public void setPromoId(int promoId) { this.promoId = promoId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getReceipt() { return receipt; }
        public void setReceipt(String receipt) { this.receipt = receipt; }
        public String getRuleno() { return ruleno; }
        public void setRuleno(String ruleno) { this.ruleno = ruleno; }
        public String getRuleValue() { return ruleValue; }
        public void setRuleValue(String ruleValue) { this.ruleValue = ruleValue; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTypeValue() { return typeValue; }
        public void setTypeValue(String typeValue) { this.typeValue = typeValue; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getItemCount() { return itemCount; }
        public void setItemCount(String itemCount) { this.itemCount = itemCount; }
        public String getPlu() { return plu; }
        public void setPlu(String plu) { this.plu = plu; }
        public String getDone() { return done; }
        public void setDone(String done) { this.done = done; }
        public String getPromoName() { return promoName; }
        public void setPromoName(String promoName) { this.promoName = promoName; }
        public String getDealType() { return dealType; }
        public void setDealType(String dealType) { this.dealType = dealType; }
        public String getPromoTarget() { return promoTarget; }
        public void setPromoTarget(String promoTarget) { this.promoTarget = promoTarget; }
        public String getMaxUses() { return maxUses; }
        public void setMaxUses(String maxUses) { this.maxUses = maxUses; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getUnitPrice() { return unitPrice; }
        public void setUnitPrice(String unitPrice) { this.unitPrice = unitPrice; }
    }

    private class FetchPromoDetailsTask extends AsyncTask<Integer, Void, PromoItem2> {
        private PromoItemViewHolder holder;
        private int position;
        private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        private SimpleDateFormat dbDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        public FetchPromoDetailsTask(PromoItemViewHolder holder, int position) {
            this.holder = holder;
            this.position = position;
        }

        @Override
        protected PromoItem2 doInBackground(Integer... params) {
            int promoId = params[0];
            PromoItem2 detailedItem = new PromoItem2();

            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
            String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
            String databaseName = Constants.DATABASE_NAME;
            String username = Constants.USERNAME;
            String password = Constants.PASSWORD;

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectionString = String.format(
                        "jdbc:jtds:sqlserver://%s:%s/%s",
                        ipAddress,
                        portNumber,
                        databaseName
                );
                connection = DriverManager.getConnection(connectionString, username, password);

                String query = "SELECT [ID], [PromoID], [Description], [Receipt], [Ruleno], [RuleValue], " +
                        "[Type], [TypeValue], [Start], [Enddate], [Item_Count], [PLU], [done], " +
                        "[PromoName], [DealType], [PromoTarget], [MaxUses], [Status], [UnitPrice] " +
                        "FROM [STAR_RETAIL].[dbo].[tbl_Promotion] WHERE PromoID = ?";

                statement = connection.prepareStatement(query);
                statement.setInt(1, promoId);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Set all fields from the result set
                    detailedItem.setId(resultSet.getInt("ID"));
                    detailedItem.setPromoId(resultSet.getInt("PromoID"));
                    detailedItem.setDescription(resultSet.getString("Description"));
                    detailedItem.setReceipt(resultSet.getString("Receipt"));
                    detailedItem.setRuleno(resultSet.getString("Ruleno"));
                    detailedItem.setRuleValue(resultSet.getString("RuleValue"));
                    detailedItem.setType(resultSet.getString("Type"));
                    detailedItem.setTypeValue(resultSet.getString("TypeValue"));
                    detailedItem.setPromoName(resultSet.getString("PromoName"));
                    detailedItem.setDealType(resultSet.getString("DealType"));
                    detailedItem.setPromoTarget(resultSet.getString("PromoTarget"));
                    detailedItem.setMaxUses(resultSet.getString("MaxUses"));
                    detailedItem.setStatus(resultSet.getString("Status"));
                    detailedItem.setUnitPrice(resultSet.getString("UnitPrice"));
                    detailedItem.setItemCount(resultSet.getString("Item_Count"));
                    detailedItem.setPlu(resultSet.getString("PLU"));
                    detailedItem.setDone(resultSet.getString("done"));

                    // Handle date formatting
                    try {
                        String startDateStr = resultSet.getString("Start");
                        if (startDateStr != null) {
                            Date startDate = dbDateFormat.parse(startDateStr);
                            detailedItem.setStartDate(displayDateFormat.format(startDate));
                        }

                        String endDateStr = resultSet.getString("Enddate");
                        if (endDateStr != null) {
                            Date endDate = dbDateFormat.parse(endDateStr);
                            detailedItem.setEndDate(displayDateFormat.format(endDate));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        detailedItem.setStartDate(resultSet.getString("Start"));
                        detailedItem.setEndDate(resultSet.getString("Enddate"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return detailedItem;
        }

        @Override
        protected void onPostExecute(PromoItem2 result) {
            if (result != null) {
                // Ensure the list is large enough
                while (detailedItems.size() <= position) {
                    detailedItems.add(null);
                }
                detailedItems.set(position, result);

                // Update UI with the retrieved data
                holder.txtPromoName.setText(result.getPromoName() != null ? result.getPromoName() : "N/A");
                String startDate = result.getStartDate() != null ? result.getStartDate() : "N/A";
                String endDate = result.getEndDate() != null ? result.getEndDate() : "N/A";

                String displayStart = formatToDisplayDate(startDate);
                String displayEnd = formatToDisplayDate(endDate);

                holder.txtDates.setText(displayStart + " to " + displayEnd);


                holder.txtitemcount.setText("QTY: " + (result.getRuleValue() != null ? result.getRuleValue() : "N/A"));
                holder.txtpromotype.setText(result.getType() != null ? result.getType() : "N/A");

                updateStatusButton(holder, result.getStatus());
            } else {
                holder.txtPromoName.setText("Details not available");
                holder.txtDates.setText("N/A / N/A");
                holder.txtitemcount.setText("QTY: N/A");
                holder.txtpromotype.setText("N/A");
                holder.btnStatus.setText("N/A");
            }
        }
    }

    private String formatToDisplayDate(String date) {
        try {
            // If your date is coming as yyyyMMdd from DB
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = dbFormat.parse(date);
            return displayFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date; // fallback
        }
    }



    private class DeletePromoTask extends AsyncTask<Void, Void, Boolean> {
        private int position;
        private String barcode; // Changed from promoId to barcode

        public DeletePromoTask(int position, String barcode) {
            this.position = position;
            this.barcode = barcode;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
            String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
            String databaseName = Constants.DATABASE_NAME;
            String username = Constants.USERNAME;
            String password = Constants.PASSWORD;

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectionString = String.format(
                        "jdbc:jtds:sqlserver://%s:%s/%s",
                        ipAddress,
                        portNumber,
                        databaseName
                );
                connection = DriverManager.getConnection(connectionString, username, password);

                // Delete from Promo_Items table based on barcode
                String deleteItemsQuery = "DELETE FROM [STAR_RETAIL].[dbo].[Promo_Items] WHERE Barcode = ?";
                statement = connection.prepareStatement(deleteItemsQuery);
                statement.setString(1, barcode); // Using barcode instead of promoId
                int itemsDeleted = statement.executeUpdate();

                return itemsDeleted > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                if (position >= 0 && position < promoItems.size()) {
                    promoItems.remove(position);
                }
                if (position >= 0 && position < detailedItems.size()) {
                    detailedItems.remove(position);
                }
                notifyItemRemoved(position);

                Toast.makeText(context, "Promotion item deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete promotion item", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void showStatusChangeDialog(PromoItem2 promoItem, int position, PromoItemViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Change Promotion Status");

        String currentStatus = promoItem.getStatus();
        String newStatus = "Active".equalsIgnoreCase(currentStatus) ? "Disabled" : "Active";

        builder.setMessage("Are you sure you want to change status to " + newStatus + "?");

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new UpdateStatusTask(promoItem.getPromoId(), newStatus, position, holder).execute();

                PromoItemsFragment1 editFragment = new PromoItemsFragment1();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, editFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize button colors if needed
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
    }


    private class UpdateStatusTask extends AsyncTask<Void, Void, Boolean> {
        private int promoId;
        private String newStatus;
        private int position;
        private PromoItemViewHolder holder;

        public UpdateStatusTask(int promoId, String newStatus, int position, PromoItemViewHolder holder) {
            this.promoId = promoId;
            this.newStatus = newStatus;
            this.position = position;
            this.holder = holder;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            String ipAddress = sharedPreferences.getString(Constants.KEY_IP, "");
            String portNumber = sharedPreferences.getString(Constants.KEY_PORT, "");
            String databaseName = Constants.DATABASE_NAME;
            String username = Constants.USERNAME;
            String password = Constants.PASSWORD;

            Connection connection = null;
            PreparedStatement statement = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectionString = String.format(
                        "jdbc:jtds:sqlserver://%s:%s/%s",
                        ipAddress,
                        portNumber,
                        databaseName
                );
                connection = DriverManager.getConnection(connectionString, username, password);

                String query = "UPDATE [STAR_RETAIL].[dbo].[tbl_Promotion] SET [Status] = ? WHERE [PromoID] = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, newStatus);
                statement.setInt(2, promoId);
                int rowsAffected = statement.executeUpdate();

                return rowsAffected > 0;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                if (position >= 0 && position < detailedItems.size()) {
                    detailedItems.get(position).setStatus(newStatus);

                    // Refresh only this item in RecyclerView
                    notifyItemChanged(position);
                }

                Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void updateStatusButton(PromoItemViewHolder holder, String status) {
        if ("Active".equalsIgnoreCase(status)) {
            holder.btnStatus.setText("Active");
            holder.btnStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            holder.btnStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.btnStatus.setText("Disabled");
            holder.btnStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.dgblue));
            holder.btnStatus.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }
}