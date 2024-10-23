package com.app.digiposfinalapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDataAdapter extends ArrayAdapter<Department> {

    private static final String TAG = "DepartmentDataAdapter"; // Tag for logging
    private final Context context;
    private List<Department> departmentList;

    public DepartmentDataAdapter(Context context, List<Department> departmentList) {
        super(context, 0, departmentList != null ? departmentList : new ArrayList<>());
        this.context = context;
        this.departmentList = new ArrayList<>(departmentList != null ? departmentList : new ArrayList<>());

        Log.d(TAG, "DepartmentDataAdapter initialized with " + (departmentList != null ? departmentList.size() : 0) + " departments");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Department department = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_baranch, parent, false);
        }

        // Lookup view for data population
        TextView textView = convertView.findViewById(R.id.name);
        // Populate the data into the template view using the data object

            textView.setText(department.getAge());
            Log.d(TAG, "Binding department: " + department.getAge() + " at position: " + position);

        // Return the completed view to render on screen
        return convertView;
    }

    public void updateData(List<Department> newDepartmentList) {
        departmentList.clear();
        if (newDepartmentList != null) {
            departmentList.addAll(newDepartmentList);
            Log.d(TAG, "Updated department list with " + newDepartmentList.size() + " new departments");
        } else {
            Log.d(TAG, "Updated department list with null");
        }
        notifyDataSetChanged();
    }
}
