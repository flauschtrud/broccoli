package com.flauschcode.broccoli.backup.autoexport;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.flauschcode.broccoli.backup.BackupService;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AutoExportWorker extends Worker {

    static final String EXPORT_FILE_NAME = "broccoli-auto-export.broccoli-archive";
    static final String EXPORT_MIME_TYPE = "application/broccoli-archive";

    private final BackupService backupService;
    private final AutoExportPreferences autoExportPreferences;

    public AutoExportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams,
                             BackupService backupService, AutoExportPreferences autoExportPreferences) {
        super(context, workerParams);
        this.backupService = backupService;
        this.autoExportPreferences = autoExportPreferences;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!autoExportPreferences.isEnabled()) {
            return Result.success();
        }

        Optional<String> directoryUri = autoExportPreferences.getExportDirectoryUri();
        if (!directoryUri.isPresent()) {
            return Result.success();
        }

        DocumentFile directory = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(directoryUri.get()));
        if (directory == null || !directory.canWrite()) {
            return Result.failure();
        }

        DocumentFile exportFile = findOrCreateExportFile(directory);
        if (exportFile == null) {
            return Result.failure();
        }

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        try (OutputStream outputStream = contentResolver.openOutputStream(exportFile.getUri(), "wt")) {
            if (outputStream == null) {
                return Result.failure();
            }
            backupService.writeArchive(outputStream);
            autoExportPreferences.saveLastExportDate(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
            return Result.success();
        } catch (IOException | ExecutionException e) {
            return Result.retry();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.retry();
        }
    }

    private DocumentFile findOrCreateExportFile(DocumentFile directory) {
        DocumentFile existing = directory.findFile(EXPORT_FILE_NAME);
        if (existing != null) {
            return existing;
        }
        return directory.createFile(EXPORT_MIME_TYPE, EXPORT_FILE_NAME);
    }
}
