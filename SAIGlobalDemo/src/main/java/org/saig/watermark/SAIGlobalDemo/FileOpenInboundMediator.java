package org.saig.watermark.SAIGlobalDemo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

/**
 * The purpose of this class mediator is to fetch a given file from the S3
 * master bucket and place it inside the local file system of the server. This
 * is used to implement the FileOpen scenario.
 * 
 * @author ravindra
 *
 */
public class FileOpenInboundMediator extends AbstractMediator {
    public static String baseDirPath =
                                       "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/file-open-scenario/test-files";
    public static String s3MasterBucket = "";
    public static String s3DeliveryBucket = "";
    public static String s3Key = "";
    public static final String fileSeparator = "/";
    private String sourceFileName = "DEC-CatalogDataAPIDesign-090316-1042-7.pdf";
    /*
     * A sub-directory will be created for the destination using this orderId
     * value.
     */
    private String orderId = "1001";

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

            if (prop.getProperty("fileopen.inbound.dir") != null) {
                baseDirPath = prop.getProperty("fileopen.inbound.dir");
            }

            if (prop.getProperty("s3masterbucket") != null) {
                s3MasterBucket = prop.getProperty("s3masterbucket");
            }

            if (prop.getProperty("s3deliverybucket") != null) {
                s3DeliveryBucket = prop.getProperty("s3deliverybucket");
            }

            // Setting the parameters if they exist.
            if ((String) msgCtx.getProperty("sourceFileName") != null) {
                sourceFileName = (String) msgCtx.getProperty("sourceFileName");
            }

            if ((String) msgCtx.getProperty("orderId") != null) {
                orderId = (String) msgCtx.getProperty("orderId");
            }

            if ((String) msgCtx.getProperty("s3key") != null) {
                s3Key = (String) msgCtx.getProperty("s3key");
            }

            // TODO: save the s3key and orderId to some unique place in the
            // FileSystem. Then fetch it at the OutboundMediator and use it.
            // Finally delete the file.

            // First fetch the file from the AWS S3 instance and place it under
            // local file system of the server.
            AmazonS3Util.readFileFromS3cketBucket(s3MasterBucket, s3Key, baseDirPath,
                                                  sourceFileName);
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
