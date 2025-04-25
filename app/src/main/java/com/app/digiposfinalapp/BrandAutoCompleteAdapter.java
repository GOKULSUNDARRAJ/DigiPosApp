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

public class BrandAutoCompleteAdapter extends ArrayAdapter<BrandSpinner> {

    private List<BrandSpinner> brandListFull; // Full list for filtering
    private List<BrandSpinner> brandListFiltered;

    public BrandAutoCompleteAdapter(Context context, List<BrandSpinner> brands) {
        super(context, 0, brands);
        this.brandListFull = new ArrayList<>(brands); // Store full list
        this.brandListFiltered = new ArrayList<>(brands);
    }

    @Override
    public int getCount() {
        return brandListFiltered.size();
    }

    @Override
    public BrandSpinner getItem(int position) {
        return brandListFiltered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the custom layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_brand_item, parent, false);
        }

        // Get the TextView from the custom layout
        TextView textView = convertView.findViewById(R.id.text1);
        textView.setText(brandListFiltered.get(position).getBrand());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return brandFilter;
    }

    private Filter brandFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BrandSpinner> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(brandListFull); // Show full list if no search query
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BrandSpinner brand : brandListFull) {
                    if (brand.getBrand().toLowerCase().contains(filterPattern)) {
                        filteredList.add(brand);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            brandListFiltered.clear();
            brandListFiltered.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}