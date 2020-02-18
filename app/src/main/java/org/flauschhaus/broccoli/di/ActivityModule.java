package org.flauschhaus.broccoli.di;

import org.flauschhaus.broccoli.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface ActivityModule {

    @ContributesAndroidInjector()
    MainActivity mainActivity();

}
