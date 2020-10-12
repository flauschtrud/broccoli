package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.FileUtils;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

public class ShareRecipeAsFileService {

    private static final String AUTHORITY = "org.flauschhaus.broccoli.fileprovider";

    private Application application;
    private RecipeImageService recipeImageService;

    @Inject
    public ShareRecipeAsFileService(Application application, RecipeImageService recipeImageService) {
        this.application = application;
        this.recipeImageService = recipeImageService;
    }

    public Uri shareAsFile(Recipe recipe) throws IOException {
        String title = recipe.getTitle();

        String zipFileName = title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".broccoli";
        File zipFile = new File(application.getCacheDir(), zipFileName);

        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            String jsonFileName = title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".json";
            ZipEntry recipeEntry = new ZipEntry(jsonFileName);
            zos.putNextEntry(recipeEntry);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
            objectMapper.writeValue(zos, recipe);
            zos.closeEntry();

            if (recipe.getImageName().length() > 0) {
                ZipEntry imageEntry = new ZipEntry(recipe.getImageName());
                zos.putNextEntry(imageEntry);
                File imageFile = recipeImageService.findImage(recipe.getImageName());
                FileUtils.copy(imageFile, zos);
            }
        }

        return FileProvider.getUriForFile(application, AUTHORITY, zipFile);
    }

    public Optional<Recipe> loadFromFile(Uri uri) {
        ObjectMapper objectMapper = new ObjectMapper();

        InputStream inputStream;
        try {
            inputStream = application.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), e.getMessage());
            return Optional.empty();
        }

        Recipe recipe = null;
        String imageName =  null;

        try (ZipInputStream zis = new ZipInputStream(inputStream); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".json")) {
                    FileUtils.copy(zis, out);
                    recipe = objectMapper.readValue(new ByteArrayInputStream(out.toByteArray()), Recipe.class);
                } else if (zipEntry.getName().endsWith(".jpg")) {
                    File extractedImage = recipeImageService.createTemporaryImageFileInCache();
                    FileUtils.copy(zis, extractedImage);
                    imageName = extractedImage.getName();
                }
            }
            zis.closeEntry();
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
            return Optional.empty();
        }

        if (recipe != null) {
            recipe.getCategories().clear();
            recipe.setFavorite(false);
            if (imageName != null) {
                recipe.setImageName(imageName);
            }
            return Optional.of(recipe);
        }

        return Optional.empty();
    }

}
