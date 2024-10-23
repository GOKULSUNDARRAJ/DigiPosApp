package com.app.digiposfinalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class VatTypeAdapter extends ArrayAdapter<VatType> {

    public VatTypeAdapter(Context context, List<VatType> vatTypes) {
        super(context, 0, vatTypes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VatType vatType = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(vatType.getVat()); // Display the VAT percentage

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        VatType vatType = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(vatType.getVat()); // Display the VAT percentage

        return convertView;
    }
}
