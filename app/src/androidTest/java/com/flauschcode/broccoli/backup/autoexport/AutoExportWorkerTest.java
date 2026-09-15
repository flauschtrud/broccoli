package com.flauschcode.broccoli.backup.autoexport;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;
import androidx.work.testing.TestListenableWorkerBuilder;

import com.flauschcode.broccoli.backup.BackupService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

@RunWith(AndroidJUnit4.class)
public class AutoExportWorkerTest {

    private final BackupService backupService = mock(BackupService.class);
    private final AutoExportPreferences autoExportPreferences = mock(AutoExportPreferences.class);

    private final WorkerFactory workerFactory = new WorkerFactory() {
        @Override
        public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
            return new AutoExportWorker(appContext, workerParameters, backupService, autoExportPreferences);
        }
    };

    private AutoExportWorker buildWorker() {
        return (AutoExportWorker) TestListenableWorkerBuilder.from(ApplicationProvider.getApplicationContext(), AutoExportWorker.class)
                .setWorkerFactory(workerFactory)
                .build();
    }

    @Test
    public void does_nothing_when_auto_export_is_disabled() {
        when(autoExportPreferences.isEnabled()).thenReturn(false);

        ListenableWorker.Result result = buildWorker().doWork();

        assertThat(result, instanceOf(ListenableWorker.Result.Success.class));
        verifyNoInteractions(backupService);
    }

    @Test
    public void does_nothing_when_no_export_directory_is_chosen() {
        when(autoExportPreferences.isEnabled()).thenReturn(true);
        when(autoExportPreferences.getExportDirectoryUri()).thenReturn(Optional.empty());

        ListenableWorker.Result result = buildWorker().doWork();

        assertThat(result, instanceOf(ListenableWorker.Result.Success.class));
        verifyNoInteractions(backupService);
    }
}
