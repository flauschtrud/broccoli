package com.flauschcode.broccoli.backup;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.FileUtils;

import com.flauschcode.broccoli.R;
import com.flauschcode.broccoli.category.Category;
import com.flauschcode.broccoli.category.CategoryRepository;
import com.flauschcode.broccoli.recipe.Recipe;
import com.flauschcode.broccoli.recipe.RecipeRepository;
import com.flauschcode.broccoli.recipe.images.RecipeImageService;
import com.flauschcode.broccoli.recipe.sharing.RecipeZipReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class RestoreService extends JobIntentService {

    private static final int JOB_ID = 2;
    private static final int NOTIFICATION_ID = 2;

    @Inject
    Application application;

    @Inject
    RecipeZipReader recipeZipReader;

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    RecipeImageService recipeImageService;

    @Inject
    CategoryRepository categoryRepository;

    private NotificationManagerCompat notificationManager;

    public RestoreService() {
        super();
    }

    // for testing purposes
    public RestoreService(Application application, RecipeZipReader recipeZipReader, RecipeRepository recipeRepository, RecipeImageService recipeImageService, CategoryRepository categoryRepository) {
        this.application = application;
        this.recipeZipReader = recipeZipReader;
        this.recipeRepository = recipeRepository;
        this.recipeImageService = recipeImageService;
        this.categoryRepository = categoryRepository;
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, RestoreService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);

        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Uri uri = intent.getData();
        try {
            notifyProgress();
            int numberOfRecipes = restore(uri);
            notifyCompletion(numberOfRecipes);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
            notifyError();
        }
    }

    // package private for testing purposes
    int restore(Uri uri) throws IOException, ExecutionException, InterruptedException {
        InputStream inputStream = application.getContentResolver().openInputStream(uri);

        List<Recipe> recipes = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        int count = 0;

        try (ZipInputStream zis = new ZipInputStream(inputStream); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".broccoli")) {
                    Optional<Recipe> optionalRecipe = recipeZipReader.read().from(new ZipInputStream(zis));
                    optionalRecipe.ifPresent(recipes::add);
                    count++;
                } else if ("categories.json".equals(zipEntry.getName())) {
                    FileUtils.copy(zis, out);
                    ObjectMapper objectMapper = new ObjectMapper();
                    categories = objectMapper.readValue(new ByteArrayInputStream(out.toByteArray()), new TypeReference<List<Category>>() {});
                }
            }
            zis.closeEntry();
        }

        save(categories, recipes);
        return count;
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

    private void notifyProgress() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.restore_in_progress))
                .setProgress(0, 0,true)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyCompletion(int numberOfRecipes) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.restore_completed))
                .setContentText(getString(R.string.restore_completed_message, numberOfRecipes))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyError() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.restore_failed))
                .setContentText(getString(R.string.restore_failed_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
