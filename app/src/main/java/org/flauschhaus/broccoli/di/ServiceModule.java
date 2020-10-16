package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.backup.BackupService;
import org.flauschhaus.broccoli.backup.RestoreService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ServiceModule {

    @ContributesAndroidInjector
    BackupService backupService();

    @ContributesAndroidInjector
    RestoreService restoreService();

}
