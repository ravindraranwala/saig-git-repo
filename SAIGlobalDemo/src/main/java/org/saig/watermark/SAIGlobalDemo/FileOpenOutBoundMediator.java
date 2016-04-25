package org.saig.watermark.SAIGlobalDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class FileOpenOutBoundMediator extends AbstractMediator {
    public static String baseDirPath =
                                       "file:///home/ravindra/ESB-Team/customer-engagements/SAIGlobal/file-open-scenario/test-files/out";
    public static String s3DeliveryBucket = "";
    public static String s3Key = "";
    public static final String fileSeparator = "/";
    private String fileName = "DEC-CatalogDataAPIDesign-090316-1042-7.pdf";
    private String drmTmpFileName = null;

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

            if (prop.getProperty("fileopen.outbound.dir") != null) {
                baseDirPath = prop.getProperty("fileopen.outbound.dir");
            }

            if (prop.getProperty("s3deliverybucket") != null) {
                s3DeliveryBucket = prop.getProperty("s3deliverybucket");
            }

            if (prop.getProperty("drm.tmp.file.name") != null) {
                drmTmpFileName = prop.getProperty("drm.tmp.file.name");
            }

            if ((String) msgCtx.getProperty("FileName") != null) {
                fileName = (String) msgCtx.getProperty("FileName");
            }

            // TODO: make sure to prefix the orderID here. Write the code to
            // read the orderID and s3key from a temp file dumped by the
            // WaterMarkingMediator. FileOpenInboundMediator has no use in the
            // flow here.
            s3Key = FileUtil.readFromFile(drmTmpFileName);
            System.out.println("******** s3Key read from the file is: ==========>" + s3Key +
                               " Temp File Name: " + drmTmpFileName);
            AmazonS3Util.uploadFileToS3Bucket(s3DeliveryBucket, s3Key, baseDirPath, fileName);
            FileUtil.delete(new File(drmTmpFileName));
        } catch (FileNotFoundException e) {
            log.error("Error Occurred", e);
            return false;
        } catch (IOException e) {
            log.error("Error Occurred", e);
            return false;
        }
        return true;
    }

}
