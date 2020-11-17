package com.flauschcode.broccoli.seasons;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SeasonsModule {

    @Provides
    @Singleton
    SeasonalCalendarHolder seasonalCalendarHolder(Application application) {
        SeasonalCalendarHolder seasonalCalendarHolder = new SeasonalCalendarHolder(application);
        seasonalCalendarHolder.preload();
        return seasonalCalendarHolder;
    }
}
