package com.app.digiposfinalapp;


import static android.content.Context.PRINT_SERVICE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class PriceReduceFragment extends Fragment {

    private static final String TAG = "PriceReduceFragment";
    String barcode1, price1, productDescription1, vat1, CurrentStock1, margin1, Expiry_date1;


    EditText barcodeedt1, productdescriptionedt1, priceedt1, vatedt1, stockedt1, marginedt1, expiry_date1edt1;
    EditText editTextPageCount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_price_reduce, container, false);


        // Find the NestedScrollView
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Get the BottomNavigationView from the MainActivity
        LinearLayout bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        // Add a scroll listener to the NestedScrollView
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check if we're scrolling down
                if (scrollY > oldScrollY) {
                    // Hide the BottomNavigationView when scrolling down
                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(300);
                } else if (scrollY < oldScrollY) {
                    // Show the BottomNavigationView when scrolling up
                    bottomNavigationView.animate().translationY(0).setDuration(300);
                }
            }
        });



        ImageView backbtn = view.findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment productManagementFragment = new HomeFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
                bottomNavigationView.animate().translationY(0).setDuration(300);
            }
        });


        Button camera = view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanPriceReduceFragment productManagementFragment = new BarCodeScanPriceReduceFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });

        barcodeedt1 = view.findViewById(R.id.barcodeedt);
        barcodeedt1.setText(barcode1);

        productdescriptionedt1 = view.findViewById(R.id.productdescription);
        productdescriptionedt1.setText(productDescription1);

        priceedt1 = view.findViewById(R.id.priceedt);
        priceedt1.setText(price1);

        vatedt1 = view.findViewById(R.id.vatedt);
        vatedt1.setText(vat1);

        stockedt1 = view.findViewById(R.id.stockedt);
        stockedt1.setText(CurrentStock1);

        marginedt1 = view.findViewById(R.id.marginedt);
        marginedt1.setText(margin1);

        expiry_date1edt1 = view.findViewById(R.id.expiry_dateedt1);
        expiry_date1edt1.setText(Expiry_date1);



        editTextPageCount =view.findViewById(R.id.lablecount);
        Button printButton =view.findViewById(R.id.printbtn);

        // Example shop bill content
        String shopName = "My Shop Name";
        String billContent =
                        "Item       Qty      Price\n" +
                        "--------------------------\n" +
                        "Apple      2        $3.00\n" +
                        "Banana     3        $2.00\n" +
                        "Orange     1        $1.50\n" +
                        "-------------------------\n" +
                        "Total:              $6.50";



        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editTextPageCount.getText().toString();
                if (input.isEmpty()) {

                    Toast.makeText(getContext(), "Please enter the number of Lable Qty", Toast.LENGTH_SHORT).show();
                    return;

                }

                int numberOfPages;
                try {
                    numberOfPages = Integer.parseInt(input);
                    if (numberOfPages <= 0) {
                        Toast.makeText(getContext(), "Enter a valid number of Lable Qty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Start printing
                PrintManager printManager = (PrintManager) getActivity().getSystemService(PRINT_SERVICE);
                printManager.print("ShopBill", new MyPrintDocumentAdapter(getContext(), shopName, billContent, numberOfPages), null);
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            barcode1 = getArguments().getString("barcode");
            price1 = getArguments().getString("Price");
            productDescription1 = getArguments().getString("description");
            vat1 = getArguments().getString("vat");
            CurrentStock1 = getArguments().getString("CurrentStock");
            margin1 = getArguments().getString("Margin");
            Expiry_date1 = getArguments().getString("Expiry_date");
            Log.d(TAG, "Received Barcode: " + Expiry_date1);
        }
    }
}