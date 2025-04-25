package com.app.digiposfinalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.digiposfinalapp.util.SettingsHelper;
import com.app.digiposfinalapp.util.UIHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.device.ZebraIllegalArgumentException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import net.sourceforge.jtds.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class CustomDialogforTemplogs extends Dialog {

    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private EditText chillerNameEditText;
    private Button cancelButton, saveButton;
    private Context context;
    private FragmentManager fragmentManager; // Add this

    // Modify constructor to accept FragmentManager
    public CustomDialogforTemplogs(@NonNull Context context, FragmentManager fragmentManager) {
        super(context);
        this.context = context;
        this.fragmentManager = fragmentManager;
        setCanceledOnTouchOutside(false); // Prevent cancel on outside touch
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logouttemplog);



        // Ensure we have an activity context
        if (context instanceof AppCompatActivity) {
            this.fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        }


        // Initialize views
        chillerNameEditText = findViewById(R.id.chiller_name_edittext);
        cancelButton = findViewById(R.id.cancel);
        saveButton = findViewById(R.id.save);

        // Get SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;
        dbPassword1 = Constants.PASSWORD;

        // Set click listeners
        cancelButton.setOnClickListener(v -> navigateToHomeFragment());

        saveButton.setOnClickListener(v -> {
            String chillerName = chillerNameEditText.getText().toString().trim();
            if (chillerName.isEmpty()) {
                Toast.makeText(context, "Please enter chiller name", Toast.LENGTH_SHORT).show();
                return;
            }
            insertChillerData(chillerName);
        });
    }

    private void insertChillerData(String chillerName) {
        new Thread(() -> {
            Connection connection = null;
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String connectionString = String.format(
                        "jdbc:jtds:sqlserver://%s:%s/%s;user=%s;password=%s;",
                        ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1
                );

                connection = DriverManager.getConnection(connectionString);
                String query = "INSERT INTO [STAR_RETAIL].[dbo].[tbl_Chiller] ([Chiller_Name]) VALUES (?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, chillerName);
                int rowsAffected = preparedStatement.executeUpdate();

                ((Activity) context).runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(context, "Chiller data saved successfully", Toast.LENGTH_SHORT).show();
                        navigateToHomeFragment();
                        dismiss();
                    } else {
                        Toast.makeText(context, "Failed to save chiller data", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                try {
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void navigateToHomeFragment() {
        if (context instanceof AppCompatActivity && fragmentManager != null) {
            // Clear back stack first to avoid stacking fragments
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            TemperatureLogsFragment fragment = new TemperatureLogsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
        }
        dismiss(); // Close the dialog
    }
}