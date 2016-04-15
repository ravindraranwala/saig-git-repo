package org.saig.watermark.SAIGlobalDemo;

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
    public static String s3Key =
                                 "ETSI/etsi_en/300200_300299/DEC-CatalogDataAPIDesign-090316-1042-7.pdf";
    public static final String fileSeparator = "/";
    private String fileName = "DEC-CatalogDataAPIDesign-090316-1042-7.pdf";

    public boolean mediate(MessageContext synCtx) {
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

            AmazonS3Util.uploadFileToS3Bucket(s3DeliveryBucket, s3Key, baseDirPath, fileName);
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
