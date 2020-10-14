package org.flauschhaus.broccoli.recipe.sharing;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.flauschhaus.broccoli.FileUtils;
import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

public class ShareRecipeAsFileService {

    private static final String AUTHORITY = "org.flauschhaus.broccoli.fileprovider";

    private Application application;
    private RecipeZipWriter recipeZipWriter;
    private RecipeImageService recipeImageService;
    private CategoryRepository categoryRepository;

    @Inject
    public ShareRecipeAsFileService(Application application, RecipeZipWriter recipeZipWriter, RecipeImageService recipeImageService, CategoryRepository categoryRepository) {
        this.application = application;
        this.recipeZipWriter = recipeZipWriter;
        this.recipeImageService = recipeImageService;
        this.categoryRepository = categoryRepository;
    }

    public Uri shareAsFile(Recipe recipe) throws IOException {
        String title = recipe.getTitle();

        String zipFileName = title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".broccoli";
        File zipFile = new File(application.getCacheDir(), zipFileName);

        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            recipeZipWriter.write(recipe).to(zos);
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
            try {
                List<Category> retainedCategories = categoryRepository.retainExisting(recipe.getCategories()).get();
                recipe.setCategories(retainedCategories);
                recipe.setFavorite(false);
                if (imageName != null) {
                    recipe.setImageName(imageName);
                }
                return Optional.of(recipe);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }

        return Optional.empty();
    }

}
