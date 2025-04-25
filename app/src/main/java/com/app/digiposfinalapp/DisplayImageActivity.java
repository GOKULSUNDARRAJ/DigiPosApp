package com.app.digiposfinalapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.digiposfinalapp.util.SettingsHelper;

import com.app.digiposfinalapp.util.UIHelper;
import com.zebra.sdk.btleComm.BluetoothLeConnection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.device.ZebraIllegalArgumentException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.IOException;

public class DisplayImageActivity extends AppCompatActivity {


    private RadioButton btRadioButton;
    private RadioButton bleRadioButton;
    private EditText macAddressEditText;
    private EditText ipAddressEditText;
    private EditText portNumberEditText;
    private EditText printStoragePath;
    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String tcpAddressKey = "ZEBRA_DEMO_TCP_ADDRESS";
    private static final String tcpPortKey = "ZEBRA_DEMO_TCP_PORT";
    private static final String PREFS_NAME = "OurSavedAddress";
    private UIHelper helper = new UIHelper(this);
    private static int TAKE_PICTURE = 1;
    private static int PICTURE_FROM_GALLERY = 2;
    private static File file = null;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        ipAddressEditText = this.findViewById(R.id.ipAddressInput);
        String ip = settings.getString(tcpAddressKey, "");
        ipAddressEditText.setText(ip);

        portNumberEditText = this.findViewById(R.id.portInput);
        String port = settings.getString(tcpPortKey, "");
        portNumberEditText.setText(port);

        macAddressEditText = this.findViewById(R.id.macInput);
        String mac = settings.getString(bluetoothAddressKey, "");
        macAddressEditText.setText(mac);

        printStoragePath = findViewById(R.id.printerStorePath);

        CheckBox cb = findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                printStoragePath.setVisibility(View.VISIBLE);
            } else {
                printStoragePath.setVisibility(View.INVISIBLE);
            }
        });

        btRadioButton = this.findViewById(R.id.bluetoothRadio);
        bleRadioButton = findViewById(R.id.bleRadio);


        RadioGroup radioGroup = this.findViewById(R.id.radioGroup23);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.bluetoothRadio) {
                toggleEditField(macAddressEditText, true);
                toggleEditField(portNumberEditText, false);
                toggleEditField(ipAddressEditText, false);
            } else if (checkedId == R.id.bleRadio){
                toggleEditField(macAddressEditText, true);
                toggleEditField(portNumberEditText, false);
                toggleEditField(ipAddressEditText, false);
            } else {
                toggleEditField(portNumberEditText, true);
                toggleEditField(ipAddressEditText, true);
                toggleEditField(macAddressEditText, false);
            }
        });

        ImageView imageView = findViewById(R.id.imageView);

        // Get the byte array from Intent
        byte[] byteArray = getIntent().getByteArrayExtra("image");

        if (byteArray != null) {
            // Convert ByteArray back to Bitmap
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            // Display the Bitmap in ImageView
            imageView.setImageBitmap(bitmap);
        }


        Button galleryButton = this.findViewById(R.id.printButton);
        galleryButton.setOnClickListener(v -> printPhotoFromExternal(bitmap));



    }


    private void toggleEditField(EditText editText, boolean set) {
        /*
         * Note: Disabled EditText fields may still get focus by some other means, and allow text input.
         *       See http://code.google.com/p/android/issues/detail?id=2771
         */
        editText.setEnabled(set);
        editText.setFocusable(set);
        editText.setFocusableInTouchMode(set);
    }

    private boolean isBluetoothSelected() {
        return btRadioButton.isChecked();
    }

    private String getMacAddressFieldText() {
        return macAddressEditText.getText().toString();
    }

    private String getTcpAddress() {
        return ipAddressEditText.getText().toString();
    }

    private String getTcpPortNumber() {
        return portNumberEditText.getText().toString();
    }

    private boolean isBleSelected() {
        return bleRadioButton.isChecked();
    }



    private void printPhotoFromExternal(final Bitmap bitmap) {
        helper.showLoadingDialog("Sending image to printer");
        new Thread(() -> {
            try {
                getAndSaveSettings();
                Looper.prepare();
                Connection connection = getZebraPrinterConn();
                connection.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                // Rotate bitmap if necessary
                Bitmap rotatedBitmap = rotateBitmap(bitmap, 0);

                int width = rotatedBitmap.getWidth();
                int height = rotatedBitmap.getHeight();

                if (((CheckBox) findViewById(R.id.checkBox)).isChecked()) {
                    printer.storeImage(printStoragePath.getText().toString(),
                            new ZebraImageAndroid(rotatedBitmap), width, height);
                } else {
                    printer.printImage(new ZebraImageAndroid(rotatedBitmap), 0, 0, width, height, false);
                }

                connection.close();
                if (file != null) {
                    file.delete();
                    file = null;
                }
            } catch (ConnectionException | ZebraPrinterLanguageUnknownException |
                     ZebraIllegalArgumentException e) {
                helper.showErrorDialogOnGuiThread(e.getMessage());
            } finally {
                bitmap.recycle();
                helper.dismissLoadingDialog(2000);
                Looper.myLooper().quit();
            }
        }).start();
    }



    /**
     * Rotates a bitmap by a specified degree.
     */
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }



    private Connection getZebraPrinterConn() {
        String macAddress = getMacAddressFieldText();
        int portNumber = parseTcpPortNumber();

        if (isBleSelected()) {
            return new BluetoothLeConnection(macAddress,this);
        } else if (isBluetoothSelected()) {
            return new BluetoothConnection(macAddress);
        } else {
            return new TcpConnection(getTcpAddress(), portNumber);
        }
    }

    private int parseTcpPortNumber() {
        try {
            return Integer.parseInt(getTcpPortNumber());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void getAndSaveSettings() {
        SettingsHelper.saveBluetoothAddress(DisplayImageActivity.this, getMacAddressFieldText());
        SettingsHelper.saveIp(DisplayImageActivity.this, getTcpAddress());
        SettingsHelper.savePort(DisplayImageActivity.this, getTcpPortNumber());
    }

}
