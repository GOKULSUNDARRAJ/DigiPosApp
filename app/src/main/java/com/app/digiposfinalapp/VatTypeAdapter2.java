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

public class VatTypeAdapter2 extends ArrayAdapter<VatType> implements Filterable {
    private List<VatType> originalList;
    private List<VatType> filteredList;
    private Filter vatFilter;

    public VatTypeAdapter2(Context context, List<VatType> vatTypes) {
        super(context, R.layout.custom_spinner_item, vatTypes);
        this.originalList = new ArrayList<>(vatTypes);
        this.filteredList = new ArrayList<>(vatTypes);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public VatType getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        VatType vatType = getItem(position);

        if (vatType != null) {
            textView.setText(vatType.getVat());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        VatType vatType = getItem(position);

        if (vatType != null) {
            textView.setText(vatType.getVat());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (vatFilter == null) {
            vatFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        results.values = originalList;
                        results.count = originalList.size();
                    } else {
                        List<VatType> filteredResults = new ArrayList<>();
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (VatType vat : originalList) {
                            if (vat.getVat().toLowerCase().contains(filterPattern)) {
                                filteredResults.add(vat);
                            }
                        }

                        results.values = filteredResults;
                        results.count = filteredResults.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<VatType>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    return ((VatType) resultValue).getVat();
                }
            };
        }
        return vatFilter;
    }
}