package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PromotionDetailsFragment extends Fragment {

    ImageView imageView;

    LinearLayout conditionlayout,rewardlayout;

    TextView conditiontxt,rewardtxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_promotion_details, container, false);
        imageView=view.findViewById(R.id.imageView);

        conditionlayout=view.findViewById(R.id.conditionlayout1);
        rewardlayout=view.findViewById(R.id.rewardslayout1);

        conditiontxt=view.findViewById(R.id.conditiontxt);
        rewardtxt=view.findViewById(R.id.rewardtxt);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromotionFragment productManagementFragment = new PromotionFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });


        rewardlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardlayout.setBackgroundResource(R.drawable.blueroundsmallbg);
                conditionlayout.setBackgroundResource(R.drawable.whiteroundsmallbg);
                rewardtxt.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                conditiontxt.setTextColor(ContextCompat.getColor(getContext(), R.color.dgblue));

            }
        });

        conditionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewardlayout.setBackgroundResource(R.drawable.whiteroundsmallbg);
                conditionlayout.setBackgroundResource(R.drawable.blueroundsmallbg);

                rewardtxt.setTextColor(ContextCompat.getColor(getContext(), R.color.dgblue));
                conditiontxt.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            }
        });

        return view;
    }


}