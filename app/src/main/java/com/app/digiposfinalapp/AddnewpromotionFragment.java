package com.app.digiposfinalapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class AddnewpromotionFragment extends Fragment {

    EditText startdate, enddatedt;
    TextView bacrcodeedt;
    RadioGroup radioGroupDateOptions;
    RadioButton radioMonthEnd, radioYearEnd, radioNoEndDate;

    private String description, barcode, subDepartment, supplier, department, vat, ageLimit, Itemcode,
            Brand, UnitPerCase, CostPerCase, Price, sellingprice, Margin, plu, outerBarcode, price, addbarcode,
            endDate, startDate, dd_Price, ddpoint, manageStock, weight, capacitys, currentStock1, qty, minStock, reorderleve, Markup, discount,
            expiry_date, buyPrice, CasePrice, CaseUnit, VatValue1;

    EditText priceedt1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addnewpromotion2, container, false);

        ImageView back = view.findViewById(R.id.imageViewback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentNewPromotionsearch priceSubFragment = new BarCodeScanFragmentNewPromotionsearch();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, priceSubFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        bacrcodeedt = view.findViewById(R.id.bacrcodeedt);
        bacrcodeedt.setText(barcode);

        startdate = view.findViewById(R.id.startdate);
        enddatedt = view.findViewById(R.id.enddatedt);

        // Initialize RadioGroup and RadioButtons
        radioGroupDateOptions = view.findViewById(R.id.radioGroup); // Make sure this ID matches your XML
        radioMonthEnd = view.findViewById(R.id.radioMonthEnd); // Replace with your actual RadioButton IDs
        radioYearEnd = view.findViewById(R.id.radioYearEnd);
        radioNoEndDate = view.findViewById(R.id.radioNoEndDate);

        // Set default dates if not provided
        if (startDate == null || startDate.isEmpty()) {
            Calendar c = Calendar.getInstance();
            startdate.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));
            enddatedt.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR));
        }

        radioGroupDateOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (checkedId == R.id.radioMonthEnd) { // MONTH END selected
                    // Calculate last day of the current month
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    String monthEndDate = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (month + 1) + "-" + year;
                    enddatedt.setText(monthEndDate);
                }
                else if (checkedId == R.id.radioYearEnd) { // YEAR END selected
                    // Set to last day of the year (31-12-YYYY)
                    String yearEndDate = "31-12-" + year;
                    enddatedt.setText(yearEndDate);
                }
                else if (checkedId == R.id.radioNoEndDate) { // NO END DATE selected
                    // Add 10 years to the current date
                    calendar.add(Calendar.YEAR, 10); // Move 10 years ahead
                    String tenYearsLater = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
                    enddatedt.setText(tenYearsLater);
                }
            }
        });

        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(startdate);
                }
            }
        });

        enddatedt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(enddatedt);
                }
            }
        });

        priceedt1=view.findViewById(R.id.salepriceedt);

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

        return view;
    }

    // Helper method to show DatePickerDialog
    private void showDatePicker(final EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Do nothing to prevent back press
                }
            });
        }
    }


}