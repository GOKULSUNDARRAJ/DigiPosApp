package com.app.digiposfinalapp;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivityNew extends AppCompatActivity {


    LinearLayout layout1,layout2,layout3;

    ImageView menu1,menu2,menu3;
    TextView text1,text2,text3;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_new);


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


}
