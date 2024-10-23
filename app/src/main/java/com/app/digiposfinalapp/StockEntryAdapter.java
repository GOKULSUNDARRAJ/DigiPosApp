package com.app.digiposfinalapp;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockEntryAdapter extends RecyclerView.Adapter<StockEntryAdapter.StockEntryViewHolder> {

    private Context context;
    private List<StockEntry> stockEntryList;

    public StockEntryAdapter(Context context, List<StockEntry> stockEntryList) {
        this.context = context;
        this.stockEntryList = stockEntryList;
    }

    @NonNull
    @Override
    public StockEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_entry_item, parent, false);
        return new StockEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockEntryViewHolder holder, int position) {
        StockEntry stockEntry = stockEntryList.get(position);

        // Bind data to views in the view holder
        holder.textViewBarcode.setText(String.valueOf(stockEntry.getId()));
        holder.textViewDescription.setText(stockEntry.getBarcode());
        holder.done.setText(stockEntry.getDescription());
        // Similarly, bind other fields as needed


        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's details from the stock entry
                int id = (int) stockEntry.getId();
                String barcode = stockEntry.getBarcode();
                int plu = stockEntry.getPlu();
                String description=stockEntry.getDescription();
                String date = stockEntry.getDate();
                String time = stockEntry.getTime();
                String type=stockEntry.getType();
                String reason=stockEntry.getReason();
                int currentStock = stockEntry.getCurrentStock();
                int stockIn = stockEntry.getStockIn();
                int stockOut = stockEntry.getStockOut();
                String scaleType = stockEntry.getScaleType();
                double currentPrice = stockEntry.getCurrentPrice();
                int tillNo = stockEntry.getTillNo();
                String userName = stockEntry.getUserName();
                int logId = stockEntry.getLogId();
                String shortcutDescription = stockEntry.getShortcutDescription();

                // Open bottom sheet fragment for editing
                MyBottomSheetFragmentStockIn bottomSheetFragment = new MyBottomSheetFragmentStockIn();
                bottomSheetFragment.setDetails(view.getContext(), id, barcode, plu,description, date, time,type,reason, currentStock, stockIn, stockOut, scaleType, currentPrice, tillNo, userName, logId, shortcutDescription);
                bottomSheetFragment.show(((StockInActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });



        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StockDetailsActivity.class);
                intent.putExtra("BARCODE", String.valueOf(stockEntry.getBarcode()));
                intent.putExtra("PLU", String.valueOf(stockEntry.getPlu()));
                intent.putExtra("DESCRIPTION", String.valueOf(stockEntry.getDescription()));
                intent.putExtra("DATE", String.valueOf(stockEntry.getDate()));
                intent.putExtra("TIME", String.valueOf(stockEntry.getTime()));
                intent.putExtra("TYPE",String.valueOf( stockEntry.getType()));
                intent.putExtra("REASON", String.valueOf(stockEntry.getReason()));
                intent.putExtra("CURRENT_STOCK", String.valueOf(stockEntry.getCurrentStock()));
                intent.putExtra("STOCK_IN", String.valueOf(stockEntry.getStockIn()));
                intent.putExtra("STOCK_OUT",String.valueOf( stockEntry.getStockOut()));
                intent.putExtra("SCALE_TYPE", String.valueOf(stockEntry.getScaleType()));
                intent.putExtra("CURRENT_PRICE", String.valueOf(stockEntry.getCurrentPrice()));
                intent.putExtra("TILL_NO", String.valueOf(stockEntry.getTillNo()));
                intent.putExtra("USER_NAME", String.valueOf(stockEntry.getUserName()));
                intent.putExtra("LOG_ID", String.valueOf(stockEntry.getLogId()));
                intent.putExtra("SHORTCUT_DESCRIPTION", String.valueOf(stockEntry.getShortcutDescription()));
                intent.putExtra("ID", String.valueOf(stockEntry.getId()));

                view.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return stockEntryList.size();
    }

    public static class StockEntryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBarcode;
        TextView textViewDescription;
        TextView done;
        CardView carproduct;
        // Declare other views here
        LinearLayout edit;

        public StockEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBarcode = itemView.findViewById(R.id.itemText3410534);
            textViewDescription = itemView.findViewById(R.id.itemText341056);

            done = itemView.findViewById(R.id.itemText341056);
            carproduct=itemView.findViewById(R.id.carproduct);
            edit=itemView.findViewById(R.id.edit);
            // Initialize other views here
        }
    }
}
