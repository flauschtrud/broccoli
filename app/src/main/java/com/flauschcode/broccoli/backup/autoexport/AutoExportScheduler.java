package com.flauschcode.broccoli.backup.autoexport;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Debounces auto-export: repeated calls within {@link #DEBOUNCE_DELAY_MINUTES} collapse
 * into a single export, since {@link ExistingWorkPolicy#REPLACE} restarts the delay.
 */
@Singleton
public class AutoExportScheduler {

    static final String UNIQUE_WORK_NAME = "auto-export";
    static final long DEBOUNCE_DELAY_MINUTES = 2;

    private final WorkManager workManager;
    private final AutoExportPreferences autoExportPreferences;

    @Inject
    public AutoExportScheduler(WorkManager workManager, AutoExportPreferences autoExportPreferences) {
        this.workManager = workManager;
        this.autoExportPreferences = autoExportPreferences;
    }

    public void scheduleIfEnabled() {
        if (!autoExportPreferences.isEnabled()) {
            return;
        }

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(AutoExportWorker.class)
                .setInitialDelay(DEBOUNCE_DELAY_MINUTES, TimeUnit.MINUTES)
                .build();

        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request);
    }
}
