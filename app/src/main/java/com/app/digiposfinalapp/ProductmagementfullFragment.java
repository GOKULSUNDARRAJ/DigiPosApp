package com.app.digiposfinalapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProductmagementfullFragment extends Fragment {

    CardView cardView7,cardView8,cardView9,cardView10,cardView11,cardView12;
    LinearLayout cardView1,cardView2,cardView3,cardView4,cardView5,cardView6;
    ImageView back;
    private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_productmagementfull, container, false);



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

        sharedPreferences =getActivity().getSharedPreferences("user_preferences", MODE_PRIVATE);
        boolean addProduct = sharedPreferences.getBoolean("add_product", false);
        boolean editProduct = sharedPreferences.getBoolean("edit_product", false);
        boolean price = sharedPreferences.getBoolean("price", false);
        boolean activeproduct = sharedPreferences.getBoolean("activeproduct", false);

        if (addProduct) {
            cardView1.setVisibility(View.VISIBLE);
        }else {
            cardView1.setVisibility(View.GONE);
        }

        if (editProduct) {
            cardView2.setVisibility(View.VISIBLE);
        }else {
            cardView2.setVisibility(View.GONE);
        }

        if (price) {
            cardView3.setVisibility(View.VISIBLE);
        }else {
            cardView3.setVisibility(View.GONE);
        }

        if (activeproduct) {
            cardView4.setVisibility(View.VISIBLE);
        }else {
            cardView4.setVisibility(View.GONE);
        }




        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
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
                BarCodeScanFragmentNewSearch productManagementFragment = new BarCodeScanFragmentNewSearch();
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
                PriceSubFragment productManagementFragment = new PriceSubFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveCountProductFragment productManagementFragment = new ActiveCountProductFragment();
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
                AddDepartmentFragment productManagementFragment = new AddDepartmentFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

            }
        });

        cardView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductManagmentSearchFragment productManagementFragment = new ProductManagmentSearchFragment();
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
                SupplierFragment productManagementFragment = new SupplierFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();

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