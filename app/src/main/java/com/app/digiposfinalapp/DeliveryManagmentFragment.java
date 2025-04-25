package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class DeliveryManagmentFragment extends Fragment {

    String supplierName;

    AutoCompleteTextView supplierSpinner;
    boolean isUserInteracting = false; // Flag to track if the user is actually interacting
    private String orderID;

    EditText Referenceedt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_managment, container, false);


        ImageView home = view.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });
        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment productManagementFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });

        supplierSpinner = view.findViewById(R.id.autocomplete_supplier);
        new FetchSupplierData2(getContext(), supplierSpinner).execute();

        supplierSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                supplierName = selectedSupplier.getSupplier();
                Toast.makeText(getContext(), "Selected: " + supplierName, Toast.LENGTH_SHORT).show();
            }
        });
        supplierSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                supplierSpinner.showDropDown(); // Show all items when focused
            }
        });
        supplierSpinner.setOnClickListener(v -> supplierSpinner.showDropDown()); // Show all items when clicked

        Referenceedt = view.findViewById(R.id.Referenceedt);
        Button next = view.findViewById(R.id.nextbtn);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String referenceId = Referenceedt.getText().toString().trim();

                if (referenceId.isEmpty()) {
                    Toast.makeText(getContext(), "Reference Id is Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If supplier is not selected manually, get the first item as default
                if (supplierName == null || supplierName.isEmpty()) {
                    if (supplierSpinner.getAdapter() != null && supplierSpinner.getAdapter().getCount() > 0) {
                        SupplierSpinner defaultSupplier = (SupplierSpinner) supplierSpinner.getAdapter().getItem(0); // or 1 if you want second item
                        supplierName = defaultSupplier.getSupplier();
                        //  Toast.makeText(getContext(), "Using default supplier: " + supplierName, Toast.LENGTH_SHORT).show();
                    } else {
                        //  Toast.makeText(getContext(), "No suppliers available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Save values in SharedPreferences
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("supplierName", supplierName);
                editor.putString("ReferenceId", referenceId);
                editor.apply();

                // Navigate to the next fragment
                DelivermanagmentsearchFragment productManagementFragment = new DelivermanagmentsearchFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            // Disable back press
            ((AppCompatActivity) context).getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Do nothing to prevent back press
                }
            });
        }
    }

}