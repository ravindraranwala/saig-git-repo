package org.saig.watermark.SAIGlobalDemo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class SignedUrlMediator extends AbstractMediator {
    private String s3deliveryBucket = "";
    private String s3Key = "";
    private boolean isPreview = false;
    // TODO: use this as a prefix when you push the PDF file to the delivery
    // bucket.
    private String orderId = "";
    String generateSignedUrl = null;

    public boolean mediate(MessageContext msgCtx) {
        InputStream inputStream;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName +
                                                "' not found in the classpath");
            }

            if ((String) msgCtx.getProperty("isPreview") != null) {
                isPreview = Boolean.parseBoolean((String) msgCtx.getProperty("isPreview"));
            }

            if (prop.getProperty("s3deliverybucket") != null) {
                s3deliveryBucket = prop.getProperty("s3deliverybucket");
            }
            /*
             * If this is a Preview scenario, then we don't have any orderId.
             * orderId is applicable iff this is not a preview scenario.
             */
            if (!isPreview) {

                if ((String) msgCtx.getProperty("orderId") != null) {
                    orderId = (String) msgCtx.getProperty("orderId");
                }
            }

            if ((String) msgCtx.getProperty("s3key") != null) {
                s3Key = (String) msgCtx.getProperty("s3key");
            }

            // First fetch the Signed Url for the given objectKey and bucket.
            // TODO make sure to prepend the orderID to the s3key here.
            if (isPreview) {

                generateSignedUrl =
                                    AmazonS3Util.generateSignedUrl(s3deliveryBucket,
                                                                   WatermarkConstants.preview +
                                                                           WatermarkConstants.fileSeparator +
                                                                           s3Key);
            } else {
                generateSignedUrl =
                                    AmazonS3Util.generateSignedUrl(s3deliveryBucket,
                                                                   orderId +
                                                                           WatermarkConstants.fileSeparator +
                                                                           s3Key);
            }

            msgCtx.setProperty("SignedUrl", generateSignedUrl);
        } catch (FileNotFoundException e) {
            log.error(e);
            return false;
        } catch (IOException e) {
            log.error(e);
            return false;
        }
        return true;
    }

}
