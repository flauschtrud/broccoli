package org.flauschhaus.broccoli;

import android.app.Application;

import org.flauschhaus.broccoli.di.DaggerApplicationComponent;
import org.flauschhaus.broccoli.di.DatabaseModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class BroccoliApplication extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerApplicationComponent.builder()
                .application(this)
                .database(new DatabaseModule(this))
                .build()
                .inject(this);
    }

}
