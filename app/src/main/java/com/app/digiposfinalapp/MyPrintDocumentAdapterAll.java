package com.app.digiposfinalapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyPrintDocumentAdapterAll extends PrintDocumentAdapter {

    private Context context;
    String barcode1;
    String pricereduced;
    String price;
    String description;

    public MyPrintDocumentAdapterAll(Context context, String barcode1, String pricereduced, String price, String description) {
        this.context = context;
        this.barcode1 = barcode1;
        this.pricereduced = pricereduced;
        this.price = price;
        this.description = description;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         Bundle extras) {

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("shop_bill.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)  // Only one page for the barcode
                .build();

        callback.onLayoutFinished(info, true);
    }


    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {

        // Inflate the XML layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View printView = inflater.inflate(R.layout.printer_lable_layout, null);

        // Check if barcode data is valid (not empty)
        if (barcode1 == null || barcode1.trim().isEmpty()) {
            Log.e("MyPrintDocumentAdapterAll", "Barcode content is empty.");
            // Optionally, set a default barcode or skip barcode generation
            barcode1 = "DEFAULT_BARCODE";  // Set a default value for testing
        }

        // Generate the barcode
        try {

            Bitmap barcodeBitmap = generateBarcode(barcode1, 400, 100);
            if (barcodeBitmap != null) {
                ImageView barcodeImageView = printView.findViewById(R.id.barcode_image5);
                barcodeImageView.setImageBitmap(barcodeBitmap);
            } else {
                Log.e("MyPrintDocumentAdapterAll", "Barcode bitmap generation failed.");
            }

            Bitmap qrCodeBitmap = generateBarcode(barcode1, 400, 400, BarcodeFormat.QR_CODE); // QR Code
            if (qrCodeBitmap != null) {
                ImageView qrCodeImageView = printView.findViewById(R.id.barcode_image2); // Make sure to add qr_code_image in your layout XML
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            } else {
                Log.e("MyPrintDocumentAdapterAll", "QR Code bitmap generation failed.");
            }


        } catch (Exception e) {
            Log.e("MyPrintDocumentAdapterAll", "Error generating barcode", e);
        }

        // Set other text content
        ((TextView) printView.findViewById(R.id.bill_title)).setText(description);
        ((TextView) printView.findViewById(R.id.textView10)).setText("£" + pricereduced);
        ((TextView) printView.findViewById(R.id.priceedt)).setText("Was £ " + price);

        // Create a PDF document
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(945, 472, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Prepare and draw the canvas
        Canvas canvas = page.getCanvas();
        printView.measure(View.MeasureSpec.makeMeasureSpec(945, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(472, View.MeasureSpec.EXACTLY));
        printView.layout(0, 0, printView.getMeasuredWidth(), printView.getMeasuredHeight());
        printView.draw(canvas);

        pdfDocument.finishPage(page);

        // Write the document
        try (FileOutputStream fos = new FileOutputStream(destination.getFileDescriptor())) {
            pdfDocument.writeTo(fos);
        } catch (IOException e) {
            Log.e("MyPrintDocumentAdapterAll", "Error writing PDF", e);
        } finally {
            pdfDocument.close();
        }

        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }



    private Bitmap generateBarcode(String data, int width, int height) throws Exception {
        MultiFormatWriter writer = new MultiFormatWriter();

        // Set encoding hints for the barcode
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 0);  // Optional: Set margin for barcode

        // Generate the barcode matrix
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, height, hints);

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


}
