package org.flauschhaus.broccoli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private FileUtils() {}

    public static void copy(File source, File destination) throws IOException {
        try (InputStream in = new FileInputStream(source)) {
            copy(in, destination);
        }
    }

    public static void copy(InputStream in, File destination) throws IOException {
        try (OutputStream fos = new FileOutputStream(destination)) {
           copy(in, fos);
        }
    }

    public static void copy(File source, OutputStream out) throws IOException {
        try (InputStream in = new FileInputStream(source)) {
            copy(in, out);
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }

}
