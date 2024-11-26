package com.app.digiposfinalapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.FileOutputStream;
import java.io.IOException;

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private String shopName;
    private String billContent;
    private int numberOfPages;

    public MyPrintDocumentAdapter(Context context, String shopName, String billContent, int numberOfPages) {
        this.context = context;
        this.shopName = shopName;
        this.billContent = billContent;
        this.numberOfPages = numberOfPages;
    }



    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         Bundle extras) {

        PrintDocumentInfo info = new PrintDocumentInfo.Builder("shop_bill.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(numberOfPages)
                .build();

        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {

        PdfDocument pdfDocument = new PdfDocument();


        for (int i = 0; i < numberOfPages; i++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 300, i + 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(12);
            paint.setAntiAlias(true);


            int yPosition = 20;

            // Page content
            paint.setTextSize(16);
            paint.setFakeBoldText(true);
            canvas.drawText(shopName + " - Page " + (i + 1), 10, yPosition, paint);

            yPosition += 20;
            paint.setFakeBoldText(false);
            canvas.drawText("------------------------------", 10, yPosition, paint);

            yPosition += 20;
            String[] lines = billContent.split("\n");
            for (String line : lines) {
                canvas.drawText(line, 10, yPosition, paint);
                yPosition += 20;
            }

            yPosition += 20;
            canvas.drawText("------------------------------", 10, yPosition, paint);
            yPosition += 20;
            canvas.drawText("Thank you for your purchase!", 10, yPosition, paint);

            pdfDocument.finishPage(page);
        }

        try (FileOutputStream fos = new FileOutputStream(destination.getFileDescriptor())) {
            pdfDocument.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
    }

}
