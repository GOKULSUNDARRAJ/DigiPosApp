package com.app.digiposfinalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] SWITCH_KEYS = {
            "switch1_state", "switch2_state", "switch3_state", "switch4_state",
            "switch5_state", "switch6_state", "switch7_state", "switch8_state",
            "switch9_state", "switch10_state", "switch11_state", "switch12_state",
            "switch13_state","switch14_state","switch15_state"
    };


    private PreferenceManager preferenceManager;
    private Switch[] switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferenceManager = new PreferenceManager(this);

        // Initialize switches array
        switches = new Switch[15];
        switches[0] = findViewById(R.id.switch1);
        switches[1] = findViewById(R.id.switch2);
        switches[2] = findViewById(R.id.switch3);
        switches[3] = findViewById(R.id.switch4);
        switches[4] = findViewById(R.id.switch5);
        switches[5] = findViewById(R.id.switch6);
        switches[6] = findViewById(R.id.switch7);
        switches[7] = findViewById(R.id.switch8);
        switches[8] = findViewById(R.id.switch9);
        switches[9] = findViewById(R.id.switch10);
        switches[10] = findViewById(R.id.switch11);
        switches[11] = findViewById(R.id.switch12);
        switches[12] = findViewById(R.id.switch13);
        switches[13] = findViewById(R.id.switch14);
        switches[14] = findViewById(R.id.switch15);


        // Set initial states and listeners dynamically
        for (int i = 0; i < switches.length; i++) {
            int index = i; // Final variable for use in lambda
            switches[i].setChecked(preferenceManager.getSwitchState(SWITCH_KEYS[i]));
            switches[i].setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                preferenceManager.saveSwitchState(SWITCH_KEYS[index], isChecked);
            });
        }

        ImageView back=findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this,HomeActivityNew.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SettingsActivity.this,HomeActivityNew.class);
        startActivity(intent);
        finish();
    }
}