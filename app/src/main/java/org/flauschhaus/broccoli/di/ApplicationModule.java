package org.flauschhaus.broccoli.di;

import android.app.Application;

import org.flauschhaus.broccoli.BroccoliApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(BroccoliApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application application() {
        return application;
    }
}
