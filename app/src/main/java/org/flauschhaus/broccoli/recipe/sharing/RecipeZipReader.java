package org.flauschhaus.broccoli.recipe.sharing;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.flauschhaus.broccoli.FileUtils;
import org.flauschhaus.broccoli.category.Category;
import org.flauschhaus.broccoli.category.CategoryRepository;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

public class RecipeZipReader {

    private final RecipeImageService recipeImageService;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public RecipeZipReader(RecipeImageService recipeImageService, CategoryRepository categoryRepository) {
        this.recipeImageService = recipeImageService;
        this.categoryRepository = categoryRepository;
    }

    public RecipeZipWriterBuilder read() {
        return new RecipeZipWriterBuilder();
    }

    public class RecipeZipWriterBuilder {

        private boolean unfavored = false;

        public RecipeZipWriterBuilder unfavored() {
            unfavored = true;
            return this;
        }

        public Optional<Recipe> from(ZipInputStream zis) {
            Recipe recipe = null;
            String imageName =  null;

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
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

                    if (unfavored) {
                        recipe.setFavorite(false);
                    }

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
}
