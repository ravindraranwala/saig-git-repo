package org.saig.watermark.SAIGlobalDemo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtil {
    private static final Log log = LogFactory.getLog(FileUtil.class);

    public static void delete(File file) throws IOException {

        if (file.isDirectory()) {

            // directory is empty, then delete it
            if (file.list().length == 0) {

                file.delete();

            } else {

                // list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    // construct the file structure
                    File fileDelete = new File(file, temp);

                    // recursive delete
                    delete(fileDelete);
                }

                // check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                    log.info("The Directory is deleted.");
                }
            }

        } else {
            // if file, then delete it
            file.delete();
            log.info("File is deleted ");
        }
    }

    public static void createFileWithContent(String fileName, String content) {
        BufferedWriter writer = null;
        try {
            // create a temporary file
            File tempFile = new File(fileName);

            // This will output the full path where the file will be written
            // to...
            System.out.println(tempFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public static String readFromFile(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            try {
                String line = br.readLine();

                if (line != null) {
                    return line;
                }
            } finally {
                br.close();
            }
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        createFileWithContent("/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/file-open-scenario/tmp/tmp.txt",
                              "7004/ETSI/etsi_en/300200_300299/DEC-CatalogDataAPIDesign-090316-1042-7.pdf");
        String content =
                         readFromFile("/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/file-open-scenario/tmp/tmp.txt");
        delete(new File(
                        "/home/ravindra/ESB-Team/customer-engagements/SAIGlobal/file-open-scenario/tmp/tmp.txt"));
        System.out.println(content);
    }

}
