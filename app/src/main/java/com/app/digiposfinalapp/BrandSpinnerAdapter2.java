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

public class BrandSpinnerAdapter2 extends ArrayAdapter<BrandSpinner> implements Filterable {
    private LayoutInflater inflater;
    private int resource;
    private List<BrandSpinner> brandList;
    private List<BrandSpinner> filteredList;
    private Filter brandFilter;

    public BrandSpinnerAdapter2(Context context, List<BrandSpinner> brands) {
        super(context, R.layout.custom_spinner_item, brands);
        this.inflater = LayoutInflater.from(context);
        this.resource = R.layout.custom_spinner_item;
        this.brandList = new ArrayList<>(brands);
        this.filteredList = new ArrayList<>(brands);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public BrandSpinner getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        BrandSpinner brand = getItem(position);

        if (brand != null) {
            textView.setText(brand.getBrand());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        BrandSpinner brand = getItem(position);

        if (brand != null) {
            textView.setText(brand.getBrand());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (brandFilter == null) {
            brandFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        results.values = brandList;
                        results.count = brandList.size();
                    } else {
                        List<BrandSpinner> filteredResults = new ArrayList<>();
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (BrandSpinner brand : brandList) {
                            if (brand.getBrand().toLowerCase().contains(filterPattern)) {
                                filteredResults.add(brand);
                            }
                        }

                        results.values = filteredResults;
                        results.count = filteredResults.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList = (List<BrandSpinner>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    BrandSpinner brand = (BrandSpinner) resultValue;
                    return brand.getBrand();
                }
            };
        }
        return brandFilter;
    }
}