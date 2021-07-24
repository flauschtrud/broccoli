package com.flauschcode.broccoli.backup;

import android.app.Application;
import android.net.Uri;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flauschcode.broccoli.BuildConfig;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.sharing.RecipeZipWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BackupService {

    private static final String AUTHORITY = "com.flauschcode.broccoli.fileprovider";

    Application application;
    RecipeZipWriter recipeZipWriter;
    RecipeRepository recipeRepository;
    CategoryRepository categoryRepository;

    private MutableLiveData<Integer> maxRecipes = new MutableLiveData<>(0);
    private MutableLiveData<Integer> count = new MutableLiveData<>(0);

    @Inject
    BackupService(Application application, RecipeZipWriter recipeZipWriter, RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.application = application;
        this.recipeZipWriter = recipeZipWriter;
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }

    public CompletableFuture<Uri> backup() {
        return CompletableFuture.supplyAsync(() -> {
            Uri archiveUri;
            try {
                archiveUri = backupInternal();
            } catch (IOException | ExecutionException e) {
                throw new CompletionException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException(e);
            }
            saveLastBackupDate();
            return archiveUri;
        });
    }

    public LiveData<Integer> getMaxRecipes() {
        return maxRecipes;
    }

    public LiveData<Integer> getCount() {
        return count;
    }

    // package private for testing purposes
    Uri backupInternal() throws IOException, ExecutionException, InterruptedException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String zipFileName = "EXPORT_" + timeStamp + ".broccoli-archive";
        File zipFile = new File(application.getCacheDir(), zipFileName);

        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.setComment(String.valueOf(BuildConfig.VERSION_CODE));

            List<Category> categories = categoryRepository.findAll().getValue();
            ZipEntry categoryEntry = new ZipEntry("categories.json");
            zos.putNextEntry(categoryEntry);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
            objectMapper.writeValue(zos, categories);
            zos.closeEntry();

            List<Recipe> recipes = recipeRepository.findAll().get();
            maxRecipes.postValue(recipes.size());

            for (int i = 0; i < recipes.size(); i++) {
                count.postValue(i+1);

                Recipe recipe = recipes.get(i);
                ZipEntry entry = new ZipEntry(recipe.getRecipeId() + "_" + recipe.getTitle().replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".broccoli");
                zos.putNextEntry(entry);

                ZipOutputStream nestedZos = new ZipOutputStream(zos);
                recipeZipWriter.write(recipe).to(nestedZos);
                nestedZos.finish();

                zos.closeEntry();
            }

            return FileProvider.getUriForFile(application, AUTHORITY, zipFile);
        }
    }

    private void saveLastBackupDate() {
        LocalDate date = LocalDate.now();
        String localizedCalendarDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

        PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext())
                .edit()
                .putString("last-backup-date", localizedCalendarDate)
                .apply();
    }
}
