package org.flauschhaus.broccoli.backup;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.flauschhaus.broccoli.BroccoliApplication;
import org.flauschhaus.broccoli.R;
import org.flauschhaus.broccoli.recipe.Recipe;
import org.flauschhaus.broccoli.recipe.RecipeRepository;
import org.flauschhaus.broccoli.recipe.images.RecipeImageService;
import org.flauschhaus.broccoli.recipe.sharing.RecipeZipReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
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

    private NotificationManagerCompat notificationManager;

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

        InputStream inputStream;
        try {
            inputStream = application.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e(getClass().getName(), e.getMessage());
            notifyFinish(getString(R.string.restore_failed), getString(R.string.restore_failed_message));
            return;
        }

        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            int count = 0;
            notifyProgress(count);
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".broccoli")) {
                    Optional<Recipe> optionalRecipe = recipeZipReader.read().from(new ZipInputStream(zis));
                    optionalRecipe.ifPresent(recipe -> {
                        recipeRepository.insertOrUpdate(recipe);
                        recipeImageService.moveImage(recipe.getImageName());
                    });
                    notifyProgress(count++);
                }
            }
            zis.closeEntry();
            notifyFinish(getString(R.string.restore_complete), getString(R.string.restore_complete_message, count));
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
            notifyFinish(getString(R.string.restore_failed), getString(R.string.restore_failed_message));
        }
    }

    private void notifyProgress(int i) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(getString(R.string.restore_in_progress))
                .setContentText(getString(R.string.recipes_restored, i+1))
                .setProgress(0, 0,true)
                .setNotificationSilent()
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void notifyFinish(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BroccoliApplication.CHANNEL_ID_BACKUP)
                .setSmallIcon(R.drawable.ic_button_restaurant_24dp)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
