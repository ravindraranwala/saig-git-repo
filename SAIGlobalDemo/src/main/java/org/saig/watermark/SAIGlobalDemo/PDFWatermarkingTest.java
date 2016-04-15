package org.saig.watermark.SAIGlobalDemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PDFWatermarkingTest {
    public static final String SRC =
                                     "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/DEC-CatalogDataAPIDesign-090316-1042-7.pdf";
    public static final String DEST =
                                      "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/pages_watermarked.pdf";

    public static final String FONT = "src/main/resources/FreeSans.ttf";

    public static void main(String[] args) throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        // new PDFWatermarkingTest().manipulatePdf(SRC, DEST);
        new PDFWatermarkingTest().addVerticalText(SRC, DEST);

    }

    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        // text watermark
        Font f = new Font(FontFamily.HELVETICA, 30);
        Phrase p = new Phrase("My watermark (text)", f);

        // transparency
        PdfGState gs1 = new PdfGState();
        gs1.setFillOpacity(0.5f);
        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;
        // loop over every page
        for (int i = 1; i <= n; i++) {
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / 2;
            y = (pagesize.getTop() + pagesize.getBottom()) / 2;

            x = pagesize.getLeft();
            y = pagesize.getBottom();
            over = stamper.getOverContent(i);
            over.saveState();
            over.setGState(gs1);
            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, 0);
        }
        stamper.close();
        reader.close();
    }

    public void addVerticalText(String src, String dest) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        int n = reader.getNumberOfPages();
        PdfContentByte canvas;
        Rectangle pageSize;
        float x, y;
        for (int p = 1; p <= n; p++) {
            pageSize = reader.getPageSizeWithRotation(p);
            // left of the page
            x = pageSize.getLeft();
            // middle of the height
            y = (pageSize.getTop() + pageSize.getBottom()) / 2;
            // getting the canvas covering the existing content
            canvas = stamper.getOverContent(p);
            // adding some lines to the left
            // ColumnText.showTextAligned(canvas,
            // Element.ALIGN_CENTER,
            // new Phrase(
            // "This is some extra text added to the left of the page."),
            // x + 15, y, 90);
            //
            // ColumnText.showTextAligned(canvas,
            // Element.ALIGN_CENTER,
            // new Phrase(
            // "This is some more text added to the left of the page"),
            // x + 25, y, 90);
            //
            // ColumnText.showTextAligned(canvas,
            // Element.ALIGN_CENTER,
            // new Phrase(
            // "This is some more text added to the left of the page"),
            // x + 35, y, 90);

            final String waterMarkingtext =
                                            "Licensed to RavindraRanwala on 10-04-2016.\nPersonal use license only.\nStorage, distribution or use on network prohibited.";
            String lines[] = waterMarkingtext.split("\\r?\\n");
            int initOffset = 15;
            int count = 0;
            for (String line : lines) {
                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(line),
                                           x + initOffset + (count * 10), y, 90);
                count++;
            }
        }
        stamper.close();
    }
}
