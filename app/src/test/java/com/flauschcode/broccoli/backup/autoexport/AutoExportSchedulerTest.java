package com.flauschcode.broccoli.backup.autoexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class AutoExportSchedulerTest {

    @Mock
    private WorkManager workManager;

    @Mock
    private AutoExportPreferences autoExportPreferences;

    @InjectMocks
    private AutoExportScheduler autoExportScheduler;

    @Test
    public void does_nothing_when_auto_export_is_disabled() {
        when(autoExportPreferences.isEnabled()).thenReturn(false);

        autoExportScheduler.scheduleIfEnabled();

        verifyNoInteractions(workManager);
    }

    @Test
    public void enqueues_debounced_unique_work_when_enabled() {
        when(autoExportPreferences.isEnabled()).thenReturn(true);
        ArgumentCaptor<OneTimeWorkRequest> requestCaptor = ArgumentCaptor.forClass(OneTimeWorkRequest.class);

        autoExportScheduler.scheduleIfEnabled();

        verify(workManager).enqueueUniqueWork(eq(AutoExportScheduler.UNIQUE_WORK_NAME), eq(ExistingWorkPolicy.REPLACE), requestCaptor.capture());

        OneTimeWorkRequest request = requestCaptor.getValue();
        assertThat(request.getWorkSpec().workerClassName, is(AutoExportWorker.class.getName()));
        assertThat(request.getWorkSpec().initialDelay, is(TimeUnit.MINUTES.toMillis(AutoExportScheduler.DEBOUNCE_DELAY_MINUTES)));
    }
}
