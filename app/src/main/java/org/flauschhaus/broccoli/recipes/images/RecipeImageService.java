package org.flauschhaus.broccoli.recipes.images;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;

public class RecipeImageService {

    private static final String AUTHORITY = "org.flauschhaus.broccoli.fileprovider";

    private Application application;

    @Inject
    public RecipeImageService(Application application) {
        this.application = application;
    }

    public Uri createTemporaryImage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File cacheDirectory = getCacheDirectory();
        File temporaryImage = File.createTempFile(imageFileName, ".jpg", cacheDirectory);
        return FileProvider.getUriForFile(application, AUTHORITY, temporaryImage);
    }

    public void moveImage(String imageName) throws IOException {
        File savedImage = getSavedImage(imageName);
        File temporaryImage = getTemporaryImage(imageName);

        File compressedTemporaryFile = new Compressor(application).compressToFile(temporaryImage);
        FileUtils.copy(compressedTemporaryFile, savedImage);

        temporaryImage.delete();
    }

    public boolean deleteTemporaryImage(String imageName) {
        File image = getTemporaryImage(imageName);
        return image.delete();
    }

    public boolean deleteImage(String imageName) {
        File image = findImage(imageName);
        return image.delete();
    }

    File findImage(String imageName) {
        File savedImage = getSavedImage(imageName);

        if  (savedImage.exists()) {
            return savedImage;
        }

        return getTemporaryImage(imageName);
    }

    private File getSavedImage(String imageName) {
        File imageDirectory = getImageDirectory();
        return new File(imageDirectory.getAbsolutePath() + File.separator + imageName);
    }

    private File getTemporaryImage(String imageName) {
        File cacheDirectory = getCacheDirectory();
        return new File(cacheDirectory.getAbsolutePath() + File.separator + imageName);
    }

    private File getImageDirectory() {
        return application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    private File getCacheDirectory() {
        return application.getCacheDir();
    }

}
