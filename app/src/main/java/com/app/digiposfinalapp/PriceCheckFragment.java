package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class PriceCheckFragment extends Fragment {

    ImageView Cameraimg;

    EditText barcodeedt;
    String barcode1;

    private static final String TAG = "PriceCheckFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_pice_check, container, false);

        Cameraimg=view.findViewById(R.id.Camera);

        Cameraimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanPriceCheckFragment productManagementFragment = new BarCodeScanPriceCheckFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        barcodeedt=view.findViewById(R.id.barcodeedt);
        barcodeedt.setText(barcode1);




        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            Log.d(TAG, "Received Barcode: " + barcode1);
        }
    }
}