package org.flauschhaus.broccoli.recipes;

import android.app.Application;

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
}
