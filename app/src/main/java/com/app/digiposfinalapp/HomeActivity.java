package com.app.digiposfinalapp;



import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    CardView inventortcard4,scancard,scancard58,inventortcard8,scancard5,scancard588,inventortcard88,inventortcard,inventortcard88y,scancard588y;

    private NavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle navigation view item clicks here
                int itemId = menuItem.getItemId();

                if (itemId == R.id.navigation_home) {
                    // Handle Home menu item click
                } else if (itemId == R.id.navigation_Products) {
                    // Handle Profile menu item click
                    Intent intent = new Intent(HomeActivity.this, ProductActivity.class);

                    startActivity(intent); // Start MainActivity with the intent
                }
                else if (itemId == R.id.navigation_Department) {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    // Start MainActivity with the intent
                }else if (itemId == R.id.navigation_Supplier) {
                    Intent intent = new Intent(HomeActivity.this, SupplierActivity.class);
                    startActivity(intent);
                    // Start MainActivity with the intent
                }else {
                    // Handle other menu item clicks if needed
                }

                // Close the navigation drawer
                menuItem.setChecked(true);
                navigationView.cancelBackProgress();

                return true;
            }
        });

        drawerLayout =findViewById(R.id.drawer_layout);
        inventortcard4=findViewById(R.id.inventortcard4);
        scancard58=findViewById(R.id.scancard58);
        inventortcard8=findViewById(R.id.inventortcard8);
        inventortcard88=findViewById(R.id.inventortcard88);
        inventortcard=findViewById(R.id.inventortcard);
        inventortcard88y=findViewById(R.id.inventortcard88y);
        scancard588y=findViewById(R.id.scancard588y);

        scancard588y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,ProductReduce.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        inventortcard88y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,StockInActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        inventortcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, BrandActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        inventortcard88.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, StockUpdateActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        scancard588=findViewById(R.id.scancard588);
        scancard588.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PriceChangeActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        scancard5=findViewById(R.id.scancard5);

        scancard5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SupplierActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        inventortcard8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PromotionActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        scancard=findViewById(R.id.scancard);
        scancard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });
        inventortcard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        scancard58.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ChillerActivity.class);
                startActivity(intent); // Start MainActivity with the intent
            }
        });

        ImageView imageView =findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



    }

    public void gotoAddressactivity(View view) {
        startActivity(new Intent(view.getContext(),AddressActivity.class));
    }
}