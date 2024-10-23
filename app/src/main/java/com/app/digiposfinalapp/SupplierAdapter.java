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

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SupplierViewHolder> {

    private List<SupplierModel> supplierList;


    public SupplierAdapter(List<SupplierModel> supplierList) {
        this.supplierList = supplierList;

    }

    @NonNull
    @Override
    public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supplier, parent, false);
        return new SupplierViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierViewHolder holder, int position) {
        SupplierModel supplier = supplierList.get(position);
        holder.bind(supplier);
    }

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public class SupplierViewHolder extends RecyclerView.ViewHolder {

        private TextView idTextView;
        private TextView supplierTextView;
        private TextView doneTextView;
        CardView carproduct;
        LinearLayout edit;



        public SupplierViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.itemText3410);
            supplierTextView = itemView.findViewById(R.id.itemText34105);
            doneTextView = itemView.findViewById(R.id.itemText341056);
            carproduct=itemView.findViewById(R.id.carproduct);
            edit=itemView.findViewById(R.id.edit);
        }

        public void bind(SupplierModel supplier) {
            idTextView.setText(String.valueOf(supplier.getId()));
            supplierTextView.setText(supplier.getSupplier());
            doneTextView.setText(String.valueOf(supplier.getDone()));

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the clicked item's details from the supplier object
                    String supplierName = supplier.getSupplier();
                    int done = supplier.getDone();
                    int id = supplier.getId();

                    // Open bottom sheet fragment for editing
                    MyBottomSheetFragmentsupplier bottomSheetFragment = new MyBottomSheetFragmentsupplier();
                    bottomSheetFragment.setDetails(view.getContext(), String.valueOf(supplierName), String.valueOf(done), String.valueOf(id));
                    bottomSheetFragment.show(((SupplierActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
            });


            carproduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an Intent to start the new activity
                    Intent intent = new Intent(view.getContext(), SupplierDetailActivity.class);

                    // Pass the data to the intent as strings
                    intent.putExtra("supplier_id", String.valueOf(supplier.getId()));
                    intent.putExtra("supplier_name", supplier.getSupplier());
                    intent.putExtra("supplier_done", String.valueOf(supplier.getDone()));

                    // Start the new activity
                    view.getContext().startActivity(intent);
                }
            });


        }
    }
}


