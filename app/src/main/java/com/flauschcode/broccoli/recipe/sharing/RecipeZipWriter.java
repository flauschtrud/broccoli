package com.flauschcode.broccoli.recipe.sharing;

import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flauschcode.broccoli.FileUtils;

import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

public class RecipeZipWriter {

    private final RecipeImageService recipeImageService;

    @Inject
    public RecipeZipWriter(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    public RecipeZipWriterBuilder write(Recipe recipe) {
        return new RecipeZipWriterBuilder(recipe);
    }

    public class RecipeZipWriterBuilder {

        private final Recipe recipe;

        public RecipeZipWriterBuilder(Recipe recipe) {
            this.recipe = recipe;
        }

        public void to(ZipOutputStream zos) throws IOException {
            sanitizeImage();
            writeRecipe(zos);
            writeRecipeImage(zos);
        }

        private void writeRecipe(ZipOutputStream zos) throws IOException {
            ZipEntry recipeEntry = new ZipEntry(getEntryNameFor(recipe));
            zos.putNextEntry(recipeEntry);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
            objectMapper.writeValue(zos, recipe);

            zos.closeEntry();
        }

        private String getEntryNameFor(Recipe recipe) {
            return recipe.getTitle().replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".json";
        }

        private void writeRecipeImage(ZipOutputStream zos) throws IOException {
            if (recipe.getImageName().length() > 0) {
                ZipEntry imageEntry = new ZipEntry(recipe.getImageName());
                zos.putNextEntry(imageEntry);
                File imageFile = recipeImageService.findImage(recipe.getImageName());
                FileUtils.copy(imageFile, zos);
                zos.closeEntry();
            }
        }

        /**
         * Could not find a better way to avoid problems like https://github.com/flauschtrud/broccoli/issues/119.
         * It would be nice to be able to remove a broken recipe from the zip after an exception occurs,
         * but this is not possible with old school Java zipping. And stupid Android does not support JAVA NIO zip file system.
         */
        private void sanitizeImage() {
            if (recipe.getImageName().length() > 0 && !recipeImageService.findImage(recipe.getImageName()).exists()) {
                Log.w(getClass().getName(), "The image file \"" + recipe.getImageName() + "\" for recipe \"" + recipe.getTitle() + "\" does not exist.");
                recipe.setImageName("");
            }
        }
    }
}
