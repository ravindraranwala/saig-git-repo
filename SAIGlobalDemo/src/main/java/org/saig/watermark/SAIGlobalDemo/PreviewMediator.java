package org.saig.watermark.SAIGlobalDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

import com.itextpdf.text.DocumentException;

public class PreviewMediator extends AbstractMediator {
    public static String baseDirPath =
                                       "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/preview-demo";
    public static String s3MasterBucket = "";
    public static String s3DeliveryBucket = "";
    public static String s3Key = "";
    public static final String fileSeparator = "/";
    private String sourceFileName = "DEC-CatalogDataAPIDesign-090316-1042-7.pdf";
    private String destinationFileName = "pages_previewed.pdf";
    private String watermarkingText = "My watermark (text)";
    private String staffAccountName = null;
    private String date = null;
    private String documentType = null;
    private String templateString = null;
    private static final Log log = LogFactory.getLog(PreviewMediator.class);
    private WaterMarkingController waterMarkingController = new WaterMarkingController();
    private String coverPage = null;
    private int numOfPgsforPreview = 1;
    private String formTitle = null;
    private String formLicense = null;
    private String coverPageFilled = null;

    public boolean mediate(MessageContext msgCtx) {
        try {
            InputStream inputStream;
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName +
                                                "' not found in the classpath");
            }

            if (prop.getProperty("preview.dir") != null) {
                baseDirPath = prop.getProperty("preview.dir");
            }

            if (prop.getProperty("s3masterbucket") != null) {
                s3MasterBucket = prop.getProperty("s3masterbucket");
            }

            if (prop.getProperty("s3deliverybucket") != null) {
                s3DeliveryBucket = prop.getProperty("s3deliverybucket");
            }

            if (prop.getProperty("coverPage") != null) {
                coverPage = prop.getProperty("coverPage");
            }

            if (prop.getProperty("filledform") != null) {
                coverPageFilled = prop.getProperty("filledform");
            }

            // Setting the parameters if they exist.
            if ((String) msgCtx.getProperty("sourceFileName") != null) {
                sourceFileName = (String) msgCtx.getProperty("sourceFileName");
            }

            if ((String) msgCtx.getProperty("destinationFileName") != null) {
                destinationFileName = (String) msgCtx.getProperty("destinationFileName");
            }

            if ((String) msgCtx.getProperty("staffAccountName") != null) {
                staffAccountName = (String) msgCtx.getProperty("staffAccountName");
            }

            if ((String) msgCtx.getProperty("date") != null) {
                date = (String) msgCtx.getProperty("date");
            }

            if ((String) msgCtx.getProperty("documentType") != null) {
                documentType = (String) msgCtx.getProperty("documentType");
            }

            if ((String) msgCtx.getProperty("s3key") != null) {
                s3Key = (String) msgCtx.getProperty("s3key");
            }

            if ((String) msgCtx.getProperty("numOfPgs") != null) {
                numOfPgsforPreview = Integer.valueOf((String) msgCtx.getProperty("numOfPgs"));
            }

            if (prop.getProperty(documentType) != null) {
                templateString = prop.getProperty(documentType);
            }

            if ((String) msgCtx.getProperty("formTitle") != null) {
                formTitle = (String) msgCtx.getProperty("formTitle");
            }

            if ((String) msgCtx.getProperty("formLicense") != null) {
                formLicense = (String) msgCtx.getProperty("formLicense");
            }
            // First fetch the file from the AWS S3 instance and place it under
            // local file system of the server.

            // TODO uncomment this and test this in their AWS instance.
            AmazonS3Util.readFileFromS3cketBucket(s3MasterBucket, s3Key, baseDirPath,
                                                  sourceFileName);

            // Now go ahead and do the watermarking
            // Create the Watermarking test.
            watermarkingText = createWaterMarkingText();

            // will create a sub folder for each orderId
            File theDir = new File(baseDirPath + fileSeparator + WatermarkConstants.preview);
            // if the directory does not exist, create it
            if (!theDir.exists()) {
                theDir.mkdir();
            }

            // Call the water marking module to get it done.
            final String sourceFilepath = baseDirPath + fileSeparator + sourceFileName;
            final String destinationFilePath =
                                               baseDirPath + fileSeparator +
                                                       WatermarkConstants.preview + fileSeparator +
                                                       destinationFileName;
            final String destinationTempFilePath =
                                                   baseDirPath + fileSeparator +
                                                           WatermarkConstants.preview +
                                                           fileSeparator + "tmp" + fileSeparator +
                                                           destinationFileName;

            File tmpDir =
                          new File(baseDirPath + fileSeparator + WatermarkConstants.preview +
                                   fileSeparator + "tmp");
            // if the directory does not exist, create it
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }

            waterMarkingController.manipulateCoverPage(coverPage, coverPageFilled,
                                                       staffAccountName, date, formTitle,
                                                       formLicense);
            waterMarkingController.addVerticalTextForPreview(sourceFilepath,
                                                             destinationTempFilePath,
                                                             watermarkingText, numOfPgsforPreview);
            waterMarkingController.mergeDocs(destinationTempFilePath, coverPageFilled,
                                             destinationFilePath);

            // Once water marking thing is done and file is kept under the
            // output dir, call the AWSS3 utility to push it back to the S3
            // delivery bucket.
            // TODO uncomment this and test this in their AWS instance. Make
            // sure
            // to prefix the preview to the Amazon s3Key here.
            AmazonS3Util.uploadFileToS3Bucket(s3DeliveryBucket, WatermarkConstants.preview +
                                                                WatermarkConstants.fileSeparator +
                                                                s3Key, baseDirPath + fileSeparator +
                                                                       WatermarkConstants.preview,
                                              destinationFileName);

            // Once water Marking is done, remove the original source file.
            FileUtil.delete(new File(sourceFilepath));
            FileUtil.delete(new File(coverPageFilled));
        } catch (FileNotFoundException e) {
            log.error(e);
            return false;
        } catch (IOException e) {
            log.error(e);
            return false;
        } catch (NoSuchFieldException e) {
            log.error(e);
            return false;
        } catch (SecurityException e) {
            log.error(e);
            return false;
        } catch (IllegalArgumentException e) {
            log.error(e);
            return false;
        } catch (IllegalAccessException e) {
            log.error(e);
            return false;
        } catch (DocumentException e) {
            log.error(e);
            return false;
        }
        return true;
    }

    private String createWaterMarkingText() throws NoSuchFieldException, SecurityException,
                                           IllegalArgumentException, IllegalAccessException {
        DocumentDetails d = new DocumentDetails(staffAccountName, date);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("d", d);

        // get the property value and pass it with the map for the
        // formatting.
        return StringFormatter.format(templateString, map);

    }

}
