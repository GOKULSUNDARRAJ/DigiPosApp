package com.app.digiposfinalapp;


import static android.content.Context.PRINT_SERVICE;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PriceReduceFragment extends Fragment {

    private static final String TAG = "PriceReduceFragment";
    String barcode1, price1, productDescription1, vat1, CurrentStock1, margin1, Expiry_date1;
    LinearLayout layoutToConvert;

    EditText barcodeedt1, productdescriptionedt1, priceedt1, vatedt1, stockedt1, marginedt1, expiry_date1edt1,reducedpriceedt;
    EditText editTextPageCount;

    Spinner vatSpinner;
    String vat;
    String vatValue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_price_reduce, container, false);



                ImageView home=view.findViewById(R.id.home);
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

        // Find the NestedScrollView
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Get the BottomNavigationView from the MainActivity"

        LinearLayout bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);


        bottomNavigationView.setVisibility(View.GONE);

        ImageView backbtn = view.findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceSubFragment productManagementFragment = new PriceSubFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
                bottomNavigationView.animate().translationY(0).setDuration(300);
            }
        });



        barcodeedt1 = view.findViewById(R.id.barcodeedt);
        barcodeedt1.setText(barcode1);

        productdescriptionedt1 = view.findViewById(R.id.productdescription);
        productdescriptionedt1.setText(productDescription1);

        priceedt1 = view.findViewById(R.id.priceedt);
        priceedt1.setText(price1);

        vatSpinner =view.findViewById(R.id.vat_spinner);

        new FetchVatEditData(getActivity(), vatSpinner, vat1).execute();

        vatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                VatType selectedVatType = (VatType) parent.getItemAtPosition(position);
                int vatId = selectedVatType.getId(); // Get the ID of the selected VAT type
                vatValue = selectedVatType.getVat(); // Get the VAT percentage value
                int vatDone = selectedVatType.getDone(); // Get the status of the selected VAT type


                // Display selected VAT information
                // Toast.makeText(view.getContext(), "Selected VAT: " + vatValue + " (ID: " + vatId + ")", Toast.LENGTH_SHORT).show();

                // Optionally, you can perform additional actions based on the selected VAT type
                // For example, you can save the selected VAT's ID or value for further use
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
                Toast.makeText(parent.getContext(), "No VAT type selected", Toast.LENGTH_SHORT).show();
            }
        });



        stockedt1 = view.findViewById(R.id.stockedt);
        stockedt1.setText(CurrentStock1);

        marginedt1 = view.findViewById(R.id.marginedt);
        marginedt1.setText(margin1);



        expiry_date1edt1 = view.findViewById(R.id.expiry_dateedt1);



        editTextPageCount = view.findViewById(R.id.lablecount);
        Button printButton = view.findViewById(R.id.printbtn);


        reducedpriceedt=view.findViewById(R.id.reducedpriceedt);

        // Example shop bill content
        String shopName = "My Shop Name";

        String billContent =
                "Item       Qty      Price\n" +
                        "-------------------------\n" +
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
                    Toast.makeText(getContext(), "Please enter the number of pages", Toast.LENGTH_SHORT).show();
                    return;
                }

                int numberOfPages;
                try {
                    numberOfPages = Integer.parseInt(input);
                    if (numberOfPages <= 0) {
                        Toast.makeText(getContext(), "Enter a valid number of pages", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
                    return;
                }

                layoutToConvert = view.findViewById(R.id.layoutToConvert);
                try {
                    Bitmap barcodeBitmap = generateBarcode(barcode1, 400, 100);
                    ImageView barcodeImageView = view.findViewById(R.id.barcode_image);
                    barcodeImageView.setImageBitmap(barcodeBitmap);

                    TextView billTitle = view.findViewById(R.id.bill_title);
                    billTitle.setText("REDUCED");
                    TextView barcode2 = view.findViewById(R.id.barcode2);
                    barcode2.setText(barcode1);

                    TextView pricereduced1 = view.findViewById(R.id.pricereduced);
                    pricereduced1.setText("Now " + reducedpriceedt.getText().toString());

                    TextView price1 = view.findViewById(R.id.priceedt45);
                    price1.setText("Was " + priceedt1.getText().toString());


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // Convert layout to image

                convertLayoutToImage(layoutToConvert);  // Replace with your actual layout ID
            }
        });



        reducedpriceedt.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a custom pattern
                decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setGroupingUsed(true); // Enable grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }



            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    reducedpriceedt.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        reducedpriceedt.setText(currentText);
                        reducedpriceedt.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        reducedpriceedt.setText("");
                    }

                    reducedpriceedt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });







        priceedt1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a custom pattern
                decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setGroupingUsed(true); // Enable grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    priceedt1.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        priceedt1.setText(currentText);
                        priceedt1.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        priceedt1.setText("");
                    }

                    priceedt1.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        marginedt1.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a custom pattern
                decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setGroupingUsed(true); // Enable grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    marginedt1.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        marginedt1.setText(currentText);
                        marginedt1.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        marginedt1.setText("");
                    }

                    marginedt1.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        EditText costedt=view.findViewById(R.id.costedt);

        costedt.addTextChangedListener(new TextWatcher() {
            private String currentText = "";
            private DecimalFormat decimalFormat;

            {
                // Initialize DecimalFormat with a custom pattern
                decimalFormat = new DecimalFormat("#,##0.00");
                decimalFormat.setGroupingUsed(true); // Enable grouping (thousands separator)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentText)) {
                    costedt.removeTextChangedListener(this);

                    // Remove non-numeric characters
                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString) / 100;
                        currentText = decimalFormat.format(parsed);
                        costedt.setText(currentText);
                        costedt.setSelection(currentText.length());
                    } else {
                        currentText = "";
                        costedt.setText("");
                    }

                    costedt.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        EditText expiry_dateedt1=view.findViewById(R.id.expiry_dateedt1);

        expiry_dateedt1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Get the current date (today's date)
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // Create the DatePickerDialog with the current date
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Use String.format to ensure proper zero-padding for month and day
                            String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                            expiry_dateedt1.setText(formattedDate); // Set the selected date
                        }
                    }, year, month, day); // Initialize with the current date

                    // Set the minimum date (today's date) to prevent selecting past dates
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

                    // Set OnDismissListener to clear focus when the dialog is dismissed
                    datePickerDialog.setOnDismissListener(dialog -> {
                        expiry_dateedt1.clearFocus(); // Cancel focus on the EditText
                    });

                    // Show the DatePickerDialog
                    datePickerDialog.show();
                }
            }
        });

        expiry_dateedt1.setText(convertToReadableDate(Expiry_date1));

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

    public String convertToReadableDate(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            return ""; // Return empty if no input
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()); // Input format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Output format

        try {
            Date date = inputFormat.parse(inputDate); // Convert to Date object
            return outputFormat.format(date); // Convert back to formatted string
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty in case of error
        }
    }



    private void convertLayoutToImage(View layout) {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);

        // Convert Bitmap to ByteArray
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Send the image to the Activity
        Intent intent = new Intent(getActivity(), DisplayImageActivity.class);
        intent.putExtra("image", byteArray);
        startActivity(intent);
    }

    private void saveImage(Bitmap bitmap) {
        String filename = "layout_image_" + System.currentTimeMillis() + ".png";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, filename);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(getContext(), "Image saved in Downloads: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private Bitmap generateBarcode(String data, int width, int height) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height, hints);

        int bitmapWidth = bitMatrix.getWidth();
        int bitmapHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        return bitmap;
    }


}
