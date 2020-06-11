package org.flauschhaus.broccoli;

import android.app.Application;
import android.content.Context;

import androidx.databinding.DataBindingUtil;

import org.flauschhaus.broccoli.di.ApplicationComponent;
import org.flauschhaus.broccoli.di.BindingComponent;
import org.flauschhaus.broccoli.di.DaggerApplicationComponent;
import org.flauschhaus.broccoli.di.DaggerBindingComponent;
import org.flauschhaus.broccoli.di.DatabaseModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class BroccoliApplication extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    private static Context context;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

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

    public static Context getContext() {
        return context;
    }

}
