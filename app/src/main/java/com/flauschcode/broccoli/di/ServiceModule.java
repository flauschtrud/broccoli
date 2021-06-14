package com.flauschcode.broccoli.di;

import com.flauschcode.broccoli.backup.RestoreService;
import com.flauschcode.broccoli.backup.BackupService;
import com.flauschcode.broccoli.support.BillingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ServiceModule {

    @ContributesAndroidInjector
    BackupService backupService();

    @ContributesAndroidInjector
    RestoreService restoreService();

    @ContributesAndroidInjector
    BillingService billingService();

}
