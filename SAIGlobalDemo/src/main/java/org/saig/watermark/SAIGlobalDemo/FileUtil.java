package org.saig.watermark.SAIGlobalDemo;

import java.io.File;
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

}
