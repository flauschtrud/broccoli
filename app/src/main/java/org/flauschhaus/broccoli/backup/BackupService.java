package org.flauschhaus.broccoli.backup;

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

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.BuildConfig;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.sharing.RecipeZipWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class BackupService extends JobIntentService {

    private static final int JOB_ID = 1;
    private static final String AUTHORITY = "org.flauschhaus.broccoli.fileprovider";
    private static final int NOTIFICATION_ID = 1;

    @Inject
    Application application;

    @Inject
    RecipeZipWriter recipeZipWriter;

    @Inject
    RecipeRepository recipeRepository;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, BackupService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String zipFileName = "EXPORT_" + timeStamp + ".broccoli-archive";
        File zipFile = new File(application.getCacheDir(), zipFileName);

        try (FileOutputStream fos = new FileOutputStream(zipFile); ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.setComment(String.valueOf(BuildConfig.VERSION_CODE));

            List<Recipe> recipes = recipeRepository.findAll().get();

            int numberOfRecipes = recipes.size();
            for (int i = 0; i < numberOfRecipes; i++) {
                notifyProgress(numberOfRecipes, i);
                Recipe recipe = recipes.get(i);
                recipeZipWriter.write(recipe).inSubDirectory(recipe.getRecipeId() + "_" + recipe.getTitle().replaceAll("[^a-zA-Z0-9\\.\\-]", "_")).to(zos);
            }

            Uri archiveUri = FileProvider.getUriForFile(application, AUTHORITY, zipFile);
            notifyCompletion(archiveUri);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
            notifyError();
        }
    }

    private void notifyProgress(int numberOfRecipes, int i) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.backup_in_progress))
                .setContentText(getString(R.string.backup_recipe) + " " + i+1 + "/" + numberOfRecipes)
                .setProgress(numberOfRecipes, i,false)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyCompletion(Uri archiveUri) {
        Intent backupIntent = new Intent(this, BackupActivity.class);
        backupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        backupIntent.setData(archiveUri);
        backupIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, backupIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.backup_complete))
                .setContentText(getString(R.string.backup_complete_prompt))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_launcher_foreground, "Save", pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyError() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.backup_failed))
                .setContentText(getString(R.string.backup_failed_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
