package org.flauschhaus.broccoli.recipes.images;

import android.app.Application;
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

    File getFile(String imageName) {
        File imageDirectory = getImageDirectory();
        return new File(imageDirectory.getAbsolutePath() + File.separator + imageName);
    }

    private File getImageDirectory() {
        return application.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

}
