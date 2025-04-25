package com.app.digiposfinalapp;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class ProductManagmentSearchFragment extends Fragment {

    private FruitAdapter adapter;

    ImageView back;
    Spinner departmentSpinner, subdepartmentSpinner,supplierSpinner,brandSpinner,vatSpinner;
    int subdepartmentId, departmentId,BrandDone;
    String supplierName;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_product_managment_search, container, false);

        departmentSpinner = view.findViewById(R.id.spinner_department);
        subdepartmentSpinner = view.findViewById(R.id.spinner_subdepartment);
        supplierSpinner=view.findViewById(R.id.spinner_spuulier);
        brandSpinner=view.findViewById(R.id.spinner_brand);

        new FetchDepartmentData(getContext(), departmentSpinner).execute();
        new FetchSubDepartmentData(getContext(), subdepartmentSpinner).execute();
        new FetchSupplierData(getContext(), supplierSpinner).execute();
        new FetchBrandData(getContext(), brandSpinner).execute();

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Departmentspinner selectedDepartment = (Departmentspinner) parent.getItemAtPosition(position);
                departmentId = selectedDepartment.getId(); // Get the ID of the selected department
                String departmentName = selectedDepartment.getDepartment(); // Get the name of the selected department

                // Display selected department information
                Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + departmentId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        subdepartmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubDepartmentspinner selectedDepartment = (SubDepartmentspinner) parent.getItemAtPosition(position);
                subdepartmentId = selectedDepartment.getId(); // Get the ID of the selected department
                String departmentName = selectedDepartment.getSubDepartment(); // Get the name of the selected department

                // Display selected department information
                Toast.makeText(view.getContext(), "Selected: " + departmentName + " (ID: " + subdepartmentId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SupplierSpinner selectedSupplier = (SupplierSpinner) parent.getItemAtPosition(position);
                int supplierId = selectedSupplier.getId(); // Get the ID of the selected supplier
                supplierName = selectedSupplier.getSupplier(); // Get the name of the selected supplier

                // Display selected supplier information
                Toast.makeText(view.getContext(), "Selected: " + supplierName + " (ID: " + supplierId + ")", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BrandSpinner selectedBrand = (BrandSpinner) parent.getItemAtPosition(position);
                int brandId = selectedBrand.getId(); // Get the ID of the selected brand
                String brandName = selectedBrand.getBrand();
                BrandDone = selectedBrand.getId();
                // Get the name of the selected brand
                // Display selected brand information
                Toast.makeText(view.getContext(), "Selected: " + brandName + " (ID: " + brandId + ")", Toast.LENGTH_SHORT).show();
                // Optionally, you can perform additional actions based on the selected brand
                // For example, you can save the selected brand's ID or name for further use
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
                Toast.makeText(parent.getContext(), "No brand selected", Toast.LENGTH_SHORT).show();
            }
        });

        back=view.findViewById(R.id.imageView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductmagementfullFragment bottomBarFragment = new ProductmagementfullFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
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
