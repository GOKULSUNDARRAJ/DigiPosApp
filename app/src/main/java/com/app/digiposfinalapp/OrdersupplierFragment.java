package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

public class OrdersupplierFragment extends Fragment {

    String supplierName;
    AutoCompleteTextView supplierSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ordersupplier, container, false);


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

        supplierSpinner = view.findViewById(R.id.autocomplete_supplier);
        new FetchSupplierData2(getContext(), supplierSpinner).execute();

        supplierSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                supplierName = selectedSupplier.getSupplier();
                Toast.makeText(getContext(), "Selected: " + supplierName, Toast.LENGTH_SHORT).show();

                // Save the supplier to SharedPreferences
                saveSupplierToSharedPreferences(supplierName);

                // Navigate to next fragment after selection
                navigateToNextFragment();
            }
        });

        supplierSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                supplierSpinner.showDropDown();
            }
        });

        supplierSpinner.setOnClickListener(v -> supplierSpinner.showDropDown());


        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderCategoryFragment productManagementFragment = new OrderCategoryFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void saveSupplierToSharedPreferences(String supplierName) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selected_supplier", supplierName);
        editor.apply();

        Log.d("OrdersupplierFragment", "Saved supplier to SharedPreferences: " + supplierName);
    }

    private void navigateToNextFragment() {
        BarCodeScanOrderCreateSerachFragment addOrderFragment = new BarCodeScanOrderCreateSerachFragment();

        // Create bundle and add supplier data
        Bundle args = new Bundle();
        args.putString("selected_supplier", supplierName);
        addOrderFragment.setArguments(args);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, addOrderFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}