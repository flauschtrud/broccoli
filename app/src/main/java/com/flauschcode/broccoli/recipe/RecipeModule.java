package com.flauschcode.broccoli.recipe;

import android.app.Application;

import com.flauschcode.broccoli.recipe.ingredients.ScaledQuantityBuilder;

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
    @Singleton
    ScaledQuantityBuilder scaledQuantityBuilder() {
        return new ScaledQuantityBuilder();
    }

}
