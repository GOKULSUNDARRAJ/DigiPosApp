package com.app.digiposfinalapp;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {

    CardView cardView1,cardView2,cardView3,cardView4,cardView5,cardView6,cardView7,cardView8,cardView9,cardView10,cardView11,cardView12;

    private DrawerLayout drawerLayout;

    private ImageView drawerToggleImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        cardView1=view.findViewById(R.id.card1);
        cardView2=view.findViewById(R.id.card2);
        cardView3=view.findViewById(R.id.card3);
        cardView4=view.findViewById(R.id.card4);
        cardView5=view.findViewById(R.id.card5);
        cardView6=view.findViewById(R.id.card6);
        cardView7=view.findViewById(R.id.card7);
        cardView8=view.findViewById(R.id.card8);
        cardView9=view.findViewById(R.id.card9);
        cardView10=view.findViewById(R.id.card10);
        cardView11=view.findViewById(R.id.card11);
        cardView12=view.findViewById(R.id.card12);

        drawerLayout =view.findViewById(R.id.drawer_layout);
        drawerToggleImageView =view.findViewById(R.id.imageView); // Initialize ImageView

        NavigationView navigationView =view.findViewById(R.id.navigation_view);
        // Handle drawer item clicks

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                // Handle navigation item clicks here
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set an OnClickListener for the ImageView to open the DrawerLayout
        drawerToggleImageView.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductmagementfullFragment productManagementFragment = new ProductmagementfullFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StockInByProductsFragment productManagementFragment = new StockInByProductsFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaleFragment productManagementFragment = new SaleFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        cardView7.setOnClickListener(new View.OnClickListener() {
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

        cardView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagmentFragment productManagementFragment = new UserManagmentFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });



        return view;
    }
}