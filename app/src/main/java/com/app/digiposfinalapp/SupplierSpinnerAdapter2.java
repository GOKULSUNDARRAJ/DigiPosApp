package com.app.digiposfinalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SupplierSpinnerAdapter2 extends ArrayAdapter<SupplierSpinner> implements Filterable {
    private List<SupplierSpinner> supplierList;  // Original list
    private List<SupplierSpinner> filteredList;  // Filtered list
    private Filter supplierFilter;
    private LayoutInflater inflater;

    public SupplierSpinnerAdapter2(Context context, List<SupplierSpinner> supplierList) {
        super(context, R.layout.custom_spinner_item, supplierList);
        this.supplierList = new ArrayList<>(supplierList);
        this.filteredList = new ArrayList<>(supplierList);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public SupplierSpinner getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView tvSupplierName = convertView.findViewById(R.id.tvSupplierName);

        SupplierSpinner supplier = getItem(position);
        if (supplier != null) {
            tvSupplierName.setText(supplier.getSupplier());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (supplierFilter == null) {
            supplierFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        results.values = supplierList;
                        results.count = supplierList.size();
                    } else {
                        List<SupplierSpinner> filteredResults = new ArrayList<>();
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (SupplierSpinner supplier : supplierList) {
                            if (supplier.getSupplier().toLowerCase().contains(filterPattern) ) {
                                filteredResults.add(supplier);
                            }
                        }

                        results.values = filteredResults;
                        results.count = filteredResults.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<SupplierSpinner>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    SupplierSpinner supplier = (SupplierSpinner) resultValue;
                    return supplier.getSupplier(); // Only show supplier name in input field
                }
            };
        }
        return supplierFilter;
    }
}
