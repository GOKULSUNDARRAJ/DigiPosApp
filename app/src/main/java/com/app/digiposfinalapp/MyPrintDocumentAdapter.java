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

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    String barcode1;
    String pricereduced;
    String price;

    public MyPrintDocumentAdapter(Context context, String barcode1, String pricereduced, String price) {
        this.context = context;
        this.barcode1 = barcode1;
        this.pricereduced = pricereduced;
        this.price = price;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         Bundle extras) {

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("shop_bill.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build();

        callback.onLayoutFinished(info, true);
    }

    private Bitmap convertLayoutToImage(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View printView = inflater.inflate(R.layout.print_layout, null);

        try {
            Bitmap barcodeBitmap = generateBarcode(barcode1, 400, 100);
            ImageView barcodeImageView = printView.findViewById(R.id.barcode_image);
            barcodeImageView.setImageBitmap(barcodeBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView billTitle = printView.findViewById(R.id.bill_title);
        billTitle.setText("REDUCED");
        TextView barcode2 = printView.findViewById(R.id.barcode2);
        barcode2.setText(barcode1);

        TextView pricereduced1 = printView.findViewById(R.id.pricereduced);
        pricereduced1.setText("Now " + pricereduced);

        TextView price1 = printView.findViewById(R.id.priceedt);
        price1.setText("Was " + price);

        Bitmap layoutBitmap = convertLayoutToImage(printView, 600, 750);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 750, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(layoutBitmap, 0, 0, null);

        pdfDocument.finishPage(page);

        try (FileOutputStream fos = new FileOutputStream(destination.getFileDescriptor())) {
            pdfDocument.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }

    private Bitmap generateBarcode(String data, int width, int height) throws Exception {
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
}
