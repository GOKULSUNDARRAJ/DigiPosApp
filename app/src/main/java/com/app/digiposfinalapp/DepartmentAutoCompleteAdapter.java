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

public class DepartmentAutoCompleteAdapter extends ArrayAdapter<Departmentspinner> {

    private List<Departmentspinner> departmentListFull; // Full list for filtering
    private List<Departmentspinner> departmentListFiltered;

    public DepartmentAutoCompleteAdapter(Context context, List<Departmentspinner> departments) {
        super(context, 0, departments);
        this.departmentListFull = new ArrayList<>(departments); // Store full list
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
            // Inflate the custom layout
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_textview_item, parent, false);
        }

        // Get the TextView from the custom layout
        TextView textView = convertView.findViewById(R.id.text1);

        // Set the department name to the TextView
        textView.setText(departmentListFiltered.get(position).getDepartment());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return departmentFilter;
    }

    private Filter departmentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Departmentspinner> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(departmentListFull); // Show full list if no search query
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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            departmentListFiltered.clear();
            departmentListFiltered.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}