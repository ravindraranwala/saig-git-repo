package org.saig.watermark.SAIGlobalDemo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class FillForm {
    public static final String SRC =
                                     "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/CoverPage.pdf";
    public static final String DEST =
                                      "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/filled-form.pdf";

    public static void main(String[] args) throws DocumentException, IOException {
        new FillForm().manipulateCoverPage(SRC, DEST);

    }

    public void manipulatePdf(String src, String dest) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        AcroFields form = stamper.getAcroFields();
        form.setField("course", "Copying and Pasting from somewhere");
        form.setField("name", "Some dude on somewhere");
        form.setField("date", "April 10, 2016");
        form.setField("description",
                      "In this course, people consistently ignore the existing documentation completely. "
                              + "They are encouraged to do no effort whatsoever, but instead post their questions "
                              + "on somewhere. It would be a mistake to refer to people completing this course "
                              + "as developers. A better designation for them would be copy/paste artist. "
                              + "Only in very rare cases do these people know what they are actually doing. "
                              + "Not a single student has ever learned anything substantial during this course.");
        stamper.setFormFlattening(true);
        stamper.close();
    }

    public void manipulateCoverPage(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        AcroFields form = stamper.getAcroFields();
        Set<String> fldNames = form.getFields().keySet();

        for (String fldName : fldNames) {
            System.out.println(fldName + ": " + form.getField(fldName));
        }

        form.setField("conditions", "This is a dummy condition which comes here.");
        form.setField("purchby", "Ravindra Ranwala");
        form.setField("purchdate", "April 10, 2016");
        form.setField("Title", "Test Title comes here.");
        stamper.setFormFlattening(true);
        stamper.close();
    }
}
