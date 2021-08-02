package com.flauschcode.broccoli;

import android.app.Application;
import android.os.StrictMode;

import androidx.databinding.DataBindingUtil;

import com.flauschcode.broccoli.di.ApplicationComponent;
import com.flauschcode.broccoli.di.BindingComponent;
import com.flauschcode.broccoli.di.DaggerApplicationComponent;
import com.flauschcode.broccoli.di.DaggerBindingComponent;
import com.flauschcode.broccoli.di.DatabaseModule;

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

        if(BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
            StrictMode.allowThreadDiskReads();
            StrictMode.allowThreadDiskWrites();
        }

        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .application(this)
                .database(new DatabaseModule(this))
                .build();
        applicationComponent.inject(this);

        BindingComponent bindingComponent = DaggerBindingComponent.builder()
                .applicationComponent(applicationComponent)
                .application(this)
                .build();
        bindingComponent.inject(this);
        DataBindingUtil.setDefaultComponent(bindingComponent);
    }

}
