package com.app.digiposfinalapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarcodeAdapter extends RecyclerView.Adapter<BarcodeAdapter.ViewHolder> {

    private List<BarcodeDataModel> dataList;

    public BarcodeAdapter(List<BarcodeDataModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_pricereduce, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BarcodeDataModel data = dataList.get(position);
        holder.idTextView.setText(String.valueOf(data.getDepartmentId()));
        holder.pluTextView.setText(data.getPLU());
        holder.barcodeTextView.setText(data.getBarcode());
        holder.detailTextView.setText(data.getDetail());
        holder.priceTextView.setText(data.getPrice());



        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked item's details
                String plu = data.getPLU();
                String barcode = data.getBarcode();
                String detail = data.getDetail();
                String shop = data.getShop();
                String price = data.getPrice();
                String date = data.getDtDate();
                String capacity = data.getCapacity();
                int qty = data.getQty();
                int id = data.getDepartmentId(); // Assuming you have a method to get the ID of the item

                // Show the clicked item's details in a Toast
                String message = "PLU: " + plu + "\nBarcode: " + barcode + "\nDetail: " + detail + "\nShop: " + shop + "\nPrice: " + price + "\nDate: " + date + "\nCapacity: " + capacity + "\nQty: " + qty;

                // Open bottom sheet fragment for editing
                MyBottomSheetFragmentPRICECHANGE bottomSheetFragment = new MyBottomSheetFragmentPRICECHANGE();
                bottomSheetFragment.setDetails(view.getContext(), plu, barcode, detail, shop, price, date, capacity, qty, id);
                bottomSheetFragment.show(((PriceChangeActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });


        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to open the new activity
                Intent intent = new Intent(view.getContext(), PriceChangeDetailActivity.class);

                // Put extra data (details) into the intent
                intent.putExtra("DEPARTMENTID", String.valueOf(data.getDepartmentId()));
                intent.putExtra("PLU", String.valueOf(data.getPLU()));
                intent.putExtra("BARCODE", String.valueOf(data.getBarcode()));
                intent.putExtra("DETAIL", String.valueOf(data.getDetail()));
                intent.putExtra("SHOP", String.valueOf(data.getShop()));
                intent.putExtra("PRICE", String.valueOf(data.getPrice()));
                intent.putExtra("DATE", String.valueOf(data.getDtDate()));
                intent.putExtra("CAPACITY", String.valueOf(data.getCapacity()));
                intent.putExtra("QTY", String.valueOf(data.getQty()));

                // Start the new activity
                view.getContext().startActivity(intent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Click",Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView, pluTextView, barcodeTextView, detailTextView,priceTextView;
        CardView carproduct;

        LinearLayout edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            idTextView = itemView.findViewById(R.id.itemText3410);
            pluTextView = itemView.findViewById(R.id.itemText34105);
            barcodeTextView = itemView.findViewById(R.id.itemText341056);
            detailTextView = itemView.findViewById(R.id.itemText3410567);
            priceTextView = itemView.findViewById(R.id.itemText34105678);
            carproduct=itemView.findViewById(R.id.carproduct);
            edit=itemView.findViewById(R.id.edit);

        }
    }
}
