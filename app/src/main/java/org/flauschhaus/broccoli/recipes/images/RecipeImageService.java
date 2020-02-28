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

    Bitmap loadBitmapWithName(String imageName, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = getOptions(targetWidth, targetHeight);
        File imageDirectory = getImageDirectory();
        return BitmapFactory.decodeFile(imageDirectory.getAbsolutePath() + File.separator + imageName, options);
    }

    private File getImageDirectory() {
        return application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private BitmapFactory.Options getOptions(int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        int scaleFactor = Math.min(imageWidth/targetWidth, imageHeight/targetHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        return options;
    }

}
