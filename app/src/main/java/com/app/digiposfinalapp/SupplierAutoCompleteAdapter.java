package com.app.digiposfinalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SupplierAutoCompleteAdapter extends ArrayAdapter<SupplierSpinner> {

    private List<SupplierSpinner> supplierListFull; // Full list for filtering
    private List<SupplierSpinner> supplierListFiltered;

    public SupplierAutoCompleteAdapter(Context context, List<SupplierSpinner> suppliers) {
        super(context, 0, suppliers != null ? suppliers : new ArrayList<SupplierSpinner>());
        this.supplierListFull = new ArrayList<>(suppliers != null ? suppliers : new ArrayList<SupplierSpinner>()); // Store full list
        this.supplierListFiltered = new ArrayList<>(this.supplierListFull);
    }

    @Override
    public int getCount() {
        return supplierListFiltered != null ? supplierListFiltered.size() : 0;
    }

    @Override
    public SupplierSpinner getItem(int position) {
        if (supplierListFiltered != null && position < supplierListFiltered.size()) {
            return supplierListFiltered.get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the custom layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_supplier_item, parent, false);
        }

        // Get the TextView from the custom layout
        TextView textView = convertView.findViewById(R.id.text1);
        SupplierSpinner supplier = getItem(position);
        if (supplier != null) {
            textView.setText(supplier.getSupplier());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return supplierFilter;
    }

    private Filter supplierFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<SupplierSpinner> filteredList = new ArrayList<>();

            if (supplierListFull == null) {
                results.values = filteredList;
                results.count = 0;
                return results;
            }

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(supplierListFull); // Show full list if no search query
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SupplierSpinner supplier : supplierListFull) {
                    if (supplier != null && supplier.getSupplier() != null &&
                            supplier.getSupplier().toLowerCase().contains(filterPattern)) {
                        filteredList.add(supplier);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Clear the current filtered list
            if (supplierListFiltered != null) {
                supplierListFiltered.clear();

                // Safely add the filtered results
                if (results.values != null) {
                    try {
                        List<SupplierSpinner> filtered = (List<SupplierSpinner>) results.values;
                        if (filtered != null) {
                            supplierListFiltered.addAll(filtered);
                        }
                    } catch (ClassCastException e) {
                        // Handle the case where the cast fails
                        supplierListFiltered.addAll(supplierListFull);
                    }
                }

                notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            if (resultValue instanceof SupplierSpinner) {
                return ((SupplierSpinner) resultValue).getSupplier();
            }
            return super.convertResultToString(resultValue);
        }
    };

    // Method to update the full list if needed
    public void updateList(List<SupplierSpinner> newList) {
        this.supplierListFull = new ArrayList<>(newList != null ? newList : new ArrayList<SupplierSpinner>());
        this.supplierListFiltered = new ArrayList<>(this.supplierListFull);
        notifyDataSetChanged();
    }
}