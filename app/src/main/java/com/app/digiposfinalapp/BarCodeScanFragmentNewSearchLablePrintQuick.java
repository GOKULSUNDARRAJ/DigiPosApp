package com.app.digiposfinalapp;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BarCodeScanFragmentNewSearchLablePrintQuick extends Fragment {


    String TAG = "BarCodeScanFragmentNewSearchLablePrintQuick";
    private String barcodeValue;
    private String ipAddress1, portNumber1, databaseName1, dbUsername1, dbPassword1;
    private boolean isBarcodeDetected = false;
    private BroadcastReceiver scanReceiver;
    EditText searchbarcode;

    LinearLayout layoutToConvert2, layoutToConvert3;

    private View selectedLayout;
    private Button printerbtn;
    private RadioGroup choselayout;

    private static final String DEFAULT_BLE_ADDRESS = "8C:D5:4A:13:77:AD"; // Replace with your default BLE address
    private static final String DEFAULT_BLE_ADDRESS_2 = "60:95:32:17:03:3C"; // Replace with actual address

    private UIHelper helper;
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bar_code_scan_new_search_lableprint_quick, container, false);


        ImageView home = view.findViewById(R.id.home);
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

        helper = new UIHelper(getActivity());
        // Corrected line to get SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        ipAddress1 = sharedPreferences.getString(Constants.KEY_IP, "");
        portNumber1 = sharedPreferences.getString(Constants.KEY_PORT, "");
        databaseName1 = Constants.DATABASE_NAME;
        dbUsername1 = Constants.USERNAME;  // Use actual database username
        dbPassword1 = Constants.PASSWORD;  // Use actual database password


        ImageView camera = view.findViewById(R.id.Camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LablePrintFragmentQuick productManagementFragment = new LablePrintFragmentQuick();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getSupportFragmentManager()
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        ImageView back = view.findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
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

        searchbarcode = view.findViewById(R.id.barcodeedt);

        scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("nlscan.action.SCANNER_RESULT".equals(intent.getAction())) {
                    String scanResult = intent.getStringExtra("SCAN_BARCODE1"); // Get scanned text
                    if (scanResult != null) {
                        searchbarcode.setText(scanResult); // Set scanned QR code text in EditText
                    }
                }
            }
        };

        // Register the receiver using requireContext()
        IntentFilter filter = new IntentFilter("nlscan.action.SCANNER_RESULT");
        registerReceiver(requireContext(), scanReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);

        searchbarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                barcodeValue = searchbarcode.getText().toString();
                requireActivity().runOnUiThread(() -> {
                    new DatabaseTask().execute(); // Call DatabaseTask with barcode value
                });
                v.clearFocus(); // Hide keyboard
                return true;
            }
            return false;
        });


        // Initialize views

        layoutToConvert2 = view.findViewById(R.id.layoutToConvertlable21);
        layoutToConvert3 = view.findViewById(R.id.layoutToConvertlable3); // New third layout

        printerbtn = view.findViewById(R.id.print);
        choselayout = view.findViewById(R.id.radioGroup);

// Set the first radio button as the default selected option
        choselayout.check(R.id.radioLayout2); // Default to the first layout
        selectedLayout = layoutToConvert2; // Set the default selected layout

