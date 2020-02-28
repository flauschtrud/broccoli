package org.flauschhaus.broccoli.recipes.images;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class RecipeImageService {

    private Application application;

    @Inject
    public RecipeImageService(Application application) {
        this.application = application;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageDirectory = getImageDirectory();
        return File.createTempFile(imageFileName,".jpg", imageDirectory);
    }

    Bitmap loadBitmapWithName(String imageName) {
        File imageDirectory = getImageDirectory();
        return BitmapFactory.decodeFile(imageDirectory.getAbsolutePath() + "/" + imageName);
    }

    File getFile(String imageName) {
        File imageDirectory = getImageDirectory();
        return new File(imageDirectory.getAbsolutePath() + "/" + imageName);
    }

    Bitmap loadBitmapWithName(String imageName, int targetWidth, int targetHeight) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoHeight = bmOptions.outWidth;
        int imageHeight = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoHeight/targetWidth, imageHeight/targetHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        File imageDirectory = getImageDirectory();
        return BitmapFactory.decodeFile(imageDirectory.getAbsolutePath() + "/" + imageName, bmOptions);
    }

    private File getImageDirectory() {
        return application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

}
