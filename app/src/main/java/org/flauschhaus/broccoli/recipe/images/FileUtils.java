package org.flauschhaus.broccoli.recipe.images;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileUtils {

    private FileUtils() {}

    static void copy(File source, File destination) throws IOException {
        try (InputStream in = new FileInputStream(source)) {
            copy(in, destination);
        }
    }

    static void copy(InputStream in, File destination) throws IOException {
        try (OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

}
