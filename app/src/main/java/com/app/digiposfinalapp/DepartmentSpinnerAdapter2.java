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

public class DepartmentSpinnerAdapter2 extends ArrayAdapter<Departmentspinner> {
    private List<Departmentspinner> departmentListFull;
    private List<Departmentspinner> departmentListFiltered;
    private LayoutInflater inflater;

    public DepartmentSpinnerAdapter2(Context context, List<Departmentspinner> departments) {
        super(context, 0, departments);
        this.inflater = LayoutInflater.from(context);
        this.departmentListFull = new ArrayList<>(departments);
        this.departmentListFiltered = new ArrayList<>(departments);
    }

    @Override
    public int getCount() {
        return departmentListFiltered.size();
    }

    @Override
    public Departmentspinner getItem(int position) {
        return departmentListFiltered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        Departmentspinner department = getItem(position);
        if (department != null) {
            textView.setText(department.getDepartment());
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tvSupplierName);
        Departmentspinner department = getItem(position);
        if (department != null) {
            textView.setText(department.getDepartment());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return departmentFilter;
    }

    private final Filter departmentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Departmentspinner> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(departmentListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Departmentspinner department : departmentListFull) {
                    if (department.getDepartment().toLowerCase().contains(filterPattern)) {
                        filteredList.add(department);
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
            departmentListFiltered.clear();
            departmentListFiltered.addAll((List<Departmentspinner>) results.values);
            notifyDataSetChanged();
        }
    };
}
