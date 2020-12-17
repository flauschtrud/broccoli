package com.flauschcode.broccoli.backup;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flauschcode.broccoli.BroccoliApplication;
import com.flauschcode.broccoli.BuildConfig;
import com.flauschcode.broccoli.R;
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
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class BackupService extends JobIntentService {

    private static final int JOB_ID = 1;
    private static final String AUTHORITY = "com.flauschcode.broccoli.fileprovider";
    private static final int NOTIFICATION_ID = 1;

    @Inject
    Application application;

    @Inject
    RecipeZipWriter recipeZipWriter;

    @Inject
    RecipeRepository recipeRepository;

    @Inject
    CategoryRepository categoryRepository;

    private NotificationManagerCompat notificationManager;

    public BackupService() {
        super();
    }

    // for testing purposes
    BackupService(Application application, RecipeZipWriter recipeZipWriter, RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.application = application;
        this.recipeZipWriter = recipeZipWriter;
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, BackupService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);

        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
       try {
           notifyProgress();
           Uri archiveUri = backup();
           saveLastBackupDate();
           notifyCompletion(archiveUri);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
            notifyError();
        }
    }

    // package private for testing purposes
    Uri backup() throws IOException, ExecutionException, InterruptedException {
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
            for (int i = 0; i < recipes.size(); i++) {
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

    private void notifyProgress() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.backup_in_progress))
                .setProgress(0, 0,true)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyCompletion(Uri archiveUri) {
        Intent backupIntent = new Intent(this, SaveBackupActivity.class);
        backupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        backupIntent.setData(archiveUri);
        backupIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, backupIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.backup_complete))
                .setContentText(getString(R.string.backup_complete_prompt))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyError() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.backup_failed))
                .setContentText(getString(R.string.backup_failed_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void saveLastBackupDate() {
        LocalDate date = LocalDate.now();
        String localizedCalendarDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString("last-backup-date", localizedCalendarDate)
                .apply();
    }
}
