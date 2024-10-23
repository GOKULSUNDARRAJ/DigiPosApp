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

public class ChillerReportAdapter extends RecyclerView.Adapter<ChillerReportAdapter.ChillerReportViewHolder> {

    private List<ChillerReportItem> itemList;

    public ChillerReportAdapter(List<ChillerReportItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ChillerReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chiller_report, parent, false);
        return new ChillerReportViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChillerReportViewHolder holder, int position) {
        ChillerReportItem currentItem = itemList.get(position);

        holder.textViewChillerName.setText(currentItem.getChillerName());
        holder.textViewTemperature.setText(String.valueOf(currentItem.getTemperature()));
        holder.textViewid.setText(String.valueOf(currentItem.getId()));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's details
                int id = currentItem.getId();
                String chillerName = currentItem.getChillerName();
                double temperature = currentItem.getTemperature();
                String date = currentItem.getDate();
                String time = currentItem.getTime();
                int changeId = currentItem.getChangeId();
                String day = currentItem.getDay();
                MyBottomSheetFragmentchiller bottomSheetFragment = new MyBottomSheetFragmentchiller();
                bottomSheetFragment.setDetails(view.getContext(), String.valueOf(id), String.valueOf(chillerName), String.valueOf(temperature), String.valueOf(date), String.valueOf(time), String.valueOf(changeId), String.valueOf(day));
                bottomSheetFragment.show(((ChillerActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the context from the itemView
                Context context = view.getContext();
                // Create an Intent to start the new activity
                Intent intent = new Intent(context, ChillerDetailActivity.class);
                // Put extra data into the Intent
                intent.putExtra("id", String.valueOf(currentItem.getId()));
                intent.putExtra("chiller_name", String.valueOf(currentItem.getChillerName()));
                intent.putExtra("temperature", String.valueOf(currentItem.getTemperature()));
                intent.putExtra("date", String.valueOf(currentItem.getDate()));
                intent.putExtra("time", String.valueOf(currentItem.getTime()));
                intent.putExtra("changeid", String.valueOf(currentItem.getChangeId()));
                intent.putExtra("day", String.valueOf(currentItem.getDay()));
                context.startActivity(intent);

            }
        });

        // Set other views similarly
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ChillerReportViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewChillerName;
        public TextView textViewTemperature;
        public TextView textViewid;
        CardView carproduct;

        LinearLayout edit;

        public ChillerReportViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewChillerName = itemView.findViewById(R.id.itemText34105);
            textViewTemperature = itemView.findViewById(R.id.itemText341056);
            textViewid = itemView.findViewById(R.id.itemText3410);
            carproduct = itemView.findViewById(R.id.carproduct);
            edit = itemView.findViewById(R.id.edit);

        }

    }

}





