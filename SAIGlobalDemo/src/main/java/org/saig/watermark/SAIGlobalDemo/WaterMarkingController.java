package org.saig.watermark.SAIGlobalDemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class WaterMarkingController {
    private Font f = new Font(FontFamily.HELVETICA, 20);
    private Rectangle pageSize;
    private static final Log log = LogFactory.getLog(WaterMarkingController.class);

    private float x;
    private float y;

    public void watermark(String sourceFilepath, String destinationFile, String watermarkingText) {
        PdfReader reader;
        try {
            reader = new PdfReader(sourceFilepath);
            PdfReader.unethicalreading = true;
            int n = reader.getNumberOfPages();
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destinationFile));
            // text water mark
            Phrase p = new Phrase(watermarkingText, f);

            // transparency
            PdfGState gs1 = new PdfGState();
            gs1.setFillOpacity(0.5f);
            // properties
            PdfContentByte over;
            // loop over every page
            for (int i = 1; i <= n; i++) {
                pageSize = reader.getPageSizeWithRotation(i);
                x = (pageSize.getLeft() + pageSize.getRight()) / 2;
                y = (pageSize.getTop() + pageSize.getBottom()) / 2;
                over = stamper.getOverContent(i);
                over.saveState();
                over.setGState(gs1);
                // ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x,
                // y, 0);

                ColumnText ct = new ColumnText(over);
                ct.setSimpleColumn(p, x / 4, y / 2, 580, 317, 15, Element.ALIGN_LEFT);
                ct.go();
            }
            stamper.close();
            reader.close();
        } catch (IOException e) {
            log.error(e);
        } catch (DocumentException e) {
            log.error(e);
        }

    }

    public void addVerticalText(String src, String dest, String watermarkingText)
                                                                                 throws DocumentException,
                                                                                 IOException {
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
            String lines[] = watermarkingText.split("\\r?\\n");
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

    public void addVerticalTextForPreview(String src, String dest, String watermarkingText,
                                          int numOfPgsForPreview) throws IOException,
                                                                 DocumentException {
        PdfReader reader = new PdfReader(src);
        reader.selectPages("1-" + (numOfPgsForPreview - 1));
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
            String lines[] = watermarkingText.split("\\r?\\n");
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

    public void mergeDocs(String sourceFileOne, String sourceFileTwo, String destinationFile) {
        PdfReader cover;
        try {
            cover = new PdfReader(sourceFileTwo);
            PdfReader reader = new PdfReader(sourceFileOne);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destinationFile));
            stamper.insertPage(1, cover.getPageSizeWithRotation(1));
            PdfContentByte page1 = stamper.getOverContent(1);
            PdfImportedPage page = stamper.getImportedPage(cover, 1);
            page1.addTemplate(page, 0, 0);
            stamper.close();
            cover.close();
            reader.close();
        } catch (IOException e) {
            log.error(e);
        } catch (DocumentException e) {
            log.error(e);
        }
    }

    public void manipulateCoverPage(String src, String dest, String staffAccountName, String date,
                                    String formTitle, String formLicense) throws IOException,
                                                                         DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        AcroFields form = stamper.getAcroFields();
        form.setField("conditions", formLicense);
        form.setField("purchby", staffAccountName);
        form.setField("purchdate", date);
        form.setField("Title", formTitle);
        stamper.setFormFlattening(true);
        stamper.close();
    }

    public static void main(String[] args) throws IOException, DocumentException {
        File theDir = new File("src/main/resources/result");
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            theDir.mkdir();
        }
        // new
        // WaterMarkingController().mergeDocs("src/main/resources/DEC-CatalogDataAPIDesign-090316-1042-7.pdf",
        // "/home/ravindra/workspace/bugfixing-hackathon/SAIGlobalDemo/src/main/resources/CoverPage.pdf");
        final WaterMarkingController waterMarkingController = new WaterMarkingController();
        waterMarkingController.addVerticalTextForPreview("/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/DEC-CatalogDataAPIDesign-090316-1042-7.pdf",
                                                         "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/tmp-preview/DEC-CatalogDataAPIDesign-090316-1042-7.pdf",
                                                         "Downloaded by Ravindra Ranwala on 10-04-2016.\nFor internal pre-sales support use within SAI Group only.",
                                                         2);
        waterMarkingController.mergeDocs("/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/tmp-preview/DEC-CatalogDataAPIDesign-090316-1042-7.pdf",
                                         "/home/ravindra/workspace/bugfixing-hackathon/SAIGlobalDemo/src/main/resources/CoverPage.pdf",
                                         "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/preview/DEC-CatalogDataAPIDesign-090316-1042-7.pdf");
    }
}
