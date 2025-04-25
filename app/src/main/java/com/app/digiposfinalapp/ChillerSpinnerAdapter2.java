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

public class ChillerSpinnerAdapter2 extends ArrayAdapter<ChillerSpinner> {
    private List<ChillerSpinner> chillerListFull;
    private List<ChillerSpinner> chillerListFiltered;
    private LayoutInflater inflater;

    public ChillerSpinnerAdapter2(Context context, List<ChillerSpinner> chillers) {
        super(context, 0, chillers);
        this.inflater = LayoutInflater.from(context);
        this.chillerListFull = new ArrayList<>(chillers);
        this.chillerListFiltered = new ArrayList<>(chillers);
    }

    @Override
    public int getCount() {
        return chillerListFiltered.size();
    }

    @Override
    public ChillerSpinner getItem(int position) {
        return chillerListFiltered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        ChillerSpinner chiller = getItem(position);
        if (chiller != null) {
            textView.setText(chiller.getChillerName());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        ChillerSpinner chiller = getItem(position);
        if (chiller != null) {
            textView.setText(chiller.getChillerName());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return chillerFilter;
    }

    private final Filter chillerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ChillerSpinner> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(chillerListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChillerSpinner chiller : chillerListFull) {
                    if (chiller.getChillerName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(chiller);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chillerListFiltered.clear();
            chillerListFiltered.addAll((List<ChillerSpinner>) results.values);
            notifyDataSetChanged();
        }
    };
}