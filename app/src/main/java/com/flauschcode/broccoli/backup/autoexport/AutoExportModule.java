package com.flauschcode.broccoli.backup.autoexport;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.work.WorkManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AutoExportModule {

    @Provides
    @Singleton
    SharedPreferences sharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    WorkManager workManager(Application application) {
        return WorkManager.getInstance(application);
    }
}
