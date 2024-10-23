package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    private final List<String> itemList;
    private final SharedPreferences sharedPreferences;
    private final Spinner mySpinner;

    private boolean selectAllChecked = false;

    public CustomAdapter(Context context, int resource, List<String> itemList, SharedPreferences sharedPreferences, Spinner mySpinner) {
        super(context, resource, itemList);
        this.itemList = itemList;
        this.sharedPreferences = sharedPreferences;
        this.mySpinner = mySpinner;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    private View getCustomView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_layout, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.checkBox);
        TextView textView = convertView.findViewById(R.id.textView);
        ImageView textView2 = convertView.findViewById(R.id.button2);


        textView.setText(itemList.get(position));

        if (position != 0) {
            checkBox.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.GONE);

            boolean isChecked = sharedPreferences.getBoolean("item_" + position, false);
            checkBox.setChecked(isChecked);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("item_" + position, isChecked1);
                editor.apply();
            });
        } else {
            checkBox.setVisibility(View.GONE);

        }

        Button button = convertView.findViewById(R.id.button);

        if (position == getCount() - 1) {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show());
            checkBox.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.GONE);
        }

        TextView button1 = convertView.findViewById(R.id.button1);
        if (position == 0) {
            button1.setVisibility(View.VISIBLE);
            button1.setOnClickListener(v -> {
                if (mySpinner.isShown()) {
                    mySpinner.performClick();
                } else {
                    mySpinner.performClick();
                }
            });
            checkBox.setVisibility(View.GONE);
        } else {
            button1.setVisibility(View.GONE);
        }


        button1.setOnClickListener(v -> {
            if (mySpinner.isShown()) {
                mySpinner.performClick();
            } else {
                mySpinner.performClick();
            }
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("item_" + position, isChecked1);
            editor.apply();


            if (position == 1) {
                selectAllChecked = isChecked1;


                for (int i = 2; i < getCount(); i++) {
                    editor.putBoolean("item_" + i, isChecked1);
                }
                editor.apply();
                notifyDataSetChanged();
            }
        });



        return convertView;
    }
}






























