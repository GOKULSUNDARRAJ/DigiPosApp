package com.app.digiposfinalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReasonTypeAdapter extends ArrayAdapter<ReasonType> {

    public ReasonTypeAdapter(Context context, List<ReasonType> reasonTypes) {
        super(context, 0, reasonTypes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReasonType reasonType = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(reasonType.getReason()); // Display the reason field

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ReasonType reasonType = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(reasonType.getReason()); // Display the reason field

        return convertView;
    }
}
