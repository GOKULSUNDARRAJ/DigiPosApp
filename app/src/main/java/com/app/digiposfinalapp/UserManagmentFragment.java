package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;


public class UserManagmentFragment extends Fragment  implements CustomSpinner.OnSpinnerEventsListener{

    private CustomSpinner spinner_fruits,spinner_username;

    private FruitAdapter adapter;

    ImageView imageView;

    CheckBox myCheckBox;

    LinearLayout button3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_user_managment, container, false);
        imageView=view.findViewById(R.id.imageView);

        spinner_fruits =view.findViewById(R.id.spinner_history);

        spinner_fruits.setSpinnerEventsListener(this);

        adapter = new FruitAdapter(getContext(), Data.getFruitList());

        spinner_fruits.setAdapter(adapter);


        spinner_username=view.findViewById(R.id.spinner_usernmae);

        spinner_username.setSpinnerEventsListener(this);

        adapter = new FruitAdapter(getContext(), Data.getFruitList());

        spinner_username.setAdapter(adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
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


        CheckBox myCheckBox =view.findViewById(R.id.myCheckBox);

// You can dynamically change the background based on its state
        myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setButtonDrawable(R.drawable.custom_checkbox);
                } else {
                    buttonView.setButtonDrawable(R.drawable.checkbox_unchecked);
                }
            }
        });


        button3=view.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagmentCreateAccountFragment productManagementFragment = new UserManagmentCreateAccountFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });


        return view;
    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinner_fruits.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit_up));
        spinner_username.setBackground(getResources().getDrawable(R.drawable.bg_spinner_fruit_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {

    }
}