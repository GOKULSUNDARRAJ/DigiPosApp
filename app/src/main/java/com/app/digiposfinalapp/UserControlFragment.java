package com.app.digiposfinalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class UserControlFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private Switch switchProductManagement;
    private Switch switchAddProduct;
    private Switch switchEditProduct;
    private Switch switchactive;


    private Switch switchPrice;
    private Switch switchPriceCheck;
    private Switch switchPriceReduce;

    // Add these new switches
    private Switch switchStock;
    private Switch switchStockInBy;
    private Switch switchStockTakes;
    private Switch switchStockadjustment;

    private Switch switchStockadjustment2;




    // Add the new switches
    private Switch switchOrder;
    private Switch switchNewOrder;

    private Switch switchdelivery;

    private Switch switchpromotion;
    private Switch switchaddpromotion;
    private Switch switchsearchpromotion;


    private Switch switchreports;


    private Switch switchlable;
    private Switch switchlable1;
    private Switch switchlable2;


    private Switch switchtemp;


    private EditText barcodeEditText;

    private SharedPreferences sharedPreferences2;


    ImageView back;

    private SharedPreferences sharedPreferencesaddress;
    private EditText barcodeEdt1, barcodeEdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_control, container, false);


        sharedPreferencesaddress = getActivity().getSharedPreferences("PrinterPrefs", Context.MODE_PRIVATE);

        // Initialize EditText fields
        barcodeEdt1 = view.findViewById(R.id.barcodeedt1);
        barcodeEdt = view.findViewById(R.id.barcodeedt);

        // Set click listeners for save buttons
        Button saveAddress1 = view.findViewById(R.id.saveaddress1);
        saveAddress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrinterAddress("self_label_address", barcodeEdt1);
            }
        });

        Button saveAddress = view.findViewById(R.id.saveaddress);
        saveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrinterAddress("promo_label_address", barcodeEdt);
            }
        });



        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("user_preferences", getContext().MODE_PRIVATE);


        // Initialize Switches
        switchProductManagement = view.findViewById(R.id.switchproductmanagment);
        switchAddProduct = view.findViewById(R.id.addproduct);
        switchEditProduct = view.findViewById(R.id.editproduct);
        switchactive= view.findViewById(R.id.active);
        switchPrice = view.findViewById(R.id.price);
        switchPriceCheck = view.findViewById(R.id.pricecheck);
        switchPriceReduce = view.findViewById(R.id.pricereduce);
        // Initialize the new switches
        switchStock = view.findViewById(R.id.switchstock);
        switchStockInBy = view.findViewById(R.id.stockinby);
        switchStockTakes = view.findViewById(R.id.stocktakes);
        switchStockadjustment = view.findViewById(R.id.switchStockadjustment);
        switchStockadjustment2= view.findViewById(R.id.switchStockadjustment2);

        // Initialize the new switches
        switchOrder = view.findViewById(R.id.switchorder);
        switchNewOrder = view.findViewById(R.id.neworder);


        switchdelivery = view.findViewById(R.id.switchdelivery);

        switchpromotion = view.findViewById(R.id.switchpromotion);
        switchaddpromotion= view.findViewById(R.id.addpromotion);
        switchsearchpromotion=view.findViewById(R.id.searchpromotion);


        switchreports=view.findViewById(R.id.switchreports);

        switchlable=view.findViewById(R.id.switchlable);
        switchlable1=view.findViewById(R.id.switchlable1);
        switchlable2=view.findViewById(R.id.switchlable2);

        switchtemp=view.findViewById(R.id.switchtemp);

        // Set the switches' state from SharedPreferences
        switchProductManagement.setChecked(sharedPreferences.getBoolean("product_management", false));
        switchAddProduct.setChecked(sharedPreferences.getBoolean("add_product", false));
        switchEditProduct.setChecked(sharedPreferences.getBoolean("edit_product", false));
        switchactive.setChecked(sharedPreferences.getBoolean("activeproduct", false));
        switchPrice.setChecked(sharedPreferences.getBoolean("price", false));
        switchPriceCheck.setChecked(sharedPreferences.getBoolean("price_check", false));
        switchPriceReduce.setChecked(sharedPreferences.getBoolean("price_reduce", false));
        // Set state for the new switches
        switchStock.setChecked(sharedPreferences.getBoolean("stock_management", false));
        switchStockInBy.setChecked(sharedPreferences.getBoolean("stock_in_by_products", false));
        switchStockTakes.setChecked(sharedPreferences.getBoolean("stock_takes", false));
        switchStockadjustment.setChecked(sharedPreferences.getBoolean("card3_visibility", false));
        switchStockadjustment2.setChecked(sharedPreferences.getBoolean("card4_visibility", false));

        // Set state for the new switches
        switchOrder.setChecked(sharedPreferences.getBoolean("order_management", false));
        switchNewOrder.setChecked(sharedPreferences.getBoolean("new_order", false));


        switchdelivery.setChecked(sharedPreferences.getBoolean("delivery_management", false));

        switchpromotion.setChecked(sharedPreferences.getBoolean("promotion_management", false));
        switchaddpromotion.setChecked(sharedPreferences.getBoolean("add_promotion", false));
        switchsearchpromotion.setChecked(sharedPreferences.getBoolean("search_promotion", false));

        switchreports.setChecked(sharedPreferences.getBoolean("report_management", false));

        switchlable.setChecked(sharedPreferences.getBoolean("lable_management", false));
        switchlable1.setChecked(sharedPreferences.getBoolean("PRINT_List", false));
        switchlable2.setChecked(sharedPreferences.getBoolean("quick_PRINT", false));


        switchtemp.setChecked(sharedPreferences.getBoolean("templog_management", false));


        switchProductManagement.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("product_management", isChecked);

            // If switchProductManagement is set to false, set the other switches to false as well
            if (!isChecked) {
                switchAddProduct.setChecked(false);
                switchEditProduct.setChecked(false);
                switchactive.setChecked(false);
                switchPrice.setChecked(false);
                switchPriceCheck.setChecked(false);
                switchPriceReduce.setChecked(false);

                // Save the state for these switches as well
                saveState("add_product", false);
                saveState("edit_product", false);
                saveState("price", false);
                saveState("price_check", false);
                saveState("price_reduce", false);
            }
        });

        switchAddProduct.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("add_product", isChecked));
        switchEditProduct.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("edit_product", isChecked));
        switchactive.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("activeproduct", isChecked));


        switchPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("price", isChecked);

            // If switchPrice is set to false, set switchPriceCheck and switchPriceReduce to false as well
            if (!isChecked) {
                switchPriceCheck.setChecked(false);
                switchPriceReduce.setChecked(false);

                // Save the state for these switches as well
                saveState("price_check", false);
                saveState("price_reduce", false);
            }
        });




        switchPriceCheck.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("price_check", isChecked));
        switchPriceReduce.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("price_reduce", isChecked));
        // Set listeners for the new switches

        switchStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("stock_management", isChecked);

            // If switchStock is set to false, set switchStockInBy and switchStockTakes to false as well
            if (!isChecked) {
                switchStockInBy.setChecked(false);
                switchStockTakes.setChecked(false);

                // Save the state for these switches as well
                saveState("stock_in_by_products", false);
                saveState("stock_takes", false);
            }
        });


        switchStockInBy.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("stock_in_by_products", isChecked));
        switchStockTakes.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("stock_takes", isChecked));
        switchStockadjustment.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("card3_visibility", isChecked));

        switchStockadjustment2.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("card4_visibility", isChecked));

        // Set listeners for the new switches
        switchOrder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("order_management", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchNewOrder.setChecked(false);

                // Save the state for switchNewOrder as well
                saveState("new_order", false);
            }
        });

        switchNewOrder.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("new_order", isChecked));





        switchdelivery.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("delivery_management", isChecked));
        // Set listeners for the new switches
        switchdelivery.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("delivery_management", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchdelivery.setChecked(false);

                // Save the state for switchNewOrder as well
                saveState("delivery_management", false);
            }
        });


        switchpromotion.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("promotion_management", isChecked));
        // Set listeners for the new switches
        switchpromotion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("promotion_management", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchpromotion.setChecked(false);
                switchaddpromotion.setChecked(false);
                switchsearchpromotion.setChecked(false);
                // Save the state for switchNewOrder as well
                saveState("promotion_management", false);
                saveState("add_promotion", false);
                saveState("search_promotion", false);
            }
        });

        switchaddpromotion.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("add_promotion", isChecked));
        // Set listeners for the new switches
        switchaddpromotion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("add_promotion", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchaddpromotion.setChecked(false);

                // Save the state for switchNewOrder as well
                saveState("add_promotion", false);
            }
        });


        switchsearchpromotion.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("search_promotion", isChecked));
        // Set listeners for the new switches
        switchsearchpromotion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("search_promotion", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchsearchpromotion.setChecked(false);

                // Save the state for switchNewOrder as well
                saveState("search_promotion", false);
            }
        });



        switchreports.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("report_management", isChecked));
        // Set listeners for the new switches
        switchreports.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("report_management", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchreports.setChecked(false);

                saveState("report_management", false);

            }
        });



        switchtemp.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("templog_management", isChecked));
        // Set listeners for the new switches
        switchtemp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("templog_management", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchtemp.setChecked(false);

                saveState("templog_management", false);

            }
        });









        switchlable.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("lable_management", isChecked));
        // Set listeners for the new switches
        switchlable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("lable_management", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchlable.setChecked(false);

                saveState("lable_management", false);

            }
        });



        switchlable1.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("PRINT_List", isChecked));
        // Set listeners for the new switches
        switchlable1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("PRINT_List", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchlable1.setChecked(false);
                saveState("PRINT_List", false);

            }
        });


        switchlable2.setOnCheckedChangeListener((buttonView, isChecked) -> saveState("quick_PRINT", isChecked));
        // Set listeners for the new switches
        switchlable2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveState("quick_PRINT", isChecked);

            // If switchOrder is set to false, set switchNewOrder to false as well
            if (!isChecked) {
                switchlable2.setChecked(false);
                saveState("quick_PRINT", false);

            }
        });









        back=view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    // Method to save the switch state in SharedPreferences
    private void saveState(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();  // Apply changes asynchronously
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



    private void savePrinterAddress(String key, EditText editText) {
        String address = editText.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, address);
        editor.apply();

        Toast.makeText(getContext(), "Address saved successfully!", Toast.LENGTH_SHORT).show();
    }
}