// Set radio group listener to store the selected layout
        choselayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioLayout2) {
                    selectedLayout = layoutToConvert2; // Store layout 2
                } else if (checkedId == R.id.radioLayout3) { // Handling new third radio button
                    selectedLayout = layoutToConvert3; // Store layout 3
                }
            }
        });

        Button searchbutton = view.findViewById(R.id.findbarcodeedt);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeValue = searchbarcode.getText().toString();

                if (selectedLayout == layoutToConvert2) {
                    requireActivity().runOnUiThread(() -> {
                        new DatabaseTask().execute(); // Call DatabaseTask with barcode value
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        new PromoItemDatabaseTask().execute(); // Call PromoItemDatabaseTask for other layouts
                    });
                }
            }
        });

        searchbarcode.requestFocus();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scanReceiver != null) {
            requireContext().unregisterReceiver(scanReceiver); // Unregister receiver in Fragment
        }
    }

    private void convertLayoutToImage(View layout) {
        try {
            // Enable drawing cache (optional, can help with some view types)
            layout.setDrawingCacheEnabled(true);
            layout.buildDrawingCache();

            // Measure with actual dimensions or specific size requirements
            int widthSpec = View.MeasureSpec.makeMeasureSpec(
                    layout.getWidth() > 0 ? layout.getWidth() : View.MeasureSpec.UNSPECIFIED,
                    layout.getWidth() > 0 ? View.MeasureSpec.EXACTLY : View.MeasureSpec.UNSPECIFIED);

            int heightSpec = View.MeasureSpec.makeMeasureSpec(
                    layout.getHeight() > 0 ? layout.getHeight() : View.MeasureSpec.UNSPECIFIED,
                    layout.getHeight() > 0 ? View.MeasureSpec.EXACTLY : View.MeasureSpec.UNSPECIFIED);

            layout.measure(widthSpec, heightSpec);

            // Layout with measured dimensions
            layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());

            // Create bitmap with proper dimensions
            Bitmap bitmap = Bitmap.createBitmap(
                    layout.getMeasuredWidth(),
                    layout.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);

            // Create canvas with high quality settings
            Canvas canvas = new Canvas(bitmap);
            canvas.setDensity(DisplayMetrics.DENSITY_XXHIGH);  // Set high density

            // Draw with anti-aliasing if needed
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            // Draw the layout
            layout.draw(canvas);

            // Compress with highest quality
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // PNG for lossless quality
            byte[] byteArray = stream.toByteArray();

            // Clean up
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            stream.close();

            sendimage(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error appropriately
        } finally {
            layout.setDrawingCacheEnabled(false); // Clean up drawing cache
        }
    }

    private void sendimage(byte[] byteArray) {
        // First check if we have a bitmap from arguments
        if (getArguments() != null && getArguments().getByteArray("image") != null) {
            byteArray = getArguments().getByteArray("image");
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        // If we still don't have a bitmap, create one from the layout
        if (bitmap == null) {
            // Convert the selected layout to bitmap
            selectedLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            selectedLayout.layout(0, 0, selectedLayout.getMeasuredWidth(), selectedLayout.getMeasuredHeight());

            bitmap = Bitmap.createBitmap(selectedLayout.getMeasuredWidth(),
                    selectedLayout.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            selectedLayout.draw(canvas);
        }

        if (bitmap != null) {
            printPhotoFromExternal(bitmap);
        } else {
            Toast.makeText(getContext(), "Failed to create image for printing", Toast.LENGTH_SHORT).show();
        }
    }


//SELF LABLE PRINTER SEPARATE
//    private void printPhotoFromExternal(final Bitmap bitmap) {
//        if (bitmap == null || bitmap.isRecycled()) {
//            requireActivity().runOnUiThread(() -> {
//                helper.dismissLoadingDialog();
//                helper.showErrorDialogOnGuiThread2("No valid image to print", () -> {
//                    // Restart the fragment when dialog is dismissed
//                    restartFragment();
//                });
//            });
//            return;
//        }
//
//        helper.showLoadingDialog("Sending image to printer");
//        new Thread(() -> {
//            try {
//                getAndSaveSettings();
//                Looper.prepare();
//                com.zebra.sdk.comm.Connection connection = getZebraPrinterConn();
//                connection.open();
//                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
//
//                // Rotate bitmap if necessary
//                Bitmap rotatedBitmap = rotateBitmap(bitmap, 0);
//                int width = rotatedBitmap.getWidth();
//                int height = rotatedBitmap.getHeight();
//
//                // Print directly (remove checkbox logic)
//                printer.printImage(new ZebraImageAndroid(rotatedBitmap), 0, 0, width, height, false);
//
//                connection.close();
//            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
//                requireActivity().runOnUiThread(() ->
//                        helper.showErrorDialogOnGuiThread2(e.getMessage(), () -> {
//                            // Restart the fragment when dialog is dismissed
//                            restartFragment();
//                        }));
//            } finally {
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }
//                requireActivity().runOnUiThread(() -> helper.dismissLoadingDialog());
//                Looper.myLooper().quit();
//            }
//        }).start();
//    }


    private void printPhotoFromExternal(final Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            requireActivity().runOnUiThread(() -> {
                helper.dismissLoadingDialog();
                helper.showErrorDialogOnGuiThread2("No valid image to print", () -> {
                    // Restart the fragment when dialog is dismissed
                    restartFragment();
                });
            });
            return;
        }

        helper.showLoadingDialog("Sending image to printer");
        new Thread(() -> {
            try {
                getAndSaveSettings();
                Looper.prepare();
                com.zebra.sdk.comm.Connection connection = getZebraPrinterConn();
                connection.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);

                // Rotate bitmap based on selected layout
                Bitmap rotatedBitmap;
                if (selectedLayout == layoutToConvert3) {
                    // Rotate 90 degrees for layout 3
                    rotatedBitmap = rotateBitmap(bitmap, 270);
                } else {
                    // No rotation for other layouts
                    rotatedBitmap = rotateBitmap(bitmap, 0);
                }

                int width = rotatedBitmap.getWidth();
                int height = rotatedBitmap.getHeight();

                // Print directly
                printer.printImage(new ZebraImageAndroid(rotatedBitmap), 0, 0, width, height, false);


                BarCodeScanFragmentNewSearchLablePrintQuick productManagementFragment = new BarCodeScanFragmentNewSearchLablePrintQuick();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                helper.dismissLoadingDialog();
                connection.close();
            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
                requireActivity().runOnUiThread(() ->
                        helper.showErrorDialogOnGuiThread2(e.getMessage(), () -> {
                            // Restart the fragment when dialog is dismissed
                            restartFragment();
                        }));
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                requireActivity().runOnUiThread(() -> helper.dismissLoadingDialog());
                Looper.myLooper().quit();
            }
        }).start();
    }

    // Method to restart the current fragment
    private void restartFragment() {
        BarCodeScanFragmentNewSearchLablePrintsub productManagementFragment = new BarCodeScanFragmentNewSearchLablePrintsub();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private com.zebra.sdk.comm.Connection getZebraPrinterConn() {
        if (selectedLayout == layoutToConvert2) {
            return new BluetoothLeConnection(DEFAULT_BLE_ADDRESS, requireContext());
        } else {
            return new BluetoothLeConnection(DEFAULT_BLE_ADDRESS_2, requireContext());
        }
    }

    private void getAndSaveSettings() {
        // Save the default BLE address to SharedPreferences
        SettingsHelper.saveBluetoothAddress(requireContext(), DEFAULT_BLE_ADDRESS);
    }


    // Generate barcode or QR code
    private Bitmap generateBarcode(String data, int width, int height, BarcodeFormat format) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();

        // Set encoding hints for the barcode
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);  // Optional: Set margin for barcode

        // Generate the barcode or QR code matrix
        BitMatrix bitMatrix = writer.encode(data, format, width, height, hints);

        // Convert BitMatrix to Bitmap
        int bitmapWidth = bitMatrix.getWidth();
        int bitmapHeight = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Set the bitmap to be transparent (0x00000000 represents fully transparent)
        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT); // Make the white parts transparent
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


    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, description, barcode, subDepartment, supplier, buyPrice, quantity, department, id, Expiry_date,
                saleWithVAT, Markup, discount, costPerCase, price, vat, margin, ageLimit, itemcode, Brand, UnitPerCase, currentstock, minStock, reorderleve,
                CostPerCase, Price, sellingprice, Margin, outerbarcode, costprice, addbarcode, startDate, enddate, dd_price, ddpoints, manageStock, weight, capatitys, CasePrice,
                CaseUnit, VatValue;

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            String searchQuery = barcodeValue; // Get the search query from the barcode
            String query = ""; // Initialize query
            String filterValue = ""; // Initialize filter value

            if (!searchQuery.isEmpty()) {
                // If the search query is not empty, search by the query
                query = "SELECT * FROM tbl_Products WHERE Barcode = ?";
                filterValue = searchQuery;
            } else {
                return null; // Exit early if both search query and barcode are empty
            }

            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;

            try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, filterValue); // Set search query as the filter
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getString("PLU");
                        plu = resultSet.getString("PLU");
                        description = resultSet.getString("Description");
                        barcode = resultSet.getString("Barcode");
                        subDepartment = resultSet.getString("Sub_Department");
                        supplier = resultSet.getString("Supplier");
                        buyPrice = resultSet.getString("Buy_Price");
                        quantity = resultSet.getString("Quantity");
                        department = resultSet.getString("Department");
                        saleWithVAT = resultSet.getString("SaleWithVAT");
                        discount = resultSet.getString("Discount");
                        costPerCase = resultSet.getString("CostPerCase");
                        price = resultSet.getString("Price");
                        vat = resultSet.getString("VAT");
                        margin = resultSet.getString("Margin");
                        ageLimit = resultSet.getString("Age_Limit");
                        itemcode = resultSet.getString("Item_code");
                        Brand = resultSet.getString("Brand");
                        UnitPerCase = resultSet.getString("UnitPerCase");
                        CostPerCase = resultSet.getString("CostPerCase");
                        Price = resultSet.getString("Price");
                        Markup = resultSet.getString("Markup");
                        sellingprice = resultSet.getString("SS_PRICE");
                        Margin = resultSet.getString("Margin");
                        outerbarcode = resultSet.getString("OuterBarcode");
                        costprice = resultSet.getString("Price");
                        addbarcode = resultSet.getString("AdditionalBarcode1");
                        startDate = resultSet.getString("StartDate");
                        enddate = resultSet.getString("EndDate");
                        dd_price = resultSet.getString("DD_Price");
                        ddpoints = resultSet.getString("SS_POINTS");
                        manageStock = resultSet.getString("ManageStock");
                        weight = resultSet.getString("Weight");
                        capatitys = resultSet.getString("Capacity");
                        currentstock = resultSet.getString("CurrentStock");
                        minStock = resultSet.getString("MinStock");
                        reorderleve = resultSet.getString("ReorderLevel");
                        Expiry_date = resultSet.getString("Expiry_date");
                        CasePrice = resultSet.getString("CasePrice");
                        CaseUnit = resultSet.getString("CaseUnit");
                        VatValue = resultSet.getString("VatValue");


                        Bundle bundle = new Bundle();
                        bundle.putString("plu", plu);
                        bundle.putString("description", description);
                        bundle.putString("barcode", barcode);
                        bundle.putString("subDepartment", subDepartment);
                        bundle.putString("supplier", supplier);
                        bundle.putString("buyPrice", buyPrice);
                        bundle.putString("quantity", quantity);
                        bundle.putString("department", department);
                        bundle.putString("saleWithVAT", saleWithVAT);
                        bundle.putString("discount", discount);
                        bundle.putString("costPerCase", costPerCase);
                        bundle.putString("price", price);
                        bundle.putString("vat", vat);
                        bundle.putString("margin", margin);
                        bundle.putString("ageLimit", ageLimit);
                        bundle.putString("Itemcode", itemcode);
                        bundle.putString("Brand", Brand);
                        bundle.putString("UnitPerCase", UnitPerCase);
                        bundle.putString("CostPerCase", CostPerCase);
                        bundle.putString("Price", Price);
                        bundle.putString("SS_PRICE", sellingprice);
                        bundle.putString("Margin", Margin);
                        bundle.putString("OuterBarcode", outerbarcode);
                        bundle.putString("Price", costprice);
                        bundle.putString("AdditionalBarcode1", addbarcode);
                        bundle.putString("ID", id);
                        bundle.putString("StartDate", startDate);
                        bundle.putString("EndDate", enddate);
                        bundle.putString("DD_Price", dd_price);
                        bundle.putString("SS_POINTS", ddpoints);
                        bundle.putString("ManageStock", manageStock);
                        bundle.putString("Weight", weight);
                        bundle.putString("Capacity", capatitys);
                        bundle.putString("CurrentStock", currentstock);
                        bundle.putString("MinStock", minStock);
                        bundle.putString("Reorderleve", reorderleve);
                        bundle.putString("Discount", discount);
                        bundle.putString("Markup", Markup);
                        bundle.putString("CasePrice", CasePrice);
                        bundle.putString("CaseUnit", CaseUnit);
                        bundle.putString("VatValue", VatValue);
                        bundle.putString("Expiry_date", Expiry_date);

                        if (selectedLayout == null) {
                            // Show a message to the user to select a layout first
                            Toast.makeText(getContext(), "Please select a layout first", Toast.LENGTH_SHORT).show();

                        }

                        // Generate barcodes
                        Bitmap qrCodeBitmap = null;
                        Bitmap barcodeBitmap = null;

                        try {
                            qrCodeBitmap = generateBarcode(barcodeValue, 400, 400, BarcodeFormat.QR_CODE);
                            barcodeBitmap = generateBarcodelinear(barcodeValue, 400, 100);
                        } catch (Exception e) {
                            Log.e("BarcodeGeneration", "Error generating barcode", e);

                        }

                        if (qrCodeBitmap != null && barcodeBitmap != null) {
                            // Update text and image views within the selected layout
                            ((TextView) selectedLayout.findViewById(R.id.bill_title)).setText(description);
                            ((TextView) selectedLayout.findViewById(R.id.textView10)).setText("£" + Price);
                            ((TextView) selectedLayout.findViewById(R.id.priceedt)).setText("Was £");
                            ((TextView) selectedLayout.findViewById(R.id.barcode)).setText(barcodeValue);
                            ((TextView) selectedLayout.findViewById(R.id.price)).setText("ANY 2 FOR " + "£" + Price);


                            ImageView qrCodeImageView = selectedLayout.findViewById(R.id.barcode_image5);
                            qrCodeImageView.setImageBitmap(barcodeBitmap);

                            ImageView qrCodeImageView2 = selectedLayout.findViewById(R.id.barcode_image2);
                            qrCodeImageView2.setImageBitmap(qrCodeBitmap);

                            // Convert the selected layout to an image
                            convertLayoutToImage(selectedLayout);

                        } else {
                            Log.e("BarcodeGeneration", "QR Code or barcode bitmap generation failed.");
                        }


                    } else {
                        requireActivity().runOnUiThread(() -> {

                            Toast.makeText(getContext(), "No product found", Toast.LENGTH_SHORT).show();
                            new GetLastPLUTask().execute();

                        });

                    }
                }

            } catch (SQLException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "SQL Exception: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Update UI with the retrieved data
            if (plu != null) {
                requireActivity().runOnUiThread(() -> {
                    // Update the TextView with product details

                    //    Toast.makeText(getContext(), "Product details updated", Toast.LENGTH_SHORT).show();
                });
                isBarcodeDetected = false; // Reset detection for the next barcode scan
            }
        }

    }

    private class GetLastPLUTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String maxPLU = null;

            try {
                String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + "/" + databaseName1;
                try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1)) {
                    // SQL query to get the maximum PLU value
                    String sql = "SELECT MAX(CAST(PLU AS BIGINT)) AS MaxPLU FROM tbl_Products WHERE ISNUMERIC(PLU) = 1";
                    try (PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {

                        if (resultSet.next()) {
                            maxPLU = resultSet.getString("MaxPLU");
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "SQL Exception: " + e.getMessage(), e);
            }
            return maxPLU;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Create a new Bundle to pass the barcode value
                Bundle bundle = new Bundle();
                bundle.putString("barcode", barcodeValue);
                bundle.putString("fromBarCodeScanFragmentNewSearchLablePrintQuick", "fromBarCodeScanFragmentNewSearchLablePrintQuick");

                // Handle PLU value safely
                int pluValue;
                if (result != null && !result.trim().isEmpty()) {
                    try {
                        pluValue = Integer.parseInt(result.trim()) + 1;
                    } catch (NumberFormatException e) {
                        pluValue = 3001; // Default value if parsing fails
                    }
                } else {
                    pluValue = 3001; // Default value if result is null or empty
                }
                bundle.putString("PLU", String.valueOf(pluValue));

                // Create and show the fragment
                ProductManagmentAddFragment productManagementFragment = new ProductManagmentAddFragment();
                productManagementFragment.setArguments(bundle);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, productManagementFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } catch (Exception e) {
                Log.e(TAG, "Error in onPostExecute: ", e);
                Toast.makeText(requireActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
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

    private class PromoItemDatabaseTask extends AsyncTask<Void, Void, Void> {
        private String plu, barcode, promoPrice, discountPrice, ddPrice;
        private String description = "";
        private String promoName = "";
        private String promoStartDate = "";
        private String promoEndDate = "";
        private String ProducrName = "";
        private String Producrprice = "";

        @Override
        protected Void doInBackground(Void... voids) {
            String query = "SELECT PI.PLU, PI.Barcode, PI.PromotionPrice, PI.DiscountPrice, PI.DDPrice, PI.PromoID FROM Promo_Items " +
                    "PI WHERE CAST(PI.Barcode AS VARCHAR(50)) = ?";

            String connectionUrl = "jdbc:jtds:sqlserver://" + ipAddress1 + ":" + portNumber1 + ";databaseName=" + databaseName1;

            try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername1, dbPassword1);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, barcodeValue);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        plu = resultSet.getString("PLU");
                        barcode = resultSet.getString("Barcode");
                        promoPrice = resultSet.getString("PromotionPrice");
                        discountPrice = resultSet.getString("DiscountPrice");
                        ddPrice = resultSet.getString("DDPrice");
                        String promoID = resultSet.getString("PromoID");

                        // Now get promotion details from tbl_Promotion
                        if (promoID != null) {
                            String promoQuery = "SELECT Description, PromoName, Start, Enddate " +
                                    "FROM tbl_Promotion " +
                                    "WHERE PromoID = ?";
                            try (PreparedStatement promoStmt = connection.prepareStatement(promoQuery)) {
                                promoStmt.setString(1, promoID);
                                try (ResultSet promoRs = promoStmt.executeQuery()) {
                                    if (promoRs.next()) {
                                        description = promoRs.getString("Description");
                                        promoName = promoRs.getString("PromoName");
                                        promoStartDate = promoRs.getString("Start");
                                        promoEndDate = promoRs.getString("Enddate");

                                        // If description is still empty, try to get from tbl_Products as fallback
                                        if (plu != null) {
                                            String productQuery = "SELECT Description, price FROM tbl_Products WHERE PLU = ?";
                                            try (PreparedStatement productStmt = connection.prepareStatement(productQuery)) {
                                                productStmt.setString(1, plu);
                                                try (ResultSet productRs = productStmt.executeQuery()) {
                                                    if (productRs.next()) {
                                                        ProducrName = productRs.getString("Description");
                                                        Producrprice = productRs.getString("Price");


                                                        if (selectedLayout == null) {
                                                            // Show a message to the user to select a layout first
                                                            Toast.makeText(getContext(), "Please select a layout first", Toast.LENGTH_SHORT).show();

                                                        }

                                                        // Generate barcodes
                                                        Bitmap qrCodeBitmap = null;
                                                        Bitmap barcodeBitmap = null;

                                                        try {
                                                            qrCodeBitmap = generateBarcode(barcodeValue, 400, 400, BarcodeFormat.QR_CODE);
                                                            barcodeBitmap = generateBarcodelinear(barcodeValue, 400, 100);
                                                        } catch (Exception e) {
                                                            Log.e("BarcodeGeneration", "Error generating barcode", e);

                                                        }

                                                        if (qrCodeBitmap != null && barcodeBitmap != null) {
                                                            // Update text and image views within the selected layout
                                                            ((TextView) selectedLayout.findViewById(R.id.bill_title)).setText(promoName);
                                                            ((TextView) selectedLayout.findViewById(R.id.textView10)).setText("£" + Producrprice);
                                                            ((TextView) selectedLayout.findViewById(R.id.barcode)).setText(barcodeValue);


                                                            String gramValue;
                                                            try {
                                                                double price = Double.parseDouble(Producrprice);
                                                                double gramPrice = price / 100;
                                                                gramValue = String.format(Locale.UK, "%.2f", gramPrice); // Formats to 2 decimal places
                                                            } catch (NumberFormatException e) {
                                                                gramValue = "0.00"; // Default value if parsing fails
                                                                Log.e("PriceFormat", "Error parsing price: " + Producrprice, e);
                                                            }

// Then use gramValue in your text view
                                                            ((TextView) selectedLayout.findViewById(R.id.textView11)).setText(gramValue);


                                                            // Get references to the TextViews for before and after pound symbol
                                                            TextView textViewBefore = selectedLayout.findViewById(R.id.price1); // Replace with your actual ID
                                                            TextView textViewAfter = selectedLayout.findViewById(R.id.price2);   // Replace with your actual ID

                                                            // Split the description text at the pound symbol
                                                            // Split the description text at the pound symbol
                                                            // Split the description text at the pound symbol
                                                            if (description != null) {
                                                                String[] parts = description.split("£", 2); // Split into max 2 parts
                                                                if (parts.length == 2) {
                                                                    // If pound symbol was found
                                                                    textViewBefore.setText(parts[0].trim());

                                                                    // Get the part after £ and trim it
                                                                    String afterPound = parts[1].trim();

                                                                    // Handle different cases for afterPound
                                                                    if (afterPound.equals("0")) {
                                                                        textViewAfter.setText(""); // Case: £0 → empty
                                                                    } else {
                                                                        try {
                                                                            double value = Double.parseDouble(afterPound);
                                                                            if (value == 0) {
                                                                                textViewAfter.setText("");
                                                                            } else if (value % 1 == 0) {
                                                                                // Whole number case (e.g. 5.0, 10.00)
                                                                                textViewAfter.setText("£" + (int) value);
                                                                            } else {
                                                                                // Decimal number case - remove trailing .0 but keep .5
                                                                                String formatted = String.format(Locale.UK, "%.2f", value);
                                                                                formatted = formatted.replaceAll("0$", "").replaceAll("\\.$", "");
                                                                                textViewAfter.setText("£" + formatted);
                                                                            }
                                                                        } catch (
                                                                                NumberFormatException e) {
                                                                            // If not a valid number, show as-is
                                                                            textViewAfter.setText("£" + afterPound);
                                                                        }
                                                                    }
                                                                } else {
                                                                    // If no pound symbol was found
                                                                    textViewBefore.setText(description);
                                                                    textViewAfter.setText("");
                                                                }
                                                            }


                                                            ImageView qrCodeImageView = selectedLayout.findViewById(R.id.barcode_image5);
                                                            qrCodeImageView.setImageBitmap(barcodeBitmap);

                                                            ImageView qrCodeImageView2 = selectedLayout.findViewById(R.id.barcode_image2);
                                                            qrCodeImageView2.setImageBitmap(qrCodeBitmap);

                                                            // Convert the selected layout to an image
                                                            convertLayoutToImage(selectedLayout);

                                                        } else {
                                                            Log.e("BarcodeGeneration", "QR Code or barcode bitmap generation failed.");
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "No promo item found", Toast.LENGTH_SHORT).show();
                        });
                        return null;
                    }
                }
            } catch (SQLException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "SQL Exception: " + e.getMessage());
                return null;
            }


            return null;
        }


    }


}
























































