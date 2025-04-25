package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import static com.fasterxml.jackson.databind.util.ClassUtil.getPackageName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {

    LinearLayout cardView1, cardView2, cardView3, cardView4, cardView5, cardView6, cardView7,
            cardView8, cardView9, cardView10, cardView11, cardView12, cardView13, cardView14, cardView15, cardView16;

    private DrawerLayout drawerLayout;

    private ImageView drawerToggleImageView;

    private SharedPreferences sharedPreferences;
    private PreferenceManager preferenceManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        cardView1 = view.findViewById(R.id.card1);
        cardView2 = view.findViewById(R.id.card2);
        cardView3 = view.findViewById(R.id.card3);
        cardView4 = view.findViewById(R.id.card4);
        cardView5 = view.findViewById(R.id.card5);
        cardView6 = view.findViewById(R.id.card6);
        cardView7 = view.findViewById(R.id.card7);
        cardView8 = view.findViewById(R.id.card8);
        cardView9 = view.findViewById(R.id.card9);
        cardView10 = view.findViewById(R.id.card10);
        cardView11 = view.findViewById(R.id.card11);
        cardView12 = view.findViewById(R.id.card12);
        cardView13 = view.findViewById(R.id.card13);
        cardView14 = view.findViewById(R.id.card14);
        cardView15 = view.findViewById(R.id.card15);
        cardView16 = view.findViewById(R.id.card16);


        drawerLayout = view.findViewById(R.id.drawer_layout);
        drawerToggleImageView = view.findViewById(R.id.imageView); // Initialize ImageView

        NavigationView navigationView = view.findViewById(R.id.navigation_view);

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


        sharedPreferences = getActivity().getSharedPreferences("user_preferences", MODE_PRIVATE);
        // Retrieve the state of each switch from SharedPreferences
        boolean productManagement = sharedPreferences.getBoolean("product_management", false);
        boolean stockManagement = sharedPreferences.getBoolean("stock_management", false);
        boolean orderManagement = sharedPreferences.getBoolean("order_management", false);
        boolean deliveryManagement = sharedPreferences.getBoolean("delivery_management", false);
        boolean promotionManagement = sharedPreferences.getBoolean("promotion_management", false);
        boolean reportManagement = sharedPreferences.getBoolean("report_management", false);
        boolean lable_management = sharedPreferences.getBoolean("lable_management", false);
        boolean templog_management = sharedPreferences.getBoolean("templog_management", false);

        // Check if any switch is enabled and show a Toast
        if (productManagement) {
            cardView1.setVisibility(View.VISIBLE);
        } else {
            cardView1.setVisibility(View.GONE);
        }

        if (stockManagement) {
            cardView2.setVisibility(View.VISIBLE);
        } else {
            cardView2.setVisibility(View.GONE);
        }

        if (orderManagement) {
            cardView5.setVisibility(View.VISIBLE);
        } else {
            cardView5.setVisibility(View.GONE);
        }

        if (deliveryManagement) {
            cardView10.setVisibility(View.VISIBLE);
        } else {
            cardView10.setVisibility(View.GONE);
        }

        if (promotionManagement) {
            cardView7.setVisibility(View.VISIBLE);
        } else {
            cardView7.setVisibility(View.GONE);
        }

        if (reportManagement) {
            cardView6.setVisibility(View.VISIBLE);
        } else {
            cardView6.setVisibility(View.GONE);
        }

        if (lable_management) {
            cardView8.setVisibility(View.VISIBLE);
        } else {
            cardView8.setVisibility(View.GONE);
        }

        if (templog_management) {
            cardView16.setVisibility(View.VISIBLE);
        } else {
            cardView16.setVisibility(View.GONE);
        }

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
                StockSubFragment productManagementFragment = new StockSubFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });


        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderCategoryFragment productManagementFragment = new OrderCategoryFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });


        cardView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryManagmentFragment productManagementFragment = new DeliveryManagmentFragment();
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
                SubnewpromotionFragment productManagementFragment = new SubnewpromotionFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportSubCategoryFragment productManagementFragment = new ReportSubCategoryFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        cardView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentNewSearchLablePrintsub productManagementFragment = new BarCodeScanFragmentNewSearchLablePrintsub();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });


        cardView16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TemperatureLogsFragment productManagementFragment = new TemperatureLogsFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
                bottomNavigationView.animate().translationY(0).setDuration(300);
            }
        });

        cardView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserControlFragment productManagementFragment = new UserControlFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });





        cardView13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                StockSubFragment productManagementFragment = new StockSubFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
                bottomNavigationView.animate().translationY(0).setDuration(300);
            }
        });

        cardView14.setOnClickListener(new View.OnClickListener() {
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

        cardView15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateaNewOrderFragment productManagementFragment = new CreateaNewOrderFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
                bottomNavigationView.animate().translationY(0).setDuration(300);
            }
        });





        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        String savedUsertype = sharedPreferences.getString(Constants.KEY_USERTYPE, null);


        if (savedUsertype.equalsIgnoreCase("admin")) {
            cardView9.setVisibility(View.VISIBLE);
        } else {
            cardView9.setVisibility(View.GONE);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle item selection using if-else
                if (item.getItemId() == R.id.nav_settings) {
                    if (savedUsername.equals("ADMIN")) {
                        SettingsFragment productManagementFragment = new SettingsFragment();
                        FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                        fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                        fragmentTransaction.commit();
                    } else {
                        Toast.makeText(getContext(), "Only Admin can access this settings", Toast.LENGTH_SHORT).show();
                    }

                } else if (item.getItemId() == R.id.nav_logout) {
                    showLogoutDialog();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Find the TextView in the header layout
        View headerView = navigationView.getHeaderView(0);

        TextView navHeaderTextView = headerView.findViewById(R.id.nav_header_username);
        navHeaderTextView.setText(savedUsername);

        TextView nav_header_version = headerView.findViewById(R.id.nav_header_version);

        try {
            // Use requireContext() to get the fragment's context
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            String versionInfo =  pInfo.versionName;
            nav_header_version.setText(versionInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            nav_header_version.setText("vUnknown");
        }



        return view;
    }


    public void showLogoutDialog() {
        CustomDialoglogout cdd = new CustomDialoglogout(getContext());
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.show();
    }


}
