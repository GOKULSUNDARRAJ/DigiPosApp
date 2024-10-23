package com.app.digiposfinalapp;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;

public class TopSaleProductFragment extends Fragment {

    ImageView back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_top_sale_product,container, false);

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

        // on below line we are initializing our variables.
        EditText pickDateBtn = view.findViewById(R.id.startdate);

        pickDateBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // on below line we are getting
                    // the instance of our calendar.
                    final Calendar c = Calendar.getInstance();

                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our text view.

                                    pickDateBtn.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }
                            },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();


                }
            }
        });




        // on below line we are initializing our variables.
        EditText enddate =view.findViewById(R.id.enddate);

        enddate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private boolean hasFocusedOnce = false; // Variable to track first focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // on below line we are getting
                    // the instance of our calendar.
                    final Calendar c = Calendar.getInstance();
                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our text view.

                                    enddate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show toaa
                    // display our date picker dialog.
                    datePickerDialog.show();
                }
            }
        });



        return view;
    }

}
