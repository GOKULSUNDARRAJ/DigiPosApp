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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

public class SettingsFragment extends Fragment {


    private static final String[] SWITCH_KEYS = {
            "switch1_state", "switch2_state", "switch3_state", "switch4_state",
            "switch5_state", "switch6_state", "switch7_state", "switch8_state",
            "switch9_state", "switch10_state", "switch11_state", "switch12_state",
            "switch13_state","switch14_state","switch15_state"
    };


    private PreferenceManager preferenceManager;
    private Switch[] switches;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_settings, container, false);


        preferenceManager = new PreferenceManager(getContext());

        // Initialize switches array
        switches = new Switch[15];
        switches[0] = view.findViewById(R.id.switch1);
        switches[1] = view.findViewById(R.id.switch2);
        switches[2] = view.findViewById(R.id.switch3);
        switches[3] = view.findViewById(R.id.switch4);
        switches[4] = view.findViewById(R.id.switch5);
        switches[5] = view.findViewById(R.id.switch6);
        switches[6] = view.findViewById(R.id.switch7);
        switches[7] = view.findViewById(R.id.switch8);
        switches[8] = view.findViewById(R.id.switch9);
        switches[9] = view.findViewById(R.id.switch10);
        switches[10] = view.findViewById(R.id.switch11);
        switches[11] = view.findViewById(R.id.switch12);
        switches[12] = view.findViewById(R.id.switch13);
        switches[13] = view.findViewById(R.id.switch14);
        switches[14] = view.findViewById(R.id.switch15);


        // Set initial states and listeners dynamically
        for (int i = 0; i < switches.length; i++) {
            int index = i; // Final variable for use in lambda
            switches[i].setChecked(preferenceManager.getSwitchState(SWITCH_KEYS[i]));
            switches[i].setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                preferenceManager.saveSwitchState(SWITCH_KEYS[index], isChecked);
            });
        }

        ImageView back=view.findViewById(R.id.imageView);
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