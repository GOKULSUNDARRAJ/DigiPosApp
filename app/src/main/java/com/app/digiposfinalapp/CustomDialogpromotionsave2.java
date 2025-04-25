package com.app.digiposfinalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CustomDialogpromotionsave2 extends Dialog {

    private Context context;
    private View selectedLayout;
    private Button printerbtn;
    private RadioGroup choselayout;
    private LinearLayout layoutToConvert, layoutToConvert2, layoutToConvert3;
    private static final String DEFAULT_BLE_ADDRESS = "60:95:32:17:03:3C";
    private UIHelper helper;
    private Bitmap bitmap;

    private Activity activity;
    String ipAddress, portNumber, databaseName, username, password;
    int size;
    public CustomDialogpromotionsave2(@NonNull Activity activity, int size) {
        super(activity);
        this.activity = activity;
        this.context = activity;
        this.size = size;// Keep context reference if needed elsewhere
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logoutpromotion2);
        helper = new UIHelper(activity);

        Button clear = findViewById(R.id.cancelbtn);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (activity instanceof AppCompatActivity) {
                    SubnewpromotionFragment productManagementFragment = new SubnewpromotionFragment();
                    FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        layoutToConvert = findViewById(R.id.layoutToConvertlable);
        layoutToConvert2 = findViewById(R.id.layoutToConvertlable2);
        layoutToConvert3 = findViewById(R.id.layoutToConvertlable3);

        printerbtn = findViewById(R.id.savebtn);
        choselayout = findViewById(R.id.radioGroup);

        // Set the first radio button as the default selected option
        choselayout.check(R.id.radioLayout1);
        selectedLayout = layoutToConvert;

        choselayout.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLayout1) {
                selectedLayout = layoutToConvert;
            } else if (checkedId == R.id.radioLayout2) {
                selectedLayout = layoutToConvert2;
            } else if (checkedId == R.id.radioLayout3) {
                selectedLayout = layoutToConvert3;
            }
        });

        printerbtn.setOnClickListener(v -> {
            if (selectedLayout == null) {
                Toast.makeText(context, "Please select a layout first", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Bitmap qrCodeBitmap = generateBarcode("3453463463", 400, 400, BarcodeFormat.QR_CODE);
                Bitmap barcodeBitmap = generateBarcodelinear("346346346", 400, 100);

                if (qrCodeBitmap != null && barcodeBitmap != null) {
                    updateLayoutViews(selectedLayout, qrCodeBitmap, barcodeBitmap);
                    convertLayoutToImage(selectedLayout);
                } else {
                    Log.e("BarcodeGeneration", "QR Code or barcode bitmap generation failed.");
                }
            } catch (Exception e) {
                Log.e("BarcodeGeneration", "Error generating barcode", e);
                Toast.makeText(context, "Error generating barcode", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLayoutViews(View layout, Bitmap qrCodeBitmap, Bitmap barcodeBitmap) {
        ((TextView) layout.findViewById(R.id.bill_title)).setText("rhyeryerye");
        ((TextView) layout.findViewById(R.id.textView10)).setText("£0");
        ((TextView) layout.findViewById(R.id.priceedt)).setText("Was £");
        ((TextView) layout.findViewById(R.id.barcode)).setText("45635635636");

        ImageView qrCodeImageView = layout.findViewById(R.id.barcode_image5);
        qrCodeImageView.setImageBitmap(barcodeBitmap);

        ImageView qrCodeImageView2 = layout.findViewById(R.id.barcode_image2);
        qrCodeImageView2.setImageBitmap(qrCodeBitmap);
    }

    private void convertLayoutToImage(View layout) {
        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);

        Bitmap trimmedBitmap = trimBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        trimmedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        sendImageToPrinter(trimmedBitmap);
    }

    private void sendImageToPrinter(Bitmap bitmap) {
        this.bitmap = bitmap;
        printPhotoFromExternal(bitmap, size); // Print 5 copies
    }

    private Bitmap trimBitmap(Bitmap bitmap) {
        int imgHeight = bitmap.getHeight();
        int imgWidth = bitmap.getWidth();

        int minX = imgWidth, minY = imgHeight;
        int maxX = -1, maxY = -1;

        int[] pixels = new int[imgWidth * imgHeight];
        bitmap.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);

        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int pixel = pixels[y * imgWidth + x];
                if ((pixel >> 24) != 0x00) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        if (maxX < minX || maxY < minY) return bitmap;

        return Bitmap.createBitmap(bitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    private Bitmap generateBarcode(String data, int width, int height, BarcodeFormat format) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);

        BitMatrix bitMatrix = writer.encode(data, format, width, height, hints);
        int bitmapWidth = bitMatrix.getWidth();
        int bitmapHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        return bitmap;
    }

    private Bitmap generateBarcodelinear(String data, int width, int height) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height, hints);

        int bitmapWidth = bitMatrix.getWidth();
        int bitmapHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        return bitmap;
    }

    private void printPhotoFromExternal(final Bitmap bitmap, final int printCount) {
        helper.showLoadingDialog("Sending image to printer");
        new Thread(() -> {
            try {
                getAndSaveSettings();
                Looper.prepare();
                com.zebra.sdk.comm.Connection connection = getZebraPrinterConn();
                connection.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                Bitmap rotatedBitmap = rotateBitmap(bitmap, 0);
                int width = rotatedBitmap.getWidth();
                int height = rotatedBitmap.getHeight();

                CheckBox checkBox = findViewById(R.id.checkBox);

                // Print multiple times
                for (int i = 0; i < printCount; i++) {
                    if (checkBox != null && checkBox.isChecked()) {
                        // Only store once if checkbox is checked
                        if (i == 0) {
                            printer.storeImage("E:IMAGE.PNG", new ZebraImageAndroid(rotatedBitmap), width, height);
                        }
                    } else {
                        printer.printImage(new ZebraImageAndroid(rotatedBitmap), 0, 0, width, height, false);
                    }

                    // Small delay between prints if needed
                    if (i < printCount - 1) {
                        Thread.sleep(200); // 200ms delay between prints
                    }
                }

                connection.close();
            } catch (ConnectionException | ZebraPrinterLanguageUnknownException |
                     ZebraIllegalArgumentException | InterruptedException e) {
                Log.e("PrinterError", "Printing error", e);
                activity.runOnUiThread(() -> helper.showErrorDialogOnGuiThread(e.getMessage()));
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                activity.runOnUiThread(() -> helper.dismissLoadingDialog());
                Looper.myLooper().quit();
            }
        }).start();
    }

    private void runOnUiThread(Runnable action) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(action);
        }
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private com.zebra.sdk.comm.Connection getZebraPrinterConn() {
        return new BluetoothLeConnection(DEFAULT_BLE_ADDRESS, context);
    }

    private void getAndSaveSettings() {
        SettingsHelper.saveBluetoothAddress(context, DEFAULT_BLE_ADDRESS);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return true;
    }

    @Override
    public void onBackPressed() {
        // Do nothing on back press
    }
}





















