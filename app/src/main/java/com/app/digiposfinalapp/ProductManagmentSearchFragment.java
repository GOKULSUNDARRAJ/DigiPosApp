package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;

public class ProductManagmentSearchFragment extends Fragment implements CustomSpinner.OnSpinnerEventsListener{

    private CustomSpinner spinner_fruits;

    private FruitAdapter adapter;

    ImageView back;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_product_managment_search, container, false);

        spinner_fruits =view.findViewById(R.id.spinner_department);

        spinner_fruits.setSpinnerEventsListener(this);

        adapter = new FruitAdapter(getContext(), Data.getFruitList());



        spinner_fruits.setAdapter(adapter);

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
    public void onPopupWindowOpened(Spinner spinner) {
        spinner_fruits.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {

    }

}
