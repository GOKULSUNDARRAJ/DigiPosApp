package com.app.digiposfinalapp;

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

public class StockUpdateAdapter extends RecyclerView.Adapter<StockUpdateAdapter.StockUpdateViewHolder> {
    private List<StockUpdateModel> stockUpdateList;

    public StockUpdateAdapter(List<StockUpdateModel> stockUpdateList) {
        this.stockUpdateList = stockUpdateList;
    }

    @NonNull
    @Override
    public StockUpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_update, parent, false);
        return new StockUpdateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockUpdateViewHolder holder, int position) {
        StockUpdateModel stockUpdate = stockUpdateList.get(position);
        holder.id.setText(String.valueOf(stockUpdate.getId()));
        holder.barcodeTextView.setText(String.valueOf(stockUpdate.getBarcode()));
        holder.doneByTextView.setText(String.valueOf(stockUpdate.getDoneBy()));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's details from the stock update record
                int id = stockUpdate.getId();
                String barcode = stockUpdate.getBarcode();
                int pul=stockUpdate.getPLU();
                String date = stockUpdate.getDate();
                String time = stockUpdate.getTime();
                int currentQty = stockUpdate.getCurrentQty();
                int updateQty = stockUpdate.getUpdateQty();
                int doneBy = stockUpdate.getDoneBy();

                // Open bottom sheet fragment for editing
                MyBottomSheetFragmentStock bottomSheetFragment = new MyBottomSheetFragmentStock();
                bottomSheetFragment.setDetails(view.getContext(), id, barcode,pul, date, time, currentQty, updateQty, doneBy);
                bottomSheetFragment.show(((StockUpdateActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });


        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's position
                int position = holder.getAdapterPosition();

                // Get the corresponding StockUpdateModel object
                StockUpdateModel clickedStockUpdate = stockUpdateList.get(position);

                // Create an Intent to start the new activity
                Intent intent = new Intent(view.getContext(), StockUpdateDetailActivity.class);

                // Put extra data into the intent
                intent.putExtra("id",String.valueOf(clickedStockUpdate.getId()));
                intent.putExtra("barcode",String.valueOf(clickedStockUpdate.getBarcode()));
                intent.putExtra("plu",String.valueOf(clickedStockUpdate.getPLU()));
                intent.putExtra("date",String.valueOf(clickedStockUpdate.getDate()));
                intent.putExtra("time",String.valueOf(clickedStockUpdate.getTime()));
                intent.putExtra("currentQty",String.valueOf(clickedStockUpdate.getCurrentQty()));
                intent.putExtra("updateQty",String.valueOf(clickedStockUpdate.getUpdateQty()));
                intent.putExtra("doneBy",String.valueOf(clickedStockUpdate.getDoneBy()));

                // Start the new activity
                view.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return stockUpdateList.size();
    }

    public static class StockUpdateViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView barcodeTextView;
        public TextView doneByTextView;

        CardView carproduct;
        LinearLayout edit;

        public StockUpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.itemText3410);
            barcodeTextView = itemView.findViewById(R.id.itemText34105);
            doneByTextView = itemView.findViewById(R.id.itemText341056);
            carproduct=itemView.findViewById(R.id.carproduct);

            edit=itemView.findViewById(R.id.edit);
        }
    }
}
