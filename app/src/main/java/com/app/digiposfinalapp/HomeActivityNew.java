package com.app.digiposfinalapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivityNew extends AppCompatActivity {


    LinearLayout layout1,layout2,layout3;

    ImageView menu1,menu2,menu3;
    TextView text1,text2,text3;

    private static final int CONNECT_ID = 0;
    private static final int DISCO_ID = 1;
    private static final int IMGPRNT_ID = 2;
    private static final int LSTFORMATS_ID = 3;
    private static final int MAGCARD_ID = 4;
    private static final int PRNTSTATUS_ID = 5;
    private static final int SMRTCARD_ID = 6;
    private static final int SIGCAP_ID = 7;
    private static final int SNDFILE_ID = 8;
    private static final int STRDFRMT_ID = 9;
    private static final int STATUSCHANNEL_ID = 10;
    private static final int CONNECTIONBUILDER_ID = 11;
    private static final int RECEIPT_ID = 12;
    private static final int MULTICHANNEL_ID = 13;

    private int selectedPosition = -1;
    private static final int BLUETOOTH_REQUEST_CODE = 3000;
    private static final int LOCATION_REQUEST_CODE = 4000;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);




        checkBluetoothPermission();
        enableBluetooth();

        layout1=findViewById(R.id.layout1);
        layout2=findViewById(R.id.layout2);
        layout3=findViewById(R.id.layout3);


        menu1=findViewById(R.id.menu1);
        menu2=findViewById(R.id.menu2);
        menu3=findViewById(R.id.menu3);

        text1=findViewById(R.id.textView1);
        text2=findViewById(R.id.textView2);
        text3=findViewById(R.id.textView3);

        HomeFragment bottomBarFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
        fragmentTransaction.commit();

        Drawable newDrawable = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.digipos2);
        menu1.setImageDrawable(newDrawable);

        Drawable newDrawable2 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.home11);
        menu2.setImageDrawable(newDrawable2);

        Drawable newDrawable3 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.profile1);
        menu3.setImageDrawable(newDrawable3);

        text1.setTextColor(getResources().getColor(R.color.blue1));
        text2.setTextColor(getResources().getColor(R.color.black1));
        text3.setTextColor(getResources().getColor(R.color.black1));


        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeFragment bottomBarFragment = new HomeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.commit();

                Drawable newDrawable = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.digipos2);
                menu1.setImageDrawable(newDrawable);

                Drawable newDrawable2 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.home11);
                menu2.setImageDrawable(newDrawable2);

                Drawable newDrawable3 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.profile1);
                menu3.setImageDrawable(newDrawable3);

                text1.setTextColor(getResources().getColor(R.color.blue1));
                text2.setTextColor(getResources().getColor(R.color.black1));
                text3.setTextColor(getResources().getColor(R.color.black1));

            }
        });




        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProductmagementfullFragment bottomBarFragment = new ProductmagementfullFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.commit();

                Drawable newDrawable = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.digipos1);
                menu1.setImageDrawable(newDrawable);

                Drawable newDrawable2 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.home22);
                menu2.setImageDrawable(newDrawable2);

                Drawable newDrawable3 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.profile1);
                menu3.setImageDrawable(newDrawable3);

                text1.setTextColor(getResources().getColor(R.color.black1));
                text2.setTextColor(getResources().getColor(R.color.blue1));
                text3.setTextColor(getResources().getColor(R.color.black1));

            }
        });


        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileFragment bottomBarFragment = new ProfileFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, bottomBarFragment);
                fragmentTransaction.commit();

                Drawable newDrawable = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.digipos1);
                menu1.setImageDrawable(newDrawable);

                Drawable newDrawable2 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.home11);
                menu2.setImageDrawable(newDrawable2);

                Drawable newDrawable3 = ContextCompat.getDrawable(HomeActivityNew.this, R.drawable.profile2png);
                menu3.setImageDrawable(newDrawable3);

                text1.setTextColor(getResources().getColor(R.color.black1));
                text2.setTextColor(getResources().getColor(R.color.black1));
                text3.setTextColor(getResources().getColor(R.color.blue1));

            }
        });


    }





    private boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_REQUEST_CODE);
            } else {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
                } else {
                    return true;
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Please switch on the bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                return true;
            }
        } else {
            // Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Location permission denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission required for Bluetooth scanning", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Location permission denied, show a message or handle accordingly
                Toast.makeText(this, "Location permission required for Bluetooth scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