//
//
//
//package com.app.digiposfinalapp;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.app.digiposfinalapp.util.SettingsHelper;
//import com.app.digiposfinalapp.util.UIHelper;
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.common.BitMatrix;
//import com.zebra.sdk.btleComm.BluetoothLeConnection;
//import com.zebra.sdk.comm.ConnectionException;
//import com.zebra.sdk.device.ZebraIllegalArgumentException;
//import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
//import com.zebra.sdk.printer.ZebraPrinter;
//import com.zebra.sdk.printer.ZebraPrinterFactory;
//import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
//
//import java.io.ByteArrayOutputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//public class CustomDialogpromotionsave2 extends Dialog {
//
//    private Context context;
//    private View selectedLayout;
//    private Button printerbtn;
//    private RadioGroup choselayout;
//    private LinearLayout layoutToConvert, layoutToConvert2, layoutToConvert3;
//    private static final String DEFAULT_BLE_ADDRESS = "60:95:32:17:03:3C";
//    private UIHelper helper;
//    private Bitmap bitmap;
//
//    private Activity activity;
//
//    public CustomDialogpromotionsave2(@NonNull Activity activity) {
//        super(activity);
//        this.activity = activity;
//        this.context = activity;  // Keep context reference if needed elsewhere
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_logoutpromotion2);
//        helper = new UIHelper(activity);
//
//        Button clear = findViewById(R.id.cancelbtn);
//        clear.setOnClickListener(v -> dismiss());
//
//        layoutToConvert = findViewById(R.id.layoutToConvertlable);
//        layoutToConvert2 = findViewById(R.id.layoutToConvertlable2);
//        layoutToConvert3 = findViewById(R.id.layoutToConvertlable3);
//
//        printerbtn = findViewById(R.id.savebtn);
//        choselayout = findViewById(R.id.radioGroup);
//
//        // Set the first radio button as the default selected option
//        choselayout.check(R.id.radioLayout1);
//        selectedLayout = layoutToConvert;
//
//        choselayout.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.radioLayout1) {
//                selectedLayout = layoutToConvert;
//            } else if (checkedId == R.id.radioLayout2) {
//                selectedLayout = layoutToConvert2;
//            } else if (checkedId == R.id.radioLayout3) {
//                selectedLayout = layoutToConvert3;
//            }
//        });
//
//        printerbtn.setOnClickListener(v -> {
//            if (selectedLayout == null) {
//                Toast.makeText(context, "Please select a layout first", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            try {
//                Bitmap qrCodeBitmap = generateBarcode("3453463463", 400, 400, BarcodeFormat.QR_CODE);
//                Bitmap barcodeBitmap = generateBarcodelinear("346346346", 400, 100);
//
//                if (qrCodeBitmap != null && barcodeBitmap != null) {
//                    updateLayoutViews(selectedLayout, qrCodeBitmap, barcodeBitmap);
//                    convertLayoutToImage(selectedLayout);
//                } else {
//                    Log.e("BarcodeGeneration", "QR Code or barcode bitmap generation failed.");
//                }
//            } catch (Exception e) {
//                Log.e("BarcodeGeneration", "Error generating barcode", e);
//                Toast.makeText(context, "Error generating barcode", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void updateLayoutViews(View layout, Bitmap qrCodeBitmap, Bitmap barcodeBitmap) {
//        ((TextView) layout.findViewById(R.id.bill_title)).setText("rhyeryerye");
//        ((TextView) layout.findViewById(R.id.textView10)).setText("£0");
//        ((TextView) layout.findViewById(R.id.priceedt)).setText("Was £");
//        ((TextView) layout.findViewById(R.id.barcode)).setText("45635635636");
//
//        ImageView qrCodeImageView = layout.findViewById(R.id.barcode_image5);
//        qrCodeImageView.setImageBitmap(barcodeBitmap);
//
//        ImageView qrCodeImageView2 = layout.findViewById(R.id.barcode_image2);
//        qrCodeImageView2.setImageBitmap(qrCodeBitmap);
//    }
//
//    private void convertLayoutToImage(View layout) {
//        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
//
//        Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        layout.draw(canvas);
//
//        Bitmap trimmedBitmap = trimBitmap(bitmap);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        trimmedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//
//        sendImageToPrinter(trimmedBitmap);
//    }
//
//    private void sendImageToPrinter(Bitmap bitmap) {
//        this.bitmap = bitmap;
//        printPhotoFromExternal(bitmap);
//    }
//
//    private Bitmap trimBitmap(Bitmap bitmap) {
//        int imgHeight = bitmap.getHeight();
//        int imgWidth = bitmap.getWidth();
//
//        int minX = imgWidth, minY = imgHeight;
//        int maxX = -1, maxY = -1;
//
//        int[] pixels = new int[imgWidth * imgHeight];
//        bitmap.getPixels(pixels, 0, imgWidth, 0, 0, imgWidth, imgHeight);
//
//        for (int y = 0; y < imgHeight; y++) {
//            for (int x = 0; x < imgWidth; x++) {
//                int pixel = pixels[y * imgWidth + x];
//                if ((pixel >> 24) != 0x00) {
//                    if (x < minX) minX = x;
//                    if (x > maxX) maxX = x;
//                    if (y < minY) minY = y;
//                    if (y > maxY) maxY = y;
//                }
//            }
//        }
//
//        if (maxX < minX || maxY < minY) return bitmap;
//
//        return Bitmap.createBitmap(bitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
//    }
//
//    private Bitmap generateBarcode(String data, int width, int height, BarcodeFormat format) throws Exception {
//        MultiFormatWriter writer = new MultiFormatWriter();
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.MARGIN, 0);
//
//        BitMatrix bitMatrix = writer.encode(data, format, width, height, hints);
//        int bitmapWidth = bitMatrix.getWidth();
//        int bitmapHeight = bitMatrix.getHeight();
//        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//
//        for (int x = 0; x < bitmapWidth; x++) {
//            for (int y = 0; y < bitmapHeight; y++) {
//                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
//            }
//        }
//
//        return bitmap;
//    }
//
//    private Bitmap generateBarcodelinear(String data, int width, int height) throws Exception {
//        MultiFormatWriter writer = new MultiFormatWriter();
//        Map<EncodeHintType, Object> hints = new HashMap<>();
//        hints.put(EncodeHintType.MARGIN, 0);
//        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height, hints);
//
//        int bitmapWidth = bitMatrix.getWidth();
//        int bitmapHeight = bitMatrix.getHeight();
//        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//
//        for (int x = 0; x < bitmapWidth; x++) {
//            for (int y = 0; y < bitmapHeight; y++) {
//                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
//            }
//        }
//
//        return bitmap;
//    }
//
//    private void printPhotoFromExternal(final Bitmap bitmap) {
//        helper.showLoadingDialog("Sending image to printer");
//        new Thread(() -> {
//            try {
//                getAndSaveSettings();
//                Looper.prepare();
//                com.zebra.sdk.comm.Connection connection = getZebraPrinterConn();
//                connection.open();
//                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
//
//                Bitmap rotatedBitmap = rotateBitmap(bitmap, 0);
//                int width = rotatedBitmap.getWidth();
//                int height = rotatedBitmap.getHeight();
//
//                CheckBox checkBox = findViewById(R.id.checkBox);
//                if (checkBox != null && checkBox.isChecked()) {
//                    printer.storeImage("E:IMAGE.PNG", new ZebraImageAndroid(rotatedBitmap), width, height);
//                } else {
//                    printer.printImage(new ZebraImageAndroid(rotatedBitmap), 0, 0, width, height, false);
//                }
//
//                connection.close();
//            } catch (ConnectionException | ZebraPrinterLanguageUnknownException | ZebraIllegalArgumentException e) {
//                Log.e("PrinterError", "Printing error", e);
//                runOnUiThread(() -> helper.showErrorDialogOnGuiThread(e.getMessage()));
//            } finally {
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }
//                runOnUiThread(() -> helper.dismissLoadingDialog());
//                Looper.myLooper().quit();
//            }
//        }).start();
//    }
//
//    private void runOnUiThread(Runnable action) {
//        if (context instanceof android.app.Activity) {
//            ((android.app.Activity) context).runOnUiThread(action);
//        }
//    }
//
//    private Bitmap rotateBitmap(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }
//
//    private com.zebra.sdk.comm.Connection getZebraPrinterConn() {
//        return new BluetoothLeConnection(DEFAULT_BLE_ADDRESS, context);
//    }
//
//    private void getAndSaveSettings() {
//        SettingsHelper.saveBluetoothAddress(context, DEFAULT_BLE_ADDRESS);
//    }
//
//    @Override
//    public boolean onTouchEvent(@NonNull MotionEvent event) {
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        // Do nothing on back press
//    }
//}