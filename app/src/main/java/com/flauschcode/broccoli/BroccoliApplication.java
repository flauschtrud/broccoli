package com.flauschcode.broccoli;

import static com.flauschcode.broccoli.settings.SettingsFragment.THEME_KEY;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

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

        String theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(THEME_KEY, getString(R.string.MODE_NIGHT_FOLLOW_SYSTEM));
        if (getString(R.string.MODE_NIGHT_NO).equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (getString(R.string.MODE_NIGHT_YES).equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (getString(R.string.MODE_NIGHT_FOLLOW_SYSTEM).equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

}
