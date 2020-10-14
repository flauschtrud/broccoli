package org.flauschhaus.broccoli.recipe.sharing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.flauschhaus.broccoli.FileUtils;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

public class RecipeZipWriter {

    private RecipeImageService recipeImageService;

    @Inject
    public RecipeZipWriter(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    public RecipeZipWriterBuilder write(Recipe recipe) {
        return new RecipeZipWriterBuilder(recipe);
    }

    public class RecipeZipWriterBuilder {

        private Recipe recipe;
        private String subDirectory;

        public RecipeZipWriterBuilder(Recipe recipe) {
            this.recipe = recipe;
        }

        public RecipeZipWriterBuilder inSubDirectory(String subDirectory) {
            this.subDirectory = subDirectory;
            return this;
        }

        public void to(ZipOutputStream zos) throws IOException {
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
            String jsonFileName = recipe.getTitle().replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".json";
            return getEntryNameFor(jsonFileName);
        }

        private String getEntryNameFor(String fileName) {
            return subDirectory != null? subDirectory + File.separator + fileName : fileName;
        }

        private void writeRecipeImage(ZipOutputStream zos) throws IOException {
            if (recipe.getImageName().length() > 0) {
                ZipEntry imageEntry = new ZipEntry(getEntryNameFor(recipe.getImageName()));
                zos.putNextEntry(imageEntry);
                File imageFile = recipeImageService.findImage(recipe.getImageName());
                FileUtils.copy(imageFile, zos);
                zos.closeEntry();
            }
        }
    }
}
