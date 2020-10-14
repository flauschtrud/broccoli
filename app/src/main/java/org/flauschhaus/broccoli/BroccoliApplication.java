package org.flauschhaus.broccoli;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

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

    public  static final String CHANNEL_ID_BACKUP = "org.flauschhaus.broccoli.channel.backup";

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

        createNotificationChannel();
    }

    public static Context getContext() {
        return context;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_BACKUP, getString(R.string.channel_name_backup), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.channel_description_backup));
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
