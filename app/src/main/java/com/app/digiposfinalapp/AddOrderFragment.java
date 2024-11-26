package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddOrderFragment extends Fragment {
    Spinner supplierSpinner;
    String supplierName;
    boolean isUserInteracting = false; // Flag to track if the user is actually interacting
    private String orderID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_order, container, false);

        if (getArguments() != null) {
            orderID = getArguments().getString("orderID"); // Get the OrderID
            Log.d("AddOrderFragment", "Received OrderID: " + orderID);
        }

        supplierSpinner = view.findViewById(R.id.spinner_spuulier);

        new FetchSupplierData(getContext(), supplierSpinner).execute();

        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserInteracting && position > 0) { // Only proceed if the user is actually interacting and has selected an item beyond the placeholder
                    SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                    int supplierId = selectedSupplier.getId();
                    supplierName = selectedSupplier.getSupplier();

                    // Display selected supplier information
                    Toast.makeText(view.getContext(), "Selected: " + supplierName + " (ID: " + supplierId + ")", Toast.LENGTH_SHORT).show();

                    BarCodeScanOrderCreateFragment productManagementFragment = new BarCodeScanOrderCreateFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("supplierName", supplierName);
                    bundle.putString("orderID", orderID);

                    productManagementFragment.setArguments(bundle);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        // Set listener to track user interaction with spinner
        supplierSpinner.setOnTouchListener((v, event) -> {
            isUserInteracting = true;
            return false;
        });

        return view;
    }
}
