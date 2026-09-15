package com.flauschcode.broccoli.backup.autoexport;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import com.flauschcode.broccoli.backup.BackupService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manually wires Dagger dependencies into {@link AutoExportWorker}, since this project
 * uses vanilla Dagger rather than Hilt (which would generate this kind of factory).
 */
@Singleton
public class DaggerWorkerFactory extends WorkerFactory {

    private final BackupService backupService;
    private final AutoExportPreferences autoExportPreferences;

    @Inject
    public DaggerWorkerFactory(BackupService backupService, AutoExportPreferences autoExportPreferences) {
        this.backupService = backupService;
        this.autoExportPreferences = autoExportPreferences;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        if (AutoExportWorker.class.getName().equals(workerClassName)) {
            return new AutoExportWorker(appContext, workerParameters, backupService, autoExportPreferences);
        }
        return null;
    }
}
