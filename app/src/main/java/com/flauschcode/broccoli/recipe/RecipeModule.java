package com.flauschcode.broccoli.recipe;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.test.espresso.idling.CountingIdlingResource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import id.zelory.compressor.Compressor;

@Module
public class RecipeModule {

    @Provides
    @Singleton
    Compressor compressor(Application application) {
        return new Compressor(application);
    }

    @Provides
    @Nullable
    @Singleton
    CountingIdlingResource importIdlingResource(){
        return null;
    }
}
