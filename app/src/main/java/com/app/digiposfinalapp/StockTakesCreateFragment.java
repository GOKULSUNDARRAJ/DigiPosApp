package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StockTakesCreateFragment extends Fragment {

    Button createbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stock_takes_create, container, false);



        createbtn=view.findViewById(R.id.createbtn);

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockTakesFragment productManagementFragment = new StockTakesFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}