package org.saig.watermark.SAIGlobalDemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class AmazonS3Util {
    private static AWSCredentials credentials = null;
    private static final String fileSeparator = "/";
    private static final Log log = LogFactory.getLog(AmazonS3Util.class);
    static {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (~/.aws/credentials).
         */
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                                            "Cannot load the credentials from the credential profiles file. "
                                                    + "Please make sure that your credentials file is at the correct "
                                                    + "location (~/.aws/credentials), and is in valid format.",
                                            e);
        }
    }

    public static void readFileFromS3cketBucket(String bucketName, String key, String dirPath,
                                                String fileName) {
        FilterInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            // Remove the file if it already exists.
            if (new File(dirPath + WatermarkingMediator.fileSeparator + fileName).exists()) {
                FileUtil.delete(new File(dirPath + WatermarkingMediator.fileSeparator + fileName));
            }

            AmazonS3 s3 = new AmazonS3Client(credentials);
            Region usEast1 = Region.getRegion(Regions.US_EAST_1);
            s3.setRegion(usEast1);
            /*
             * Download an object - When you download an object, you get all of
             * the object's metadata and a stream from which to read the
             * contents.
             * It's important to read the contents of the stream as quickly as
             * possibly since the data is streamed directly from Amazon S3 and
             * your
             * network connection will remain open until you read all the data
             * or
             * close the input stream.
             * 
             * GetObjectRequest also supports several other options, including
             * conditional downloading of objects based on modification times,
             * ETags, and selectively downloading a range of an object.
             */
            log.info("Downloading an object from the S3 bucket.");
            S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
            log.info("Content-Type: " + object.getObjectMetadata().getContentType());
            inputStream = object.getObjectContent();

            File dirForOrder = new File(dirPath);
            if (!dirForOrder.exists()) {
                dirForOrder.mkdir();
            }

            outputStream = new FileOutputStream(new File(dirPath + fileSeparator + fileName));
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public static void uploadFileToS3Bucket(String bucketName, String key, String dirPath,
                                            String fileName) {
        AmazonS3 s3 = new AmazonS3Client(credentials);
        Region usEast1 = Region.getRegion(Regions.US_EAST_1);
        s3.setRegion(usEast1);
        s3.putObject(new PutObjectRequest(bucketName, key, new File(dirPath + fileSeparator +
                                                                    fileName)));
        try {
            FileUtil.delete(new File(dirPath));
        } catch (IOException e) {
            log.error(e);
        }

    }

    public static void main(String[] args) {
        // readFileFromS3cketBucket("saig-usstd-dev-decp",
        // "ETSI/etsi_en/300200_300299/en_300299v010301p.pdf",
        // "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/7704",
        // "en_300299v010301p.pdf");
        // uploadFileToS3Bucket("saig-usstd-dev-delivery",
        // "ETSI/etsi_en/300200_300299/en_300299v010301p-ravindra-test.pdf",
        // "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/7704/",
        // "7004", "en_300299v010301p-ravindra-test.pdf");

        // uploadFileToS3Bucket("saig-usstd-dev-decp",
        // "ETSI/etsi_en/300200_300299/DEC-CatalogDataAPIDesign-090316-1042-7.pdf",
        // "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/7004/",
        // "7004", "DEC-CatalogDataAPIDesign-090316-1042-7.pdf");

        readFileFromS3cketBucket("saig-usstd-dev-delivery",
                                 "ETSI/etsi_en/300200_300299/DEC-CatalogDataAPIDesign-090316-1042-7.pdf",
                                 "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/watermarking-demo/7004",
                                 "DEC-CatalogDataAPIDesign-090316-1042-7.pdf");
    }
}
