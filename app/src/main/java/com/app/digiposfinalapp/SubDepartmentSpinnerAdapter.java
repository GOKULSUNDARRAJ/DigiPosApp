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

public class SubDepartmentSpinnerAdapter extends ArrayAdapter<SubDepartmentspinner> {
    private List<SubDepartmentspinner> subDepartmentListFull;
    private List<SubDepartmentspinner> subDepartmentListFiltered;
    private LayoutInflater inflater;

    public SubDepartmentSpinnerAdapter(Context context, List<SubDepartmentspinner> subDepartments) {
        super(context, 0, subDepartments);
        this.inflater = LayoutInflater.from(context);
        this.subDepartmentListFull = new ArrayList<>(subDepartments);
        this.subDepartmentListFiltered = new ArrayList<>(subDepartments);
    }

    @Override
    public int getCount() {
        return subDepartmentListFiltered.size();
    }

    @Override
    public SubDepartmentspinner getItem(int position) {
        return subDepartmentListFiltered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        SubDepartmentspinner subDepartment = getItem(position);
        if (subDepartment != null) {
            textView.setText(subDepartment.getSubDepartment());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        SubDepartmentspinner subDepartment = getItem(position);
        if (subDepartment != null) {
            textView.setText(subDepartment.getSubDepartment());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return subDepartmentFilter;
    }

    private final Filter subDepartmentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SubDepartmentspinner> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subDepartmentListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SubDepartmentspinner subDepartment : subDepartmentListFull) {
                    if (subDepartment.getSubDepartment().toLowerCase().contains(filterPattern)) {
                        filteredList.add(subDepartment);
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
            subDepartmentListFiltered.clear();
            subDepartmentListFiltered.addAll((List<SubDepartmentspinner>) results.values);
            notifyDataSetChanged();
        }
    };
}
