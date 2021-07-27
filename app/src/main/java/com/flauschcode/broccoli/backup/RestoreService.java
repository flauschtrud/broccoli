package com.flauschcode.broccoli.backup;

import android.app.Application;
import android.net.Uri;

import androidx.room.Transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flauschcode.broccoli.FileUtils;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.recipe.sharing.RecipeZipReader;

import org.apache.commons.io.input.CloseShieldInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RestoreService {

    Application application;
    RecipeZipReader recipeZipReader;
    RecipeRepository recipeRepository;
    RecipeImageService recipeImageService;
    CategoryRepository categoryRepository;


    @Inject
    public RestoreService(Application application, RecipeZipReader recipeZipReader, RecipeRepository recipeRepository, RecipeImageService recipeImageService, CategoryRepository categoryRepository) {
        this.application = application;
        this.recipeZipReader = recipeZipReader;
        this.recipeRepository = recipeRepository;
        this.recipeImageService = recipeImageService;
        this.categoryRepository = categoryRepository;
    }


    public CompletableFuture<Integer> restore(Uri uri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return restoreInternal(uri);
            } catch (IOException | ExecutionException e) {
                throw new CompletionException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException(e);
            }
        });
    }

    // package private for testing purposes
    int restoreInternal(Uri uri) throws IOException, ExecutionException, InterruptedException {
        InputStream inputStream = application.getContentResolver().openInputStream(uri);

        List<Recipe> recipes = new ArrayList<>();
        List<Category> categories = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(inputStream); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".broccoli")) {
                    try (CloseShieldInputStream cloned = CloseShieldInputStream.wrap(zis); ZipInputStream nestedZis = new ZipInputStream(cloned)) {
                        Optional<Recipe> optionalRecipe = recipeZipReader.read().from(nestedZis);
                        optionalRecipe.ifPresent(recipes::add);
                    }
                } else if ("categories.json".equals(zipEntry.getName())) {
                    FileUtils.copy(zis, out);
                    ObjectMapper objectMapper = new ObjectMapper();
                    categories = objectMapper.readValue(new ByteArrayInputStream(out.toByteArray()), new TypeReference<List<Category>>() {});
                }
            }
            zis.closeEntry();
        }

        save(categories, recipes);
        return recipes.size();
    }

    @Transaction
    private void save(List<Category> categories, List<Recipe> recipes) throws ExecutionException, InterruptedException {
        List<Category> nonExistingCategories = categoryRepository.retainNonExisting(categories).get();
        for (Category category : nonExistingCategories) {
            categoryRepository.insertOrUpdate(category).get();
        }

        for (Recipe recipe : recipes) {
            List<Category> retainedCategories = categoryRepository.retainExisting(recipe.getCategories()).get();
            recipe.setCategories(retainedCategories);
            recipeRepository.insertOrUpdate(recipe);
            if (!"".equals(recipe.getImageName())) {
                recipeImageService.moveImage(recipe.getImageName());
            }
        }
    }

}
