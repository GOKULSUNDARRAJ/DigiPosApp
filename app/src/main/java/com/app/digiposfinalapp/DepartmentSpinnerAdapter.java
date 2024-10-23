package com.app.digiposfinalapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DepartmentSpinnerAdapter extends ArrayAdapter<Departmentspinner> {

    public DepartmentSpinnerAdapter(Context context, List<Departmentspinner> departments) {
        super(context, 0, departments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Departmentspinner department = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        // Lookup view for data population
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        textView.setText(department.getDepartment());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Departmentspinner department = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        // Lookup view for data population
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        textView.setText(department.getDepartment());

        return convertView;
    }
}
